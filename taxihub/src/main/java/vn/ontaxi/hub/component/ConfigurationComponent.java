package vn.ontaxi.hub.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import vn.ontaxi.common.jpa.entity.PriceConfiguration;
import vn.ontaxi.common.jpa.entity.PromotionPlan;
import vn.ontaxi.common.jpa.repository.PriceConfigurationRepository;
import vn.ontaxi.common.jpa.repository.PromotionPlanRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class ConfigurationComponent {
    private final PromotionPlanRepository promotionPlanRepository;
    private final PriceConfigurationRepository priceConfigurationRepository;
    private double promotionPercentage;
    private PriceConfiguration priceConfiguration;

    @Autowired
    public ConfigurationComponent(PromotionPlanRepository promotionPlanRepository, PriceConfigurationRepository priceConfigurationRepository) {
        this.promotionPlanRepository = promotionPlanRepository;
        this.priceConfigurationRepository = priceConfigurationRepository;
    }

    @PostConstruct
    public void initConfig() {
        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        if (dayOfWeek <= 5) {
            promotionPercentage = 4;
        }
        this.priceConfiguration = priceConfigurationRepository.findAll().get(0);
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

    public double getPromotionPercentage() {
        return promotionPercentage;
    }

    public void setPromotionPercentage(double promotionPercentage) {
        this.promotionPercentage = promotionPercentage;
    }

    public PriceConfiguration getPriceConfiguration() {
        return priceConfiguration;
    }
}
