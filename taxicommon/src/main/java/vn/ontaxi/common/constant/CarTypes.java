package vn.ontaxi.common.constant;

public enum CarTypes {
    N4("4 Chỗ", 4), G4("4 Chỗ Vios", 5), N7("7 Chỗ", 7), N16("16 Chỗ", 16);

    private String description;
    private int noOfSeats;

    CarTypes(String description, int noOfSeats) {
        this.description = description;
        this.noOfSeats = noOfSeats;
    }

    @Override
    public String toString() {
        return getDescription();
    }

    public String getDescription() {
        return description;
    }

    public int getNoOfSeats() {
        return noOfSeats;
    }
}
