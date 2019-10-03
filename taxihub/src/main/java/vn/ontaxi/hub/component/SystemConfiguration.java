package vn.ontaxi.hub.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class SystemConfiguration {
    @Value("${google_map_js_api_key}")
    private String googleMapJSApiKey;

    public String getGoogleMapJSApiKey() {
        return googleMapJSApiKey;
    }
}
