package vn.ontaxi.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.ExporterConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import vn.ontaxi.jasper.JasperUtils;
import vn.ontaxi.jasper.ReportConstant;
import vn.ontaxi.jasper.ReportFormatType;

import javax.sql.DataSource;
import java.io.File;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    private final DataSource dataSource;

    @Autowired
    public ReportService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void generateReport(final String reportResourceName, final Map<String, Object> jrParameters, final ReportFormatType formatType, final OutputStream outputStream) throws JRException {
        this.generateReport(reportResourceName, jrParameters, formatType, outputStream, null);
    }
    
    /**
     * Generate report content and write it to the outputStream
     *
     * @param outputStream Report content will be write to the outputStream
     */
    private void generateReport(final String reportResourceName, final Map<String, Object> jrParameters, final ReportFormatType formatType, final OutputStream outputStream, final ExporterConfiguration configuration) throws JRException {
        final JasperDesign jasperDesign = JRXmlLoader.load(getClass().getResourceAsStream("/report_templates/" + reportResourceName));
        final JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        final Connection mainReportConnection = DataSourceUtils.getConnection(dataSource);
        try {
            ReportService.setupCommonJRParameters(jrParameters, formatType);
            JasperUtils.writeReportToOutputStream(jasperReport, jrParameters, mainReportConnection, formatType, outputStream, configuration);
        } finally {
            this.closeReportConnection(jrParameters, mainReportConnection);
        }
    }

    // Close the connection explicitly to avoid the connection pool overhead.
    private void closeReportConnection(final Map<String, Object> jrParameters, final Connection mainReportConnection) {
        try {
            this.closeUnClosedConnection(mainReportConnection);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeUnClosedConnection(final Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private static void setupCommonJRParameters(final Map<String, Object> jrParameters, final ReportFormatType formatType) {
        // Do some common parameter setup here
    }


    private static void compileSubReport(final JasperDesign jasperDesign, final String reportParentFolder) throws JRException {
        final List<JRDesignSubreport> subReports = JasperUtils.getSubReports(jasperDesign);

        for (final JRDesignSubreport designSubreport : subReports) {
            final String subreportName = ReportService.extractSubreportFileName(designSubreport);
            final String jasperFilePath = reportParentFolder + File.separator + subreportName;
            final String jrxmlFilePath = jasperFilePath.replace(ReportConstant.JASPER_EXTENSION, ReportConstant.JRXML_EXTENSION);
        }
    }

    private static String extractSubreportFileName(final JRDesignSubreport designSubreport) {
        return designSubreport.getExpression().getText().replace("\"", "");
    }
}
