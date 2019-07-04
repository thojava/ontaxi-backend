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
import reactor.bus.Event;
import reactor.bus.EventBus;
import springfox.documentation.annotations.ApiIgnore;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.jpa.entity.Role;
import vn.ontaxi.common.jpa.repository.DriverRepository;
import vn.ontaxi.common.model.Location;
import vn.ontaxi.common.model.LocationWithDriver;
import vn.ontaxi.rest.config.security.CurrentUser;
import vn.ontaxi.rest.payload.JwtAuthenticationResponse;
import vn.ontaxi.rest.service.LocationWithDriverService;
import vn.ontaxi.rest.utils.JwtTokenProvider;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static reactor.bus.selector.Selectors.$;

@RestController
@RequestMapping("/driver")
public class RestDriverController {
    private final LocationWithDriverService driversMapComponent;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final MessageSource messageSource;
    private final DriverRepository driverRepository;
    private final EventBus eventBus;
    private final LocationWithDriverService locationWithDriverService;

    @Autowired
    public RestDriverController(LocationWithDriverService driversMapComponent, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, MessageSource messageSource, DriverRepository driverRepository, EventBus eventBus, LocationWithDriverService locationWithDriverService) {
        this.driversMapComponent = driversMapComponent;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.messageSource = messageSource;
        this.driverRepository = driverRepository;
        this.eventBus = eventBus;
        this.locationWithDriverService = locationWithDriverService;
    }

    @PostConstruct
    public void init() {
        eventBus.on($("updateLocation"), locationWithDriverService);
    }


    @CrossOrigin
    @RequestMapping(path = "/location", method = RequestMethod.GET)
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

    @RequestMapping(path = "/getDriverDetail", method = RequestMethod.POST)
    public RestResult getDriverDetail(@ApiIgnore @CurrentUser Driver driver) {
        RestResult restResult = new RestResult();
        restResult.setData(driver);
        return restResult;
    }

    @RequestMapping(value = "/uploadCurrentLocation/{versionCode}", method = RequestMethod.POST)
    public void uploadCurrentLocation(@ApiIgnore @CurrentUser Driver driver, @PathVariable int versionCode, @RequestBody Location currentLocation) {
//        logger.debug(versionCode + " " + driverCode + " " + currentLocation.getLongitude() + ":" + currentLocation.getLatitude() + ":" + currentLocation.getAccuracy());
        eventBus.notify("updateLocation", Event.wrap(new LocationWithDriver(currentLocation, driver.getEmail(), versionCode, new Date())));
    }
}
