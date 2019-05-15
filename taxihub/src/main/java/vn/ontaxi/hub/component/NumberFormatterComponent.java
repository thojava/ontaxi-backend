package vn.ontaxi.hub.component;

import org.springframework.stereotype.Component;
import vn.ontaxi.common.utils.NumberUtils;

@Component
public class NumberFormatterComponent {
    public String formatDistance(double distance) {
        return NumberUtils.distanceWithKM(distance);
    }

    public String formatPrice(double price) {
        return NumberUtils.formatAmountInVND(price);
    }
}
