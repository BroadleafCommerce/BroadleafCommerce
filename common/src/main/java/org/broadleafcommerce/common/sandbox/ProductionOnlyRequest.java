/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.sandbox;

import org.broadleafcommerce.common.presentation.AdminPresentationAdornedTargetCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;

/**
 * Holder for the {@link #CRITERIA} constant 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface ProductionOnlyRequest {
    
    /**
     * <p>
     * Determines that all admin persistence request should be in production and skip any workflow/sandbox processing
     *  This can be added as customCriteria to any
     * {@link PersistencePackage#getCustomCriteria()} or any of the {@link AdminPresentationCollection#customCriteria()},
     * {@link AdminPresentationAdornedTargetCollection#customCriteria()}, {@link AdminPresentationToOneLookup#customCriteria()}
     * annotations.
     * 
     * <p>
     * One use case for this is for all inventory requests in the advanced inventory module. When executing Sku lookups
     * or actually displaying the list of inventory in the admin, all of those requests should be made against production
     * records
     * 
     * <p>
     * This criteria is only utilized within the enterprise module.
     */
    public static final String CRITERIA = "PRODUCTION_ONLY_REQUEST";

}
