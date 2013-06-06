package org.broadleafcommerce.core.offer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.service.processor.OfferTimeZoneProcessor;
import org.broadleafcommerce.core.offer.service.processor.OfferTimeZoneProcessorImpl;

import java.util.TimeZone;

/**
 * @author Jeff Fischer
 */
public class TestOfferTimeZoneProcessorImpl implements OfferTimeZoneProcessor {

    private static final Log LOG = LogFactory.getLog(OfferTimeZoneProcessorImpl.class);

    public TimeZone getTimeZone(Offer offer) {
        return TimeZone.getTimeZone("CST");
    }

}
