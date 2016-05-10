/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.web;

/**
 * Defines the state in which sandboxable collections in the Enterprise module should adhere to Broadleaf defined behavior.
 * When FALSE, {@link org.hibernate.collection.spi.PersistentCollection} extensions in the Enterprise module will delegate
 * to the standard Hibernate behavior. This is useful when the desire is to build and persist entity object structures (that
 * the Enterprise module would otherwise interpret as sandboxable) without interference from the Enterprise module
 * on the collection persistence behavior. When the Enterprise module is loaded, the behavior is enforced by default.
 *
 * @author Jeff Fischer
 */
public enum EnforceEnterpriseCollectionBehaviorState {
    TRUE,FALSE,UNDEFINED
}
