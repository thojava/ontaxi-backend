package vn.ontaxi.hub.component;

import vn.ontaxi.hub.component.viewmodel.TaxiLazyDataModel;
import vn.ontaxi.common.constant.BookingOrder;
import vn.ontaxi.common.constant.BooleanConstants;
import vn.ontaxi.common.constant.OrderStatus;
import vn.ontaxi.common.jpa.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.entity.CustomerSurvey;
import vn.ontaxi.common.jpa.repository.CustomerSurveyRepository;
import vn.ontaxi.hub.service.LazyDataService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import java.util.Map;

@Component
@Scope("view")
@Transactional
public class CustomerSurveyComponent {
    private Booking booking;
    private CustomerSurvey customerSurvey;
    private Iterable<CustomerSurvey> filteredSurveyedBooking;
    private final LazyDataService lazyDataService;
    private final BookingRepository bookingRepository;
    private final CustomerSurveyRepository customerSurveyRepository;
    private final ResourceBundleBean resourceBundleBean;
    private TaxiLazyDataModel<Booking> toBeSurveyedBooking;
    private TaxiLazyDataModel<CustomerSurvey> surveyedBooking;

    @Autowired
    public CustomerSurveyComponent(LazyDataService lazyDataService, BookingRepository bookingRepository, CustomerSurveyRepository customerSurveyRepository, ResourceBundleBean resourceBundleBean) {
        this.lazyDataService = lazyDataService;
        this.bookingRepository = bookingRepository;
        this.customerSurveyRepository = customerSurveyRepository;
        this.resourceBundleBean = resourceBundleBean;
    }

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String parameterOne = params.get("id");
        if (parameterOne != null) {
            booking = bookingRepository.findOne(Long.parseLong(parameterOne));
            if (booking.getSurveyId() > 0) {
                customerSurvey = customerSurveyRepository.findOne(booking.getSurveyId());
            } else {
                customerSurvey = new CustomerSurvey();
                customerSurvey.setBooking(booking);
            }
        }
    }

    public TaxiLazyDataModel<Booking> getToBeSurveyedBooking() {
        if (toBeSurveyedBooking == null) {
            toBeSurveyedBooking = new TaxiLazyDataModel<>(lazyDataService, bookingRepository, BookingOrder.ARRIVAL_TIME_DESC);
            toBeSurveyedBooking.addPredicate((criteriaBuilder, root) -> criteriaBuilder.equal(root.get("status"), OrderStatus.COMPLETED));
            toBeSurveyedBooking.addPredicate((criteriaBuilder, root) -> criteriaBuilder.equal(root.get("surveyId"), 0));
            toBeSurveyedBooking.addPredicate((criteriaBuilder, root) -> criteriaBuilder.equal(root.get("isLaterPaid"), BooleanConstants.NO));
        }

        return toBeSurveyedBooking;
    }

    public TaxiLazyDataModel<CustomerSurvey> getSurveyedBooking() {
        if (surveyedBooking == null) {
            surveyedBooking = new TaxiLazyDataModel<>(lazyDataService, customerSurveyRepository, BookingOrder.ID_DESC);
        }

        return surveyedBooking;
    }

    public String selectBooking(Booking booking) {
        return "survey_detail.jsf?faces-redirect=true&id=" + booking.getId();
    }

    public Booking getBooking() {
        if (booking == null) {
            Map<String, String> params = FacesContext.getCurrentInstance().
                    getExternalContext().getRequestParameterMap();
            String parameterOne = params.get("id");
            booking = bookingRepository.findOne(Long.parseLong(parameterOne));
        }

        return booking;
    }

    public String viewBookingDetail() {
        return "order_detail.jsf?faces-redirect=true&amp;id=" + booking.getId();
    }

    public String completeSurvey() {
        customerSurvey = customerSurveyRepository.saveAndFlush(customerSurvey);

        booking.setSurveyId(customerSurvey.getId());
        bookingRepository.saveAndFlush(booking);

        return "customer_survey.jsf?faces-redirect=true";
    }

    public String createBooking() {
        return "new_order.jsf?faces-redirect=true&name=" + booking.getName() + "&mobile=" + booking.getMobile();
    }

    public String getEvaluationDesc(String evaluation) {
        if (evaluation.equalsIgnoreCase("N")) {
            return resourceBundleBean.get("normal");
        } else if (evaluation.equalsIgnoreCase("G")) {
            return resourceBundleBean.get("good");
        }
        return resourceBundleBean.get("bad");
    }

    // ======================================= GETTER/SETTER ===========================================================
    public CustomerSurvey getCustomerSurvey() {
        return customerSurvey;
    }

    public void setCustomerSurvey(CustomerSurvey customerSurvey) {
        this.customerSurvey = customerSurvey;
    }

    public Iterable<CustomerSurvey> getFilteredSurveyedBooking() {
        return filteredSurveyedBooking;
    }

    public void setFilteredSurveyedBooking(Iterable<CustomerSurvey> filteredSurveyedBooking) {
        this.filteredSurveyedBooking = filteredSurveyedBooking;
    }
}
