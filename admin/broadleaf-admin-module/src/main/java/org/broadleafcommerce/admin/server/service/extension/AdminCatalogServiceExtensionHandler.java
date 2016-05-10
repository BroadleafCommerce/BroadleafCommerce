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
package org.broadleafcommerce.admin.server.service.extension;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;

import java.util.List;


/**
 * Extension handler for {@link org.broadleafcommerce.admin.server.service.AdminCatalogService}
 *
 * @author Jeff Fischer
 */
public interface AdminCatalogServiceExtensionHandler extends ExtensionHandler {

    public static final int DEFAULT_PRIORITY = 1000;

    /**
     * Customize the persistence of generated sku permutations based on product options.
     *
     * @param product
     * @param permutationsToGenerate
     * @param erh
     * @return
     */
    ExtensionResultStatusType persistSkuPermutation(Product product, List<List<ProductOptionValue>> permutationsToGenerate, ExtensionResultHolder<Integer> erh);

}
