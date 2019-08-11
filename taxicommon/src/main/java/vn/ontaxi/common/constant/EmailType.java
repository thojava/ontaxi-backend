package vn.ontaxi.common.constant;

public enum EmailType {
    MARKETING("Marketing"),
    SET_PASSWORD("Khách hàng nhập password cho account mới"),
    ACCOUNT_ACTIVATED_NOTIFICATION("Thông báo cho khách hàng sau khi tài khoản được kích hoạt"),
    RESET_PASSWORD("Khách hàng reset password cho account"),
    ACTIVE_ACCOUNT("Khách hàng kích hoạt tài khoản"),
    BOOKING_SUCCESS_CONFIRMATION("Thông báo đặt xe thành công");

    private String description;
    EmailType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
