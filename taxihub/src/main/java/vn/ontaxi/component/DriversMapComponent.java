package vn.ontaxi.component;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.ontaxi.jpa.entity.Driver;
import vn.ontaxi.model.LocationWithDriver;
import vn.ontaxi.service.DriverService;
import reactor.bus.Event;
import reactor.fn.Consumer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class DriversMapComponent implements Consumer<Event<LocationWithDriver>> {
    private Map<String, LocationWithDriver> driversLocation = new HashMap<>();

    private final DriverService driverService;

    @Autowired
    public DriversMapComponent(DriverService driverService) {
        this.driverService = driverService;
    }

    @Override
    public void accept(Event<LocationWithDriver> locationEvent) {
        LocationWithDriver data = locationEvent.getData();
        driversLocation.put(data.getDriverCode(), data);
    }

    public String getLocationJson() {
        Map<String, LocationWithDriver> onlineDriverMap = getOnlineDriversLocation(false);

        return new Gson().toJson(onlineDriverMap);
    }

    public Map<String, LocationWithDriver> getOnlineDriversLocation(boolean fromWebsite) {
        long currentTime = new Date().getTime();
        Map<String, LocationWithDriver> onlineDriverMap = new HashMap<>();
        for (String driverCode : driversLocation.keySet()) {
            LocationWithDriver locationWithDriver = driversLocation.get(driverCode);
            if (currentTime - locationWithDriver.getReceived_time().getTime() <= 60 * 1000 * 10) { // If not update more than 10 minutes. Consider to be offline
                Driver driver = driverService.findByEmail(driverCode);
                if (driver != null) {
                    String driverInfo;
                    if (fromWebsite) {
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
