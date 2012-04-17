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

import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around OrderItem.
 * For simplicity and most use cases, this wrapper only serializes attributes of <code>DiscreteOrderItem</code>
 * This wrapper should be extended for BundledOrderItems etc...
 *
 * User: Elbert Bautista
 * Date: 4/10/12
 */
@XmlRootElement(name = "orderItem")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class OrderItemWrapper extends BaseWrapper implements APIWrapper<OrderItem> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected String name;

    @XmlElement
    protected Integer quantity;

    @XmlElement
    protected MoneyWrapper retailPrice;

    @XmlElement
    protected MoneyWrapper salePrice;

    @XmlElement
    protected CategoryWrapper category;

    @XmlElement
    protected OrderWrapper order;

    @XmlElement
    protected SkuWrapper sku;

    @XmlElement
    protected ProductWrapper product;

    @Override
    public void wrap(OrderItem model, HttpServletRequest request) {
        this.id = model.getId();
        this.name = model.getName();
        this.quantity = model.getQuantity();

        MoneyWrapper retailPriceWrapper = (MoneyWrapper) context.getBean(MoneyWrapper.class.getName());
        retailPriceWrapper.wrap(model.getRetailPrice(), request);
        this.retailPrice = retailPriceWrapper;

        MoneyWrapper salePriceWrapper = (MoneyWrapper) context.getBean(MoneyWrapper.class.getName());
        salePriceWrapper.wrap(model.getSalePrice(), request);
        this.salePrice = salePriceWrapper;

        CategoryWrapper categoryWrapper = (CategoryWrapper) context.getBean(CategoryWrapper.class.getName());
        categoryWrapper.wrap(model.getCategory(), request);
        this.category = categoryWrapper;

        OrderWrapper orderWrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
        orderWrapper.wrap(model.getOrder(), request);
        this.order = orderWrapper;

        if (model instanceof DiscreteOrderItem) {
            DiscreteOrderItem doi = (DiscreteOrderItem) model;

            SkuWrapper skuWrapper = (SkuWrapper) context.getBean(SkuWrapper.class.getName());
            skuWrapper.wrap(doi.getSku(), request);
            this.sku = skuWrapper;

            ProductWrapper productWrapper = (ProductWrapper) context.getBean(ProductWrapper.class.getName());
            productWrapper.wrap(doi.getProduct(), request);
            this.product = productWrapper;
        }
    }
}
