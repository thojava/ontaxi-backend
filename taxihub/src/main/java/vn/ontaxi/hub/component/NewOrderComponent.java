package vn.ontaxi.hub.component;

import vn.ontaxi.hub.component.abstracts.AbstractOrderComponent;
import vn.ontaxi.common.constant.BookingTypes;
import vn.ontaxi.common.constant.BooleanConstants;
import vn.ontaxi.common.constant.OrderStatus;
import vn.ontaxi.common.constant.SendToGroupOptions;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.jpa.repository.BookingRepository;
import vn.ontaxi.common.jpa.repository.DriverRepository;
import vn.ontaxi.common.jpa.repository.PersistentCustomerRepository;
import vn.ontaxi.common.jpa.repository.PromotionPlanRepository;
import vn.ontaxi.common.service.DistanceMatrixService;
import vn.ontaxi.common.service.FCMService;
import vn.ontaxi.common.service.PriceCalculator;
import vn.ontaxi.common.utils.BookingUtils;
import vn.ontaxi.hub.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.utils.PriceUtils;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Scope("view")
public class NewOrderComponent extends AbstractOrderComponent {
    private Booking booking;

    private final DriverRepository driverRepository;
    private final BookingRepository bookingRepository;
    private final FCMService fcmService;
    private final PromotionPlanRepository promotionPlanRepository;
    private final UserCredentialComponent userCredentialComponent;
    private final PriceCalculator priceCalculator;

    private List<Driver> selectedDrivers = new ArrayList<>();

    @Autowired
    public NewOrderComponent(DriverRepository driverRepository, BookingRepository bookingRepository, FCMService fcmService, UserCredentialComponent userCredentialComponent, MessageSource messageSource, PromotionPlanRepository promotionPlanRepository, PersistentCustomerRepository persistentCustomerRepository, PriceCalculator priceCalculator) {
        super(messageSource, persistentCustomerRepository);
        this.driverRepository = driverRepository;
        this.bookingRepository = bookingRepository;
        this.userCredentialComponent = userCredentialComponent;
        this.fcmService = fcmService;
        this.promotionPlanRepository = promotionPlanRepository;
        this.priceCalculator = priceCalculator;
    }

    @PostConstruct
    public void init() {
        booking = new Booking();
        booking.setPromotionPercentage(BookingUtils.calculatePromotionPercentage(DateUtils.today(), 0, false, promotionPlanRepository));
        booking.setFee_percentage(BookingUtils.calculateFeePercentage(booking));
        booking.setStatus(OrderStatus.NEW);

        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        booking.setName(params.get("name"));
        booking.setMobile(params.get("mobile"));

        sendToGroupOption = SendToGroupOptions.INDIVIDUAL;
    }

    public String scheduleBooking() {
        booking.setUnit_price(priceCalculator.getPricePerKm(booking.getCar_type()));
        calculateDistanceAndPriceAndFee(true);

        booking.setStatus(OrderStatus.ORDERED);
        booking.setViewed(BooleanConstants.YES);
        booking.setCreatedBy(userCredentialComponent.getUserName());
        booking = bookingRepository.saveAndFlush(booking);
        return "index.jsf?faces-redirect=true";
    }

    public String postBooking() {
        booking.setBooking_type(BookingTypes.XE_DI_TINH);
        booking.setStatus(OrderStatus.NEW);
        booking.setUnit_price(priceCalculator.getPricePerKm(booking.getCar_type()));
        calculateDistanceAndPriceAndFee(false);

        if(booking.getTotal_distance() == 0) {
            throw new RuntimeException("Tổng khoảng cách là 0đ. Cần chọn lại điểm đi điểm đến");
        }

        BookingUtils.setupToDriver(booking, selectedDrivers, sendToGroupOption, getDrivers());

        booking.setCreatedBy(userCredentialComponent.getUserName());
        booking = bookingRepository.saveAndFlush(booking);

        fcmService.postNewTaxiOrder(booking);

        return "index.jsf?faces-redirect=true";
    }

    private void calculateDistanceAndPriceAndFee(boolean recalculateFee) {
        booking.setTotal_distance(DistanceMatrixService.getDistance(booking.getFrom_location(), booking.getTo_location()) / 1000);
        if(recalculateFee) {
            booking.setPromotionPercentage(BookingUtils.calculatePromotionPercentage(booking.getDeparture_time(), booking.getTotal_distance(), booking.isLaterPaidPersistentCustomer(), promotionPlanRepository));
            booking.setFee_percentage(BookingUtils.calculateFeePercentage(booking));
        }
        if (!BooleanConstants.YES.equalsIgnoreCase(booking.getIsFixedPrice())) {
            priceCalculator.calculateEstimatedPrice(booking);
        } else {
            booking.setTotalPriceBeforePromotion(booking.getTotal_price());
        }

        double fee = PriceUtils.calculateDriverFee(booking.getTotalPriceBeforePromotion() - booking.getTransport_fee(), booking.getFee_percentage(), booking.getPromotionPercentage());
        booking.setTotal_fee(fee);
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Iterable<Driver> getDrivers() {
        return driverRepository.findAll();
    }

    public List<Driver> getSelectedDrivers() {
        return selectedDrivers;
    }

    public void setSelectedDrivers(List<Driver> selectedDrivers) {
        this.selectedDrivers = selectedDrivers;
    }

    public String getSendToGroupOption() {
        return sendToGroupOption;
    }

    public void setSendToGroupOption(String sendToGroupOption) {
        this.sendToGroupOption = sendToGroupOption;
    }
}
