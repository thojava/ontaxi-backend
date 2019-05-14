package vn.ontaxi.component.abstracts;

import org.springframework.context.MessageSource;
import vn.ontaxi.constant.SendToGroupOptions;
import vn.ontaxi.jpa.entity.Driver;
import vn.ontaxi.jpa.repository.PersistentCustomerRepository;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractOrderComponent extends AbstractComponent {
    protected String sendToGroupOption;
    private final PersistentCustomerRepository persistentCustomerRepository;

    public AbstractOrderComponent(MessageSource messageSource, PersistentCustomerRepository persistentCustomerRepository) {
        super(messageSource);
        this.persistentCustomerRepository = persistentCustomerRepository;
    }

    public void validateSelectDriver(FacesContext context, UIComponent component, Object value) {
        @SuppressWarnings("unchecked")
        List<Driver> selectedValues = (List<Driver>) value;
        if (SendToGroupOptions.INDIVIDUAL.equalsIgnoreCase(sendToGroupOption) && selectedValues.isEmpty()) {
            throw new ValidatorException(new FacesMessage(messageSource.getMessage("error.post_order.must_select_one_driver", null, Locale.getDefault())));
        }
    }

    public List<SelectItem> getFeePercentageSelectItems() {
        return Stream.of(12, 10, 15, 6, 0).map(i -> new SelectItem(i, i + "%")).collect(Collectors.toList());
    }

    public List<SelectItem> getPromotionPercentageSelectItems() {
        return Stream.of(10, 6, 4, 0).map(i -> new SelectItem(i, i + "%")).collect(Collectors.toList());
    }

    public List<SelectItem> getSendToDriverGroupsOptions() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem(SendToGroupOptions.ALL, "Tất Cả"));
        items.add(new SelectItem(SendToGroupOptions.LONG_HAUL, "Đường Dài"));
        items.add(new SelectItem(SendToGroupOptions.PRIORITY, "Lái Xe Ưu Tiên"));
        items.add(new SelectItem(SendToGroupOptions.INDIVIDUAL, "Chọn Riêng"));
        return items;
    }

    public String getSendToGroupOption() {
        return sendToGroupOption;
    }

    public void setSendToGroupOption(String sendToGroupOption) {
        this.sendToGroupOption = sendToGroupOption;
    }

    public boolean isSendToIndividual() {
        return SendToGroupOptions.INDIVIDUAL.equalsIgnoreCase(this.sendToGroupOption);
    }
}
