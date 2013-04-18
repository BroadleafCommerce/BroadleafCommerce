package org.broadleafcommerce.common.util;

import java.text.SimpleDateFormat;

/**
 * @author Jeff Fischer
 */
public class FormatUtil {

    public static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss Z";

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat(DATE_FORMAT);
    }
}
