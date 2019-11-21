package vn.ontaxi.hub.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import vn.ontaxi.common.jpa.entity.FeeConfiguration;
import vn.ontaxi.common.jpa.entity.PriceConfiguration;
import vn.ontaxi.common.jpa.entity.PromotionPlan;
import vn.ontaxi.common.jpa.repository.FeeConfigurationRepository;
import vn.ontaxi.common.jpa.repository.PriceConfigurationRepository;
import vn.ontaxi.common.jpa.repository.PromotionPlanRepository;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class ConfigurationComponent {
    private final PromotionPlanRepository promotionPlanRepository;
    private final PriceConfigurationRepository priceConfigurationRepository;
    private final FeeConfigurationRepository feeConfigurationRepository;
    private PriceConfiguration priceConfiguration;
    private FeeConfiguration feeConfiguration;

    @Autowired
    public ConfigurationComponent(PromotionPlanRepository promotionPlanRepository, PriceConfigurationRepository priceConfigurationRepository, FeeConfigurationRepository feeConfigurationRepository) {
        this.promotionPlanRepository = promotionPlanRepository;
        this.priceConfigurationRepository = priceConfigurationRepository;
        this.feeConfigurationRepository = feeConfigurationRepository;
    }

    @PostConstruct
    public void initConfig() {
        this.priceConfiguration = priceConfigurationRepository.findAll().get(0);
        this.feeConfiguration = feeConfigurationRepository.findAll().get(0);
    }

    public List<PromotionPlan> getPromotionPlans() {
        return promotionPlanRepository.findAll();
    }

    public void savePromotion(PromotionPlan promotionPlan) {
        promotionPlanRepository.saveAndFlush(promotionPlan);
    }

    public void savePriceConfiguration() {
        priceConfigurationRepository.saveAndFlush(priceConfiguration);
    }

    public void saveFeeConfiguration() { feeConfigurationRepository.saveAndFlush(feeConfiguration); }

    public PriceConfiguration getPriceConfiguration() {
        return priceConfiguration;
    }

    public FeeConfiguration getFeeConfiguration() { return feeConfiguration; }
}
