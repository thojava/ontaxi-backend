package vn.ontaxi.jasper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportFilterOperator {
	public static final String EQUAL_OPER = "EQ";
	public static final String GREATER_OPER = "GT";
	public static final String GREATER_OR_EQUAL_OPER = "GE";
	public static final String LESS_OPER = "LT";
	public static final String LESS_OR_EQUAL_OPER = "LE";

	public static final String NOT_EQUAL_OPER = "NE";
	public static final String HAS_A_VALUE_OPER = "HV";
	public static final String HAS_NO_VALUE_OPER = "HN";
	public static final String LIKE_OPER = "LK";
	public static final String BETWEEN_AND_OPER = "BA";

	public static final String IN_OPER = "IN";
	public static final String NOT_IN_OPER = "NI";

	public static final Map<String, String> FilterOperatorMap;
	static {
		FilterOperatorMap = new HashMap<String, String>();

		ReportFilterOperator.FilterOperatorMap.put(ReportFilterOperator.EQUAL_OPER, "=");
		ReportFilterOperator.FilterOperatorMap.put(ReportFilterOperator.GREATER_OPER, ">");
		ReportFilterOperator.FilterOperatorMap.put(ReportFilterOperator.GREATER_OR_EQUAL_OPER, ">=");
		ReportFilterOperator.FilterOperatorMap.put(ReportFilterOperator.LESS_OPER, "<");
		ReportFilterOperator.FilterOperatorMap.put(ReportFilterOperator.LESS_OR_EQUAL_OPER, "<=");
		ReportFilterOperator.FilterOperatorMap.put(ReportFilterOperator.NOT_EQUAL_OPER, "!=");
	}

	public static final List<String> ListOperatorNames;
	static {
		ListOperatorNames = new ArrayList<String>();
		ReportFilterOperator.ListOperatorNames.add(ReportFilterOperator.EQUAL_OPER);
		ReportFilterOperator.ListOperatorNames.add(ReportFilterOperator.GREATER_OPER);
		ReportFilterOperator.ListOperatorNames.add(ReportFilterOperator.GREATER_OR_EQUAL_OPER);
		ReportFilterOperator.ListOperatorNames.add(ReportFilterOperator.LESS_OPER);
		ReportFilterOperator.ListOperatorNames.add(ReportFilterOperator.LESS_OR_EQUAL_OPER);

		ReportFilterOperator.ListOperatorNames.add(ReportFilterOperator.NOT_EQUAL_OPER);
		ReportFilterOperator.ListOperatorNames.add(ReportFilterOperator.HAS_A_VALUE_OPER);
		ReportFilterOperator.ListOperatorNames.add(ReportFilterOperator.HAS_NO_VALUE_OPER);
		ReportFilterOperator.ListOperatorNames.add(ReportFilterOperator.LIKE_OPER);
		ReportFilterOperator.ListOperatorNames.add(ReportFilterOperator.BETWEEN_AND_OPER);

		ReportFilterOperator.ListOperatorNames.add(ReportFilterOperator.IN_OPER);
		ReportFilterOperator.ListOperatorNames.add(ReportFilterOperator.NOT_IN_OPER);
	}
}
