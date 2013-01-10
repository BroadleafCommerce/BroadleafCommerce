/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.web.api.wrapper;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.SkuBundleItem;

@XmlRootElement(name = "skuBundleItem")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class SkuBundleItemWrapper extends BaseWrapper implements APIWrapper<SkuBundleItem> {
    
    @XmlElement
    protected Long id;
    
    @XmlElement
    protected Integer quantity;
    
    @XmlElement
    protected Money salePrice;
    
    @XmlElement
    protected Money retailPrice;
    
    @XmlElement
    protected Long bundleId;
    
    @XmlElement
    protected SkuWrapper sku;
    
    @Override
    public void wrap(SkuBundleItem model, HttpServletRequest request) {
        this.id = model.getId();
        this.quantity = model.getQuantity();
        this.salePrice = model.getSalePrice();
        this.retailPrice = model.getRetailPrice();
        this.bundleId = model.getBundle().getId();
        this.sku = (SkuWrapper)context.getBean(SkuWrapper.class.getName());
        this.sku.wrap(model.getSku(), request);
    }

}
