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
 * Default implementation of {@link FulfillmentLocationResolver} that stores a
 * single Address. Useful for businesses that do not have a complicated warehouse solution
 * and fulfill from a single location.
 * 
 * @author Phillip Verheyden
 */
public class SimpleFulfillmentLocationResolver implements FulfillmentLocationResolver {

    protected Address address;

    @Override
    public Address resolveLocationForFulfillmentGroup(FulfillmentGroup group) {
        return address;
    }

    public Address getAddress() {
        return address;
    }
    
    public void setAddress(Address address) {
        this.address = address;
    }

}
