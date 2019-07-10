package vn.ontaxi.hub.component;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.*;
import vn.ontaxi.common.jpa.repository.BookingRepository;
import vn.ontaxi.common.jpa.repository.CustomerRepository;
import vn.ontaxi.common.jpa.repository.DriverRepository;
import vn.ontaxi.hub.model.user.LoggedInUser;
import vn.ontaxi.hub.utils.PhoneUtils;

import javax.faces.context.FacesContext;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("view")
public class CallManagementDialogComponent {

    private Customer customerInfo;
    private Driver driverInfo;
    private boolean isFromCustomer = true;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public void resetData(){
        customerInfo = new Customer();
        driverInfo = new Driver();
    }

    public void updateInfo(String phone) {
        resetData();

        Driver driver = driverRepository.findByMobile(PhoneUtils.normalizePhone(phone));
        if (driver != null) {
            this.driverInfo = driver;
            this.isFromCustomer = false;
        } else {
            this.isFromCustomer = true;
            List<Customer> customers = customerRepository.findByPhone(PhoneUtils.normalizePhone(phone));
            if (CollectionUtils.isNotEmpty(customers))
                customerInfo = customers.get(0);
            else {
                customerInfo.setPhone(PhoneUtils.normalizePhone(phone));
            }
        }

    }

    public String createNewOrder() {
        return "new_order.jsf?faces-redirect=true&mobile=" + PhoneUtils.normalizePhone(customerInfo.getPhone());
    }

    public List<Booking> getLatestBooking() {
        if (isFromCustomer && customerInfo != null) {
            List<Booking> bookings = bookingRepository.findByMobile(customerInfo.getPhone(), new Sort(Sort.Direction.DESC, "id"));
            if (CollectionUtils.isNotEmpty(bookings))
                return bookings.subList(0, Math.min(2, bookings.size()));
        } else if (!isFromCustomer && driverInfo != null) {
            List<Booking> bookings = bookingRepository.findByAcceptedByDriver_Email(driverInfo.getEmail(), new Sort(Sort.Direction.DESC, "id"));
            if (CollectionUtils.isNotEmpty(bookings))
                return bookings.subList(0, Math.min(2, bookings.size()));
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

    public void updateInfoFromUI() {
        String phone = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("phone");
        updateInfo(phone);
    }

    public Customer getCustomerInfo() {
        return customerInfo;
    }

    public Driver getDriverInfo() {
        return driverInfo;
    }

    public String getAccessToken() {
        LoggedInUser user = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication(). getPrincipal();

        if (user.getUser().isHelpDesk())
            return user.getUser().getStringeeAccessToken();

        return "";
    }

    public boolean isFromCustomer() {
        return isFromCustomer;
    }

    public void setFromCustomer(boolean fromCustomer) {
        isFromCustomer = fromCustomer;
    }
}
