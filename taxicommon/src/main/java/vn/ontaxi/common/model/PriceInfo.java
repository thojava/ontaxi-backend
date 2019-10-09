package vn.ontaxi.common.model;

public class PriceInfo {
    public double outwardPrice;
    public double returnPrice;
    public double waitPrice;
    public double terrainPrice;
    double transportFee;
    double promotionPercentage;


    public double getTotal_price_before_promotion() {
        double priceWithoutTransportFee = outwardPrice + returnPrice + waitPrice;
        return priceWithoutTransportFee + transportFee;
    }

    public double getTotal_price() {
        double priceWithoutTransportFee = outwardPrice + returnPrice + waitPrice + terrainPrice;
        return priceWithoutTransportFee * (1 - promotionPercentage / 100) + transportFee;
    }

    public PriceInfo(double outwardPrice, double returnPrice, double waitPrice, double terrainPrice, double transportFee, double promotionPercentage) {
        this.outwardPrice = outwardPrice;
        this.returnPrice = returnPrice;
        this.waitPrice = waitPrice;
        this.terrainPrice = terrainPrice;
        this.transportFee = transportFee;
        this.promotionPercentage = promotionPercentage;
    }

    @Override
    public String toString() {
        return "Outward " + outwardPrice + " return " + returnPrice + " wait " + waitPrice + " total " + (outwardPrice + returnPrice + waitPrice);
    }
}
