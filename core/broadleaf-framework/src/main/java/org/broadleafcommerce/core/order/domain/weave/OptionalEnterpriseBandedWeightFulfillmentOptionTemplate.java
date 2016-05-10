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
package org.broadleafcommerce.core.order.domain.weave;

import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.core.order.fulfillment.domain.FulfillmentWeightBand;
import org.broadleafcommerce.core.order.fulfillment.domain.FulfillmentWeightBandImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;

/**
 * This class is meant as a template to provide overriding of the annotations on fields in 
 * <code>org.broadleafcommerce.core.order.fulfillment.domain.BandedWeightFulfillmentOptionImpl</code>.  This provides a 
 * stop gap measure to allow someone to weave in the appropriate annotations in 4.0.x without forcing a schema change on those 
 * who prefer not to use it.  This should likely be removed in 4.1 for fixed annotations on the entity itself.
 * 
 * See 
 * 
 * @author Kelly Tisdell
 *
 */
@Deprecated
public abstract class OptionalEnterpriseBandedWeightFulfillmentOptionTemplate {

    @OneToMany(mappedBy = "option", targetEntity = FulfillmentWeightBandImpl.class)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
    @AdminPresentationCollection(friendlyName = "BandedWeightFulfillmentOptionBands", excluded = true)
    protected List<FulfillmentWeightBand> bands = new ArrayList<FulfillmentWeightBand>();

}
