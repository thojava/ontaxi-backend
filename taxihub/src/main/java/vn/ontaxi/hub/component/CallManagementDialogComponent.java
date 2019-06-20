package vn.ontaxi.hub.component;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.Customer;
import vn.ontaxi.common.jpa.repository.CustomerRepository;
import vn.ontaxi.hub.utils.PhoneUtils;

import javax.faces.context.FacesContext;
import java.util.List;

@Component
public class CallManagementDialogComponent {

    private Customer customerInfo;

    @Autowired
    private CustomerRepository customerRepository;

    public void resetCustomerInfo(){
        customerInfo = new Customer();
    }

    public void updateCustomerInfo(String phone) {
        resetCustomerInfo();
        List<Customer> customers = customerRepository.findByPhone(PhoneUtils.normalizePhone(phone));
        if (CollectionUtils.isNotEmpty(customers))
            customerInfo = customers.get(0);
        else {
            customerInfo.setPhone(PhoneUtils.normalizePhone(phone));
        }
    }

    public void updateCustomerInfoFromUI() {
        String phone = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("phone");
        updateCustomerInfo(phone);
    }

    public Customer getCustomerInfo() {
        return customerInfo;
    }

}
