
package vn.ontaxi.json.model.google.sp;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class SnappedPoint {

    @SerializedName("location")
    private Location mLocation;
    @SerializedName("originalIndex")
    private Long mOriginalIndex;
    @SerializedName("placeId")
    private String mPlaceId;

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public Long getOriginalIndex() {
        return mOriginalIndex;
    }

    public void setOriginalIndex(Long originalIndex) {
        mOriginalIndex = originalIndex;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public void setPlaceId(String placeId) {
        mPlaceId = placeId;
    }

}
