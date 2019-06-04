package vn.ontaxi.rest.controller;

import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.jpa.repository.DriverRepository;
import vn.ontaxi.rest.payload.JwtAuthenticationResponse;
import vn.ontaxi.rest.utils.JwtTokenProvider;

import java.util.Locale;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final DriverRepository driverRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final MessageSource messageSource;

    public AuthController(DriverRepository driverRepository, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, MessageSource messageSource) {
        this.driverRepository = driverRepository;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.messageSource = messageSource;
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

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(driver, null, null));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        restResult.setData(new JwtAuthenticationResponse(jwt));

        return restResult;
    }
}
