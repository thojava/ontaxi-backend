package vn.ontaxi.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class NumberUtils {
    private static final NumberFormat numberFormat;

    static {
        Locale locale = new Locale("vi", "VN");
        Currency currency = Currency.getInstance("VND");

        DecimalFormatSymbols df = DecimalFormatSymbols.getInstance(locale);
        df.setCurrency(currency);
        numberFormat = NumberFormat.getCurrencyInstance(locale);
        numberFormat.setCurrency(currency);
    }

    public static String formatAmountInVND(double amt) {
        return numberFormat.format(amt);
    }

    public static String distanceWithKM(double distance) {
        return roundDistance(distance) + " KM";
    }

    public static double roundDistance(double value) {
        BigDecimal bd = new BigDecimal(value).setScale(1, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }

    public static double roundHour(double value) {
        BigDecimal bd = new BigDecimal(value).setScale(1, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }
}
