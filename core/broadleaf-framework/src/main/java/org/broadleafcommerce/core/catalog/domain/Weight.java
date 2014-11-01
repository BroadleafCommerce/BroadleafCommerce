/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.catalog.domain;

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
public class Weight implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "WEIGHT")
    @AdminPresentation(friendlyName = "ProductWeight_Product_Weight", order = 8000,
        tab = ProductImpl.Presentation.Tab.Name.Shipping, tabOrder = ProductImpl.Presentation.Tab.Order.Shipping,
        group = ProductImpl.Presentation.Group.Name.Shipping, groupOrder = ProductImpl.Presentation.Group.Order.Shipping)
    protected BigDecimal weight;

        
    @Column(name = "WEIGHT_UNIT_OF_MEASURE")
    @AdminPresentation(friendlyName = "ProductWeight_Product_Weight_Units", order = 9000,
        tab = ProductImpl.Presentation.Tab.Name.Shipping, tabOrder = ProductImpl.Presentation.Tab.Order.Shipping,
        group = ProductImpl.Presentation.Group.Name.Shipping, groupOrder = ProductImpl.Presentation.Group.Order.Shipping,
        fieldType= SupportedFieldType.BROADLEAF_ENUMERATION, 
        broadleafEnumeration="org.broadleafcommerce.common.util.WeightUnitOfMeasureType")
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
