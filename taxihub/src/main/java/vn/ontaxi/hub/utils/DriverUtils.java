package vn.ontaxi.hub.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import vn.ontaxi.common.model.LocationWithDriver;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DriverUtils {
    public static List<LocationWithDriver> getLocationJson(String taxiApiKey, String restUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + taxiApiKey);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> entity = restTemplate.exchange(restUrl + "/driver/location?showFullDriverInfo=true", HttpMethod.GET, new HttpEntity<>(headers), String.class);
        Type locationType = new TypeToken<List<LocationWithDriver>>() {}.getType();
        return ObjectUtils.defaultIfNull(new Gson().fromJson(entity.getBody(), locationType), new ArrayList<>());
    }
}
