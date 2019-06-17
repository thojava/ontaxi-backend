package vn.ontaxi.common.constant;

public enum EmailType {
    SYSTEM("Hệ Thống"),
    MARKETING("Marketing"),
    SET_PASSWORD("Khách hàng nhập password cho account mới"),
    RESET_PASSWORD("Khách hàng reset password cho account");

    private String description;
    EmailType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
