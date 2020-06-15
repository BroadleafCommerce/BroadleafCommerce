/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2020 Broadleaf Commerce
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
package org.broadleafcommerce.core.purge;

import org.springframework.stereotype.Component;

/**
 * Default specification for the customer purge.  
 * 
 * @author dcolgrove
 *
 */
@Component("blAnonymousCustomerPurgeSpec")
public class DefaultAnonymousCustomerPurgeSpec implements SpecDefinition {

    private PurgeSpecification customerPurgeSpec;
    
    public DefaultAnonymousCustomerPurgeSpec() {
        //Defines the table relationships traversed from the Customer
        customerPurgeSpec = new PurgeSpecification("blc_customer", "customer_id")
            .addTableRefToRoot("blc_customer_attribute", "customer_attribute_id", "customer_id")
            .newRoot()
                .addTableRefToRoot("blc_customer_address", "customer_address_id", "customer_id")
                .addTableRefFromRoot("blc_address", "address_id", "address_id")
            .endRoot()
            .addTableRefToRoot("blc_customer_role", "customer_role_id", "customer_id");
    }

    
    @Override
    public PurgeSpecification getPurgeSpecification() {
        return customerPurgeSpec;
    }

    
    @Override
    public void setPurgeSpecification(PurgeSpecification customerPurgeSpec) {
        this.customerPurgeSpec = customerPurgeSpec;
    }

}
