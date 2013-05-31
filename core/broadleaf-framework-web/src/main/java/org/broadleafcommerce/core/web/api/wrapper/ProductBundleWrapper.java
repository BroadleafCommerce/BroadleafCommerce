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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.SkuBundleItem;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "productBundle")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ProductBundleWrapper extends ProductWrapper {

    @XmlElement
    protected Integer priority;

    @XmlElement
    protected Money bundleItemsRetailPrice;
    
    @XmlElement
    protected Money bundleItemsSalePrice;
    
    @XmlElement(name="skuBundleItem")
    @XmlElementWrapper(name="skuBundleItems")
    protected List<SkuBundleItemWrapper> skuBundleItems;

    @Override
    public void wrap(Product model, HttpServletRequest request) {
        if (model instanceof ProductBundle) {
            super.wrap(model, request);
            ProductBundle bundle = (ProductBundle)model;
            this.priority = bundle.getPriority();
            this.bundleItemsRetailPrice = bundle.getBundleItemsRetailPrice();
            this.bundleItemsSalePrice = bundle.getBundleItemsSalePrice();
            
            if (bundle.getSkuBundleItems() != null) {
                this.skuBundleItems = new ArrayList<SkuBundleItemWrapper>();
                List<SkuBundleItem> bundleItems = bundle.getSkuBundleItems();
                for (SkuBundleItem item : bundleItems) {
                    SkuBundleItemWrapper skuBundleItemsWrapper = (SkuBundleItemWrapper)context.getBean(SkuBundleItemWrapper.class.getName());
                    skuBundleItemsWrapper.wrap(item, request);
                    this.skuBundleItems.add(skuBundleItemsWrapper);
                }
            }
        } else {
            throw new IllegalArgumentException("ProductBundleWrapper could not wrap Product type: " + model.getClass().getName());
        }
    }
    
    
}
