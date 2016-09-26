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
package org.broadleafcommerce.core.store.weave;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.core.offer.domain.LegacyOfferUsesImpl;
import org.broadleafcommerce.core.store.domain.StoreImpl;

import javax.persistence.Column;
import javax.persistence.Embedded;

/**
 * This should be weaved in via property `enable.optional.store.open.field=true` when starting 5.0 with 
 * SQL Server.  This can be removed in 5.1.
 * 
 * @author Chad Harchar (charchar)
 */
public abstract class WeaveStoreOpen {

    @Column(name = "STORE_OPEN")
    @AdminPresentation(friendlyName = "StoreImpl_Open")
    protected Boolean open;
}
