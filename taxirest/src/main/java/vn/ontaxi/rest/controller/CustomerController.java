package vn.ontaxi.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import vn.ontaxi.rest.payload.CustomerLogin;
import vn.ontaxi.rest.payload.JwtAuthenticationResponse;
import vn.ontaxi.rest.payload.SetPasswordRequest;
import vn.ontaxi.rest.payload.dto.AddressDTO;
import vn.ontaxi.rest.payload.dto.BehaviorDTO;
import vn.ontaxi.rest.payload.dto.CustomerDTO;
import vn.ontaxi.rest.utils.BaseMapper;
import vn.ontaxi.rest.utils.JwtTokenProvider;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private BaseMapper<Customer, CustomerDTO> mapper = new BaseMapper<>(Customer.class, CustomerDTO.class);
    private BaseMapper<Address, AddressDTO> addressMapper = new BaseMapper<>(Address.class, AddressDTO.class);
    private BaseMapper<Behavior, BehaviorDTO> behaviorMapper = new BaseMapper<>(Behavior.class, BehaviorDTO.class);

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
    public RestResult<String> createCustomerAccount(@Valid @RequestBody Customer customer) {
        RestResult<String> restResult = new RestResult<>();
        if (StringUtils.isEmpty(customer.getEmail()) || StringUtils.isEmpty(customer.getPhone())) {
            restResult.setSucceed(false);
            restResult.setMessage("Email và số điện thoại không được để trống");
            return restResult;
        }

        CustomerAccount foundCustomerAccount = customerAccountRepository.findByCustomerEmailOrCustomerPhone(customer.getEmail(), customer.getPhone());
        if (foundCustomerAccount != null) {
            restResult.setSucceed(false);
            restResult.setMessage("Email hoặc số điện thoại đã được đăng ký trước đó");
            return restResult;
        }

        Customer persistedCustomer = customerRepository.findByPhoneOrEmail(customer.getPhone(), customer.getEmail());
        if (persistedCustomer == null) {
            customer.getAddresses().forEach(address -> address.setCustomer(customer));
            persistedCustomer = customerRepository.save(customer);
        }

        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setCustomer(persistedCustomer);
        customerAccount.setActived(false);
        customerAccount.setToken(UUID.randomUUID().toString());
        customerAccountRepository.save(customerAccount);

        new Thread(() -> {
            EmailTemplate setPasswordTemplate = emailTemplateRepository.findByEmailType(EmailType.SET_PASSWORD);
            String emailContent = EmailUtils.getEmailContentCustomizedForCustomer(setPasswordTemplate.getEmailContent(), customer);
            emailContent = StringUtils.fillRegexParams(emailContent, new HashMap<String, String>() {{
                put("\\$\\{activate_link\\}", "https://ontaxi.vn/khach-hang/nhap-mat-khau?token=" + customerAccount.getToken());
            }});
            emailService.sendEmail(setPasswordTemplate.getSubject(), customer.getEmail(), emailContent);
        }).start();

        restResult.setMessage("Kiểm tra email để hoàn thành cài đặt");
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
        CustomerAccount customer = customerAccountRepository.findByCustomerPhone(phone);
        if (customer == null) {
            restResult.setSucceed(false);
            restResult.setMessage("Thông tin khách hàng không tồn tại");
            return restResult;
        }
        customer.setToken(UUID.randomUUID().toString());
        customerAccountRepository.save(customer);
        EmailTemplate resetPassword = emailTemplateRepository.findByEmailType(EmailType.RESET_PASSWORD);
        String emailContent = vn.ontaxi.common.utils.StringUtils.fillRegexParams(resetPassword.getEmailContent(), new HashMap<String, String>() {{
            put("\\$\\{name\\}", customer.getCustomer().getName());
            put("\\$\\{reset_password_link\\}", customer.getToken());
        }});

        emailService.sendEmail(resetPassword.getSubject(), customer.getCustomer().getEmail(), emailContent);
        restResult.setData(customer.getToken());
        return restResult;
    }

    @RequestMapping(path = "/currentCustomerProfile", method = RequestMethod.GET)
    public RestResult<CustomerDTO> getCustomerDetail(@ApiIgnore @CurrentUser Customer customer) {
        RestResult<CustomerDTO> restResult = new RestResult<>();
        restResult.setData(mapper.toDtoBean(customer));
        return restResult;
    }

    @RequestMapping(path = "/getCustomerInfo/{email:.+}", method = RequestMethod.GET)
    public RestResult<CustomerDTO> getCustomerInfo(@PathVariable String email) {
        RestResult<CustomerDTO> restResult = new RestResult<>();
        restResult.setData(mapper.toDtoBean(customerRepository.findByEmail(email)));
        return restResult;
    }

    @ApiOperation(value = "Login the customer with phone number and password")
    @ApiResponse(code = 200, message = "When the token is valid and data checking completed then the password will be set to the account and the account is activated. This API will return access token for client to access or update customer profile later")
    @RequestMapping(path = "/customerLogin", method = RequestMethod.POST)
    public RestResult customerLogin(@Valid @RequestBody CustomerLogin customerLogin) {
        RestResult restResult = new RestResult();
        CustomerAccount customerAccount = customerAccountRepository.findByCustomerEmailOrCustomerPhone(customerLogin.getPhone(), customerLogin.getPhone());
        if (customerAccount == null) {
            restResult.setSucceed(false);
            restResult.setMessage(messageSource.getMessage("account_is_not_registered", new String[]{customerLogin.getPhone()}, Locale.getDefault()));
            return restResult;
        }

        if (!passwordEncoder.matches(customerLogin.getPassword(), customerAccount.getPassword())) {
            restResult.setSucceed(false);
            restResult.setMessage(messageSource.getMessage("password_incorrect", new String[]{}, Locale.getDefault()));
            return restResult;
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(customerAccount.getCustomer(), null, Arrays.asList(new SimpleGrantedAuthority(Role.ROLE_CUSTOMER.name()))));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        restResult.setData(new JwtAuthenticationResponse(jwt));

        return restResult;
    }

    @RequestMapping(path = "/updateCustomer", method = RequestMethod.POST)
    public RestResult<CustomerDTO> updateCustomerInfo(@ApiIgnore @CurrentUser Customer customer, @RequestBody CustomerDTO customerDTO) {

        customer.setPhone(customerDTO.getPhone());
        customer.setEmail(customerDTO.getEmail());
        customer.setName(customerDTO.getName());
        customer.setGender(customerDTO.getGender());
        customer.setBirthDay(customerDTO.getBirthDay());
        customer.setJob(customerDTO.getJob());
        customer.setAddresses(addressMapper.toPersistenceBean(customerDTO.getAddresses()));
        if (CollectionUtils.isNotEmpty(customer.getAddresses()))
            customer.getAddresses().forEach(address -> address.setCustomer(customer));
        customer.setBehaviors(new HashSet<>(behaviorMapper.toPersistenceBean(new ArrayList<>(customerDTO.getBehaviors()))));

        customerRepository.save(customer);
        RestResult<CustomerDTO> restResult = new RestResult<>();
        restResult.setData(mapper.toDtoBean(customer));
        return restResult;
    }

}
