package vn.ontaxi.rest.controller;

import com.google.gson.Gson;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.jpa.entity.Role;
import vn.ontaxi.common.jpa.repository.DriverRepository;
import vn.ontaxi.rest.payload.JwtAuthenticationResponse;
import vn.ontaxi.rest.service.LocationWithDriverService;
import vn.ontaxi.rest.utils.JwtTokenProvider;

import java.util.Arrays;
import java.util.Locale;

@RestController
@RequestMapping("/driver")
public class RestDriverController {
    private final LocationWithDriverService driversMapComponent;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final MessageSource messageSource;
    private final DriverRepository driverRepository;

    @Autowired
    public RestDriverController(LocationWithDriverService driversMapComponent, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, MessageSource messageSource, DriverRepository driverRepository) {
        this.driversMapComponent = driversMapComponent;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.messageSource = messageSource;
        this.driverRepository = driverRepository;
    }

    @CrossOrigin
    @RequestMapping(path = "/location")
    public String getLocationJson() {
        return new Gson().toJson(driversMapComponent.getOnlineDriversLocation(true).values());
    }

    @ApiOperation("Verify driver account via email")
    @RequestMapping(path = "/validateLoginEmail/{email:.+}", method = RequestMethod.GET)
    public RestResult validateLoginEmail(@PathVariable String email) {
        RestResult restResult = new RestResult();
        Driver driver = driverRepository.findByEmailAndBlockedFalse(email);
        if (driver == null) {
            restResult.setSucceed(false);
            restResult.setMessage(messageSource.getMessage("account_is_not_registered", new String[]{email}, Locale.getDefault()));
            return restResult;
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(driver, null, Arrays.asList(new SimpleGrantedAuthority(Role.ROLE_DRIVER.name()))));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        restResult.setData(new JwtAuthenticationResponse(jwt));

        return restResult;
    }
}
