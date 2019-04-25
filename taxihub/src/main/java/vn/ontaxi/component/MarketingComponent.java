package vn.ontaxi.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.ontaxi.jpa.entity.ViewPrice;
import vn.ontaxi.jpa.repository.ViewPriceRepository;
import vn.ontaxi.utils.DateUtils;

import java.util.Date;
import java.util.List;

@Component
public class MarketingComponent {
    private final ViewPriceRepository viewPriceRepository;

    @Autowired
    public MarketingComponent(ViewPriceRepository viewPriceRepository) {
        this.viewPriceRepository = viewPriceRepository;
    }

    public List<ViewPrice> getTodayViewPrices() {
        Date today = DateUtils.today();
        return viewPriceRepository.findByCreatedDatetimeBetweenOrderByCreatedDatetimeDesc(DateUtils.getStartOfDay(today), DateUtils.getEndOfDay(today));
    }
}
