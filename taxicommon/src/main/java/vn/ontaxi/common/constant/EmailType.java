package vn.ontaxi.common.constant;

public enum EmailType {
    MARKETING("Marketing"),

    REQUEST_RESET_PASSWORD("Khách hàng reset password cho account"),
    RESET_PASSWORD_SUCCESSFUL("Khách hàng reset password thành công"),

    ACTIVE_ACCOUNT("Khách hàng kích hoạt tài khoản"),
    ACCOUNT_ACTIVATED_SUCCESSFUL("khách hàng kích hoạt tài khoản thành công"),

    BOOKING_SUCCESS_CONFIRMATION("Thông báo đặt xe thành công");

    private String description;
    EmailType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
