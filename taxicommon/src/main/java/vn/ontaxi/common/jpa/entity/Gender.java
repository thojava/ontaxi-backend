package vn.ontaxi.common.jpa.entity;

public enum Gender {
    FEMALE("Nữ"),
    MALE("Name");

    private String gender;
    Gender(String gender) {
        this.gender = gender;
    }

    public String getString() {
        return gender;
    }
}
