package vn.ontaxi.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.fn.Consumer;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.model.LocationWithDriver;
import vn.ontaxi.common.service.DriverService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class LocationWithDriverService implements Consumer<Event<LocationWithDriver>> {
    private Map<String, LocationWithDriver> driversLocation = new HashMap<>();

    private final DriverService driverService;

    @Autowired
    public LocationWithDriverService(DriverService driverService) {
        this.driverService = driverService;
    }

    @Override
    public void accept(Event<LocationWithDriver> locationEvent) {
        LocationWithDriver data = locationEvent.getData();
        driversLocation.put(data.getDriverCode(), data);
    }

    public LocationWithDriver getLocationWithDriverByCode(String driverCode) {
        return driversLocation.getOrDefault(driverCode, new LocationWithDriver());
    }

    public Map<String, LocationWithDriver> getOnlineDriversLocation(boolean showFullDriverInfo) {
        long currentTime = new Date().getTime();
        Map<String, LocationWithDriver> onlineDriverMap = new HashMap<>();
        for (String driverCode : driversLocation.keySet()) {
            LocationWithDriver locationWithDriver = driversLocation.get(driverCode);
            if (currentTime - locationWithDriver.getReceived_time().getTime() <= 60 * 1000 * 10) { // If not update more than 10 minutes. Consider to be offline
                Driver driver = driverService.findByEmail(driverCode);
                if (driver != null) {
                    String driverInfo;
                    if (!showFullDriverInfo) {
                        driverInfo = driver.getLicense_plates() + " - " + driver.getCarType();
                    } else {
                        driverInfo = driver.getName() + " - " + driver.getMobile() + " - " + driver.getCarType();
                    }
                    locationWithDriver.setDriverInfo(driverInfo);
                    onlineDriverMap.put(driverCode, locationWithDriver);
                }
            }
        }

        return onlineDriverMap;
    }
}
