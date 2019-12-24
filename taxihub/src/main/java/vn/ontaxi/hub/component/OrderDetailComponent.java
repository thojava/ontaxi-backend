package vn.ontaxi.hub.component;

import com.google.gson.Gson;
import com.google.maps.model.LatLng;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.constant.BookingTypes;
import vn.ontaxi.common.constant.BooleanConstants;
import vn.ontaxi.common.constant.OrderStatus;
import vn.ontaxi.common.constant.SendToGroupOptions;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.jpa.repository.BookingRepository;
import vn.ontaxi.common.jpa.repository.DriverRepository;
import vn.ontaxi.common.jpa.repository.PersistentCustomerRepository;
import vn.ontaxi.common.model.Location;
import vn.ontaxi.common.model.direction.GoogleDirections;
import vn.ontaxi.common.service.*;
import vn.ontaxi.common.utils.BookingUtils;
import vn.ontaxi.common.utils.NumberUtils;
import vn.ontaxi.common.utils.PriceUtils;
import vn.ontaxi.common.utils.StringUtils;
import vn.ontaxi.hub.component.abstracts.AbstractOrderComponent;
import vn.ontaxi.hub.service.CustomerService;
import vn.ontaxi.hub.utils.PolyUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.*;
import java.util.stream.Collectors;

import static vn.ontaxi.common.utils.BookingUtils.DELIMITER;

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
    private final CustomerService customerService;
    private final PriceUtils priceUtils;

    private boolean with_snap;
    private boolean display_outward_routes = true;
    private List<Driver> selectedDrivers = new ArrayList<>();

    @Autowired
    public OrderDetailComponent(Environment env, BookingRepository bookingRepository, DriverRepository driverRepository, FCMService fcmService, DistanceMatrixService distanceMatrixService, ConfigurationService configurationService, MessageSource messageSource, PersistentCustomerRepository persistentCustomerRepository, PriceCalculator priceCalculator, CustomerService customerService, PriceUtils priceUtils) {
        super(messageSource, persistentCustomerRepository);
        this.env = env;
        this.bookingRepository = bookingRepository;
        this.driverRepository = driverRepository;
        this.fcmService = fcmService;
        this.distanceMatrixService = distanceMatrixService;
        this.configurationService = configurationService;
        this.priceCalculator = priceCalculator;
        this.customerService = customerService;
        this.priceUtils = priceUtils;
    }

    @PostConstruct
    public void init() {
        sendToGroupOption = SendToGroupOptions.INDIVIDUAL;
    }

    public void recalculateDriverFee() {
        double fee = priceUtils.calculateDriverFee(booking.getTotalPriceBeforePromotion(), booking.getFee_percentage(), booking.getPromotionPercentage());
        booking.setTotal_fee(fee);
        saveBooking();
    }

    public void onChangeCarType() {
        if (!booking.isFixedPrice()) {
            booking.setUnit_price(priceCalculator.getPricePerKm(booking.getCar_type()));
            priceCalculator.calculateEstimatedPrice(booking);
        }

        double fee = priceUtils.calculateDriverFee(booking.getTotalPriceBeforePromotion(), booking.getFee_percentage(), booking.getPromotionPercentage());
        booking.setTotal_fee(fee);

        saveBooking();
    }

    public void onChangeDriverWillWait() {
        priceCalculator.calculateEstimatedPrice(booking);
        saveBooking();
    }

    public void saveBooking() {
        booking = bookingRepository.saveAndFlush(this.booking);
    }

    public void onChangeFixedPrice() {
        booking.setTotalPriceBeforePromotion(booking.getTotal_price());

        double fee = priceUtils.calculateDriverFee(booking.getTotalPriceBeforePromotion() - booking.getTransport_fee(), booking.getFee_percentage(), booking.getPromotionPercentage());
        booking.setTotal_fee(fee);

        saveBooking();
    }

    public String payDriver() {
        booking.setPaidToDriver(BooleanConstants.YES);
        booking = bookingRepository.saveAndFlush(booking);

        return "index.jsf?faces-redirect=true";
    }

    public String confirmOrder() {
        booking.setStatus(OrderStatus.CUSTOEMR_CONFIRM);
        bookingRepository.save(booking);
        return "index.jsf?faces-redirect=true";
    }

    public String sendOrderToDriver() {
        List<Driver> driversToBeSent = BookingUtils.getDriversToBeSent(booking, selectedDrivers, sendToGroupOption, getDrivers());
        if (driversToBeSent.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Hệ thống không tìm được tài xế phù hợp để gửi order"));
            return null;
        }

        booking.setTo_drivers(driversToBeSent.stream().map(Driver::getEmail).collect(Collectors.joining(DELIMITER)));
        booking.setBooking_type(BookingTypes.XE_DI_TINH);
        booking.setStatus(OrderStatus.NEW);
        booking = bookingRepository.saveAndFlush(booking);

        List<String> fcmTokens = driversToBeSent.stream().map(Driver::getFcmToken).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        fcmService.postNewTaxiOrder(booking, fcmTokens);

        customerService.updateCustomerInfo(booking);
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
        return driverRepository.findByDeletedFalseAndStatus(Driver.Status.ACTIVATED);
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
            this.booking = bookingRepository.findById(Long.parseLong(parameterOne)).orElse(null);
            if(this.booking != null && this.booking.isNew()) {
                selectedDrivers = Arrays.stream(this.booking.getTo_drivers().split(DELIMITER)).map(driverRepository::findByEmail).collect(Collectors.toList());
            }
        }

        return booking;
    }

    public String totalPriceInDetail(Booking booking) {
        StringBuilder sb = new StringBuilder();
        if (booking.isRoundTrip()) {
            double lowDistance = Math.min(booking.getOutward_distance(), booking.getReturn_distance());
            double highDistance = Math.max(booking.getOutward_distance(), booking.getReturn_distance());

            double freeWaitTime = PriceUtils.getFreeWaitTime(highDistance);


            sb.append(NumberUtils.distanceWithKM(highDistance)).append(" * ").append(NumberUtils.formatAmountInVND(booking.getUnit_price()))
                    .append(" + ").append(NumberUtils.distanceWithKM(lowDistance)).append(" * ").append(NumberUtils.formatAmountInVND(booking.getUnit_price()))
                    .append(" * ").append(priceUtils.getReturnRoundPercentage(booking.isDriver_will_wait())).append("%")
                    .append(" + (").append(Math.max(booking.getWait_hours(), freeWaitTime)).append(" giờ ").append(" - ").append(freeWaitTime).append(" giờ ").append(")")
                    .append(" = ").append(Math.max(booking.getWait_hours(), freeWaitTime) - freeWaitTime).append(" giờ ")
                    .append(" * ").append(NumberUtils.formatAmountInVND(PriceUtils.getPricePerWaitHour(booking.getCar_type())))
                    .append(" + ").append(NumberUtils.formatAmountInVND(booking.getTransport_fee()));
            sb.append(" = ").append(NumberUtils.formatAmountInVND(booking.getActual_total_price()));


        } else {
            sb.append(NumberUtils.distanceWithKM(booking.getActual_total_distance())).append(" * ").append(NumberUtils.formatAmountInVND(booking.getUnit_price()))
                    .append(" = ").append(NumberUtils.formatAmountInVND(booking.getActual_total_price()));
        }

        return sb.toString();
    }

    public String totalEstimationPriceInDetail(Booking booking) {
        StringBuilder sb = new StringBuilder();
        if (booking.isRoundTrip()) {
            double lowDistance = booking.getTotal_distance();
            double highDistance = booking.getTotal_distance();

            double freeWaitTime = PriceUtils.getFreeWaitTime(highDistance);


            sb.append(NumberUtils.distanceWithKM(highDistance)).append(" * ").append(NumberUtils.formatAmountInVND(booking.getUnit_price()))
                    .append(" + ").append(NumberUtils.distanceWithKM(lowDistance)).append(" * ").append(NumberUtils.formatAmountInVND(booking.getUnit_price()))
                    .append(" * ").append(priceUtils.getReturnRoundPercentage(booking.isDriver_will_wait())).append("%");
            if (booking.isDriver_will_wait()) {
                sb.append(String.format(" + (%.2f giờ - %.2f giờ) * %s",
                        Math.max(booking.getWait_hours(), freeWaitTime),
                        freeWaitTime,
                        NumberUtils.formatAmountInVND(PriceUtils.getPricePerWaitHour(booking.getCar_type()))));
            }
            sb.append(" = ").append(NumberUtils.formatAmountInVND(booking.getTotal_price()));


        } else {
            sb.append(NumberUtils.distanceWithKM(booking.getTotal_distance())).append(" * ").append(NumberUtils.formatAmountInVND(booking.getUnit_price()))
                    .append(" = ").append(NumberUtils.formatAmountInVND(booking.getTotal_price()));
        }

        return sb.toString();
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
