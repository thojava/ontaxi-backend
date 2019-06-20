package vn.ontaxi.common.constant;

public enum EmailType {
    SYSTEM("Hệ Thống"),
    MARKETING("Marketing"),
    SET_PASSWORD("Khách hàng nhập password cho account mới"),
    ACCOUNT_ACTIVATED_NOTIFICATION("Thông báo cho khách hàng sau khi tài khoản được kích hoạt"),
    RESET_PASSWORD("Khách hàng reset password cho account");

    private String description;
    EmailType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
