package vn.ontaxi.common.constant;

public enum CarTypes {
    N4("4 Chỗ"), G4("5 Chỗ"), N7("7 Chỗ"), N16("16 Chỗ");

    private String description;

    CarTypes(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getNoOfSeats() {
        switch (this) {
            case N4:
                return 4;
            case G4:
                return 5;
            case N7:
                return 7;
            case N16:
                return 16;
        }

        return -1;
    }
}
