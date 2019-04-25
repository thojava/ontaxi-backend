package vn.ontaxi.component;

import vn.ontaxi.jpa.entity.Driver;
import vn.ontaxi.jpa.entity.DriverPayment;
import vn.ontaxi.jpa.repository.DriverPaymentRepository;
import vn.ontaxi.jpa.repository.DriverRepository;
import vn.ontaxi.model.LocationWithDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Scope("view")
public class DriversComponent {
    private final DriverRepository driverRepository;
    private Map<String, LocationWithDriver> onlineDriversLocation;
    private DriverPaymentRepository driverPaymentRepository;
    private final ConfigurationComponent configurationComponent;
    private List<Driver> longHaulDrivers;
    private List<Driver> airportDrivers;

    @Autowired
    public DriversComponent(DriverRepository driverRepository, DriversMapComponent driversMapComponent, DriverPaymentRepository driverPaymentRepository, ConfigurationComponent configurationComponent) {
        this.driverRepository = driverRepository;
        onlineDriversLocation = driversMapComponent.getOnlineDriversLocation(true);
        this.driverPaymentRepository = driverPaymentRepository;
        this.configurationComponent = configurationComponent;
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
        return driver.getAmount() < configurationComponent.getDriver_balance_low_limit() + 100000;
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

    public void updateDriver(Driver driver) {
        driverRepository.saveAndFlush(driver);
    }

    public List<DriverPayment> getDriverPayments() {
        return driverPaymentRepository.findAllByOrderByIdDesc();
    }
}
