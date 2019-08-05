package vn.ontaxi.common.utils;

import vn.ontaxi.common.jpa.entity.Customer;

import java.util.HashMap;

public class EmailUtils {
    public static String getEmailContentCustomizedForCustomer(String originalContent, Customer customer) {
        return vn.ontaxi.common.utils.StringUtils.fillRegexParams(originalContent, new HashMap<String, String>() {{
            put("\\$\\{name\\}", customer.getName());
        }});
    }
}
