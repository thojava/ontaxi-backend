
package vn.ontaxi.common.json.model.google.sp;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.util.List;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class SnappedPoints {

    @SerializedName("snappedPoints")
    private List<SnappedPoint> mSnappedPoints;
    @SerializedName("warningMessage")
    private String mWarningMessage;

    public List<SnappedPoint> getSnappedPoints() {
        return mSnappedPoints;
    }

    public void setSnappedPoints(List<SnappedPoint> snappedPoints) {
        mSnappedPoints = snappedPoints;
    }

    public String getWarningMessage() {
        return mWarningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        mWarningMessage = warningMessage;
    }

}
