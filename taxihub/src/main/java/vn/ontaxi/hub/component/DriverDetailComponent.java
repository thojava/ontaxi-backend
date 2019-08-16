package vn.ontaxi.hub.component;

import vn.ontaxi.hub.jasper.ReportFormatType;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.jpa.entity.DriverTransaction;
import vn.ontaxi.common.jpa.repository.DriverRepository;
import vn.ontaxi.common.jpa.repository.DriverTransactionRepository;
import vn.ontaxi.hub.service.ReportService;
import vn.ontaxi.hub.utils.HttpUtils;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("view")
public class DriverDetailComponent {
    private final DriverRepository driverRepository;
    private final DriverTransactionRepository driverTransactionRepository;
    private final ReportService reportService;
    private Driver newDriver;


    @Autowired
    public DriverDetailComponent(DriverRepository driverRepository, DriverTransactionRepository driverTransactionRepository, ReportService reportService) {
        this.driverRepository = driverRepository;
        this.driverTransactionRepository = driverTransactionRepository;
        this.reportService = reportService;
    }

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if (params.containsKey("id")) {
            String parameterOne = params.get("id");
            newDriver = driverRepository.findById(Long.parseLong(parameterOne)).get();
        } else {
            newDriver = new Driver();
        }
    }

    public String addNewDriver() {
        driverRepository.saveAndFlush(newDriver);
        return "driver.jsf?faces-redirect=true";
    }

    public void exportFinancialReport() throws IOException, JRException {
        ReportFormatType exportFormatType = ReportFormatType.EXCEL;
        final HttpServletResponse httpResponse = HttpUtils.setupHttpResponseForMIMEContent(exportFormatType.getMineType(), newDriver.getName() + ".xls");
        final OutputStream outputStream = httpResponse.getOutputStream();


        HashMap<String, Object> jrParameters = new HashMap<>();
        jrParameters.put("DRIVER", newDriver.getEmail());
        reportService.generateReport("driverFinancialReport.jrxml", jrParameters, exportFormatType, outputStream);

        outputStream.flush();
        outputStream.close();
    }

    public Driver getNewDriver() {
        return newDriver;
    }

    public void setNewDriver(Driver newDriver) {
        this.newDriver = newDriver;
    }

    public List<DriverTransaction> getDriverTransactions() {
        return driverTransactionRepository.findByDriverOrderByLastUpdatedDatetimeDesc(newDriver.getEmail());
    }
}
