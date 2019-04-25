package vn.ontaxi.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PromotionPlan extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private double from_distance;
    private double to_distance;
    private double monday_percentage;
    private double tuesday_percentage;
    private double wednesday_percentage;
    private double thursday_percentage;
    private double friday_percentage;
    private double saturday_percentage;
    private double sunday_percentage;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getFrom_distance() {
        return from_distance;
    }

    public void setFrom_distance(double from_distance) {
        this.from_distance = from_distance;
    }

    public double getTo_distance() {
        return to_distance;
    }

    public void setTo_distance(double to_distance) {
        this.to_distance = to_distance;
    }

    public double getMonday_percentage() {
        return monday_percentage;
    }

    public void setMonday_percentage(double monday_percentage) {
        this.monday_percentage = monday_percentage;
    }

    public double getTuesday_percentage() {
        return tuesday_percentage;
    }

    public void setTuesday_percentage(double tuesday_percentage) {
        this.tuesday_percentage = tuesday_percentage;
    }

    public double getWednesday_percentage() {
        return wednesday_percentage;
    }

    public void setWednesday_percentage(double wednesday_percentage) {
        this.wednesday_percentage = wednesday_percentage;
    }

    public double getThursday_percentage() {
        return thursday_percentage;
    }

    public void setThursday_percentage(double thursday_percentage) {
        this.thursday_percentage = thursday_percentage;
    }

    public double getFriday_percentage() {
        return friday_percentage;
    }

    public void setFriday_percentage(double friday_percentage) {
        this.friday_percentage = friday_percentage;
    }

    public double getSaturday_percentage() {
        return saturday_percentage;
    }

    public void setSaturday_percentage(double saturday_percentage) {
        this.saturday_percentage = saturday_percentage;
    }

    public double getSunday_percentage() {
        return sunday_percentage;
    }

    public void setSunday_percentage(double sunday_percentage) {
        this.sunday_percentage = sunday_percentage;
    }

    @Override
    @JsonIgnore
    public String getKey() {
        return String.valueOf(getId());
    }
}
