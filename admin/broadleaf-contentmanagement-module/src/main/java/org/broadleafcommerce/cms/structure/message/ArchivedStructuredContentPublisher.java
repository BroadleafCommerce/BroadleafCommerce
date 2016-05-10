/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.structure.message;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;

/**
 * The ArchivedStructuredContentPublisher will be notified when a StructuredContent item has
 * been marked as archived.    This provides a convenient cache-eviction
 * point for items in production.
 *
 * Implementers of this service could send a JMS or AMQP message so
 * that other VMs can evict the item.
 *
 * Created by bpolster.
 */
public interface ArchivedStructuredContentPublisher {
    void processStructuredContentArchive(StructuredContent sc, String baseTypeKey, String baseNameKey);
}
