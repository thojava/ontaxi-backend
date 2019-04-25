package vn.ontaxi.component;

import vn.ontaxi.constant.OrderStatus;
import vn.ontaxi.jpa.entity.Booking;
import vn.ontaxi.service.BookingService;
import vn.ontaxi.utils.DateUtils;
import org.primefaces.model.chart.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@Scope("view")
public class StatisticComponent {
    private final BookingService bookingService;

    private LineChartModel incomeChartModel;

    @Autowired
    public StatisticComponent(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostConstruct
    public void init() {
        createLineModels();
    }

    private void createLineModels() {
        incomeChartModel = initLinearModel();
        incomeChartModel.setLegendPosition("e");
        incomeChartModel.setDatatipFormat("<span style=\\\"display:none;\\\">%s</span><span>%s</span>");

        incomeChartModel.getAxes().put(AxisType.X, new CategoryAxis("Ngày"));

        Axis yAxis = incomeChartModel.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(1000 * 1000 * 1.5);
    }


    private LineChartModel initLinearModel() {
        LineChartModel model = new LineChartModel();

        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("Thu Nhập");

        Date today = DateUtils.today();
        List<Booking> completedBookings = bookingService.getBookingByStatusAndArrivalTimeBetweenOrderByArrivalTimeAsc(OrderStatus.COMPLETED,
                DateUtils.getStartOfDay(DateUtils.addDays(today, -30)), DateUtils.getEndOfDay(today));
        Map<Integer, Double> incomeByDateMap = new TreeMap<>();
        Map<Integer, Date> mapOfDayInYearsAndDate = new HashMap<>();
        for (Booking completedBooking : completedBookings) {
            Calendar calendar = DateUtils.toCalendar(completedBooking.getArrival_time());
            int dateKey = calendar.get(Calendar.DAY_OF_YEAR);
            if(!incomeByDateMap.containsKey(dateKey)) {
                incomeByDateMap.put(dateKey, 0.d);
                mapOfDayInYearsAndDate.put(dateKey, calendar.getTime());
            }

            incomeByDateMap.put(dateKey, incomeByDateMap.get(dateKey) + completedBooking.getActual_total_fee());
        }

        for (Integer dateKey : incomeByDateMap.keySet()) {
            series1.set(DateUtils.ddMMuDateFormat.format(mapOfDayInYearsAndDate.get(dateKey)), incomeByDateMap.get(dateKey));
        }

        model.addSeries(series1);

        return model;
    }

    public LineChartModel getIncomeChartModel() {
        return incomeChartModel;
    }

}
