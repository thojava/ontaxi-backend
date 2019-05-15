package vn.ontaxi.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.jpa.repository.DriverRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@Scope("singleton")
public class DriverService {
    private final DriverRepository driverRepository;
    private Map<String, Driver> cachedDriverInfo = new HashMap<>();

    @Autowired
    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public Driver findByEmail(String email) {
        if (!cachedDriverInfo.containsKey(email)) {
            cachedDriverInfo.put(email, driverRepository.findByEmail(email));
        }

        return cachedDriverInfo.get(email);
    }
}
