/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around Product.
 *
 * User: Kelly Tisdell
 * Date: 4/10/12
 */
@XmlRootElement(name = "productOption")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ProductOptionWrapper extends BaseWrapper implements APIWrapper<ProductOption> {
    
    @XmlElement
    protected String attributeName;

    @XmlElement
    protected String label;

    @XmlElement
    protected Boolean required;
    
    @XmlElement
    protected String productOptionType;
    @XmlElement
    protected String productOptionValidationStrategyType;
    @XmlElement
    protected String productOptionValidationType;
    @XmlElement(name = "allowedValue")
    @XmlElementWrapper(name = "allowedValues")
    protected List<ProductOptionValueWrapper> allowedValues;
    @XmlElement
    protected String validationString;
    
    @Override
    public void wrapDetails(ProductOption model, HttpServletRequest request) {
        this.attributeName = "productOption." + model.getAttributeName();
        this.label = model.getLabel();
        this.required = model.getRequired();
        if (model.getType() != null) {
            this.productOptionType = model.getType().getType();
        }
        if (model.getProductOptionValidationStrategyType() != null) {
            this.productOptionValidationStrategyType = model.getProductOptionValidationStrategyType().getType();
        }
        if (model.getProductOptionValidationStrategyType() != null) {
            this.productOptionValidationType = model.getProductOptionValidationType().getType();
        }
        this.validationString = model.getValidationString();
        List<ProductOptionValue> optionValues = model.getAllowedValues();
        if (optionValues != null) {
            ArrayList<ProductOptionValueWrapper> allowedValueWrappers = new ArrayList<ProductOptionValueWrapper>();
            for (ProductOptionValue value : optionValues) {
                ProductOptionValueWrapper optionValueWrapper = (ProductOptionValueWrapper)context.getBean(ProductOptionValueWrapper.class.getName());
                optionValueWrapper.wrapSummary(value, request);
                allowedValueWrappers.add(optionValueWrapper);
            }
            this.allowedValues = allowedValueWrappers;
        }
    }

    @Override
    public void wrapSummary(ProductOption model, HttpServletRequest request) {
        wrapDetails(model, request);
    }
}
