package vn.ontaxi.utils;

import vn.ontaxi.jpa.entity.PersistentCustomer;
import vn.ontaxi.jpa.repository.PersistentCustomerRepository;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectItemGenerator {
    public static List<SelectItem> getDebtorSelectItems(PersistentCustomerRepository persistentCustomerRepository) {
        List<PersistentCustomer> customers = persistentCustomerRepository.findAll();
        return customers.stream().map(c -> new SelectItem(c.getEmail(), c.getName())).collect(Collectors.toList());
    }
}
