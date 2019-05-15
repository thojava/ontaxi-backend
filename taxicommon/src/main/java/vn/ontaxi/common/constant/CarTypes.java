package vn.ontaxi.common.constant;

public enum CarTypes {
    N4("4 Chỗ"), G4("4 Chỗ Vios"), N7( "7 Chỗ");

    private String description;
    CarTypes(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
