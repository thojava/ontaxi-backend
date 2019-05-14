package vn.ontaxi.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.ontaxi.constant.CarTypes;
import vn.ontaxi.jpa.entity.PersistentCustomer;
import vn.ontaxi.jpa.repository.PersistentCustomerRepository;

import javax.faces.model.SelectItem;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
}
