package vn.ontaxi.hub.component;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.constant.BookingOrder;
import vn.ontaxi.common.jpa.entity.Address;
import vn.ontaxi.common.jpa.entity.Behavior;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.entity.Customer;
import vn.ontaxi.common.jpa.repository.BookingRepository;
import vn.ontaxi.common.jpa.repository.CustomerRepository;
import vn.ontaxi.hub.component.viewmodel.TaxiLazyDataModel;
import vn.ontaxi.hub.model.user.LoggedInUser;
import vn.ontaxi.hub.utils.DateUtils;
import vn.ontaxi.hub.utils.PhoneUtils;

import javax.faces.context.FacesContext;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CallManagementDialogComponent {

    private Customer customerInfo;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BookingRepository bookingRepository;

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

    public String createNewOrder() {
        return "new_order.jsf?faces-redirect=true&mobile=" + PhoneUtils.normalizePhone(customerInfo.getPhone());
    }

    public List<Booking> getLatestBooking() {
        if (customerInfo != null) {
            List<Booking> byMobile = bookingRepository.findByMobile(customerInfo.getPhone(), new Sort(Sort.Direction.DESC, "id"));
            if (CollectionUtils.isNotEmpty(byMobile))
                return byMobile.subList(0, Math.min(2, byMobile.size()));
        }

        return new ArrayList<>();
    }

    public List<String> getAddresses() {
        if (customerInfo != null) {
            Collections.sort(customerInfo.getAddresses(), (s1, s2) -> s2.getNumOfBooking() - s1.getNumOfBooking());
            List<String> addresses = customerInfo.getAddresses().stream().map(Address::getAddress).collect(Collectors.toList());
            return addresses.subList(0, Math.min(2, addresses.size()));
        }
        return new ArrayList<>();
    }

    public String getBehaviours() {
        if (customerInfo != null) {
            Set<Behavior> behaviors = customerInfo.getBehaviors();
            return StringUtils.join(behaviors.stream().map(Behavior::getName).collect(Collectors.toList()), ", ");
        }

        return "";
    }

    public void updateCustomerInfoFromUI() {
        String phone = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("phone");
        updateCustomerInfo(phone);
    }

    public Customer getCustomerInfo() {
        return customerInfo;
    }

    public String getAccessToken() {
        LoggedInUser user = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication(). getPrincipal();

        if (user.getUser().isHelpDesk())
            return user.getUser().getStringeeAccessToken();

        return "";

    }
}
