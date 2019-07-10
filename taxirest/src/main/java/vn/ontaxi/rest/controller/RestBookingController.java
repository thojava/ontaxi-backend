package vn.ontaxi.rest.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import vn.ontaxi.common.constant.OrderStatus;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.jpa.entity.ViewPrice;
import vn.ontaxi.common.jpa.repository.BookingRepository;
import vn.ontaxi.common.jpa.repository.PromotionPlanRepository;
import vn.ontaxi.common.jpa.repository.ViewPriceRepository;
import vn.ontaxi.common.service.DistanceMatrixService;
import vn.ontaxi.common.service.PriceCalculator;
import vn.ontaxi.common.utils.BookingUtils;
import vn.ontaxi.common.utils.PriceUtils;
import vn.ontaxi.rest.config.security.CurrentUser;
import vn.ontaxi.rest.payload.dto.BookingDTO;
import vn.ontaxi.rest.payload.dto.request.BookingCalculatePriceRequestDTO;
import vn.ontaxi.rest.payload.dto.request.PostBookingRequestDTO;
import vn.ontaxi.rest.payload.dto.response.BookingCalculatePriceResponseDTO;
import vn.ontaxi.rest.utils.BaseMapper;

import java.util.ArrayList;
import java.util.List;

@RestController
@Transactional
@RequestMapping("/booking")
public class RestBookingController {
    private BaseMapper<Booking, BookingDTO> mapper = new BaseMapper<>(Booking.class, BookingDTO.class);

    private final BookingRepository bookingRepository;
    private final ViewPriceRepository viewPriceRepository;
    private final PromotionPlanRepository promotionPlanRepository;
    private final PriceCalculator priceCalculator;

    @Autowired
    public RestBookingController(BookingRepository bookingRepository, ViewPriceRepository viewPriceRepository, PromotionPlanRepository promotionPlanRepository, PriceCalculator priceCalculator) {
        this.bookingRepository = bookingRepository;
        this.viewPriceRepository = viewPriceRepository;
        this.promotionPlanRepository = promotionPlanRepository;
        this.priceCalculator = priceCalculator;
    }

    @CrossOrigin
    @RequestMapping(path = "/calculateDistanceAndPrice", method = RequestMethod.POST)
    public BookingCalculatePriceResponseDTO calculateDistanceAndPrice(@RequestBody BookingCalculatePriceRequestDTO bookingCalculatePriceRequestDTO) {
        Booking booking = calculateDistanceAndPriceHelper(bookingCalculatePriceRequestDTO);

        if(bookingCalculatePriceRequestDTO.isFirstPriceView()) {
            ViewPrice viewPrice = new ViewPrice();
            viewPrice.setFrom_location(booking.getFrom_location());
            viewPrice.setTo_location(booking.getTo_location());
            viewPrice.setTotal_distance(booking.getTotal_distance());
            viewPrice.setDepartureTime(booking.getDeparture_time());
            viewPrice.setCar_type(booking.getCar_type().name());
            viewPriceRepository.saveAndFlush(viewPrice);
        }

        BaseMapper<Booking, BookingCalculatePriceResponseDTO> mapper = new BaseMapper<>(Booking.class, BookingCalculatePriceResponseDTO.class);
        return mapper.toDtoBean(booking);
    }

    private Booking calculateDistanceAndPriceHelper(BookingCalculatePriceRequestDTO bookingCalculatePriceRequestDTO) {
        BaseMapper<Booking, BookingCalculatePriceRequestDTO> mapper = new BaseMapper<>(Booking.class, BookingCalculatePriceRequestDTO.class);
        Booking booking = mapper.toPersistenceBean(bookingCalculatePriceRequestDTO);
        booking.setUnit_price(priceCalculator.getPricePerKm(bookingCalculatePriceRequestDTO.getCar_type()));
        double distance = DistanceMatrixService.getDistance(bookingCalculatePriceRequestDTO.getFrom_location(), bookingCalculatePriceRequestDTO.getTo_location()) / 1000;
        booking.setTotal_distance(distance);

        double promotionPercentage = BookingUtils.calculatePromotionPercentage(bookingCalculatePriceRequestDTO.getDeparture_time(), distance, false, promotionPlanRepository);
        booking.setPromotionPercentage(promotionPercentage);

        priceCalculator.calculateEstimatedPrice(booking);

        return booking;
    }


    @CrossOrigin
    @RequestMapping(path = "/postBookingFromWebsite", method = RequestMethod.POST)
    public Booking postBookingFromWebsite(@RequestBody PostBookingRequestDTO bookingDTO) {
        // Recalculate the price
        Booking booking = calculateDistanceAndPriceHelper(bookingDTO);
        booking.setName(bookingDTO.getName());
        booking.setEmail(bookingDTO.getEmail());
        booking.setMobile(bookingDTO.getMobile());
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
