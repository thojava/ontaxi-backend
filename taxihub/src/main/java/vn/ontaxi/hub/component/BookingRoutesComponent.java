package vn.ontaxi.hub.component;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.Booking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BookingRoutesComponent {
    private Booking booking;

    public String getLocationJson() {
        String routes = booking.getRoutes();
        String[] locations = routes.replaceAll("\n", "").split(";");
        List<Map<String, Double>> coordinates = new ArrayList<>();
        for (String location : locations) {
            double lat = Double.parseDouble(location.split(",")[0]);
            double lon = Double.parseDouble(location.split(",")[1]);
            Map<String, Double> cor = new HashMap<>();
            cor.put("lat", lat);
            cor.put("lng", lon);
            coordinates.add(cor);
        }

        return new Gson().toJson(coordinates);
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
