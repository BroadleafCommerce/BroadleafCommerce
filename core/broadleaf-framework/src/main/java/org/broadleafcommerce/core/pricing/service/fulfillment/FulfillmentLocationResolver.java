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
package org.broadleafcommerce.core.pricing.service.fulfillment;

import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.profile.core.domain.Address;

/**
 * This can be used by various third-party fulfillment pricing services in order to
 * resolve a location that items will be shipped from in order to properly calculate the
 * cost of fulfilling that particular fulfillment group.
 * 
 * <p>Note: the bean name in XML should be blFulfillmentLocationResolver
 * 
 * @author Phillip Verheyden
 * @see {@link SimpleFulfillmentLocationResolver}
 */
public interface FulfillmentLocationResolver {

    /**
     * This method should give an {@link Address} that a particular {@link FulfillmentGroup} will
     * be fulfilled from. Implementations could store this information in the database or integrate
     * with an existing warehouse solution.
     * 
     * @param group
     * @return the {@link Address} that <b>group</b> should be fulfilled from
     */
    public Address resolveLocationForFulfillmentGroup(FulfillmentGroup group);

}
