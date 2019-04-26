
package vn.ontaxi.model.direction;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.util.List;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class GeocodedWaypoint {

    @SerializedName("geocoder_status")
    private String mGeocoderStatus;
    @SerializedName("place_id")
    private String mPlaceId;
    @SerializedName("types")
    private List<String> mTypes;

    public String getGeocoderStatus() {
        return mGeocoderStatus;
    }

    public void setGeocoderStatus(String geocoderStatus) {
        mGeocoderStatus = geocoderStatus;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public void setPlaceId(String placeId) {
        mPlaceId = placeId;
    }

    public List<String> getTypes() {
        return mTypes;
    }

    public void setTypes(List<String> types) {
        mTypes = types;
    }

}
