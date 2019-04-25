package vn.ontaxi.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ontaxi.component.DriversMapComponent;

@RestController
@RequestMapping("rest")
public class RestDriverController {
    private final DriversMapComponent driversMapComponent;

    @Autowired
    public RestDriverController(DriversMapComponent driversMapComponent) {
        this.driversMapComponent = driversMapComponent;
    }

    @CrossOrigin
    @RequestMapping(path = "/driverLocation")
    public String getLocationJson() {
        return new Gson().toJson(driversMapComponent.getOnlineDriversLocation(true).values());
    }
}
