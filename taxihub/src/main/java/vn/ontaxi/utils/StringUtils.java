package vn.ontaxi.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class StringUtils {
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
}
