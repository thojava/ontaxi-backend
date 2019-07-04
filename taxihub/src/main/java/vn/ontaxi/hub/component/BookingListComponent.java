package vn.ontaxi.hub.component;

import org.springframework.security.core.context.SecurityContextHolder;
import vn.ontaxi.hub.component.viewmodel.TaxiLazyDataModel;
import vn.ontaxi.common.constant.BookingOrder;
import vn.ontaxi.common.constant.BooleanConstants;
import vn.ontaxi.common.constant.OrderStatus;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.jpa.repository.BookingRepository;
import vn.ontaxi.common.jpa.repository.DriverRepository;
import vn.ontaxi.hub.service.BookingService;
import vn.ontaxi.hub.service.LazyDataService;
import vn.ontaxi.common.service.FCMService;
import vn.ontaxi.hub.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Component
@Scope("view")
@Transactional
public class BookingListComponent {
    private static final Logger logger = LoggerFactory.getLogger(BookingListComponent.class);

    private final LazyDataService lazyDataService;
    private final BookingService bookingService;
    private final DriverRepository driverRepository;
    private final FCMService fcmService;
    private final BookingRepository bookingRepository;

    private TaxiLazyDataModel<Booking> scheduledBookings;
    private TaxiLazyDataModel<Booking> newBookings;
    private TaxiLazyDataModel<Booking> acceptedBookings;
    private TaxiLazyDataModel<Booking> completedBookings;

    private List<Booking> filteredNewBookings;
    private List<Booking> filteredCompletedBookings;
    private Date filterFromDate;
    private Date filterToDate;


    @Autowired
    public BookingListComponent(DriverRepository driverRepository, FCMService fcmService, LazyDataService lazyDataService, BookingService bookingService, BookingRepository bookingRepository) {
        this.driverRepository = driverRepository;
        this.fcmService = fcmService;
        this.lazyDataService = lazyDataService;
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
    }

    public void resetNewOrder() {
        this.scheduledBookings = null;
    }

    public void cancelBooking() {
        for (Booking newBooking : getScheduledBookings()) {
            if (newBooking.isBeanSelected()) {
                newBooking.setStatus(OrderStatus.ABORTED);
                bookingRepository.saveAndFlush(newBooking);
                return;
            }
        }

        for (Booking newBooking : getNewBookings()) {
            if (newBooking.isBeanSelected()) {
                newBooking.setStatus(OrderStatus.ABORTED);
                bookingRepository.saveAndFlush(newBooking);

                fcmService.abortNewTaxiOrder(newBooking);
                return;
            }
        }

        for (Booking acceptedBooking : getAcceptedBookings()) {
            if (acceptedBooking.isBeanSelected()) {
                Driver accepted_by_driver = acceptedBooking.getAccepted_by_driver();
                accepted_by_driver.increaseAmt(acceptedBooking.getTotal_fee(), logger);
                driverRepository.saveAndFlush(accepted_by_driver);

                acceptedBooking.setStatus(OrderStatus.ABORTED);
                bookingRepository.saveAndFlush(acceptedBooking);

                fcmService.abortNewTaxiOrder(acceptedBooking);
                return;
            }
        }

        for (Booking completedBooking : getCompletedBookings()) {
            if (completedBooking.isBeanSelected()) {
                Driver accepted_by_driver = completedBooking.getAccepted_by_driver();
                accepted_by_driver.increaseAmt(completedBooking.getActual_total_fee(), logger);
                driverRepository.saveAndFlush(accepted_by_driver);

                completedBooking.setStatus(OrderStatus.ABORTED);
                bookingRepository.saveAndFlush(completedBooking);

                return;
            }
        }

        newBookings = null;
        scheduledBookings = null;
        acceptedBookings = null;
        completedBookings = null;
    }

    public TaxiLazyDataModel<Booking> getScheduledBookings() {
        if (scheduledBookings == null) {
            scheduledBookings = new TaxiLazyDataModel<>(lazyDataService, bookingRepository, BookingOrder.DEPARTURE_TIME_ASC);
            scheduledBookings.addPredicate(((criteriaBuilder, root) -> criteriaBuilder.equal(root.get("status"), OrderStatus.ORDERED)));
        }

        return scheduledBookings;
    }

    public Long getScheduledBookingSize() {
        return getScheduledBookings().getTotalSize();
    }

    public TaxiLazyDataModel<Booking> getNewBookings() {
        if (newBookings == null) {
            newBookings = new TaxiLazyDataModel<>(lazyDataService, bookingRepository, BookingOrder.DEPARTURE_TIME_ASC);
            newBookings.addPredicate(((criteriaBuilder, root) -> criteriaBuilder.equal(root.get("status"), OrderStatus.NEW)));
        }

        return newBookings;
    }

    public Long getNewBookingSize() {
        return getNewBookings().getTotalSize();
    }

    public TaxiLazyDataModel<Booking> getAcceptedBookings() {
        if (acceptedBookings == null) {
            acceptedBookings = new TaxiLazyDataModel<>(lazyDataService, bookingRepository, BookingOrder.ARRIVAL_TIME_DESC);
            acceptedBookings.addPredicate(((criteriaBuilder, root) -> criteriaBuilder.equal(root.get("status"), OrderStatus.ACCEPTED)));
        }

        return acceptedBookings;
    }

    public Long getAcceptedBookingSize() {
        return getAcceptedBookings().getTotalSize();
    }

    public TaxiLazyDataModel<Booking> getCompletedBookings() {
        if (completedBookings == null) {
            completedBookings = new TaxiLazyDataModel<>(lazyDataService, bookingRepository, BookingOrder.ARRIVAL_TIME_DESC);
            completedBookings.addPredicate(((criteriaBuilder, root) -> criteriaBuilder.equal(root.get("status"), OrderStatus.COMPLETED)));
            if (filterFromDate != null && filterToDate != null) {
                completedBookings.addPredicate(((criteriaBuilder, root) -> criteriaBuilder.between(root.get("arrivalTime"), DateUtils.getStartOfDay(filterFromDate), DateUtils.getEndOfDay(filterToDate))));
            }
        }

        return completedBookings;
    }

    public TaxiLazyDataModel<Booking> getCompletedBookingsForCustomer(String phoneNumber) {
        if (completedBookings == null) {
            completedBookings = new TaxiLazyDataModel<>(lazyDataService, bookingRepository, BookingOrder.ARRIVAL_TIME_DESC);
            //completedBookings.addPredicate(((criteriaBuilder, root) -> criteriaBuilder.equal(root.get("status"), OrderStatus.COMPLETED)));
            completedBookings.addPredicate(((criteriaBuilder, root) -> criteriaBuilder.equal(root.get("mobile"), phoneNumber)));
            if (filterFromDate != null && filterToDate != null) {
                completedBookings.addPredicate(((criteriaBuilder, root) -> criteriaBuilder.between(root.get("arrivalTime"), DateUtils.getStartOfDay(filterFromDate), DateUtils.getEndOfDay(filterToDate))));
            }
        }

        return completedBookings;
    }

    public Long getCompletedBookingSize() {
        return getCompletedBookings().getTotalSize();
    }

    public double getTotalCompletedKM() {
        return bookingService.getTotalCompletedKM();
    }

    public double getTotalCompletedPrice() {
        return bookingService.getTotalCompletedPrice();
    }

    public double getTotalCompletedFee() {
        return bookingService.getTotalCompletedFee();
    }

    public String selectBooking(Booking booking) {
        if (booking.isOrdered()) {
            booking.setViewed(BooleanConstants.YES);
            bookingRepository.saveAndFlush(booking);
        }
        return "order_detail.jsf?faces-redirect=true&id=" + booking.getId();
    }

    public void filterCompletedBooking() {
        completedBookings = null;
    }

    public String justifyLocation(String location) {
        return location.replaceAll("(, Vietnam|, Việt Nam)", "");
    }

    public boolean filterByDate(Object value, Object filter, Locale locale) {
        return filter == null || value != null && DateUtils.truncatedEquals((Date) filter, (Date) value, Calendar.DATE);
    }

    public List<Booking> getFilteredNewBookings() {
        return filteredNewBookings;
    }

    public void setFilteredNewBookings(List<Booking> filteredNewBookings) {
        this.filteredNewBookings = filteredNewBookings;
    }

    public List<Booking> getFilteredCompletedBookings() {
        return filteredCompletedBookings;
    }

    public void setFilteredCompletedBookings(List<Booking> filteredCompletedBookings) {
        this.filteredCompletedBookings = filteredCompletedBookings;
    }

    public Date getFilterFromDate() {
        return filterFromDate;
    }

    public void setFilterFromDate(Date filterFromDate) {
        this.filterFromDate = filterFromDate;
    }

    public Date getFilterToDate() {
        return filterToDate;
    }

    public void setFilterToDate(Date filterToDate) {
        this.filterToDate = filterToDate;
    }
}
