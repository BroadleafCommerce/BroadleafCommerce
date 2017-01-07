/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.core.util.service.type;

/**
 * @author Jeff Fischer
 */
public enum PurgeCustomerVariableNames {
    IS_REGISTERED //looking for registered or anonymous customers
    ,IS_DEACTIVATED //looking for active or inactive customers
    ,SECONDS_OLD //looking for customers older than this
    ,IS_PREVIEW //looking for customers that are marked as preview (generally only meaningful in an enterprise context)
    ,SITE //looking for customers that belong to a particular site (generally only meaningful in an multi-tenant context)
    ,BATCH_SIZE //the max size of the purge batch (null results in the batch size matching the number of qualified customers to purge)
    ,RETRY_FAILED_SECONDS //the number of seconds that a failed purge should be ignored before being retrying
}
