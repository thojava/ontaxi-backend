package vn.ontaxi.rest.controller;

import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.ontaxi.common.jpa.entity.CustomerAccount;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.jpa.entity.Role;
import vn.ontaxi.common.jpa.repository.CustomerAccountRepository;
import vn.ontaxi.common.jpa.repository.DriverRepository;
import vn.ontaxi.rest.payload.CustomerLogin;
import vn.ontaxi.rest.payload.JwtAuthenticationResponse;
import vn.ontaxi.rest.utils.JwtTokenProvider;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Locale;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final DriverRepository driverRepository;
    private final CustomerAccountRepository customerAccountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final MessageSource messageSource;
    private final PasswordEncoder passwordEncoder;

    public AuthController(DriverRepository driverRepository, CustomerAccountRepository customerAccountRepository, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, MessageSource messageSource, PasswordEncoder passwordEncoder) {
        this.driverRepository = driverRepository;
        this.customerAccountRepository = customerAccountRepository;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.messageSource = messageSource;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(path = "/validateLoginEmail/{email:.+}")
    public RestResult validateLoginEmail(@PathVariable String email) {
        RestResult restResult = new RestResult();
        Driver driver = driverRepository.findByEmailAndBlockedFalse(email);
        if (driver == null) {
            restResult.setSucceed(false);
            restResult.setMessage(messageSource.getMessage("account_is_not_registered", new String[]{email}, Locale.getDefault()));
            return restResult;
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(driver, null, Arrays.asList(new SimpleGrantedAuthority(Role.ROLE_DIRVER.name()))));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        restResult.setData(new JwtAuthenticationResponse(jwt));

        return restResult;
    }

    @RequestMapping(path = "/customerLogin", method = RequestMethod.POST)
    public RestResult customerLogin(@Valid @RequestBody CustomerLogin customerLogin) {
        RestResult restResult = new RestResult();
        CustomerAccount customerAccount = customerAccountRepository.findByCustomerEmailOrCustomerPhone(customerLogin.getEmailOrPhone(), customerLogin.getEmailOrPhone());
        if (customerAccount == null) {
            restResult.setSucceed(false);
            restResult.setMessage(messageSource.getMessage("account_is_not_registered", new String[]{customerLogin.getEmailOrPhone()}, Locale.getDefault()));
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

}
