package vn.ontaxi.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    public static String removeFileExtension(final String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String mapToURLQueryString(final Map<String, String> map) {
        final StringBuilder stringBuilder = new StringBuilder();

        for (final String key : map.keySet()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            final String value = map.get(key);
            try {
                stringBuilder.append((key != null ? URLEncoder.encode(key, "UTF-8") : ""));
                stringBuilder.append("=");
                stringBuilder.append(value != null ? URLEncoder.encode(value, "UTF-8") : "");
            }
            catch (final UnsupportedEncodingException e) {
                throw new RuntimeException("This method requires UTF-8 encoding support", e);
            }
        }

        return stringBuilder.toString();
    }

    public static String fillRegexParams(String content, Map<String, String> params) {

        for (String strPattern : params.keySet()) {
            content = content.replaceAll(strPattern, params.get(strPattern));
        }

        return content;
    }
}
