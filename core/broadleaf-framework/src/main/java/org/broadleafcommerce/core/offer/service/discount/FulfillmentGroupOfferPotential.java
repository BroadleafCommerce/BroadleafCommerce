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
package org.broadleafcommerce.core.offer.service.discount;

import org.broadleafcommerce.common.money.BankersRounding;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.Offer;

/**
 * 
 * @author jfischer
 *
 */
public class FulfillmentGroupOfferPotential {
    
    protected Offer offer;
    protected Money totalSavings = new Money(BankersRounding.zeroAmount());
    protected int priority;
    
    public Offer getOffer() {
        return offer;
    }
    
    public void setOffer(Offer offer) {
        this.offer = offer;
    }
    
    public Money getTotalSavings() {
        return totalSavings;
    }
    
    public void setTotalSavings(Money totalSavings) {
        this.totalSavings = totalSavings;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((offer == null) ? 0 : offer.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        FulfillmentGroupOfferPotential other = (FulfillmentGroupOfferPotential) obj;
        if (offer == null) {
            if (other.offer != null) {
                return false;
            }
        } else if (!offer.equals(other.offer)) {
            return false;
        }
        return true;
    }

}
