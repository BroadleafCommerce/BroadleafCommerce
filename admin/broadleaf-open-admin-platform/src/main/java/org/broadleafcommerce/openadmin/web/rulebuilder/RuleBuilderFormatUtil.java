package org.broadleafcommerce.openadmin.web.rulebuilder;

import java.text.SimpleDateFormat;

/**
 * @author Jeff Fischer
 */
public class RuleBuilderFormatUtil {

    public static final String DATE_FORMAT = "MM/dd/yyyy HH:mm";

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat(DATE_FORMAT);
    }
}
