package vn.ontaxi.rest.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.ontaxi.common.constant.EmailType;
import vn.ontaxi.common.jpa.entity.Customer;
import vn.ontaxi.common.jpa.entity.CustomerAccount;
import vn.ontaxi.common.jpa.entity.EmailTemplate;
import vn.ontaxi.common.jpa.repository.CustomerAccountRepository;
import vn.ontaxi.common.jpa.repository.CustomerRepository;
import vn.ontaxi.common.jpa.repository.EmailTemplateRepository;
import vn.ontaxi.common.service.EmailService;
import vn.ontaxi.rest.config.security.CurrentUser;
import vn.ontaxi.rest.payload.SetPasswordRequest;
import vn.ontaxi.rest.payload.dto.CustomerDTO;
import vn.ontaxi.rest.utils.BaseMapper;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private BaseMapper<Customer, CustomerDTO> mapper = new BaseMapper<>(Customer.class, CustomerDTO.class);

    private final CustomerRepository customerRepository;
    private final CustomerAccountRepository customerAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailService emailService;

    public CustomerController(CustomerRepository customerRepository, CustomerAccountRepository customerAccountRepository, PasswordEncoder passwordEncoder, EmailTemplateRepository emailTemplateRepository, EmailService emailService) {
        this.customerRepository = customerRepository;
        this.customerAccountRepository = customerAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailTemplateRepository = emailTemplateRepository;
        this.emailService = emailService;
    }

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping(value = "/createCustomerAccount", method = RequestMethod.POST)
    public RestResult<String> createCustomerAccount(@Valid @RequestBody Customer customer) {
        RestResult<String> restResult = new RestResult<>();
        if (StringUtils.isEmpty(customer.getEmail()) || StringUtils.isEmpty(customer.getPhone())) {
            restResult.setSucceed(false);
            restResult.setMessage("Email và số điện thoại không được để trống");
            return restResult;
        }

        Customer byPhoneOrEmail = customerRepository.findByPhoneOrEmail(customer.getPhone(), customer.getEmail());
        if (byPhoneOrEmail != null) {
            restResult.setSucceed(false);
            restResult.setMessage("Email hoặc số điện thoại đã được đăng ký trước đó");
            return restResult;
        }

        Customer savedCustomer = customerRepository.save(customer);
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setCustomer(savedCustomer);
        customerAccount.setActived(false);
        customerAccount.setToken(UUID.randomUUID().toString());
        customerAccountRepository.save(customerAccount);

        restResult.setData(customerAccount.getToken());

        EmailTemplate setPasswordTemplate = emailTemplateRepository.findByEmailType(EmailType.SET_PASSWORD);
        String emailContent = vn.ontaxi.common.utils.StringUtils.fillRegexParams(setPasswordTemplate.getEmailContent(), new HashMap<String, String>() {{
            put("\\$\\{name\\}", customer.getName());
            put("\\$\\{activate_link\\}", customerAccount.getToken());
        }});

        emailService.sendEmail(setPasswordTemplate.getSubject(), customer.getEmail(), emailContent);

        return restResult;
    }

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

        return restResult;
    }

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping(path = "/resetPassword/{email:.+}")
    public RestResult<String> customerRequestResetPassword(@PathVariable String email) {
        RestResult<String> restResult = new RestResult<>();
        CustomerAccount customerEmail = customerAccountRepository.findByCustomerEmail(email);
        if (customerEmail == null) {
            restResult.setSucceed(false);
            restResult.setMessage("Thông tin khách hàng không tồn tại");
            return restResult;
        }
        customerEmail.setToken(UUID.randomUUID().toString());
        customerAccountRepository.save(customerEmail);
        EmailTemplate resetPassword = emailTemplateRepository.findByEmailType(EmailType.RESET_PASSWORD);
        String emailContent = vn.ontaxi.common.utils.StringUtils.fillRegexParams(resetPassword.getEmailContent(), new HashMap<String, String>() {{
            put("\\$\\{name\\}", customerEmail.getCustomer().getName());
            put("\\$\\{reset_password_link\\}", customerEmail.getToken());
        }});

        emailService.sendEmail(resetPassword.getSubject(), customerEmail.getCustomer().getEmail(), emailContent);
        restResult.setData(customerEmail.getToken());
        return restResult;
    }

    @RequestMapping(path = "/getDetail", method = RequestMethod.GET)
    public RestResult<CustomerDTO> getCustomerDetail(@CurrentUser Customer customer) {
        RestResult<CustomerDTO> restResult = new RestResult<>();
        restResult.setData(mapper.toDtoBean(customer));
        return restResult;
    }


}
