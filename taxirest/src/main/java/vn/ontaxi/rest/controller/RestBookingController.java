package vn.ontaxi.rest.controller;

import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import vn.ontaxi.common.constant.BooleanConstants;
import vn.ontaxi.common.constant.OrderStatus;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.jpa.entity.ViewPrice;
import vn.ontaxi.common.jpa.repository.BookingRepository;
import vn.ontaxi.common.jpa.repository.DriverRepository;
import vn.ontaxi.common.jpa.repository.PromotionPlanRepository;
import vn.ontaxi.common.jpa.repository.ViewPriceRepository;
import vn.ontaxi.common.service.*;
import vn.ontaxi.common.utils.BookingUtils;
import vn.ontaxi.common.utils.PriceUtils;
import vn.ontaxi.rest.config.security.CurrentUser;
import vn.ontaxi.rest.payload.dto.BookingCalculatePriceDTO;
import vn.ontaxi.rest.payload.dto.BookingDTO;
import vn.ontaxi.rest.utils.BaseMapper;
import vn.ontaxi.rest.utils.SMSContentBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@Transactional
@RequestMapping("/booking")
public class RestBookingController {
    private static final Logger logger = LoggerFactory.getLogger(RestBookingController.class);
    private BaseMapper<Booking, BookingDTO> mapper = new BaseMapper<>(Booking.class, BookingDTO.class);

    @PersistenceContext
    private EntityManager em;

    private final BookingRepository bookingRepository;
    private final ViewPriceRepository viewPriceRepository;
    private final PromotionPlanRepository promotionPlanRepository;
    private final DriverRepository driverRepository;
    private final FCMService fcmService;
    private final SMSService smsService;
    private final MessageSource messageSource;
    private final ConfigurationService configurationService;
    private final PriceCalculator priceCalculator;

    @Autowired
    public RestBookingController(BookingRepository bookingRepository, ViewPriceRepository viewPriceRepository, PromotionPlanRepository promotionPlanRepository, DriverRepository driverRepository, FCMService fcmService, SMSService smsService, MessageSource messageSource, ConfigurationService configurationService, PriceCalculator priceCalculator) {
        this.bookingRepository = bookingRepository;
        this.viewPriceRepository = viewPriceRepository;
        this.promotionPlanRepository = promotionPlanRepository;
        this.driverRepository = driverRepository;
        this.fcmService = fcmService;
        this.smsService = smsService;
        this.messageSource = messageSource;
        this.configurationService = configurationService;
        this.priceCalculator = priceCalculator;
    }

    @RequestMapping(path = "/acceptOrder", method = RequestMethod.POST)
    public synchronized RestResult acceptOrder(@ApiIgnore @CurrentUser Driver driver, @RequestBody long orderId) {
        em.flush();
        em.clear();
        Booking booking = bookingRepository.findOne(orderId);
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
        } else {
            restResult.setSucceed(false);
            restResult.setMessage(messageSource.getMessage("order_has_been_accepted_by_other_driver", null, Locale.getDefault()));
        }
        logger.debug(driver.getEmail() + " accept " + orderId + " complete " + booking.getStatus());
        return restResult;
    }

    @RequestMapping(path = "/completeOrder", method = RequestMethod.POST)
    public synchronized RestResult completeOrder(@ApiIgnore @CurrentUser Driver driver, @RequestBody Booking booking) {
        Booking persistedBooking = bookingRepository.findOne(booking.getId());
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
                PriceUtils.calculateActualPrice(persistedBooking);
            } else {
                persistedBooking.setActual_total_price(persistedBooking.getTotal_price());
                persistedBooking.setActualTotalPriceBeforePromotion(persistedBooking.getTotal_price());
            }

            double priceBeforePromotionWithoutTransportFee = persistedBooking.getActualTotalPriceBeforePromotion() - persistedBooking.getTransport_fee();
            double fee = PriceUtils.calculateDriverFee(priceBeforePromotionWithoutTransportFee, persistedBooking.getFee_percentage(), persistedBooking.getPromotionPercentage());
            persistedBooking.setActual_total_fee(fee);
            persistedBooking.setStatus(OrderStatus.COMPLETED);
            bookingRepository.saveAndFlush(persistedBooking);

            double amt = persistedBooking.getActual_total_fee() - persistedBooking.getTotal_fee();
            driver.decreaseAmt(amt, logger);
            driverRepository.saveAndFlush(driver);

            // Do not send message for contract customer
            if (!persistedBooking.isLaterPaidPersistentCustomer()) {
                new Thread(() -> smsService.sendSMS(persistedBooking.getMobile(), SMSContentBuilder.buildCompleteOrderSMSContent(persistedBooking))).start();
            }
        }

        RestResult<BookingDTO> restResult = new RestResult<>();
        restResult.setData(mapper.toDtoBean(persistedBooking));
        return restResult;
    }

    @CrossOrigin
    @RequestMapping(path = "/firstCalculateDistanceAndPrice", method = RequestMethod.POST)
    public BookingDTO firstCalculateDistanceAndPrice(@RequestBody BookingCalculatePriceDTO bookingCalculatePriceDTO) {
        Booking booking = calculateDistanceAndPrice(bookingCalculatePriceDTO);

        ViewPrice viewPrice = new ViewPrice();
        viewPrice.setFrom_location(booking.getFrom_location());
        viewPrice.setTo_location(booking.getTo_location());
        viewPrice.setTotal_distance(booking.getTotal_distance());
        viewPrice.setDepartureTime(booking.getDeparture_time());
        viewPrice.setCar_type(booking.getCar_type().name());
        viewPriceRepository.saveAndFlush(viewPrice);

        return mapper.toDtoBean(booking);
    }

    @CrossOrigin
    @RequestMapping(path = "/calculateDistanceAndPrice", method = RequestMethod.POST)
    public Booking calculateDistanceAndPrice(@RequestBody BookingCalculatePriceDTO bookingCalculatePriceDTO) {
        BaseMapper<Booking, BookingCalculatePriceDTO> mapper = new BaseMapper<>(Booking.class, BookingCalculatePriceDTO.class);
        Booking booking = mapper.toPersistenceBean(bookingCalculatePriceDTO);
        booking.setUnit_price(priceCalculator.getPricePerKm(bookingCalculatePriceDTO.getCar_type()));
        double distance = DistanceMatrixService.getDistance(bookingCalculatePriceDTO.getFrom_location(), bookingCalculatePriceDTO.getTo_location()) / 1000;
        booking.setTotal_distance(distance);

        double promotionPercentage = BookingUtils.calculatePromotionPercentage(bookingCalculatePriceDTO.getDeparture_time(), distance, false, promotionPlanRepository);
        booking.setPromotionPercentage(promotionPercentage);

        priceCalculator.calculateEstimatedPrice(booking);

        return booking;
    }


    @CrossOrigin
    @RequestMapping(path = "/postBookingFromWebsite", method = RequestMethod.POST)
    public Booking postBookingFromWebsite(@RequestBody BookingDTO bookingDTO) {
        // Recalculate the price
        Booking booking = calculateDistanceAndPrice(bookingDTO);
        // Update status
        booking.setCreatedBy("site");
        booking.setStatus(OrderStatus.ORDERED);
        // Recalculate the fee
        booking.setFee_percentage(12);
        booking.setTotal_fee(PriceUtils.calculateDriverFee(booking.getTotalPriceBeforePromotion(), booking.getFee_percentage(), booking.getPromotionPercentage()));

        bookingRepository.saveAndFlush(booking);
        return booking;
    }

    // Get the updated booking status, there are case when the app is closed and the booking status may be missed
    @RequestMapping(value = "/updateBookingStatus", method = RequestMethod.POST)
    public RestResult updateBookingStatus(@RequestBody List<Booking> bookings) {
        List<Booking> updatedBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            Booking serverBooking = bookingRepository.findOne(booking.getId());
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
    public RestResult getNewBooking() {
        List<Booking> newBooking = bookingRepository.findByStatus(OrderStatus.NEW);
        RestResult<List<BookingDTO>> restResult = new RestResult<>();
        restResult.setData(mapper.toDtoBean(newBooking));

        return restResult;
    }

    @ApiOperation("Download booking history")
    @RequestMapping(value = "/downloadHistory", method = RequestMethod.POST)
    public RestResult getAllBooking(@ApiIgnore @CurrentUser Driver driver) {

        List<Booking> allBooking = bookingRepository.findByAcceptedByDriver_Email(driver.getEmail());
        RestResult<List<BookingDTO>> restResult = new RestResult<>();
        restResult.setData(mapper.toDtoBean(allBooking));
        return restResult;
    }
}
