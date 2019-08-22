package vn.ontaxi.hub.component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.ObjectUtils;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vn.ontaxi.common.model.LocationWithDriver;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("view")
@PropertySource("classpath:application.properties")
public class DriversMapComponent implements Serializable {

    @Value("${rest.url}")
    private String restUrl;
    private int numOfActivatedCar;

    @PostConstruct
    public void init() {
    }

    public int getNumOfActivatedCar() {
        return numOfActivatedCar;
    }

    public String getLocationJson() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> entity = restTemplate.getForEntity(restUrl + "/driver/location?showFullDriverInfo=true", String.class);
        Type locationType = new TypeToken<List<LocationWithDriver>>() {}.getType();
        List<LocationWithDriver> onlineDriverMap = ObjectUtils.defaultIfNull(new Gson().fromJson(entity.getBody(), locationType), new ArrayList<>());
        numOfActivatedCar = onlineDriverMap.size();
        return new Gson().toJson(onlineDriverMap);
    }

}
