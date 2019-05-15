package vn.ontaxi.hub.jasper;

import java.util.Arrays;
import java.util.Date;

public enum ReportDataType {
	STRING("STRING", false, String.class), DATE("DATE", false, Date.class), DOUBLE("DOUBLE", false, Double.class), INTEGER("INTEGER", false, Integer.class),
	YN_BOOLEAN("YN_BOOLEAN", false, Boolean.class), RANGE_STRING("RANGE_STRING", true, String.class), RANGE_DATE("RANGE_DATE", true, Date.class), RANGE_DOUBLE("RANGE_DOUBLE", true, Double.class),
	RANGE_INTEGER("RANGE_INTEGER", true, Integer.class), LIST_STRING("LIST_STRING", false, String.class);

	@SuppressWarnings("unused")
	private String name;
	private boolean isByRange;
	private Class<?> javaType;

	ReportDataType(final String name, final boolean isByRange, final Class<?> javaType) {
		this.name = name;
		this.isByRange = isByRange;
		this.javaType = javaType;
	}

	public boolean isByRange() {
		return this.isByRange;
	}

	public Object parseValue(final String value) {
		if (this.javaType == Integer.class) {
			return Integer.parseInt(value);
		}
		else if (this.javaType == Double.class) {
			return Double.parseDouble(value);
		}
		else if (this.equals(LIST_STRING)) {
			return Arrays.asList(value.substring(1, value.length() - 1).split(", "));
		}

		return value;
	}

	public static ReportDataType value(final String name) {
		return ReportDataType.valueOf(name.toUpperCase());
	}
}
