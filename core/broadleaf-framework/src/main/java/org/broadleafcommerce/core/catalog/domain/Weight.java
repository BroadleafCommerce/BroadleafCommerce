/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.util.WeightUnitOfMeasureType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @author jfischer
 *
 */
@Embeddable
public class Weight implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "WEIGHT")
    @AdminPresentation(friendlyName = "ProductWeight_Product_Weight", order=17, group = "ProductWeight_Shipping",groupOrder=4)
    protected BigDecimal weight;

    @Column(name = "WEIGHT_UNIT_OF_MEASURE")
    @AdminPresentation(friendlyName = "ProductWeight_Product_Weight_Units", order=18, group = "ProductWeight_Shipping", fieldType= SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.common.util.WeightUnitOfMeasureType",groupOrder=4)
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

}
