package vn.ontaxi.component;

import org.springframework.stereotype.Component;
import vn.ontaxi.utils.NumberUtils;

@Component
public class NumberFormatterComponent {
    public String formatDistance(double distance) {
        return NumberUtils.distanceWithKM(distance);
    }

    public String formatPrice(double price) {
        return NumberUtils.formatAmountInVND(price);
    }
}
