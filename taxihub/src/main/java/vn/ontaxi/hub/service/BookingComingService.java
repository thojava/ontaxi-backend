package vn.ontaxi.hub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.repository.BookingRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class BookingComingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Scheduled(fixedDelay = 2 * 60 * 1000)
    public void scanBookingIsComing(){
        Date now = new Date();
        Calendar in30m = Calendar.getInstance();
        in30m.add(Calendar.MINUTE, 30);
        List<Booking> lstBookings = bookingRepository.findByAcceptedByDriverIsNotNullAndIdentifyIsNullAndDepartureTimeBetween(now, in30m.getTime());

        for (Booking booking : lstBookings)
            booking.setIdentify(UUID.randomUUID().toString());

        bookingRepository.save(lstBookings);
    }

}
