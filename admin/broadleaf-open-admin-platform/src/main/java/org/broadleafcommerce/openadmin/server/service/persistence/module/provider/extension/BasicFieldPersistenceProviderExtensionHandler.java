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
package org.broadleafcommerce.openadmin.server.service.persistence.module.provider.extension;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;

import java.io.Serializable;

/**
 * For internal usage. Allows extending API calls without subclassing the entity.
 *
 * @author Jeff Fischer
 */
public interface BasicFieldPersistenceProviderExtensionHandler extends ExtensionHandler {

    ExtensionResultStatusType transformId(ExtractValueRequest request, Property property, ExtensionResultHolder<Serializable> resultHolder);

    ExtensionResultStatusType transformForeignKey(ExtractValueRequest request, Property property, ExtensionResultHolder<Serializable> resultHolder);

    public static final int DEFAULT_PRIORITY = Integer.MAX_VALUE;

}
