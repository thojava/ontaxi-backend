package vn.ontaxi.service;

import vn.ontaxi.component.UserCredentialComponent;
import vn.ontaxi.jpa.entity.Booking;
import vn.ontaxi.jpa.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BookingService {
    private final UserCredentialComponent userCredentialComponent;
    private final BookingRepository bookingRepository;

    @Autowired
    public BookingService(UserCredentialComponent userCredentialComponent, BookingRepository bookingRepository) {
        this.userCredentialComponent = userCredentialComponent;
        this.bookingRepository = bookingRepository;
    }

    public double getTotalCompletedKM() {
        if (userCredentialComponent.hasViewAllPermission()) {
            return bookingRepository.getTotalCompletedKM();
        } else {
            return bookingRepository.getTotalCompletedKM(userCredentialComponent.getUserName());
        }
    }

    public double getTotalCompletedPrice() {
        if (userCredentialComponent.hasViewAllPermission()) {
            return bookingRepository.getTotalCompletedPrice();
        } else {
            return bookingRepository.getTotalCompletedPrice(userCredentialComponent.getUserName());
        }
    }

    public double getTotalCompletedFee() {
        if (userCredentialComponent.hasViewAllPermission()) {
            return bookingRepository.getTotalCompletedFee();
        } else {
            return bookingRepository.getTotalCompletedFee(userCredentialComponent.getUserName());
        }
    }

    public List<Booking> getBookingByStatusAndArrivalTimeBetweenOrderByArrivalTimeAsc(String status, Date startDate, Date endDate) {
        if (userCredentialComponent.hasViewAllPermission()) {
            return bookingRepository.findByStatusAndArrivalTimeBetweenOrderByDepartureTimeAsc(status, startDate, endDate);
        } else {
            return bookingRepository.findByStatusAndCreatedByAndArrivalTimeBetweenOrderByDepartureTimeAsc(status,
                    userCredentialComponent.getUserName(), startDate, endDate);
        }
    }
}
