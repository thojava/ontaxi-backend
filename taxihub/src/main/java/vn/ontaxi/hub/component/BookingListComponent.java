package vn.ontaxi.hub.component;

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
import vn.ontaxi.common.utils.DateUtils;
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
    private final OrderSummaryTabStore orderSummaryTabStore;

    private TaxiLazyDataModel<Booking> scheduledBookings;
    private TaxiLazyDataModel<Booking> customerConfirmedBookings;
    private TaxiLazyDataModel<Booking> newBookings;
    private TaxiLazyDataModel<Booking> acceptedBookings;
    private TaxiLazyDataModel<Booking> inProgressBookings;
    private TaxiLazyDataModel<Booking> completedBookings;
    private TaxiLazyDataModel<Booking> cancelledBookings;

    private List<Booking> filteredNewBookings;
    private List<Booking> filteredCompletedBookings;
    private List<Booking> filteredCancelledBookings;
    private Date filterFromDate;
    private Date filterToDate;


    @Autowired
    public BookingListComponent(DriverRepository driverRepository, FCMService fcmService, LazyDataService lazyDataService, BookingService bookingService, BookingRepository bookingRepository, OrderSummaryTabStore orderSummaryTabStore) {
        this.driverRepository = driverRepository;
        this.fcmService = fcmService;
        this.lazyDataService = lazyDataService;
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        this.orderSummaryTabStore = orderSummaryTabStore;
    }

    public void resetNewOrder() {
        this.scheduledBookings = null;
    }

    public void cancelBooking() {

        switch (orderSummaryTabStore.getActiveIndex()) {
            case 0:
                List<Booking> scheduledBookingsWrappedData = getScheduledBookings().getWrappedData();
                for (Booking newBooking : scheduledBookingsWrappedData) {
                    if (newBooking.isBeanSelected()) {
                        newBooking.setStatus(OrderStatus.ABORTED);
                        bookingRepository.saveAndFlush(newBooking);
                    }
                }
                scheduledBookings = null;
                break;
            case 1:
                List<Booking> confirmedBookingsWrappedData = getCustomerConfirmedBookings().getWrappedData();
                for (Booking confirmBooking : confirmedBookingsWrappedData) {
                    if (confirmBooking.isBeanSelected()) {
                        confirmBooking.setStatus(OrderStatus.ABORTED);
                        bookingRepository.saveAndFlush(confirmBooking);
                    }
                }
                customerConfirmedBookings = null;
                break;
            case 2:
                List<Booking> newBookingsWrappedData = getNewBookings().getWrappedData();
                for (Booking newBooking : newBookingsWrappedData) {
                    if (newBooking.isBeanSelected()) {
                        newBooking.setStatus(OrderStatus.ABORTED);
                        bookingRepository.saveAndFlush(newBooking);
                        fcmService.abortNewTaxiOrder(newBooking);
                    }
                }
                newBookings = null;
                break;
            case 3:
                List<Booking> acceptedBookingsWrappedData = getAcceptedBookings().getWrappedData();
                for (Booking acceptedBooking : acceptedBookingsWrappedData) {
                    if (acceptedBooking.isBeanSelected()) {
                        Driver accepted_by_driver = acceptedBooking.getAccepted_by_driver();
                        if (accepted_by_driver != null) {
                            accepted_by_driver.increaseAmt(acceptedBooking.getTotal_fee(), logger);
                            driverRepository.saveAndFlush(accepted_by_driver);
                        }
                        acceptedBooking.setStatus(OrderStatus.ABORTED);
                        bookingRepository.saveAndFlush(acceptedBooking);
                        fcmService.abortNewTaxiOrder(acceptedBooking);
                        return;
                    }
                }
                acceptedBookings = null;
                break;
            case 4:
                List<Booking> onGoingBookingsWrappedData = getInProgressBookings().getWrappedData();
                for (Booking onGoingBooking : onGoingBookingsWrappedData) {
                    if (onGoingBooking.isBeanSelected()) {
                        Driver accepted_by_driver = onGoingBooking.getAccepted_by_driver();
                        if (accepted_by_driver != null) {
                            accepted_by_driver.increaseAmt(onGoingBooking.getActual_total_fee(), logger);
                            driverRepository.saveAndFlush(accepted_by_driver);
                        }
                        onGoingBooking.setStatus(OrderStatus.ABORTED);
                        bookingRepository.saveAndFlush(onGoingBooking);
                        fcmService.abortNewTaxiOrder(onGoingBooking);
                    }
                }
                inProgressBookings = null;
                break;
            case 5:
                List<Booking> completedBookingsWrappedData = getCompletedBookings().getWrappedData();
                for (Booking completedBooking : completedBookingsWrappedData) {
                    if (completedBooking.isBeanSelected()) {
                        Driver accepted_by_driver = completedBooking.getAccepted_by_driver();
                        if (accepted_by_driver != null) {
                            accepted_by_driver.increaseAmt(completedBooking.getActual_total_fee(), logger);
                            driverRepository.saveAndFlush(accepted_by_driver);
                        }
                        completedBooking.setStatus(OrderStatus.ABORTED);
                        bookingRepository.saveAndFlush(completedBooking);
                    }
                }
                completedBookings = null;
                break;
        }

        cancelledBookings = null;
    }

    public TaxiLazyDataModel<Booking> getScheduledBookings() {
        if (scheduledBookings == null) {
            scheduledBookings = new TaxiLazyDataModel<>(lazyDataService, bookingRepository, BookingOrder.DEPARTURE_TIME_ASC);
            scheduledBookings.addPredicate(((criteriaBuilder, root) -> criteriaBuilder.equal(root.get("status"), OrderStatus.ORDERED)));
        }

        return scheduledBookings;
    }

    public TaxiLazyDataModel<Booking> getCustomerConfirmedBookings() {
        if (customerConfirmedBookings == null) {
            customerConfirmedBookings = new TaxiLazyDataModel<>(lazyDataService, bookingRepository, BookingOrder.DEPARTURE_TIME_ASC);
            customerConfirmedBookings.addPredicate(((criteriaBuilder, root) -> criteriaBuilder.equal(root.get("status"), OrderStatus.CUSTOEMR_CONFIRM)));
        }

        return customerConfirmedBookings;
    }

    public Long getCustomerConfirmedBookingSize() {
        return getCustomerConfirmedBookings().getTotalSize();
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
            acceptedBookings = new TaxiLazyDataModel<>(lazyDataService, bookingRepository, BookingOrder.DEPARTURE_TIME_ASC);
            acceptedBookings.addPredicate(((criteriaBuilder, root) -> criteriaBuilder.equal(root.get("status"), OrderStatus.ACCEPTED)));
        }

        return acceptedBookings;
    }

    public TaxiLazyDataModel<Booking> getInProgressBookings() {
        if (inProgressBookings == null) {
            inProgressBookings = new TaxiLazyDataModel<>(lazyDataService, bookingRepository, BookingOrder.ARRIVAL_TIME_DESC);
            inProgressBookings.addPredicate(((criteriaBuilder, root) -> criteriaBuilder.equal(root.get("status"), OrderStatus.IN_PROGRESS)));
        }

        return inProgressBookings;
    }

    public Long getInProgressBookingSize() {
        return getInProgressBookings().getTotalSize();
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

    public TaxiLazyDataModel<Booking> getCancelledBookings() {
        if(cancelledBookings == null) {
            cancelledBookings = new TaxiLazyDataModel<>(lazyDataService, bookingRepository, BookingOrder.DEPARTURE_TIME_DESC);
            cancelledBookings.addPredicate(((criteriaBuilder, root) -> criteriaBuilder.equal(root.get("status"), OrderStatus.ABORTED)));
            if (filterFromDate != null && filterToDate != null) {
                cancelledBookings.addPredicate(((criteriaBuilder, root) -> criteriaBuilder.between(root.get("departureTime"), DateUtils.getStartOfDay(filterFromDate), DateUtils.getEndOfDay(filterToDate))));
            }
        }

        return cancelledBookings;
    }
    public TaxiLazyDataModel<Booking> getCompletedBookingsForCustomer(String phoneNumber) {
        if (completedBookings == null) {
            completedBookings = new TaxiLazyDataModel<>(lazyDataService, bookingRepository, BookingOrder.ARRIVAL_TIME_DESC);
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

    public Long getCancelledBookingSize() {
        return getCancelledBookings().getTotalSize();
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

    public void filterCancelledBooking() {
        cancelledBookings = null;
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

    public List<Booking> getFilteredCancelledBookings() {
        return filteredCancelledBookings;
    }

    public void setFilteredCancelledBookings(List<Booking> filteredCancelledBookings) {
        this.filteredCancelledBookings = filteredCancelledBookings;
    }
}
