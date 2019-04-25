/*--------------------------------------------------------------------------*
 | Modification Logs:
 | DATE        		AUTHOR		DESCRIPTION
 | ------------------------------------------------
 | December-30-2010	LocHD      	First creation
 *--------------------------------------------------------------------------*/

package vn.ontaxi.jasper;

import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;

import java.io.OutputStream;
import java.util.function.Function;
import java.util.function.Supplier;

public enum ReportFormatType {
	PDF("pdf", "application/pdf", JRPdfExporter::new), EXCEL("xls", "application/xls", JRXlsExporter::new), XLSX("xlsx", "application/xlsx", JRXlsxExporter::new), HTML("html", "text/html",
			HtmlExporter::new);

	private Supplier<Exporter<ExporterInput, ? extends ReportExportConfiguration, ? extends ExporterConfiguration, ? extends ExporterOutput>> exporterConstructor;
	private String fileExtension;
	private String mimeType;
	private Class<? extends Exporter<ExporterInput, ? extends ReportExportConfiguration, ? extends ExporterConfiguration, ? extends ExporterOutput>> jasperExportedClazz;


	ReportFormatType(final String fileExtension, final String mimeType, final Supplier<Exporter<ExporterInput, ? extends ReportExportConfiguration, ? extends ExporterConfiguration, ? extends ExporterOutput>> exporterSupplier) {
		this.fileExtension = fileExtension;
		this.mimeType = mimeType;
		this.exporterConstructor = exporterSupplier;
	}

	public String getFileExt() {
		return this.fileExtension;
	}

	public String getMineType() {
		return this.mimeType;
	}

	public Class<? extends Exporter<ExporterInput, ? extends ReportExportConfiguration, ? extends ExporterConfiguration, ? extends ExporterOutput>> getJasperExporterClass() {
		return this.jasperExportedClazz;
	}

	public static ReportFormatType value(final String name) {
		return ReportFormatType.valueOf(name.toUpperCase());
	}

	public Supplier<Exporter<ExporterInput, ? extends ReportExportConfiguration, ? extends ExporterConfiguration, ? extends ExporterOutput>> getExporterSupplier() {
		return this.exporterConstructor;
	}

	public boolean isHtmlType() {
		return this.equals(HTML);
	}

	public Function<OutputStream, ? extends ExporterOutput> getExporterOuputConstructor() {
		if (this.isHtmlType()) {
			return ouputStream -> new SimpleHtmlExporterOutput(ouputStream);
		}
		else {
			return ouputStream -> new SimpleOutputStreamExporterOutput(ouputStream);
		}
	}
}
