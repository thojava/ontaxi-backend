package vn.ontaxi.hub.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import vn.ontaxi.common.jpa.entity.Address;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.entity.Customer;
import vn.ontaxi.common.jpa.repository.AddressRepository;
import vn.ontaxi.common.jpa.repository.CustomerRepository;
import vn.ontaxi.common.service.DistanceMatrixService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final Environment environment;
    private final DistanceMatrixService distanceMatrixService;
    private static final int MIN_DISTANCE = 100; // 100m

    public CustomerService(CustomerRepository customerRepository, AddressRepository addressRepository, Environment environment, DistanceMatrixService distanceMatrixService) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.environment = environment;
        this.distanceMatrixService = distanceMatrixService;
    }

    public void updateCustomerInfo(Booking booking) {

        List<Customer> customers = customerRepository.findByPhone(booking.getMobile());
        if (CollectionUtils.isEmpty(customers)) {
            Customer customer = new Customer();
            customer.setPhone(booking.getMobile());
            customer.setName(booking.getName());
            Customer savedCustomer = customerRepository.save(customer);

            List<Address> addresses = new ArrayList<>();
            addresses.add(new Address(booking.getFrom_location(), Address.AddressType.OTHER, booking.getActual_start_lat(), booking.getActual_start_lon(), savedCustomer));
            addresses.add(new Address(booking.getFrom_location(), Address.AddressType.OTHER, booking.getActual_end_lat(), booking.getActual_end_lon(), savedCustomer));
            addressRepository.save(addresses);
        } else {
            Customer customer = customers.get(0);
            if (CollectionUtils.isNotEmpty(customer.getAddresses())){
                List<Long> distanceFromLocationToOther = distanceMatrixService.getDistances(booking.getFrom_location(), customer.getAddresses().stream().map(Address::getAddress).toArray(String[]::new));
                if (distanceFromLocationToOther.stream().allMatch(distance -> distance > MIN_DISTANCE))
                    customer.getAddresses().add(new Address(booking.getFrom_location(), Address.AddressType.OTHER, booking.getActual_start_lat(), booking.getActual_start_lon(), customer));
                else {
                    int minIndex = distanceFromLocationToOther.indexOf(Collections.min(distanceFromLocationToOther));
                    customer.getAddresses().get(minIndex).increaseNumOfBooking();
                }

                List<Long> distanceToLocationToOther = distanceMatrixService.getDistances(booking.getTo_location(), customer.getAddresses().stream().map(Address::getAddress).toArray(String[]::new));
                if (distanceToLocationToOther.stream().allMatch(distance -> distance > MIN_DISTANCE))
                    customer.getAddresses().add(new Address(booking.getTo_location(), Address.AddressType.OTHER, booking.getActual_end_lat(), booking.getActual_end_lon(), customer));
                else {
                    int minIndex = distanceToLocationToOther.indexOf(Collections.min(distanceToLocationToOther));
                    customer.getAddresses().get(minIndex).increaseNumOfBooking();
                }

            } else {
                customer.getAddresses().add(new Address(booking.getFrom_location(), Address.AddressType.OTHER, booking.getActual_start_lat(), booking.getActual_start_lon(), customer));
                customer.getAddresses().add(new Address(booking.getTo_location(), Address.AddressType.OTHER, booking.getActual_end_lat(), booking.getActual_end_lon(), customer));
            }

            addressRepository.save(customer.getAddresses());

        }

    }
}
