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
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.util.WeightUnitOfMeasureType;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author jfischer
 *
 */
@Embeddable
public class Weight implements Serializable, MultiTenantCloneable<Weight> {

    private static final long serialVersionUID = 1L;

    @Column(name = "WEIGHT")
    @AdminPresentation(friendlyName = "ProductWeight_Product_Weight",
        group = SkuAdminPresentation.GroupName.ShippingFulfillment, order = SkuAdminPresentation.FieldOrder.WEIGHT)
    protected BigDecimal weight;

        
    @Column(name = "WEIGHT_UNIT_OF_MEASURE")
    @AdminPresentation(friendlyName = "ProductWeight_Product_Weight_Units",
        group = SkuAdminPresentation.GroupName.ShippingFulfillment, order = SkuAdminPresentation.FieldOrder.WEIGHT_UNIT_OF_MEASURE,
        fieldType= SupportedFieldType.BROADLEAF_ENUMERATION, 
        broadleafEnumeration="org.broadleafcommerce.common.util.WeightUnitOfMeasureType",
        defaultValue = "KILOGRAMS")
    protected String weightUnitOfMeasure;

    public WeightUnitOfMeasureType getWeightUnitOfMeasure() {
        return WeightUnitOfMeasureType.getInstance(weightUnitOfMeasure);
    }

    public void setWeightUnitOfMeasure(WeightUnitOfMeasureType weightUnitOfMeasure) {
        if (weightUnitOfMeasure != null) {
            this.weightUnitOfMeasure = weightUnitOfMeasure.getType();
        }
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    @Override
    public <G extends Weight> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        Weight clone = createResponse.getClone();
        clone.setWeight(weight);
        if (weightUnitOfMeasure != null) {
            clone.setWeightUnitOfMeasure(getWeightUnitOfMeasure());
        }
        return createResponse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) return false;

        Weight weight1 = (Weight) o;

        if (weight != null ? !weight.equals(weight1.weight) : weight1.weight != null) return false;
        if (weightUnitOfMeasure != null ? !weightUnitOfMeasure.equals(weight1.weightUnitOfMeasure) : weight1
                .weightUnitOfMeasure != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = weight != null ? weight.hashCode() : 0;
        result = 31 * result + (weightUnitOfMeasure != null ? weightUnitOfMeasure.hashCode() : 0);
        return result;
    }
}
