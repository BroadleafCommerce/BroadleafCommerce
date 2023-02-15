/*-
 * #%L
 * BroadleafCommerce Framework
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
