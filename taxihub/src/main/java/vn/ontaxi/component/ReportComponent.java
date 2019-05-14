package vn.ontaxi.component;

import vn.ontaxi.jasper.ReportFormatType;
import vn.ontaxi.jpa.entity.Driver;
import vn.ontaxi.jpa.repository.BookingRepository;
import vn.ontaxi.jpa.repository.DriverRepository;
import vn.ontaxi.jpa.repository.PersistentCustomerRepository;
import vn.ontaxi.service.ReportService;
import vn.ontaxi.utils.DateUtils;
import vn.ontaxi.utils.HttpUtils;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class ReportComponent {
    private final ReportService reportService;
    private final BookingRepository bookingRepository;
    private final DriverRepository driverRepository;
    private final PersistentCustomerRepository persistentCustomerRepository;

    private String debtor;
    private Driver driver;
    private boolean debt_only;
    private Date filterFromDate;
    private Date filterToDate;
    private boolean show_fee;

    @Autowired
    public ReportComponent(ReportService reportService, BookingRepository bookingRepository, DriverRepository driverRepository, PersistentCustomerRepository persistentCustomerRepository) {
        this.reportService = reportService;
        this.bookingRepository = bookingRepository;
        this.driverRepository = driverRepository;
        this.persistentCustomerRepository = persistentCustomerRepository;
    }

    public void generateReport() throws IOException, JRException {
        ReportFormatType exportFormatType = ReportFormatType.EXCEL;
        final HttpServletResponse httpResponse = HttpUtils.setupHttpResponseForMIMEContent(exportFormatType.getMineType(), "report.xls");
        final OutputStream outputStream = httpResponse.getOutputStream();


        HashMap<String, Object> jrParameters = new HashMap<>();
        jrParameters.put("DEBTOR", debtor);
        jrParameters.put("DRIVER", driver == null ? "" : driver.getEmail());
        jrParameters.put("DEBT_ONLY", debt_only);
        jrParameters.put("SHOW_FEE", show_fee);
        if (filterFromDate != null && filterToDate != null) {
            jrParameters.put("ARRIVAL_FROM_DATE", DateUtils.getStartOfDay(filterFromDate));
            jrParameters.put("ARRIVAL_TO_DATE", DateUtils.getStartOfDay(DateUtils.addDays(filterToDate, 1)));
            jrParameters.put("DATE_FILTER_PARAM", "Từ ngày " + DateUtils.ddMMyyyyDateFormat.format(filterFromDate) + " đến ngày " + DateUtils.ddMMyyyyDateFormat.format(filterToDate));
        } else {
            jrParameters.put("DATE_FILTER_PARAM", "");
        }

        reportService.generateReport("employeeReport.jrxml", jrParameters, exportFormatType, outputStream);

        outputStream.flush();
        outputStream.close();
    }

    public Iterable<Driver> getDrivers() {
        if (debt_only) {
            if (filterFromDate != null && filterToDate != null) {
                return bookingRepository.getLaterPaidDriversInPeriod(DateUtils.getStartOfDay(filterFromDate), DateUtils.getStartOfDay(DateUtils.addDays(filterToDate, 1)));
            } else return bookingRepository.getLaterPaidDrivers();
        } else {
            return driverRepository.findAll();
        }
    }

    public String getDebtor() {
        return debtor;
    }

    public void setDebtor(String debtor) {
        this.debtor = debtor;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDebt_only(boolean debt_only) {
        this.debt_only = debt_only;
    }

    public boolean getDebt_only() {
        return debt_only;
    }

    public void setFilterFromDate(Date filterFromDate) {
        this.filterFromDate = filterFromDate;
    }

    public Date getFilterFromDate() {
        return filterFromDate;
    }

    public void setFilterToDate(Date filterToDate) {
        this.filterToDate = filterToDate;
    }

    public Date getFilterToDate() {
        return filterToDate;
    }

    public boolean isShow_fee() {
        return show_fee;
    }

    public void setShow_fee(boolean show_fee) {
        this.show_fee = show_fee;
    }
}
