
package vn.ontaxi.common.model.direction;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.util.List;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class GoogleDirections {

    @SerializedName("geocoded_waypoints")
    private List<GeocodedWaypoint> mGeocodedWaypoints;
    @SerializedName("routes")
    private List<Route> mRoutes;
    @SerializedName("status")
    private String mStatus;

    public List<GeocodedWaypoint> getGeocodedWaypoints() {
        return mGeocodedWaypoints;
    }

    public void setGeocodedWaypoints(List<GeocodedWaypoint> geocodedWaypoints) {
        mGeocodedWaypoints = geocodedWaypoints;
    }

    public List<Route> getRoutes() {
        return mRoutes;
    }

    public void setRoutes(List<Route> routes) {
        mRoutes = routes;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

}
