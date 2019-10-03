package vn.ontaxi.hub.component.partner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.constant.BookingOrder;
import vn.ontaxi.common.jpa.entity.Partner;
import vn.ontaxi.common.jpa.repository.PartnerRepository;
import vn.ontaxi.hub.component.viewmodel.TaxiLazyDataModel;
import vn.ontaxi.hub.service.LazyDataService;

import javax.el.MethodExpression;

@Component
@Scope("view")
public class PartnerSummaryComponent {
    private static final Logger logger = LoggerFactory.getLogger(PartnerSummaryComponent.class);

    private final LazyDataService lazyDataService;
    private final PartnerRepository partnerRepository;
    private TaxiLazyDataModel<Partner> partners;

    @Autowired
    public PartnerSummaryComponent(LazyDataService lazyDataService, PartnerRepository partnerRepository) {
        this.lazyDataService = lazyDataService;
        this.partnerRepository = partnerRepository;
    }

    public TaxiLazyDataModel<Partner> getPartners() {
        if (partners == null) {
            partners = new TaxiLazyDataModel<>(lazyDataService, partnerRepository, BookingOrder.ID_DESC);
        }

        return partners;
    }

    public String newPartner() {
        Partner newPartner = new Partner();
        newPartner = partnerRepository.save(newPartner);
        return "partner_detail.jsf?faces-redirect=true&id=" + newPartner.getId();
    }
}
