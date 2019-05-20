package vn.ontaxi.hub.component;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.model.LocationWithDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DriversMapComponent {
    public String getLocationJson() {
        Map<String, LocationWithDriver> onlineDriverMap = new HashMap<>();

        return new Gson().toJson(onlineDriverMap);
    }

    public List<Driver> getOnlineDriversLocation(boolean bol) {
        return new ArrayList<>();
    }


}
