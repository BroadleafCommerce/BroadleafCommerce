/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
public enum FieldProviderResponse {
    HANDLED,NOT_HANDLED,HANDLED_BREAK
}
