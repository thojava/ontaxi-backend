package vn.ontaxi.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.ontaxi.jpa.entity.Customer;
import vn.ontaxi.jpa.repository.CustomerRepository;

@Component
public class CustomersComponent {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomersComponent(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Iterable<Customer> getCustomers() {
        return customerRepository.findAll();
    }
}
