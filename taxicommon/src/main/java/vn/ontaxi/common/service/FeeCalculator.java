package vn.ontaxi.common.service;

import org.springframework.stereotype.Service;
import vn.ontaxi.common.jpa.entity.FeeConfiguration;
import vn.ontaxi.common.jpa.repository.FeeConfigurationRepository;

import java.util.List;

@Service
public class FeeCalculator {
    private final FeeConfigurationRepository feeConfigurationRepository;

    public FeeCalculator(FeeConfigurationRepository feeConfigurationRepository) {
        this.feeConfigurationRepository = feeConfigurationRepository;
    }

    public double getDefaultFeePercentage() {
        List<FeeConfiguration> feeConfigurations = this.feeConfigurationRepository.findAll();
        if(!feeConfigurations.isEmpty()) {
            return feeConfigurations.get(0).getDefaultFeePercentage();
        }
        throw new IllegalArgumentException("Must setup default fee configuration");
    }
}
