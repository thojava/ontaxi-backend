/*--------------------------------------------------------------------------*
 | Modification Logs:
 | DATE        		AUTHOR		DESCRIPTION
 | ------------------------------------------------
 | December-30-2010	LocHD      	First creation
 *--------------------------------------------------------------------------*/
package vn.ontaxi.hub.jasper;

import net.sf.jasperreports.engine.type.CalculationEnum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ReportConstant {
	public static final String EXPORT_FORMAT_PARAM = "_format";
	public static final String REPORT_CODE_PARAM = "_rptCode";
	public static final String TASKHUB_REPORT_HYPERLINK_EXECUTION_TYPE = "TaskhubReportExecution";
	public static final String TASKHUB_REPORT_URI = "/TH6/report/jasperReport.xhtml?";
	public static final String FILTER_PARAM = "__RPT__FILTER__PARAM";
	public static final String SBU_FILTER_PARAM = "__RPT__SBU__FILTER__PARAM";
	public static final String ORDERBY_PARAM = "__RPT__ORDERBY__PARAM";
	public static final String RPT_PARAM = "__RPT";
	public static final String FILTER_CRITERIA = "__FILTER_CRITERIA";
	public static final String PARAMATER_CRITERIA = "__PARAMATER_CRITERIA";
	public static final String FILTER_PATTERN_STANDARD = "%s %s %s";
	public static final String FILTER_PATTERN_NULL = "%s IS NULL";
	public static final String FILTER_PATTERN_NOT_NULL = "%s IS NOT NULL";
	public static final String FILTER_PATTERN_LIKE = "%s LIKE '%s%s%s'";
	public static final String FILTER_PATTERN_BETWEEN_AND = "%s BETWEEN %s AND %s";
	public static final String FILTER_PATTERN_IN = "$X{IN,%s,%s}";
	public static final String FILTER_PATTERN_NOT_IN = "$X{NOTIN,%s,%s}";

	public static final String BETWEEN_AND_SEPARATOR = "-";
	public static final String RPT_PARAMETER_PATTERN = "$P{%s}";
	public static final String RPT_PARAMETER_PATTERN_2 = "$P!{%s}";

	public static final String LOOKUP_TYPE_SEARCH = "S";
	public static final String LOOKUP_TYPE_SELECT = "D";
	public static final String LOOKUP_TYPE_CHECKBOX = "C";

	//Template Keys
	public static final String LABEL_KEY = "LABEL_KEY";
	public static final String VALUE_KEY = "VALUE_KEY";
	public static final String GROUP_HEADER_SUMMARY_KEY = "GROUP_HEADER_SUMMARY_KEY";
	public static final String GROUP_FOOTER_TOTAL_KEY = "GROUP_FOOTER_TOTAL_KEY";
	public static final String INNER_FIELD_KEY = "INNER_FIELD_KEY";
	public static final String PARAMATER_CRITERIA_FIELD = "PARAMATER_CRITERIA_FIELD";
	public static final String FILTER_CRITERIA_FIELD = "FILTER_CRITERIA_FIELD";
	// Dynamic Parameter Rules
	public static final String DYNAMIC_SQL_PARAM_FROM_SUFFIX = "_SQL_PARAM_FROM";
	public static final String DYNAMIC_SQL_PARAM_TO_SUFFIX = "_SQL_PARAM_TO";
	public static final String DYNAMIC_SQL_PARAM_SUFFIX = "_SQL_PARAM";

	public static final String PARAM_FROM_SUFFIX = "_FROM";
	public static final String PARAM_TO_SUFFIX = "_TO";
	public static final String PARAM_PASSED_SUFFIX = "_PASSED";
	public static final String RPT_CROSSTAB_ROW_GROUP = "__RPT_ROW_GROUP";

	public static final String PARAMETER_SIGN = "P";
	public static final String FIELD_SIGN = "F";
	public static final String VARIABLE_SIGN = "V";
	public static final String GROUP_COUNT_VARIABLE_SUFFIX = "_COUNT";

	public static final String FX_SUM = "SUM";
	public static final String FX_AVG = "AVG";
	public static final String FX_MIN = "MIN";
	public static final String FX_MAX = "MAX";

	public static final String FINANCIAL_PERIOD_PARAM_NAME = "FINANCIAL_PERIOD";
	public static final String FINANCIAL_YEAR_PARAM_NAME = "FINANCIAL_YEAR";
	public static final String DYNAMIC_CROSS_TAB_KEY = "DYNAMIC_CROSSTAB_KEY";
	public static final List<String> FX_LIST = Arrays.asList(ReportConstant.FX_SUM, ReportConstant.FX_AVG, ReportConstant.FX_MIN, ReportConstant.FX_MAX);

	public static final String JRXML_EXTENSION = ".jrxml";
	public static final String JASPER_EXTENSION = ".jasper";
	public static final String SUBREPORT_DIR = "SUBREPORT_DIR";
	public static final String IMAGE_PATH = "IMAGE_PATH";
	public static final Map<String, CalculationEnum> FX_CAL_ENUM_MAP;

	static {
		FX_CAL_ENUM_MAP = new HashMap<String, CalculationEnum>();

		ReportConstant.FX_CAL_ENUM_MAP.put(ReportConstant.FX_SUM, CalculationEnum.SUM);
		ReportConstant.FX_CAL_ENUM_MAP.put(ReportConstant.FX_AVG, CalculationEnum.AVERAGE);
		ReportConstant.FX_CAL_ENUM_MAP.put(ReportConstant.FX_MIN, CalculationEnum.LOWEST);
		ReportConstant.FX_CAL_ENUM_MAP.put(ReportConstant.FX_MAX, CalculationEnum.HIGHEST);
	}
}
