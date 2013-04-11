package org.broadleafcommerce.openadmin.server.service.persistence.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * @author Jeff Fischer
 */
public interface DataFormatProvider {

    SimpleDateFormat getSimpleDateFormatter();

    DecimalFormat getDecimalFormatter();
}
