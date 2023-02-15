/*-
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.controller.account;

import javax.annotation.Resource;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.order.service.OrderService;


/**
 * An abstract controller that provides convenience methods and resource declarations for its children. 
 * 
 * Operations that are shared between controllers that deal with the cart belong here.
 * 
 * @author apazzolini
 */
public abstract class AbstractAccountController extends BroadleafAbstractController {
    
    @Resource(name="blOrderService")
    protected OrderService orderService;

    @Resource(name="blCatalogService")
    protected CatalogService catalogService;
    
}
