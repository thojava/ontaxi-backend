package vn.ontaxi.hub.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.jpa.entity.DriverPayment;
import vn.ontaxi.common.jpa.repository.DriverPaymentRepository;
import vn.ontaxi.common.jpa.repository.DriverRepository;
import vn.ontaxi.common.jpa.repository.PartnerRepository;
import vn.ontaxi.common.model.LocationWithDriver;
import vn.ontaxi.common.service.ConfigurationService;
import vn.ontaxi.hub.utils.DriverUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("view")
@PropertySource("classpath:application.properties")
public class DriversComponent {
    @Value("${rest.url}")
    private String restUrl;

    private final DriverRepository driverRepository;
    private final PartnerRepository partnerRepository;
    private Map<String, LocationWithDriver> onlineDriversLocation = new HashMap<>();
    private DriverPaymentRepository driverPaymentRepository;
    private final ConfigurationService configurationService;
    private List<Driver> longHaulDrivers;
    private List<Driver> airportDrivers;

    @Autowired
    public DriversComponent(DriverRepository driverRepository, DriverPaymentRepository driverPaymentRepository, ConfigurationService configurationService, PartnerRepository partnerRepository) {
        this.driverRepository = driverRepository;
        this.driverPaymentRepository = driverPaymentRepository;
        this.configurationService = configurationService;
        this.partnerRepository = partnerRepository;
    }

    @PostConstruct
    public void init() {
        String taxiApiKey = this.partnerRepository.findAll().get(0).getApiToken();
        List<LocationWithDriver> onlineDriverMap = DriverUtils.getLocationJson(taxiApiKey, restUrl);
        for (LocationWithDriver locationWithDriver : onlineDriverMap) {
            onlineDriversLocation.put(locationWithDriver.getDriverCode(), locationWithDriver);
        }
    }

    public Iterable<Driver> getLongHaulDrivers() {
        if(longHaulDrivers == null)
            longHaulDrivers = driverRepository.findByAirportFalseAndDeletedFalseOrderByCarType();
        return longHaulDrivers;
    }

    public Iterable<Driver> getAirportDrivers() {
        if(airportDrivers == null)
            airportDrivers = driverRepository.findByAirportTrueAndDeletedFalseOrderByCarType();
        return airportDrivers;
    }

    public Iterable<Driver> getDrivers() {
        return driverRepository.findAllByOrderByCarType();
    }

    public boolean isActive(Driver driver) {
        return onlineDriversLocation.containsKey(driver.getEmail());
    }

    public boolean isLowBalance(Driver driver) {
        return driver.getAmount() < configurationService.getDriver_balance_low_limit() + 100000;
    }

    public int getVersionCode(Driver driver) {
        LocationWithDriver locationWithDriver = onlineDriversLocation.get(driver.getEmail());
        return locationWithDriver != null ? locationWithDriver.getVersionCode() : 0;
    }

    public void deleteDriver() {
        for (Driver driver : getLongHaulDrivers()) {
            if(driver.isBeanSelected()) {
                driver.setDeleted(true);
                driverRepository.save(driver);
            }
        }
        for (Driver driver : getAirportDrivers()) {
            if(driver.isBeanSelected()) {
                driver.setDeleted(true);
                driverRepository.save(driver);
            }
        }
        longHaulDrivers = null;
        airportDrivers = null;
    }

    public String viewDriverDetail(Driver driver) {
        return "driver_detail.jsf?faces-redirect=true&id=" + driver.getId();
    }

    public List<DriverPayment> getDriverPayments() {
        return driverPaymentRepository.findAllByOrderByIdDesc();
    }
}
