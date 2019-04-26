package vn.ontaxi.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ConfigurationService {
    private double driver_balance_low_limit;
    private double accuracy_limit;

    @PostConstruct
    public void init() {
        driver_balance_low_limit = 500000;
        accuracy_limit = 30;
    }

    public double getDriver_balance_low_limit() {
        return driver_balance_low_limit;
    }

    public double getAccuracy_limit() {
        return accuracy_limit;
    }
}
