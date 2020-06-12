/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.offer.domain;

import org.broadleafcommerce.core.offer.service.type.OfferTimeZoneType;

import java.io.Serializable;
import java.util.List;

/**
 * Add advanced offer support to an Offer
 * 
 * @author Priyesh Patel
 */
public interface AdvancedOffer extends Serializable {

    /**
     * List of Tiers an offer supports.   Implemented in external module.
     * @return
     */
    List<OfferTier> getOfferTiers();

    /**
     * Sets the list of Tiers.
     * @param offerTiers
     */
    void setOfferTiers(List<OfferTier> offerTiers);

    /**
     * Returns true if this is a tiered offer meaning that the amount depends on the
     * quantity being purchased.
     * @return
     */
    boolean isTieredOffer();

    /**
     * Sets whether or not this is a tiered offer.
     * @param isTieredOffer
     */
    void setTieredOffer(boolean isTieredOffer);
    
    /**
     * Sets the {@link OfferTimeZoneType} 
     * @return
     */
    OfferTimeZoneType getOfferTimeZoneType();

    /**
     * Returns the {@link OfferTimeZoneType}
     * @param offerTimeZoneType
     */
    void setOfferTimeZoneType(OfferTimeZoneType offerTimeZoneType);


}
