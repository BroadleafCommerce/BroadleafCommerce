package org.broadleafcommerce.common.logging;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Log4J filter that will remove any SUPPORT level
 * log messages from appearing
 *
 * @author Jeff Fischer
 */
public class SupportLevelFilter extends Filter {

    @Override
    public int decide(LoggingEvent event) {
        if(SupportLevel.SUPPORT.equals(event.getLevel())) {
            return Filter.DENY;
        }
        return Filter.ACCEPT;
    }

}
