/*
 * #%L
 * broadleaf-multitenant-singleschema
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

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.core.offer.weave.LegacyOfferUses;
import org.hibernate.annotations.Parent;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Holds the backwards compatibility field for uses. Also hold the backwards compatibility mutator implementations
 * for maxUses.
 *
 * @author Jeff Fischer
 */
@Embeddable
public class LegacyOfferUsesImpl implements LegacyOfferUses, MultiTenantCloneable<LegacyOfferUses> {

    @Parent
    protected Offer offer;

    @Column(name = "USES")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Current_Uses", visibility = VisibilityEnum.HIDDEN_ALL)
    protected int uses;
    
    @Column(name = "APPLY_OFFER_TO_MARKED_ITEMS")
    @AdminPresentation(excluded = true)
    @Deprecated
    protected boolean applyDiscountToMarkedItems;    

    @Override
    public void setUses(int uses) {
        this.uses = uses;
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public int getMaxUses() {
        return offer.getMaxUsesPerOrder();
    }

    @Override
    public void setMaxUses(int maxUses) {
        offer.setMaxUsesPerOrder(maxUses);
    }
    
    @Override
    @Deprecated
    public boolean isApplyDiscountToMarkedItems() {
        return applyDiscountToMarkedItems;
    }

    @Deprecated
    public boolean getApplyDiscountToMarkedItems() {
        return applyDiscountToMarkedItems;
    }
    
    @Override
    @Deprecated
    public void setApplyDiscountToMarkedItems(boolean applyDiscountToMarkedItems) {
        this.applyDiscountToMarkedItems = applyDiscountToMarkedItems;
    }

    

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LegacyOfferUsesImpl)) return false;

        LegacyOfferUsesImpl that = (LegacyOfferUsesImpl) o;

        if (uses != that.uses) return false;
        return offer.equals(that.offer);

    }

    @Override
    public int hashCode() {
        int result = offer.hashCode();
        result = 31 * result + uses;
        return result;
    }

    public <G extends LegacyOfferUses> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context)
            throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        LegacyOfferUses clone = createResponse.getClone();
        clone.setUses(uses);
        return createResponse;
    }
}
