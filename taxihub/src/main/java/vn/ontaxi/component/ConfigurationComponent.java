package vn.ontaxi.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import vn.ontaxi.jpa.entity.PriceConfiguration;
import vn.ontaxi.jpa.entity.PromotionPlan;
import vn.ontaxi.jpa.repository.PriceConfigurationRepository;
import vn.ontaxi.jpa.repository.PromotionPlanRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class ConfigurationComponent {
    private final PromotionPlanRepository promotionPlanRepository;
    private final PriceConfigurationRepository priceConfigurationRepository;
    private double driver_balance_low_limit;
    private double accuracy_limit;
    private double promotionPercentage;
    private PriceConfiguration priceConfiguration;

    @Autowired
    public ConfigurationComponent(PromotionPlanRepository promotionPlanRepository, PriceConfigurationRepository priceConfigurationRepository) {
        this.promotionPlanRepository = promotionPlanRepository;
        this.priceConfigurationRepository = priceConfigurationRepository;
    }

    @PostConstruct
    public void initConfig() {
        accuracy_limit = 30;
        driver_balance_low_limit = 500000;
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

    public double getAccuracy_limit() {
        return accuracy_limit;
    }

    public void setAccuracy_limit(double accuracy_limit) {
        this.accuracy_limit = accuracy_limit;
    }

    public double getPromotionPercentage() {
        return promotionPercentage;
    }

    public void setPromotionPercentage(double promotionPercentage) {
        this.promotionPercentage = promotionPercentage;
    }

    public double getDriver_balance_low_limit() {
        return driver_balance_low_limit;
    }

    public PriceConfiguration getPriceConfiguration() {
        return priceConfiguration;
    }
}
