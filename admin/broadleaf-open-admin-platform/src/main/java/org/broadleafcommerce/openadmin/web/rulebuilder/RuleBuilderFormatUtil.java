package org.broadleafcommerce.openadmin.web.rulebuilder;

import org.broadleafcommerce.common.util.FormatUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Work with dates in rule builder mvel
 *
 * @author Jeff Fischer
 */
public class RuleBuilderFormatUtil {

    public static final String COMPATIBILITY_FORMAT = "MM/dd/yy HH:mm a Z";
    public static final String DATE_FORMAT = "MM/dd/yyyy HH:mm";

    /**
     * Prepare date for display in the admin
     *
     * @param date the date to convert
     * @return the string value to show in the admin
     */
    public static String formatDate(Date date) {
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }

    /**
     * Parse the string value of the date stored in mvel
     *
     * @param date the mvel date value
     * @return the parsed Date instance
     */
    public static Date parseDate(String date) throws ParseException {
        Date parsedDate;
        try {
            parsedDate = FormatUtil.getDateFormat().parse(date);
        } catch (ParseException e) {
            try {
                parsedDate = new SimpleDateFormat(COMPATIBILITY_FORMAT).parse(date);
            } catch (ParseException e1) {
                try {
                    parsedDate = new SimpleDateFormat(DATE_FORMAT).parse(date);
                } catch (ParseException e2) {
                    throw e;
                }
            }
        }
        return parsedDate;
    }
}
