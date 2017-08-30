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


import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * Represents a tier and amount combination for an offer.   For example, an offer might allow a
 * 10% off if a user purchases 1 -5 but then allow 15% off if they purchase more than 5.    
 * @author bpolster
 *
 */
public interface OfferTier extends Comparable<OfferTier>, Serializable, MultiTenantCloneable<OfferTier> {

    /**
     * Returns the unique id of the offer tier.
     * @return
     */
    Long getId();

    /**
     * Sets the id of the offer tier.
     * @param id
     */
    void setId(Long id);

    /**
     * Returns the amount of the offer.   The amount could be a percentage, fixed price, or amount-off
     * depending on the parent {@link Offer#getDiscountType()}
     * @return
     */
    BigDecimal getAmount();

    /**
     * Sets the amount of the tier.
     * @param amount
     */
    void setAmount(BigDecimal amount);

    /**
     * The minimum number needed to qualify for this tier.
     * @return
     */
    Long getMinQuantity();


    /**
     * Sets the minimum number need to qualify for this tier.
     * @param minQuantity
     */
    void setMinQuantity(Long minQuantity);

    /**
     * Returns the associated offer.
     * @return
     */
    Offer getOffer();

    /**
     * Sets the associated offer.
     * @param offer
     */
    void setOffer(Offer offer);

}
