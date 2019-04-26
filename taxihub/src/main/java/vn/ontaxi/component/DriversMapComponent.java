package vn.ontaxi.component;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.ontaxi.model.LocationWithDriver;
import vn.ontaxi.service.DriverService;

import java.util.HashMap;
import java.util.Map;

@Component
public class DriversMapComponent {
    public String getLocationJson() {
        Map<String, LocationWithDriver> onlineDriverMap = new HashMap<>();

        return new Gson().toJson(onlineDriverMap);
    }


}
