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
package org.broadleafcommerce.core.order.fulfillment.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.core.order.domain.FulfillmentOptionImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 
 * @author Phillip Verheyden
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULFILLMENT_OPT_BANDED_WGT")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(friendlyName = "Banded Weight Fulfillment Option")
public class BandedWeightFulfillmentOptionImpl extends FulfillmentOptionImpl implements BandedWeightFulfillmentOption {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy="option", targetEntity=FulfillmentWeightBandImpl.class)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @AdminPresentationCollection(friendlyName = "BandedWeightFulfillmentOptionBands", excluded = true)
    protected List<FulfillmentWeightBand> bands = new ArrayList<FulfillmentWeightBand>();

    @Override
    public List<FulfillmentWeightBand> getBands() {
        return bands;
    }

    @Override
    public void setBands(List<FulfillmentWeightBand> bands) {
        this.bands = bands;
    }

    @Override
    public CreateResponse<BandedWeightFulfillmentOption> createOrRetrieveCopyInstance(MultiTenantCopyContext context)
            throws CloneNotSupportedException {
        CreateResponse<BandedWeightFulfillmentOption> createResponse = super.createOrRetrieveCopyInstance(context);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        BandedWeightFulfillmentOption myClone = createResponse.getClone();

        for (FulfillmentWeightBand band : bands) {
            myClone.getBands().add(band);
        }

        return createResponse;
    }
}
