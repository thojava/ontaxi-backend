package vn.ontaxi.hub.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.constant.BookingOrder;
import vn.ontaxi.common.jpa.entity.Customer;
import vn.ontaxi.common.jpa.repository.CustomerRepository;
import vn.ontaxi.hub.component.viewmodel.TaxiLazyDataModel;
import vn.ontaxi.hub.service.LazyDataService;

@Component
public class CustomersComponent {
    private final CustomerRepository customerRepository;
    private final LazyDataService lazyDataService;
    private TaxiLazyDataModel<Customer> lstCustomers;

    @Autowired
    public CustomersComponent(CustomerRepository customerRepository, LazyDataService lazyDataService) {
        this.customerRepository = customerRepository;
        this.lazyDataService = lazyDataService;
    }

    public TaxiLazyDataModel<Customer> getCustomers() {
        if (lstCustomers == null) {
            lstCustomers = new TaxiLazyDataModel<>(lazyDataService, customerRepository, BookingOrder.ID_DESC);
        }

        return lstCustomers;
    }

}
