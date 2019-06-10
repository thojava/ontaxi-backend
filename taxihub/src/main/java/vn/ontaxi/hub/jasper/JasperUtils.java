package vn.ontaxi.hub.jasper;

import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.crosstabs.design.*;
import net.sf.jasperreports.crosstabs.type.CrosstabTotalPositionEnum;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRExporterContext;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.util.JRElementsVisitor;
import net.sf.jasperreports.engine.util.JRVisitorSupport;
import net.sf.jasperreports.export.*;
import org.apache.commons.lang3.Validate;
import vn.ontaxi.common.utils.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class JasperUtils {
	public static void hideJRElement(final JRElementGroup band, final String elementKey) {
		Validate.notNull(band);

		final List<JRElement> jrElements = JasperUtils.getAllElementsByKey(band, elementKey);
		for (final JRElement jrElement : jrElements) {
			JasperUtils.hideJRElement((JRDesignElement) jrElement);
		}
	}

	public static void removeJRElementFromGroups(final JasperDesign jasperDesign, final String elementKey) {
		for (final JRGroup group : jasperDesign.getGroups()) {
			for (final JRBand band : group.getGroupHeaderSection().getBands()) {
				JasperUtils.hideJRElement(band, elementKey);
			}
			for (final JRBand band : group.getGroupFooterSection().getBands()) {
				JasperUtils.hideJRElement(band, elementKey);
			}
		}
	}

	public static void moveJRElement(final JRBand band, final String elementKey, final int xDelta) {
		Validate.notNull(band);

		final JRElement jrElement = band.getElementByKey(elementKey);
		if (jrElement != null) {
			jrElement.setX(jrElement.getX() + xDelta);
		}
	}

	public static void moveJRElementFromGroups(final JasperDesign jasperDesign, final String elementKey, final int xDelta) {
		for (final JRGroup group : jasperDesign.getGroups()) {
			for (final JRBand band : group.getGroupHeaderSection().getBands()) {
				JasperUtils.moveJRElement(band, elementKey, xDelta);
			}
			for (final JRBand band : group.getGroupFooterSection().getBands()) {
				JasperUtils.moveJRElement(band, elementKey, xDelta);
			}
		}
	}


	/**
	 * Visit and load all subReport that included in a template
	 *
	 * @param jasperDesign Specific the parent template
	 * @return list of sub-report design
	 */
	public static List<JRDesignCrosstab> loadAllCrosstabs(final JasperDesign jasperDesign) {
		final List<JRDesignCrosstab> listOfCrosstabs = new ArrayList<JRDesignCrosstab>();
		final JRVisitor jrVisitor = new JRVisitorSupport() {
			@Override
			public void visitCrosstab(final JRCrosstab subReport) {
				listOfCrosstabs.add((JRDesignCrosstab) subReport);
			}
		};
		final JRElementsVisitor elementVisitor = new JRElementsVisitor(jrVisitor);
		elementVisitor.visitReport(jasperDesign);

		return listOfCrosstabs;
	}

	public static List<JRDesignCrosstab> loadAllCrosstabs(final JasperDesign jasperDesign, final List<JasperDesign> listOfSubReports) {
		final List<JRDesignCrosstab> listOfCrosstabs = new ArrayList<JRDesignCrosstab>();
		listOfCrosstabs.addAll(JasperUtils.loadAllCrosstabs(jasperDesign));
		for (final JasperDesign sub : listOfSubReports) {
			listOfCrosstabs.addAll(JasperUtils.loadAllCrosstabs(sub));
		}
		return listOfCrosstabs;
	}

	public static JRDesignCrosstabCell createCrosstabCell(final String rowGroupName, final String columnGroupName, final int cellWidth, final int cellHeight, final Color cellBackcolor, final Map<String, JRDesignCrosstabCell> mapOfColumnGroupAndCell) {
		final JRDesignCrosstabCell newCrosstabCell = (JRDesignCrosstabCell) mapOfColumnGroupAndCell.get(columnGroupName).clone();
		newCrosstabCell.setRowTotalGroup(rowGroupName);
		((JRDesignCellContents) newCrosstabCell.getContents()).setMode(ModeEnum.OPAQUE);
		((JRDesignCellContents) newCrosstabCell.getContents()).setBackcolor(cellBackcolor);

		return newCrosstabCell;
	}

	public static JRDesignCrosstabRowGroup createRowGroup(final JasperDesign jasperDesign, final JRDesignCrosstab designCrosstab, final String fieldName, final String previousFieldName, final int rowIndex, final int headerWidth, final int headerHeight, final Color rowColour, final Color totalHeaderColor, final int crosstabHeaderHeight, final Map<String, JRDesignCrosstabRowGroup> mapOfOriginalRowGroups, final boolean showTotalGroupHeder) throws JRException {
		final JRDesignCrosstabRowGroup newCrossTabRowGroup = new JRDesignCrosstabRowGroup();
		newCrossTabRowGroup.setName(fieldName);
		newCrossTabRowGroup.setWidth(0);
		newCrossTabRowGroup.setTotalPosition(showTotalGroupHeder ? CrosstabTotalPositionEnum.END : CrosstabTotalPositionEnum.NONE);

		JRDesignCrosstabBucket crosstabBucket = new JRDesignCrosstabBucket();
		if (mapOfOriginalRowGroups.containsKey(fieldName)) {
			crosstabBucket = (JRDesignCrosstabBucket) mapOfOriginalRowGroups.get(fieldName).getBucket();
			crosstabBucket.getExpression().getText();
		}
		else {
			crosstabBucket.setValueClassName(String.class.getName());
		}
		newCrossTabRowGroup.setBucket(crosstabBucket);

		// Row header
		if (showTotalGroupHeder) {
			if (rowIndex == 0) {
				final JRDesignCellContents totalHeaderCell = new JRDesignCellContents();
				newCrossTabRowGroup.setTotalHeader(totalHeaderCell);
				totalHeaderCell.addElement(JasperUtils.createField(jasperDesign, JasperUtils.createStringExpression("GRAND TOTAL"), headerWidth, headerHeight));
			}
			else {
				final JRDesignCellContents totalHeaderCell = new JRDesignCellContents();
				newCrossTabRowGroup.setTotalHeader(totalHeaderCell);
				final JRDesignTextField designTextField = JasperUtils.createField(jasperDesign, JasperUtils.createStringExpression("TOTAL BY \\n" + JasperUtils.getRowGroupLabel(previousFieldName)),
						headerWidth, headerHeight);
				designTextField.setStretchType(StretchTypeEnum.CONTAINER_HEIGHT);
				designTextField.setX(rowIndex * headerWidth);
				designTextField.setBold(true);
				totalHeaderCell.addElement(designTextField);
			}
		}


		designCrosstab.addRowGroup(newCrossTabRowGroup);

		// Add row group label at cross tab header
		final JRDesignCellContents crHeaderCell = (JRDesignCellContents) designCrosstab.getHeaderCell();
		int maxX = 0;
		for (final JRElement element : crHeaderCell.getElements()) {
			element.getHeight();
			maxX = Math.max(maxX, element.getX() + element.getWidth());
		}
		final JRDesignTextField rowGroupLabel = JasperUtils.createField(jasperDesign, JasperUtils.createStringExpression(JasperUtils.getRowGroupLabel(fieldName)), headerWidth, crosstabHeaderHeight);
		rowGroupLabel.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		rowGroupLabel.setBold(true);
		rowGroupLabel.setX(maxX);
		crHeaderCell.addElement(rowGroupLabel);

		return newCrossTabRowGroup;
	}

	/*
	 * Get row group label from row group name Generally, Replace "_" by " " and upper-case the first letter
	 */
	private static String getRowGroupLabel(String rowGroupName) {
		final String[] parts = rowGroupName.replace("_", " ").toLowerCase().split(" ");
		rowGroupName = new String();
		for (final String part : parts) {
			rowGroupName += part.substring(0, 1).toUpperCase() + part.substring(1) + " ";
		}
		return rowGroupName.trim();
	}

	public static JRDesignExpression createStringExpression(final String fieldName) {
		final JRDesignExpression newExpression = new JRDesignExpression();
		newExpression.setText('"' + fieldName + '"');
		return newExpression;
	}

	public static JRDesignTextField createField(final JasperDesign jasperDesign, final JRDesignExpression exp, final int w, final int h) {
		final JRDesignTextField element = new JRDesignTextField();
		element.setWidth(w);
		element.setHeight(h);
		element.setBlankWhenNull(true);
		element.setStretchWithOverflow(true);

		element.setExpression(exp);
		return element;
	}

	public static JRDesignStaticText createStaticField(final String text, final int w, final int h) {
		final JRDesignStaticText element = new JRDesignStaticText();
		element.setWidth(w);
		element.setHeight(h);
		element.setText(text);

		return element;
	}

	/**
	 * Create new expression based on a field name
	 *
	 * @param fieldName
	 * @return $F{fieldName}
	 */
	public static JRDesignExpression createFieldExpression(final String fieldName) {
		final JRDesignExpression newExpression = new JRDesignExpression();
		newExpression.setText(String.format(JasperUtils.EXPRESSION_PATTERN, ReportConstant.FIELD_SIGN, fieldName));
		return newExpression;
	}

	/**
	 * Create new expression based on a variable name
	 *
	 * @param variableName
	 * @return $F{variableName}
	 */
	public static JRDesignExpression createVariableExpression(final String variableName) {
		final JRDesignExpression newExpression = new JRDesignExpression();
		newExpression.setText(String.format(JasperUtils.EXPRESSION_PATTERN, ReportConstant.VARIABLE_SIGN, variableName));
		return newExpression;
	}

	public static final String EXPRESSION_PATTERN = "$%s{%s}";

	/**
	 * Hide JR Element by set printWhenExpression to be false.
	 *
	 * @param jrElement
	 */
	public static void hideJRElement(final JRDesignElement jrElement) {
		final JRDesignExpression falseExpression = new JRDesignExpression("false");
		jrElement.setPrintWhenExpression(falseExpression);
	}

	/**
	 * Jasper Report does not support a way to get all elements that have same key, it just return the first element. This method will loop through all children inside a element group and return list
	 * of candidates.
	 *
	 * @param elementGroup
	 * @param elementKey
	 * @return
	 */
	private static List<JRElement> getAllElementsByKey(final JRElementGroup elementGroup, final String elementKey) {
		final List<JRElement> listOfCandidates = new ArrayList<JRElement>();
		for (final JRElement child : elementGroup.getElements()) {
			if (elementKey.equalsIgnoreCase(child.getKey())) {
				listOfCandidates.add(child);
			}
		}
		return listOfCandidates;
	}


	/**
	 * Visit and load all subReport that included in a template
	 *
	 * @param jasperDesign Specific the parent template
	 * @return
	 */
	public static List<JRDesignSubreport> getSubReports(final JasperDesign jasperDesign) {
		final List<JRDesignSubreport> listOfReports = new ArrayList<JRDesignSubreport>();

		final JRVisitor jrVisitor = new JRVisitorSupport() {
			@Override
			public void visitSubreport(final JRSubreport subReport) {
				listOfReports.add((JRDesignSubreport) subReport);
			}
		};

		final JRElementsVisitor elementVisitor = new JRElementsVisitor(jrVisitor);
		elementVisitor.visitReport(jasperDesign);

		return listOfReports;
	}

	public static void compileSubReport(final List<JasperDesign> listOfSubReports, final List<String> subreportPaths) throws JRException {
		for (int i = 1; i <= listOfSubReports.size(); i++) {
			final JasperDesign subDesign = listOfSubReports.get(i - 1);
			final File jasperFile = new File(StringUtils.removeFileExtension(subreportPaths.get(i - 1)) + ReportConstant.JASPER_EXTENSION);
			// Remove the old file
			if (jasperFile.exists()) {
				jasperFile.delete();
			}
			JasperCompileManager.compileReportToFile(subDesign, jasperFile.getPath());
		}
	}

	public static void writeReportToOutputStream(final JasperReport jasperReport, final Map<String, Object> jrParameters, final Connection connection, final ReportFormatType type, final OutputStream outputStream) throws JRException {
		final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, jrParameters, connection);

		JasperUtils.exportReport(jasperPrint, type, outputStream);
	}

	public static void writeReportToOutputStream(final JasperReport jasperReport, final Map<String, Object> jrParameters, final Connection connection, final ReportFormatType type, final OutputStream outputStream, final ExporterConfiguration configuration) throws JRException {
		final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, jrParameters, connection);

		JasperUtils.exportReport(jasperPrint, type, outputStream, configuration);
	}


	public static void writeReportToOutputStream(final JasperReport jasperReport, final Map<String, Object> jrParameters, final JRDataSource jrDataSource, final ReportFormatType type, final OutputStream outputStream) throws JRException {
		final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, jrParameters, jrDataSource);

		JasperUtils.exportReport(jasperPrint, type, outputStream);
	}

	private static void exportReport(final JasperPrint jasperPrint, final ReportFormatType type, final OutputStream outputStream) throws JRException {
		JasperUtils.exportReport(jasperPrint, type, outputStream, null);
	}

	@SuppressWarnings("unchecked")
	private static <EC extends ExporterConfiguration> void exportReport(final JasperPrint jasperPrint, final ReportFormatType type, final OutputStream outputStream, final EC configuration) throws JRException {
		Validate.isTrue(outputStream != null, "Parameter 'outputFileOrStream' must be passed.");

		final Exporter<ExporterInput, ? extends ReportExportConfiguration, ? extends ExporterConfiguration, ? extends ExporterOutput> exporter = type.getExporterSupplier().get();
		if (type.isHtmlType()) {
			JasperUtils.setupExporterParameter((Exporter<ExporterInput, ? extends ReportExportConfiguration, EC, SimpleHtmlExporterOutput>) exporter,
					(Function<OutputStream, SimpleHtmlExporterOutput>) type.getExporterOuputConstructor(), jasperPrint, outputStream, configuration);
		}
		else {
			JasperUtils.setupExporterParameter((Exporter<ExporterInput, ? extends ReportExportConfiguration, EC, SimpleOutputStreamExporterOutput>) exporter,
					(Function<OutputStream, SimpleOutputStreamExporterOutput>) type.getExporterOuputConstructor(), jasperPrint, outputStream, configuration);
		}
		exporter.exportReport();
	}


	@SuppressWarnings("unchecked")
	private static <EO extends ExporterOutput, EC extends ExporterConfiguration> void setupExporterParameter(final Exporter<ExporterInput, ? extends ReportExportConfiguration, EC, EO> exporter, final Function<OutputStream, EO> exporterOutputConstructor, final JasperPrint jasperPrint, final OutputStream outputStream, final EC configuration) {
		JasperUtils.setupCommonExporterParam(exporter, exporterOutputConstructor, jasperPrint, outputStream, configuration);

		if (JRXlsAbstractExporter.class.isAssignableFrom(exporter.getClass())) {
			JasperUtils.setupExcelExporterParam((JRXlsAbstractExporter<XlsReportConfiguration, XlsExporterConfiguration, JRExporterContext>) exporter);
		}
		else if (HtmlExporter.class.isAssignableFrom(exporter.getClass())) {
			JasperUtils.setupHTMLExporterConfinguration((HtmlExporter) exporter);
		}
	}

	private static void setupHTMLExporterConfinguration(final HtmlExporter exporter) {
		final SimpleHtmlReportConfiguration simpleHtmlReportConfiguration = new SimpleHtmlReportConfiguration();
		simpleHtmlReportConfiguration.setHyperlinkProducerFactory(new TaskhubHyperlinkProducerFactory(DefaultJasperReportsContext.getInstance()));
		exporter.setConfiguration(simpleHtmlReportConfiguration);
	}

	private static <EO extends ExporterOutput, EC extends ExporterConfiguration> void setupCommonExporterParam(final Exporter<ExporterInput, ? extends ReportExportConfiguration, EC, EO> exporter, final Function<OutputStream, EO> exporterOutputConstructor, final JasperPrint jasperPrint, final OutputStream outputStream, final EC configuration) {
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(exporterOutputConstructor.apply(outputStream));

		if (configuration != null) {
			exporter.setConfiguration(configuration);
		}
	}

	private static void setupExcelExporterParam(final JRXlsAbstractExporter<XlsReportConfiguration, XlsExporterConfiguration, JRExporterContext> exporter) {
		exporter.setConfiguration(new AbstractXlsReportConfiguration() {
			@Override
			public Boolean isDetectCellType() {
				return true;
			}

			@Override
			public Boolean isRemoveEmptySpaceBetweenColumns() {
				return true;
			}

			@Override
			public Boolean isRemoveEmptySpaceBetweenRows() {
				return true;
			}

			@Override
			public Boolean isWhitePageBackground() {
				return false;
			}
		});
	}
}
