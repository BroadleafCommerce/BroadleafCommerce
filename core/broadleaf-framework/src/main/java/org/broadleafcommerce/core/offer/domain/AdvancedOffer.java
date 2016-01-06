/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    public OfferTimeZoneType getOfferTimeZoneType();

    /**
     * Returns the {@link OfferTimeZoneType}
     * @param offerTimeZoneType
     */
    public void setOfferTimeZoneType(OfferTimeZoneType offerTimeZoneType);


}
