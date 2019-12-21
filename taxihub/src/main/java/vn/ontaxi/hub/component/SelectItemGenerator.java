package vn.ontaxi.hub.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.constant.CarTypes;
import vn.ontaxi.common.constant.EmailType;
import vn.ontaxi.common.jpa.entity.PersistentCustomer;
import vn.ontaxi.common.jpa.repository.PersistentCustomerRepository;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SelectItemGenerator {
    @Autowired
    private final PersistentCustomerRepository persistentCustomerRepository;

    public SelectItemGenerator(PersistentCustomerRepository persistentCustomerRepository) {
        this.persistentCustomerRepository = persistentCustomerRepository;
    }

    public List<SelectItem> getDebtorSelectItems() {
        List<PersistentCustomer> customers = persistentCustomerRepository.findAll();
        return customers.stream().map(c -> new SelectItem(c.getEmail(), c.getName())).collect(Collectors.toList());
    }

    public List<SelectItem> getCarTypeSelectItems() {
        return Arrays.stream(CarTypes.values()).map(c -> new SelectItem(c, c.getDescription())).collect(Collectors.toList());
    }

    public List<SelectItem> getEmailTypeSelectItems() {
        return Arrays.stream(EmailType.values()).map(c -> new SelectItem(c, c.getDescription())).collect(Collectors.toList());
    }

    public List<SelectItem> getFeePercentageSelectItems() {
        return Stream.of(15, 12, 10, 6, 0).map(i -> new SelectItem(i, i + "%")).collect(Collectors.toList());
    }

    public List<SelectItem> getNoOfGuestSelectItems(int maxAvailableSets) {
        List<SelectItem> items = new ArrayList<>();
        for (int i = 1; i <= maxAvailableSets; i++) {
            items.add(new SelectItem(i, i + " người"));
        }

        return items;
    }
}
