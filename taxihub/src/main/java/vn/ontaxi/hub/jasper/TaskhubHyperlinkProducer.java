package vn.ontaxi.hub.jasper;

import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;
import vn.ontaxi.hub.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskhubHyperlinkProducer implements JRHyperlinkProducer {
	@Override
	public String getHyperlink(final JRPrintHyperlink hyperlink) {
		final Map<String, String> allParams = new HashMap<>();
		if (hyperlink.getHyperlinkParameters() != null) {
			final List<JRPrintHyperlinkParameter> parameters = hyperlink.getHyperlinkParameters().getParameters();
			parameters.stream().forEach(param -> allParams.put(param.getName(), param.getValue().toString()));
		}
		allParams.put(ReportConstant.EXPORT_FORMAT_PARAM, ReportFormatType.HTML.toString());

		return ReportConstant.TASKHUB_REPORT_URI + StringUtils.mapToURLQueryString(allParams);
	}
}
