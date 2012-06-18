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

package org.broadleafcommerce.core.web.api.wrapper;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Sku;

/**
 * This is a JAXB wrapper to wrap Sku.
 * <p/>
 * User: Kelly Tisdell
 * Date: 4/10/12
 */
@XmlRootElement(name = "sku")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class SkuWrapper extends BaseWrapper implements APIWrapper<Sku> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected Date activeStartDate;

    @XmlElement
    protected Date activeEndDate;

    @XmlElement
    protected String name;

    @XmlElement
    protected String description;

    @XmlElement
    protected Money retailPrice;
    
    @XmlElement
    protected Money salePrice;
    
    @XmlElement
    protected WeightWrapper weight;

    @XmlElement
    protected DimensionWrapper dimension;
    
    @Override
    public void wrap(Sku model, HttpServletRequest request) {
        this.id = model.getId();
        this.activeStartDate = model.getActiveStartDate();
        this.activeEndDate = model.getActiveEndDate();
        this.name = model.getName();
        this.description = model.getDescription();
        this.retailPrice = model.getRetailPrice();
        this.salePrice = model.getSalePrice();

        if (model.getWeight() != null){
            weight = (WeightWrapper)context.getBean(WeightWrapper.class.getName());
            weight.wrap(model.getWeight(), request);
        }

        if (model.getDimension() != null){
            dimension = (DimensionWrapper)context.getBean(DimensionWrapper.class.getName());
            dimension.wrap(model.getDimension(), request);
        }
    }
}
