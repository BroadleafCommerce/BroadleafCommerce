/*-
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.web.resource.resolver;

import org.broadleafcommerce.common.web.resource.BroadleafResourceHttpRequestHandler;
import org.springframework.core.Ordered;

import javax.annotation.PostConstruct;

/**
 * Constants representing out of box Broadleaf Resource Transformer ordering.
 * 
 * Used by {@link BroadleafResourceHttpRequestHandler} which sorts resolvers that 
 * implement {@link Ordered} in its {@link PostConstruct} method.
 * 
 * @author bpolster
 *
 */
public class BroadleafResourceTransformerOrder {

    public static int BLC_CACHE_RESOURCE_TRANSFORMER = 1000;
    public static int BLC_MINIFY_RESOURCE_TRANSFORMER = 10000;
}
