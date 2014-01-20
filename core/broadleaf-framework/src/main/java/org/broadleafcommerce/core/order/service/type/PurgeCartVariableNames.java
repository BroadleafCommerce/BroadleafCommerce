/*
 * #%L
 * BroadleafCommerce Workflow
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
package org.broadleafcommerce.core.order.service.type;

/**
 * @author Jeff Fischer
 */
public enum PurgeCartVariableNames {
    STATUS //looking for order with this status(es)
    ,NAME //looking for order with this name(s)
    ,SECONDS_OLD //looking for orders older than this
    ,IS_PREVIEW //looking for orders that are marked as preview (generally only meaningful in an enterprise context)
    ,SITE //looking for orders that belong to a particular site (generally only meaningful in an multi-tenant context)
}
