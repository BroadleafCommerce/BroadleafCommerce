/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.service.type;

/**
 * <p>For {@link org.broadleafcommerce.openadmin.server.dao.provider.metadata.FieldMetadataProvider} and
 * {@link org.broadleafcommerce.openadmin.server.service.persistence.module.provider.FieldPersistenceProvider}, message
 * the system on how it should interpret the provider's handling of the call. If HANDLED, then the system will consider
 * that a valid provider was found for the request, and subsequently not call the default provider. If all registered
 * providers respond with NOT_HANDLED, then the default provider is called. If HANDLED_BREAK is returned, then the
 * provider loop is immediately exited and the default provider is not called.</p>
 *
 * <p>In combination with the {@link org.springframework.core.Ordered}, this provides a way for a provider to either override
 * existing behavior by setting a low order and returning HANDLED_BREAK, or add to behavior by setting any order and
 * returning HANDLED.</p>
 *
 * @author Jeff Fischer
 */
public enum MetadataProviderResponse {
    HANDLED,NOT_HANDLED,HANDLED_BREAK
}
