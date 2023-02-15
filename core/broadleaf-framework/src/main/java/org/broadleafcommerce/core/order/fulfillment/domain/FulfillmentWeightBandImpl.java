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
package org.broadleafcommerce.core.order.fulfillment.domain;

import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.util.WeightUnitOfMeasureType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * @author Phillip Verheyden
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULFILLMENT_WEIGHT_BAND")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blFulfillmentOptionElements")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX)
})
public class FulfillmentWeightBandImpl extends FulfillmentBandImpl implements FulfillmentWeightBand {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "FulfillmentWeightBandId")
    @GenericGenerator(
        name="FulfillmentWeightBandId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="FulfillmentWeightBandImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.order.fulfillment.domain.FulfillmentWeightBandImpl")
        }
    )
    @Column(name = "FULFILLMENT_WEIGHT_BAND_ID")
    protected Long id;
    
    @Column(name = "MINIMUM_WEIGHT", precision = 19, scale = 5)
    @AdminPresentation(friendlyName = "FulfillmentWeightBandImpl_Weight")
    protected BigDecimal minimumWeight;
    
    @Column(name = "WEIGHT_UNIT_OF_MEASURE")
    @AdminPresentation(friendlyName = "FulfillmentWeightBandImpl_Weight_Units", fieldType= SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.common.util.WeightUnitOfMeasureType")
    protected String weightUnitOfMeasure;
    
    @ManyToOne(targetEntity=BandedWeightFulfillmentOptionImpl.class)
    @JoinColumn(name="FULFILLMENT_OPTION_ID")
    protected BandedWeightFulfillmentOption option;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public BigDecimal getMinimumWeight() {
        return minimumWeight;
    }
    
    @Override
    public void setMinimumWeight(BigDecimal minimumWeight) {
        this.minimumWeight = minimumWeight;
    }
    
    @Override
    public BandedWeightFulfillmentOption getOption() {
        return option;
    }

    @Override
    public void setOption(BandedWeightFulfillmentOption option) {
        this.option = option;
    }

    @Override
    public WeightUnitOfMeasureType getWeightUnitOfMeasure() {
        return WeightUnitOfMeasureType.getInstance(weightUnitOfMeasure);
    }

    @Override
    public void setWeightUnitOfMeasure(WeightUnitOfMeasureType weightUnitOfMeasure) {
        if (weightUnitOfMeasure != null) {
            this.weightUnitOfMeasure = weightUnitOfMeasure.getType();
        }
    }

}
