package vn.ontaxi.hub.component;

import com.google.maps.GeoApiContext;
import com.google.maps.PlaceAutocompleteRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AutocompletePrediction;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.env.Environment;
import vn.ontaxi.common.constant.*;
import vn.ontaxi.common.jpa.entity.Customer;
import vn.ontaxi.common.jpa.repository.*;
import vn.ontaxi.common.service.FeeCalculator;
import vn.ontaxi.common.utils.StringUtils;
import vn.ontaxi.hub.component.abstracts.AbstractOrderComponent;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.service.DistanceMatrixService;
import vn.ontaxi.common.service.FCMService;
import vn.ontaxi.common.service.PriceCalculator;
import vn.ontaxi.common.utils.BookingUtils;
import vn.ontaxi.hub.service.CustomerService;
import vn.ontaxi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.utils.PriceUtils;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final FeeCalculator feeCalculator;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final DistanceMatrixService distanceMatrixService;
    private final PriceUtils priceUtils;
    private final Environment env;
    private String GOOGLE_MAP_PLACE_API_KEY;

    private List<Driver> selectedDrivers = new ArrayList<>();

    @Autowired
    public NewOrderComponent(DriverRepository driverRepository, BookingRepository bookingRepository, FCMService fcmService, UserCredentialComponent userCredentialComponent, MessageSource messageSource, PromotionPlanRepository promotionPlanRepository, PersistentCustomerRepository persistentCustomerRepository, PriceCalculator priceCalculator, FeeCalculator feeCalculator, CustomerService customerService, CustomerRepository customerRepository, DistanceMatrixService distanceMatrixService, PriceUtils priceUtils, Environment env) {
        super(messageSource, persistentCustomerRepository);
        this.driverRepository = driverRepository;
        this.bookingRepository = bookingRepository;
        this.userCredentialComponent = userCredentialComponent;
        this.fcmService = fcmService;
        this.promotionPlanRepository = promotionPlanRepository;
        this.priceCalculator = priceCalculator;
        this.feeCalculator = feeCalculator;
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.distanceMatrixService = distanceMatrixService;
        this.priceUtils = priceUtils;
        this.env = env;
    }

    @PostConstruct
    public void init() {
        GOOGLE_MAP_PLACE_API_KEY = env.getProperty("google_map_place_recommendation_key");

        booking = new Booking();
        booking.setPromotionPercentage(BookingUtils.calculatePromotionPercentage(DateUtils.today(), 0, false, promotionPlanRepository));
        booking.setFee_percentage(feeCalculator.getDefaultFeePercentage());
        booking.setCar_type(CarType.N4);
        booking.setStatus(OrderStatus.NEW);

        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        booking.setName(params.get("name"));
        booking.setMobile(params.get("mobile"));

        sendToGroupOption = SendToGroupOptions.INDIVIDUAL;
    }

    public void updateCustomerInformation() {
        if (StringUtils.isNotEmpty(booking.getMobile())) {
            List<Customer> customers = customerRepository.findByPhone(booking.getMobile());
            if (CollectionUtils.isNotEmpty(customers)) {
                booking.setName(customers.get(0).getName());
                booking.setReturnedCustomer(true);
            } else {
                booking.setReturnedCustomer(false);
            }
        }
    }

    public List<String> recommendPlaces(String address) throws InterruptedException, ApiException, IOException {

        GeoApiContext geoApiContext = new GeoApiContext.Builder().apiKey(GOOGLE_MAP_PLACE_API_KEY)
                .build();

        AutocompletePrediction[] autocompletePredictions = PlacesApi.placeAutocomplete(geoApiContext, address, new PlaceAutocompleteRequest.SessionToken()).language("vi_VN").await();
        return Stream.of(autocompletePredictions).map(autocompletePrediction -> autocompletePrediction.description).collect(Collectors.toList());
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

    private void calculateDistanceAndPriceAndFee(boolean recalculateFee) {
        booking.setTotal_distance(distanceMatrixService.getDistance(booking.getFrom_location(), booking.getTo_location()) / 1000);
        if(recalculateFee) {
            booking.setPromotionPercentage(BookingUtils.calculatePromotionPercentage(booking.getDeparture_time(), booking.getTotal_distance(), booking.isLaterPaidPersistentCustomer(), promotionPlanRepository));
            booking.setFee_percentage(feeCalculator.getDefaultFeePercentage());
        }
        if (!BooleanConstants.YES.equalsIgnoreCase(booking.getIsFixedPrice())) {
            priceCalculator.calculateEstimatedPrice(booking);
        } else {
            booking.setTotalPriceBeforePromotion(booking.getTotal_price());
        }

        double fee = priceUtils.calculateDriverFee(booking.getTotalPriceBeforePromotion() - booking.getTransport_fee(), booking.getFee_percentage(), booking.getPromotionPercentage());
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
