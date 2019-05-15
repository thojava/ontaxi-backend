package vn.ontaxi.hub.jasper;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.DefaultHyperlinkProducerFactory;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;

public class TaskhubHyperlinkProducerFactory extends DefaultHyperlinkProducerFactory {
	public TaskhubHyperlinkProducerFactory(final JasperReportsContext jasperReportsContext) {
		super(jasperReportsContext);
	}

	@Override
	public JRHyperlinkProducer getHandler(final String linkType) {
		if (ReportConstant.TASKHUB_REPORT_HYPERLINK_EXECUTION_TYPE.equalsIgnoreCase(linkType)) {
			return new TaskhubHyperlinkProducer();
		}
		else {
			return super.getHandler(linkType);
		}
	}
}
