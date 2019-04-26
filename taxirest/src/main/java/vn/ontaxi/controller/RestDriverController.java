package vn.ontaxi.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ontaxi.service.LocationWithDriverService;

@RestController
@RequestMapping("/driver")
public class RestDriverController {
    private final LocationWithDriverService driversMapComponent;

    @Autowired
    public RestDriverController(LocationWithDriverService driversMapComponent) {
        this.driversMapComponent = driversMapComponent;
    }

    @CrossOrigin
    @RequestMapping(path = "/location")
    public String getLocationJson() {
        return new Gson().toJson(driversMapComponent.getOnlineDriversLocation(true).values());
    }
}
