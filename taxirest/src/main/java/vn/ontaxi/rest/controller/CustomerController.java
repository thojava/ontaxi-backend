package vn.ontaxi.rest.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import vn.ontaxi.common.constant.EmailType;
import vn.ontaxi.common.jpa.entity.*;
import vn.ontaxi.common.jpa.repository.CustomerAccountRepository;
import vn.ontaxi.common.jpa.repository.CustomerRepository;
import vn.ontaxi.common.jpa.repository.EmailTemplateRepository;
import vn.ontaxi.common.service.EmailService;
import vn.ontaxi.common.utils.EmailUtils;
import vn.ontaxi.common.utils.StringUtils;
import vn.ontaxi.rest.config.security.CurrentUser;
import vn.ontaxi.rest.payload.*;
import vn.ontaxi.rest.payload.dto.AddressDTO;
import vn.ontaxi.rest.payload.dto.CustomerDTO;
import vn.ontaxi.rest.utils.BaseMapper;
import vn.ontaxi.rest.utils.JwtTokenProvider;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private BaseMapper<Customer, CustomerDTO> mapper = new BaseMapper<>(Customer.class, CustomerDTO.class);
    private BaseMapper<Address, AddressDTO> addressMapper = new BaseMapper<>(Address.class, AddressDTO.class);

    private final CustomerRepository customerRepository;
    private final CustomerAccountRepository customerAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailService emailService;
    private final MessageSource messageSource;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public CustomerController(CustomerRepository customerRepository, CustomerAccountRepository customerAccountRepository, PasswordEncoder passwordEncoder, EmailTemplateRepository emailTemplateRepository, EmailService emailService, MessageSource messageSource, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.customerRepository = customerRepository;
        this.customerAccountRepository = customerAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailTemplateRepository = emailTemplateRepository;
        this.emailService = emailService;
        this.messageSource = messageSource;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @ApiOperation("Create customer account base on input json data")
    @ApiResponse(code = 200, message = "When all information is valid and customer account not existed there will be and email send to customer with set password link")
    @Transactional(rollbackFor = Exception.class)
    @RequestMapping(value = "/createCustomerAccount", method = RequestMethod.POST)
    public RestResult<String> createCustomerAccount(@Valid @RequestBody CustomerRegistration customerRegistration) {
        RestResult<String> restResult = new RestResult<>();
        if (StringUtils.isEmpty(customerRegistration.getEmail()) || StringUtils.isEmpty(customerRegistration.getPhone())) {
            restResult.setSucceed(false);
            restResult.setMessage("Email và số điện thoại không được để trống");
            return restResult;
        }

        CustomerAccount foundCustomerAccount = customerAccountRepository.findByCustomerEmailOrCustomerPhone(customerRegistration.getEmail(), customerRegistration.getPhone());
        if (foundCustomerAccount != null) {
            restResult.setSucceed(false);
            restResult.setMessage("Email hoặc số điện thoại đã được đăng ký trước đó");
            return restResult;
        }

        Customer persistedCustomer = customerRepository.findByPhoneOrEmail(customerRegistration.getPhone(), customerRegistration.getEmail());
        if (persistedCustomer == null) {
            persistedCustomer = new Customer();
            persistedCustomer.setPhone(customerRegistration.getPhone());
            persistedCustomer.setName(customerRegistration.getName());
            persistedCustomer.setJob(customerRegistration.getJob());
            persistedCustomer.setBirthDay(customerRegistration.getBirthDay());
            persistedCustomer.setGender(customerRegistration.getGender());
            persistedCustomer.setEmail(customerRegistration.getEmail());
            persistedCustomer = customerRepository.save(persistedCustomer);
        }

        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setCustomer(persistedCustomer);
        customerAccount.setActived(false);
        customerAccount.setPassword(passwordEncoder.encode(customerRegistration.getPassword()));
        customerAccount.setToken(UUID.randomUUID().toString());
        customerAccountRepository.save(customerAccount);

        Customer finalPersistedCustomer = persistedCustomer;
        new Thread(() -> {
            EmailTemplate activeAccount = emailTemplateRepository.findByEmailType(EmailType.ACTIVE_ACCOUNT);
            String emailContent = EmailUtils.getEmailContentCustomizedForCustomer(activeAccount.getEmailContent(), finalPersistedCustomer);
            emailContent = StringUtils.fillRegexParams(emailContent, new HashMap<String, String>() {{
                put("\\$\\{name\\}", customerRegistration.getName());
                put("\\$\\{activate_link\\}", "https://ontaxi.vn/khach-hang/kich-hoat-tai-khoan?token=" + customerAccount.getToken());
            }});
            emailService.sendEmail(activeAccount.getSubject(), customerRegistration.getEmail(), emailContent);
        }).start();

        restResult.setMessage("Kiểm tra email để kích hoạt tài khoản");
        return restResult;
    }

    @ApiOperation("Activate customer account. After this step, customer can login")
    @RequestMapping(value = "/activeAccount/{token}", method = RequestMethod.GET)
    public RestResult<String> activeAccount(@PathVariable String token) {
        RestResult<String> restResult = new RestResult<>();
        CustomerAccount customerAccount = customerAccountRepository.findByToken(token);
        if (customerAccount != null) {
            customerAccount.setToken("");
            customerAccount.setActived(true);
            restResult.setData("Kích hoạt tài khoản thành công");
        } else {
            restResult.setSucceed(false);
            restResult.setData("Tài khoản không tồn tại trong hệ thống");
        }

        return restResult;
    }

    @ApiOperation("/setPassword")
    @ApiResponse(code = 200, message = "When the token is valid and data checking completed then the password will be set to the account and the account is activated")
    @RequestMapping(value = "/setPassword", method = RequestMethod.POST)
    public RestResult<Void> setPassword(@Valid @RequestBody SetPasswordRequest setPasswordRequest) {
        CustomerAccount customerAccount = customerAccountRepository.findByToken(setPasswordRequest.getToken());
        RestResult<Void> restResult = new RestResult<>();
        if (customerAccount == null) {
            restResult.setSucceed(false);
            restResult.setMessage("Khách hàng không tồn tại trên hệ thống");
            return restResult;
        }

        customerAccount.setToken("");
        customerAccount.setActived(true);
        customerAccount.setPassword(passwordEncoder.encode(setPasswordRequest.getPassword()));
        customerAccountRepository.save(customerAccount);

        new Thread(() -> {
            EmailTemplate accountActivatedNotificationTemplate = emailTemplateRepository.findByEmailType(EmailType.ACCOUNT_ACTIVATED_NOTIFICATION);
            String emailContent = StringUtils.fillRegexParams(accountActivatedNotificationTemplate.getEmailContent(), new HashMap<String, String>() {{
                put("\\$\\{name\\}", customerAccount.getCustomer().getName());
            }});
            emailService.sendEmail(accountActivatedNotificationTemplate.getSubject(), customerAccount.getCustomer().getEmail(), emailContent);
        }).start();

        return restResult;
    }

    @ApiOperation("Reset password for a specific customer with input as his email")
    @ApiResponse(code = 200, message = "Request is valid. If the checking is okay then there will be an email with reset password link is sent to customer")
    @Transactional(rollbackFor = Exception.class)
    @RequestMapping(path = "/resetPassword/{phone:.+}", method = RequestMethod.POST)
    public RestResult<String> customerRequestResetPassword(@PathVariable String phone) {
        RestResult<String> restResult = new RestResult<>();
        CustomerAccount customerAccount = customerAccountRepository.findByCustomerPhone(phone);
        if (customerAccount == null) {
            restResult.setSucceed(false);
            restResult.setMessage("Thông tin khách hàng không tồn tại");
            return restResult;
        }
        customerAccount.setToken(UUID.randomUUID().toString());
        customerAccountRepository.save(customerAccount);
        EmailTemplate resetPassword = emailTemplateRepository.findByEmailType(EmailType.RESET_PASSWORD);
        String emailContent = vn.ontaxi.common.utils.StringUtils.fillRegexParams(resetPassword.getEmailContent(), new HashMap<String, String>() {{
            put("\\$\\{name\\}", customerAccount.getCustomer().getName());
            put("\\$\\{reset_password_link\\}", customerAccount.getToken());
        }});

        emailService.sendEmail(resetPassword.getSubject(), customerAccount.getCustomer().getEmail(), emailContent);
        restResult.setData(customerAccount.getToken());
        return restResult;
    }

    @RequestMapping(path = "/currentCustomerProfile", method = RequestMethod.GET)
    public RestResult<CustomerDTO> getCustomerDetail(@ApiIgnore @CurrentUser Customer customer) {
        RestResult<CustomerDTO> restResult = new RestResult<>();
        restResult.setData(mapper.toDtoBean(customer));
        return restResult;
    }

    @ApiOperation(value = "Login the customer with phone number and password")
    @ApiResponse(code = 200, message = "When the token is valid and data checking completed then the password will be set to the account and the account is activated. This API will return access token for client to access or update customer profile later")
    @RequestMapping(path = "/customerLogin", method = RequestMethod.POST)
    public RestResult customerLogin(@Valid @RequestBody CustomerLogin customerLogin) {
        RestResult restResult = new RestResult();
        CustomerAccount customerAccount = customerAccountRepository.findByCustomerEmailOrCustomerPhone(customerLogin.getPhone(), customerLogin.getPhone());
        if (customerAccount == null || !customerAccount.isActived()) {
            restResult.setSucceed(false);
            restResult.setMessage(messageSource.getMessage("account_is_not_registered", new String[]{customerLogin.getPhone()}, Locale.getDefault()));
            return restResult;
        }

        if (!passwordEncoder.matches(customerLogin.getPassword(), customerAccount.getPassword())) {
            restResult.setSucceed(false);
            restResult.setMessage(messageSource.getMessage("password_incorrect", new String []{}, Locale.getDefault()));
            return restResult;
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(customerAccount.getCustomer(), null, Arrays.asList(new SimpleGrantedAuthority(Role.ROLE_CUSTOMER.name()))));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        restResult.setData(new JwtAuthenticationResponse(jwt));

        return restResult;
    }

    @RequestMapping(path = "/updateCustomer", method = RequestMethod.POST)
    public RestResult<CustomerDTO> updateCustomerInfo(@ApiIgnore @CurrentUser Customer customer,@Valid @RequestBody CustomerUpdateInfo customerUpdateInfo) {

        customer.setPhone(customerUpdateInfo.getPhone());
        customer.setEmail(customerUpdateInfo.getEmail());
        customer.setName(customerUpdateInfo.getName());
        customer.setGender(customerUpdateInfo.getGender());
        customer.setBirthDay(customerUpdateInfo.getBirthDay());
        customer.setJob(customerUpdateInfo.getJob());
        customer.setAddresses(addressMapper.toPersistenceBean(customerUpdateInfo.getAddresses()));
        if (CollectionUtils.isNotEmpty(customer.getAddresses()))
            customer.getAddresses().forEach(address -> address.setCustomer(customer));
        customerRepository.save(customer);
        RestResult<CustomerDTO> restResult = new RestResult<>();
        restResult.setData(mapper.toDtoBean(customer));
        return restResult;
    }

}
