package vn.ontaxi.hub.component;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.constant.BookingOrder;
import vn.ontaxi.common.jpa.entity.Customer;
import vn.ontaxi.common.jpa.entity.CustomerAccount;
import vn.ontaxi.common.jpa.repository.CustomerAccountRepository;
import vn.ontaxi.common.jpa.repository.CustomerRepository;
import vn.ontaxi.common.service.EmailService;
import vn.ontaxi.hub.component.viewmodel.TaxiLazyDataModel;
import vn.ontaxi.hub.service.LazyDataService;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.UUID;

@Component
public class CustomersComponent {
    private final CustomerRepository customerRepository;
    private final CustomerAccountRepository customerAccountRepository;
    private final LazyDataService lazyDataService;
    private TaxiLazyDataModel<Customer> lstCustomers;

    @Autowired
    public CustomersComponent(CustomerRepository customerRepository, CustomerAccountRepository customerAccountRepository, EmailService emailService, LazyDataService lazyDataService) {
        this.customerRepository = customerRepository;
        this.customerAccountRepository = customerAccountRepository;
        this.lazyDataService = lazyDataService;
    }

    public TaxiLazyDataModel<Customer> getCustomers() {
        if (lstCustomers == null) {
            lstCustomers = new TaxiLazyDataModel<>(lazyDataService, customerRepository, BookingOrder.ID_DESC);
        }

        return lstCustomers;
    }

    public void saveCustomerAsTested(Customer customer) {
        customerRepository.save(customer);
    }

    public void createAccountForCustomer(Customer customer) {
        if (StringUtils.isEmpty(customer.getEmail())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Khách hàng chưa có email"));
            return;
        }
        CustomerAccount customerAccount = customerAccountRepository.findByCustomerEmail(customer.getEmail());
        if (customerAccount != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Khách hàng đã có tài khoản trước đó"));
            return;
        }

        CustomerAccount temp = new CustomerAccount();
        temp.setActived(false);
        temp.setCustomer(customer);
        temp.setToken(UUID.randomUUID().toString());

        customerAccountRepository.save(temp);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Thành công! Vui lòng yêu cầu khách hàng truy cập email để hoàn tất cài đặt"));
    }

    public void deleteCustomers() {
        List<Customer> wrappedData = lstCustomers.getWrappedData();
        for (Customer customer : wrappedData) {
            if (customer.isBeanSelected())
                customerAccountRepository.delete(customer.getCustomerAccount());
                customerRepository.delete(customer);
        }

        lstCustomers = null;
    }

    public String newCustomer() {
        Customer newCustomer = new Customer();
        newCustomer = customerRepository.save(newCustomer);
        return "customer_detail.jsf?faces-redirect=true&id=" + newCustomer.getId();
    }
}
