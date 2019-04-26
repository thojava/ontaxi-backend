package vn.ontaxi.component;

import com.google.gson.Gson;
import com.google.maps.model.LatLng;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import vn.ontaxi.component.abstracts.AbstractOrderComponent;
import vn.ontaxi.constant.BookingTypes;
import vn.ontaxi.constant.BooleanConstants;
import vn.ontaxi.constant.OrderStatus;
import vn.ontaxi.constant.SendToGroupOptions;
import vn.ontaxi.jpa.entity.Booking;
import vn.ontaxi.jpa.entity.Driver;
import vn.ontaxi.jpa.repository.BookingRepository;
import vn.ontaxi.jpa.repository.DriverRepository;
import vn.ontaxi.jpa.repository.PersistentCustomerRepository;
import vn.ontaxi.model.Location;
import vn.ontaxi.model.direction.GoogleDirections;
import vn.ontaxi.service.*;
import vn.ontaxi.utils.BookingUtils;
import vn.ontaxi.utils.PolyUtil;
import vn.ontaxi.utils.PriceUtils;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("view")
public class OrderDetailComponent extends AbstractOrderComponent {
    private Booking booking;

    private final Environment env;
    private final BookingRepository bookingRepository;
    private final DriverRepository driverRepository;
    private final FCMService fcmService;
    private final DistanceMatrixService distanceMatrixService;
    private final ConfigurationService configurationService;
    private final PriceCalculator priceCalculator;

    private boolean with_snap;
    private boolean display_outward_routes = true;
    private List<Driver> selectedDrivers = new ArrayList<>();

    @Autowired
    public OrderDetailComponent(Environment env, BookingRepository bookingRepository, DriverRepository driverRepository, FCMService fcmService, DistanceMatrixService distanceMatrixService, ConfigurationService configurationService, MessageSource messageSource, PersistentCustomerRepository persistentCustomerRepository, PriceCalculator priceCalculator) {
        super(messageSource, persistentCustomerRepository);
        this.env = env;
        this.bookingRepository = bookingRepository;
        this.driverRepository = driverRepository;
        this.fcmService = fcmService;
        this.distanceMatrixService = distanceMatrixService;
        this.configurationService = configurationService;
        this.priceCalculator = priceCalculator;
    }

    @PostConstruct
    public void init() {
        sendToGroupOption = SendToGroupOptions.INDIVIDUAL;
    }

    public void recalculateDriverFee() {
        double fee = PriceUtils.calculateDriverFee(booking.getTotalPriceBeforePromotion(), booking.getFee_percentage(), booking.getPromotionPercentage());
        booking.setTotal_fee(fee);
        saveBooking();
    }

    public void onChangeCarType() {
        if(!booking.isFixedPrice()) {
            booking.setUnit_price(priceCalculator.getPricePerKm(booking.getCar_type()));
            priceCalculator.calculateEstimatedPrice(booking);
        }

        double fee = PriceUtils.calculateDriverFee(booking.getTotalPriceBeforePromotion(), booking.getFee_percentage(), booking.getPromotionPercentage());
        booking.setTotal_fee(fee);

        saveBooking();
    }

    public void saveBooking() {
        booking = bookingRepository.saveAndFlush(this.booking);
    }

    public void onChangeFixedPrice() {
        booking.setTotalPriceBeforePromotion(booking.getTotal_price());

        double fee = PriceUtils.calculateDriverFee(booking.getTotalPriceBeforePromotion() - booking.getTransport_fee(), booking.getFee_percentage(), booking.getPromotionPercentage());
        booking.setTotal_fee(fee);

        saveBooking();
    }

    public String payDriver() {
        booking.setPaidToDriver(BooleanConstants.YES);
        booking = bookingRepository.saveAndFlush(booking);

        return "index.jsf?faces-redirect=true";
    }

    public String sendOrderToDriver() {
        BookingUtils.setupToDriver(booking, selectedDrivers, sendToGroupOption, getDrivers());

        booking.setBooking_type(BookingTypes.XE_DI_TINH);
        booking.setStatus(OrderStatus.NEW);
        booking = bookingRepository.saveAndFlush(booking);

        fcmService.postNewTaxiOrder(booking);

        return "index.jsf?faces-redirect=true";
    }

    private String parseRouteToJson(String routes) throws Exception {
        List<Map<String, Double>> coordinates = new ArrayList<>();
        List<Location> locations = SnapToRoadService.parseRoutes(routes);
        if (with_snap) {
            locations = SnapToRoadService.getDistance(locations, configurationService.getAccuracy_limit());
        }

        double previousLat = 0;
        double previousLng = 0;
        for (Location point : locations) {
            double latitude = point.getLatitude();
            double longitude = point.getLongitude();
            boolean useGoogleDirection = false;
            if (with_snap) {
                if (previousLat > 0 && previousLng > 0) {
                    float[] results = new float[1];
                    DistanceMatrixService.computeDistanceAndBearing(previousLat, previousLng, latitude, longitude, results);
                    if (results[0] > 500) {
                        useGoogleDirection = true;
                        GoogleDirections googleDirection = DistanceMatrixService.getGoogleDirection(previousLat + "," + previousLng, latitude + "," + longitude, env.getProperty("google_direction_key"));
                        List<LatLng> latLngs = PolyUtil.decode(googleDirection.getRoutes().get(0).getOverviewPolyline().getPoints());
                        for (LatLng latLng : latLngs) {
                            Map<String, Double> cor = new HashMap<>();
                            cor.put("lat", latLng.lat);
                            cor.put("lng", latLng.lng);
                            cor.put("acc", 0.d);
                            coordinates.add(cor);
                        }
                    }
                }
                previousLat = latitude;
                previousLng = longitude;
            }

            if (!useGoogleDirection) {
                Map<String, Double> cor = new HashMap<>();
                cor.put("lat", point.getLatitude());
                cor.put("lng", point.getLongitude());
                cor.put("acc", point.getAccuracy());
                coordinates.add(cor);
            }
        }

        return new Gson().toJson(coordinates);
    }

    public String getLocationJson() throws Exception {
        return parseRouteToJson(display_outward_routes ? booking.getRoutes() : booking.getReturn_routes());
    }

    public double getDistanceCalculated() throws Exception {
        return distanceMatrixService.getDistanceFromRoutes(booking.getRoutes(), with_snap) / 1000;
    }

    public double getReturnDistanceCalculated() throws Exception {
        return distanceMatrixService.getDistanceFromRoutes(booking.getReturn_routes(), with_snap) / 1000;
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

    public Booking getBooking() {
        if (booking == null) {
            Map<String, String> params = FacesContext.getCurrentInstance().
                    getExternalContext().getRequestParameterMap();
            String parameterOne = params.get("id");
            booking = bookingRepository.findOne(Long.parseLong(parameterOne));
        }

        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public boolean isWith_snap() {
        return with_snap;
    }

    public boolean getWith_snap() {
        return with_snap;
    }

    public void setDisplay_outward_routes(boolean display_outward_routes) {
        this.display_outward_routes = display_outward_routes;
    }

    public boolean getDisplay_outward_routes() {
        return display_outward_routes;
    }

    public void setWith_snap(boolean with_snap) {
        this.with_snap = with_snap;
    }
}
