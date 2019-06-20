package vn.ontaxi.hub.utils;

public class PhoneUtils {

    public static String normalizePhone(String phone) {
        return phone.replaceFirst("^84", "0");
    }

}
