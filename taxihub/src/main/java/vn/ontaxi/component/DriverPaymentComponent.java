package vn.ontaxi.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.ontaxi.constant.PaymentTypes;
import vn.ontaxi.controller.RestBookingController;
import vn.ontaxi.jpa.entity.Driver;
import vn.ontaxi.jpa.entity.DriverPayment;
import vn.ontaxi.jpa.repository.DriverPaymentRepository;
import vn.ontaxi.jpa.repository.DriverRepository;

@Component
@Scope("view")
public class DriverPaymentComponent {
    private static final Logger logger = LoggerFactory.getLogger(RestBookingController.class);
    private DriverPayment payment;
    private final DriverRepository driverRepository;
    private final DriverPaymentRepository driverPaymentRepository;
    private String reason_for_decrease;

    @Autowired
    public DriverPaymentComponent(DriverRepository driverRepository, DriverPaymentRepository driverPaymentRepository) {
        this.driverRepository = driverRepository;
        this.driverPaymentRepository = driverPaymentRepository;
        payment = new DriverPayment();
        payment.setPaymentType(PaymentTypes.RECHARGE);
    }


    @Transactional
    public String doPayment() {
        Driver driver = payment.getDriver();
        driver = driverRepository.findByEmail(driver.getEmail());
        logger.debug("Before charge " + driver.getEmail() + " " + driver.getAmount());
        if (payment.getPaymentType().equalsIgnoreCase(PaymentTypes.RECHARGE)) {
            driver.increaseAmt(payment.getAmount(), logger);
        } else {
            driver.decreaseAmt(payment.getAmount(), logger);
        }
        logger.debug("After charge " + driver.getEmail() + " " + driver.getAmount());
        driverRepository.saveAndFlush(driver);

        driverPaymentRepository.saveAndFlush(payment);

        return "driver.jsf?faces-redirect=true";
    }

    public void setPayment(DriverPayment payment) {
        this.payment = payment;
    }

    public DriverPayment getPayment() {
        return payment;
    }

    public void setReason_for_decrease(String reason_for_decrease) {
        this.reason_for_decrease = reason_for_decrease;
    }

    public String getReason_for_decrease() {
        return reason_for_decrease;
    }
}
