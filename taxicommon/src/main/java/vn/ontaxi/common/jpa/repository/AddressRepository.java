package vn.ontaxi.common.jpa.repository;

import org.springframework.data.repository.CrudRepository;
import vn.ontaxi.common.jpa.entity.Address;
import vn.ontaxi.common.jpa.entity.Customer;

import java.util.List;

public interface AddressRepository extends CrudRepository<Address, Long> {

    List<Address> findByCustomer(Customer customer);

}
