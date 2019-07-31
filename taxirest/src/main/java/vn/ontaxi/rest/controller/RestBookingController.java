package vn.ontaxi.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.ontaxi.common.constant.OrderStatus;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.entity.ViewPrice;
import vn.ontaxi.common.jpa.repository.BookingRepository;
import vn.ontaxi.common.jpa.repository.PromotionPlanRepository;
import vn.ontaxi.common.jpa.repository.ViewPriceRepository;
import vn.ontaxi.common.service.DistanceMatrixService;
import vn.ontaxi.common.service.PriceCalculator;
import vn.ontaxi.common.utils.BookingUtils;
import vn.ontaxi.common.utils.PriceUtils;
import vn.ontaxi.rest.payload.dto.request.BookingCalculatePriceRequestDTO;
import vn.ontaxi.rest.payload.dto.request.PostBookingRequestDTO;
import vn.ontaxi.rest.payload.dto.response.BookingCalculatePriceResponseDTO;
import vn.ontaxi.rest.utils.BaseMapper;

@RestController
@Transactional
@RequestMapping("/booking")
public class RestBookingController {
    private final BookingRepository bookingRepository;
    private final ViewPriceRepository viewPriceRepository;
    private final PromotionPlanRepository promotionPlanRepository;
    private final PriceCalculator priceCalculator;
    private final DistanceMatrixService distanceMatrixService;

    @Autowired
    public RestBookingController(BookingRepository bookingRepository, ViewPriceRepository viewPriceRepository, PromotionPlanRepository promotionPlanRepository, PriceCalculator priceCalculator, DistanceMatrixService distanceMatrixService) {
        this.bookingRepository = bookingRepository;
        this.viewPriceRepository = viewPriceRepository;
        this.promotionPlanRepository = promotionPlanRepository;
        this.priceCalculator = priceCalculator;
        this.distanceMatrixService = distanceMatrixService;
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
        double distance = distanceMatrixService.getDistance(bookingCalculatePriceRequestDTO.getFrom_location(), bookingCalculatePriceRequestDTO.getTo_location()) / 1000;
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
}
