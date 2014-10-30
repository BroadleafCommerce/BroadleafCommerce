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
