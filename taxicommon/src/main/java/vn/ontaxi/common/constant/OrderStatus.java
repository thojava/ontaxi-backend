package vn.ontaxi.common.constant;

import java.util.HashMap;
import java.util.Map;

public class OrderStatus {
    public static final String ORDERED = "O";
    public static final String CUSTOEMR_CONFIRM = "CF";
    public static final String NEW = "N";
    public static final String ACCEPTED = "A";
    public static final String COMPLETED = "C";
    public static final String ABORTED = "B";

    private static Map<String, String> statusWithName;
    static {
        statusWithName = new HashMap<>();
        statusWithName.put(ORDERED, "Lịch Mới");
        statusWithName.put(CUSTOEMR_CONFIRM, "Đã xác nhận");
        statusWithName.put(NEW, "Gửi cho lái xe");
        statusWithName.put(ACCEPTED, "Chấp nhận");
        statusWithName.put(COMPLETED, "Hoàn thành");
        statusWithName.put(ABORTED, "Đã hủy");
    }

    public static String getStatusName(String status) {
        return statusWithName.getOrDefault(status, "");
    }
}
