
package vn.ontaxi.json.model.google.dm;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Distance {

    @SerializedName("text")
    private String mText;
    @SerializedName("value")
    private Long mValue;

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public Long getValue() {
        return mValue;
    }

    public void setValue(Long value) {
        mValue = value;
    }

}
