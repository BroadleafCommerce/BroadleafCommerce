/*
 * #%L
 * BroadleafCommerce Framework Web
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

package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.common.util.WeightUnitOfMeasureType;
import org.broadleafcommerce.core.catalog.domain.Weight;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around Weight
 * <p/>
 * User: Kelly Tisdell
 * Date: 4/10/12
 */
@XmlRootElement(name = "weight")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class WeightWrapper implements APIWrapper<Weight>, APIUnwrapper<Weight> {

    @XmlElement
    protected BigDecimal weight;

    @XmlElement
    protected String unitOfMeasure;

    @Override
    public void wrapDetails(Weight model, HttpServletRequest request) {
        this.weight = model.getWeight();
        if (model.getWeightUnitOfMeasure() != null) {
            this.unitOfMeasure = model.getWeightUnitOfMeasure().getType();
        }
    }

    @Override
    public void wrapSummary(Weight model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    @Override
    public Weight unwrap(HttpServletRequest request, ApplicationContext context) {
        Weight wei = new Weight();
        wei.setWeight(this.weight);
        wei.setWeightUnitOfMeasure(WeightUnitOfMeasureType.getInstance(this.unitOfMeasure));
        return wei;
    }
}
