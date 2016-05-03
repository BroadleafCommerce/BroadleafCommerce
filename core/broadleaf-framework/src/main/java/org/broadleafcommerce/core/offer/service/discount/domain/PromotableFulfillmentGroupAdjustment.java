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
package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.common.money.Money;

import java.io.Serializable;

/**
 * This class holds adjustment records during the discount calculation 
 * processing.  This and other disposable objects avoid churn on the database while the 
 * offer engine determines the best offer(s) for the order being priced.
 * 
 * @author bpolster
 */
public interface PromotableFulfillmentGroupAdjustment extends Serializable {

    /**
     * Returns the associated promotableFulfillmentGroup
     * @return
     */
    public PromotableFulfillmentGroup getPromotableFulfillmentGroup();

    /**
     * Returns the associated promotableCandidateOrderOffer
     * @return
     */
    public PromotableCandidateFulfillmentGroupOffer getPromotableCandidateFulfillmentGroupOffer();

    /**
     * Returns the value of this adjustment 
     * @return
     */
    public Money getSaleAdjustmentValue();

    /**
     * Returns the value of this adjustment 
     * @return
     */
    public Money getRetailAdjustmentValue();

    /**
     * Returns the value of this adjustment 
     * @return
     */
    public Money getAdjustmentValue();

    /**
     * Returns true if this adjustment represents a combinable offer.
     */
    boolean isCombinable();

    /**
     * Returns true if this adjustment represents a totalitarian offer.   
     */
    boolean isTotalitarian();
    
    /**
     * Updates the adjustmentValue to the sales or retail value based on the passed in param
     */
    void finalizeAdjustment(boolean useSaleAdjustments);

    boolean isAppliedToSalePrice();
}
