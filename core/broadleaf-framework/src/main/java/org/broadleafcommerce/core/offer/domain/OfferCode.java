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
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.core.order.domain.Order;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public interface OfferCode extends Status, Serializable, MultiTenantCloneable<OfferCode> {

    public Long getId() ;

    public void setId(Long id) ;

    public Offer getOffer() ;

    public void setOffer(Offer offer) ;

    public String getOfferCode();

    public void setOfferCode(String offerCode);

    public Date getStartDate();

    public void setStartDate(Date startDate);

    public Date getEndDate();

    public void setEndDate(Date endDate);

    /**
     * Returns the maximum number of times that this code can be used regardless of Order or Customer
     *
     * 0 indicates unlimited usage.
     *
     * @return
     */
    public int getMaxUses();

    /**
     * Sets the maximum number of times that this code can be used regardless of Order or Customer
     *
     * 0 indicates unlimited usage.
     *
     * @param maxUses
     */
    public void setMaxUses(int maxUses);

    public String getEmailAddress();

    public void setEmailAddress(String emailAddress);

    /**
     * Indicates that this is an unlimited-use code. By default this is true if {@link #getMaxUses()} == 0
     */
    public boolean isUnlimitedUse();
    
    /**
     * Indicates that this code has a limit on how many times it can be used. By default this is true if {@link #getMaxUses()} > 0
     */
    public boolean isLimitedUse();
    
    /**
     * @deprecated replaced by the {@link OfferAudit} table
     */
    @Deprecated
    public int getUses() ;

    /**
     * @deprecated replaced by the {@link OfferAudit} table
     */
    @Deprecated
    public void setUses(int uses);

    public List<Order> getOrders();

    public void setOrders(List<Order> orders);

}
