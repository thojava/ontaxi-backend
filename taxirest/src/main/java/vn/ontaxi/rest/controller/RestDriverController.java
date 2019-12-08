package vn.ontaxi.rest.controller;

import com.google.gson.Gson;
import com.google.maps.model.LatLng;
import io.swagger.annotations.ApiOperation;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.bus.Event;
import reactor.bus.EventBus;
import springfox.documentation.annotations.ApiIgnore;
import vn.ontaxi.common.constant.BooleanConstants;
import vn.ontaxi.common.constant.OrderStatus;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.jpa.entity.Role;
import vn.ontaxi.common.jpa.repository.BookingRepository;
import vn.ontaxi.common.jpa.repository.DriverRepository;
import vn.ontaxi.common.model.Location;
import vn.ontaxi.common.model.LocationWithDriver;
import vn.ontaxi.common.service.ConfigurationService;
import vn.ontaxi.common.service.FCMService;
import vn.ontaxi.common.service.SMSService;
import vn.ontaxi.common.utils.NumberUtils;
import vn.ontaxi.common.utils.PriceUtils;
import vn.ontaxi.rest.config.security.CurrentUser;
import vn.ontaxi.rest.payload.JwtAuthenticationResponse;
import vn.ontaxi.rest.payload.dto.BookingDTO;
import vn.ontaxi.rest.payload.dto.DriverDTO;
import vn.ontaxi.rest.payload.dto.DriverInfoDTO;
import vn.ontaxi.rest.service.LocationWithDriverService;
import vn.ontaxi.rest.utils.BaseMapper;
import vn.ontaxi.common.service.JwtTokenProvider;
import vn.ontaxi.rest.utils.SMSContentBuilder;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.*;

import static reactor.bus.selector.Selectors.$;

@RestController
@Transactional
@RequestMapping("/driver")
public class RestDriverController {
    private static final Logger logger = LoggerFactory.getLogger(RestDriverController.class);

    private BaseMapper<Booking, BookingDTO> mapper = new BaseMapper<>(Booking.class, BookingDTO.class);
    private BaseMapper<Driver, DriverDTO> driverMapper = new BaseMapper<>(Driver.class, DriverDTO.class);

    private final LocationWithDriverService driversMapComponent;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final MessageSource messageSource;
    private final DriverRepository driverRepository;
    private final EventBus eventBus;
    private final LocationWithDriverService locationWithDriverService;
    private final BookingRepository bookingRepository;
    private final FCMService fcmService;
    private final SMSService smsService;
    private final ConfigurationService configurationService;
    private final PriceUtils priceUtils;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public RestDriverController(LocationWithDriverService driversMapComponent, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, MessageSource messageSource, DriverRepository driverRepository, EventBus eventBus, LocationWithDriverService locationWithDriverService, BookingRepository bookingRepository, FCMService fcmService, SMSService smsService, ConfigurationService configurationService, PriceUtils priceUtils) {
        this.driversMapComponent = driversMapComponent;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.messageSource = messageSource;
        this.driverRepository = driverRepository;
        this.eventBus = eventBus;
        this.locationWithDriverService = locationWithDriverService;
        this.bookingRepository = bookingRepository;
        this.fcmService = fcmService;
        this.smsService = smsService;
        this.configurationService = configurationService;
        this.priceUtils = priceUtils;
    }

    @PostConstruct
    public void init() {
        eventBus.on($("updateLocation"), locationWithDriverService);
    }


    @CrossOrigin
    @RequestMapping(path = "/location", method = RequestMethod.GET)
    public String getLocationJson(@RequestParam boolean showFullDriverInfo) {
        return new Gson().toJson(driversMapComponent.getOnlineDriversLocation(showFullDriverInfo).values());
    }

    @CrossOrigin
    @RequestMapping(path = "/location/{bookingIdentify}", method = RequestMethod.GET)
    public RestResult getCurrentLocation(@PathVariable String bookingIdentify) {
        RestResult<DriverInfoDTO> restResult = new RestResult<>();
        Booking booking = bookingRepository.findByIdentify(bookingIdentify);

        if (booking == null || booking.isCompleted()) {
            restResult.setSucceed(false);
            return restResult;
        }

        Driver driver = booking.getAccepted_by_driver();
        LocationWithDriver locationWithDriver = locationWithDriverService.getLocationWithDriverByCode(driver.getEmail());
        restResult.setData(new DriverInfoDTO(driver, new LatLng(locationWithDriver.getLatitude(), locationWithDriver.getLongitude())));

        return restResult;
    }

    @ApiOperation("Verify driver account via email")
    @RequestMapping(path = "/validateLoginEmail/{email:.+}", method = RequestMethod.POST)
    public RestResult validateLoginEmail(@PathVariable String email) {
        RestResult<JwtAuthenticationResponse> restResult = new RestResult<>();
        Driver driver = driverRepository.findByEmailAndDeletedFalse(email);
        if (driver == null || driver.getStatus() != Driver.Status.ACTIVATED) {
            restResult.setSucceed(false);
            restResult.setMessage(messageSource.getMessage("account_is_not_registered", new String[]{email}, Locale.getDefault()));
            return restResult;
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(driver, null, Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_DRIVER.name()))));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        restResult.setData(new JwtAuthenticationResponse(jwt, driver.getName()));

        return restResult;
    }

    @RequestMapping(path = "/getDriverDetail", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public RestResult getDriverDetail(@ApiIgnore @CurrentUser Driver driver) {
        RestResult<Driver> restResult = new RestResult<>();
        restResult.setData(driver);
        return restResult;
    }

    @RequestMapping(value = "/uploadCurrentLocation/{versionCode}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public void uploadCurrentLocation(@ApiIgnore @CurrentUser Driver driver, @PathVariable int versionCode, @RequestBody Location currentLocation) {
//        logger.debug(versionCode + " " + driverCode + " " + currentLocation.getLongitude() + ":" + currentLocation.getLatitude() + ":" + currentLocation.getAccuracy());
        eventBus.notify("updateLocation", Event.wrap(new LocationWithDriver(currentLocation, driver.getEmail(), versionCode, new Date())));
    }

    @RequestMapping(value = "/uploadFCMToken", consumes = MediaType.TEXT_PLAIN_VALUE, method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public RestResult<Boolean> uploadFCMToken(@ApiIgnore @CurrentUser Driver driver,  @RequestBody String fcmToken) {
        driver.setFcmToken(fcmToken);
        driverRepository.saveAndFlush(driver);
        RestResult<Boolean> restResult = new RestResult<>();
        restResult.setData(true);
        return restResult;
    }

    @ApiOperation("In progress booking")
    @GetMapping(path = "/inprogress/{id}")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public RestResult updateToInProgressStatus(@ApiIgnore @CurrentUser Driver driver, @PathVariable Long id) {
        RestResult restResult = new RestResult();
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isPresent() && driver.getEmail().equals(booking.get().getAccepted_by())) {
            Booking b = booking.get();
            b.setStatus(OrderStatus.IN_PROGRESS);
            bookingRepository.save(b);
        } else {
            restResult.setSucceed(false);
            restResult.setMessage("Can't find this booking");
        }

        return restResult;
    }

    @RequestMapping(path = "/acceptOrder", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public synchronized RestResult acceptOrder(@ApiIgnore @CurrentUser Driver driver, @RequestBody long orderId) {
        em.flush();
        em.clear();
        Booking booking = bookingRepository.findById(orderId).get();
        logger.debug(driver.getEmail() + " accept " + orderId + " start " + booking.getStatus());
        RestResult restResult = new RestResult();

        if (driver.getAmount() < booking.getTotal_fee() + configurationService.getDriver_balance_low_limit()) {
            restResult.setSucceed(false);
            restResult.setMessage(messageSource.getMessage("your_account_amount_is_not_enough_for_this_order", null, Locale.getDefault()));
            return restResult;
        }

        if (booking.getStatus().equalsIgnoreCase(OrderStatus.NEW)) {
            booking.setStatus(OrderStatus.ACCEPTED);
            booking.setAccepted_by(driver.getEmail());
            booking = bookingRepository.saveAndFlush(booking);

            driver.decreaseAmt(booking.getTotal_fee(), logger);
            driver = driverRepository.saveAndFlush(driver);

            // Send a broadcast fcm message to all device to notify the booking is accepted
            fcmService.postAcceptedTaxiOrder(booking);

            // Send SMS message to inform customer
            if (!booking.isLaterPaidPersistentCustomer()) {
                Booking finalBooking = booking;
                Driver finalDriver = driver;
                new Thread(() -> smsService.sendSMS(finalBooking.getMobile(), SMSContentBuilder.buildDriverAcceptedSMSContent(finalBooking, finalDriver))).start();
            }

            // Need this flush to make sure other thread trying to accept does not using old data
            em.flush();

            restResult.setMessage(messageSource.getMessage("order_has_been_accepted_successfully",
                    new Object[]{NumberUtils.formatAmountInVND(booking.getTotal_fee()), NumberUtils.formatAmountInVND(driver.getAmount())}, Locale.getDefault()));
        } else {
            restResult.setSucceed(false);
            restResult.setMessage(messageSource.getMessage("order_has_been_accepted_by_other_driver", null, Locale.getDefault()));
        }
        logger.debug(driver.getEmail() + " accept " + orderId + " complete " + booking.getStatus());
        return restResult;
    }

    @RequestMapping(path = "/completeOrder", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public synchronized RestResult completeOrder(@ApiIgnore @CurrentUser Driver driver, @RequestBody Booking booking) {
        Booking persistedBooking = bookingRepository.findById(booking.getId()).get();
        if (!booking.isCompleted()) {
            persistedBooking.setActual_total_distance(booking.getActual_total_distance());
            persistedBooking.setOutward_distance(booking.getOutward_distance());
            persistedBooking.setReturn_distance(booking.getReturn_distance());
            persistedBooking.setWait_hours(booking.getWait_hours());
            persistedBooking.setTransport_fee(booking.getTransport_fee());
            persistedBooking.setRoutes(booking.getRoutes());
            persistedBooking.setReturn_routes(booking.getReturn_routes());
            persistedBooking.setActual_departure_time(booking.getActual_departure_time());
            persistedBooking.setArrival_time(booking.getArrival_time());
            persistedBooking.setReturnDepartureTime(booking.getReturnDepartureTime());
            persistedBooking.setOutwardArrivalTime(booking.getOutwardArrivalTime());

            if (!BooleanConstants.YES.equalsIgnoreCase(persistedBooking.getIsFixedPrice())) {
                priceUtils.calculateActualPrice(persistedBooking);
            } else {
                persistedBooking.setActual_total_price(persistedBooking.getTotal_price());
                persistedBooking.setActualTotalPriceBeforePromotion(persistedBooking.getTotal_price());
            }

            double priceBeforePromotionWithoutTransportFee = persistedBooking.getActualTotalPriceBeforePromotion() - persistedBooking.getTransport_fee();
            double fee = priceUtils.calculateDriverFee(priceBeforePromotionWithoutTransportFee, persistedBooking.getFee_percentage(), persistedBooking.getPromotionPercentage());
            persistedBooking.setActual_total_fee(fee);
            persistedBooking.setStatus(OrderStatus.COMPLETED);
            persistedBooking = bookingRepository.saveAndFlush(persistedBooking);

            double amt = persistedBooking.getActual_total_fee() - persistedBooking.getTotal_fee();
            driver.decreaseAmt(amt, logger);
            driverRepository.saveAndFlush(driver);

            // Do not send message for contract customer
            if (!persistedBooking.isLaterPaidPersistentCustomer()) {
                Booking finalPersistedBooking = persistedBooking;
                new Thread(() -> smsService.sendSMS(finalPersistedBooking.getMobile(), SMSContentBuilder.buildCompleteOrderSMSContent(finalPersistedBooking, priceUtils))).start();
            }
        }

        RestResult<BookingDTO> restResult = new RestResult<>();
        BaseMapper<Booking, BookingDTO> mapper = new BaseMapper<>(Booking.class, BookingDTO.class);
        restResult.setData(mapper.toDtoBean(persistedBooking));
        return restResult;
    }

    // Get the updated booking status, there are case when the app is closed and the booking status may be missed
    @RequestMapping(value = "/updateBookingStatus", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public RestResult updateBookingStatus(@RequestBody List<Booking> bookings) {
        List<Booking> updatedBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            Booking serverBooking = bookingRepository.findById(booking.getId()).get();
            if (!serverBooking.getStatus().equals(booking.getStatus())) {
                booking.setStatus(serverBooking.getStatus());
                updatedBookings.addAll(bookings);
            }
        }
        RestResult<List<BookingDTO>> restResult = new RestResult<>();
        restResult.setData(mapper.toDtoBean(updatedBookings));

        return restResult;
    }

    @ApiOperation("Get new booking")
    @RequestMapping(value = "/getNewBooking", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public RestResult getNewBooking() {
        List<Booking> newBooking = bookingRepository.findByStatus(OrderStatus.NEW);
        RestResult<List<BookingDTO>> restResult = new RestResult<>();
        restResult.setData(mapper.toDtoBean(newBooking));

        return restResult;
    }

    @ApiOperation("Download booking history")
    @RequestMapping(value = "/downloadHistory", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public RestResult getAllBooking(@ApiIgnore @CurrentUser Driver driver) {

        List<Booking> allBooking = bookingRepository.findByAcceptedByDriver_Email(driver.getEmail());
        RestResult<List<BookingDTO>> restResult = new RestResult<>();
        restResult.setData(mapper.toDtoBean(allBooking));
        return restResult;
    }

    @ApiOperation("Register new driver")
    @PostMapping(value = "/register")
    public RestResult register(@Valid @RequestBody DriverDTO driverDTO) {

        Driver existedDriverByPhone = driverRepository.findByMobile(driverDTO.getMobile());
        Driver existedDriverByEmail = driverRepository.findByEmail(driverDTO.getEmail());
        if (existedDriverByPhone != null || existedDriverByEmail != null) {
            RestResult restResult = new RestResult();
            restResult.setSucceed(false);
            restResult.setMessage("Thông tin tài xế đã tồn tại trên hệ thống");
            return restResult;
        }


        Driver driver = driverMapper.toPersistenceBean(driverDTO);
        driver.setId(null);
        driver.setAmount(0.0);
        driver.setLevel(0);
        driver.setStatus(Driver.Status.REGISTRY);
        driverRepository.save(driver);
        return new RestResult();
    }

    @RequestMapping(value = "/getAppVersion", method = RequestMethod.GET)
    public RestResult<String> getAppVersion() {
        RestResult<String> restResult = new RestResult();
        try {
            String newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=vn.ontaxi.driver&hl=vi")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select(".hAyfc .htlgb")
                    .get(7)
                    .ownText();
            restResult.setData(newVersion);
        } catch (Exception e) {
            restResult.setData("0");
        }

        return restResult;
    }
}
