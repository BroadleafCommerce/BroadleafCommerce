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
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
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

    //These will be populated only if this is a DiscreteOrderItem
    @XmlElement
    protected Money retailPrice;

    @XmlElement
    protected Money salePrice;
    //

    //These will be populated only if this is a BundleOrderItem
    @XmlElement
    protected Money bundleRetailPrice;

    @XmlElement
    protected Money bundleSalePrice;
    //

    @XmlElement
    protected CategorySummaryWrapper category;

    @XmlElement
    protected Long orderId;

    @XmlElement
    protected SkuWrapper sku;

    @XmlElement
    protected ProductSummaryWrapper product;
    
    @XmlElement(name = "orderItemAttribute")
    @XmlElementWrapper(name = "orderItemAttributes")
    protected List<OrderItemAttributeWrapper> orderItemAttributes;
    
    @XmlElement(name = "orderItemPriceDetails")
    @XmlElementWrapper(name = "orderItemPriceDetails")
    protected List<OrderItemPriceDetailWrapper> orderItemPriceDetails;

    //This will only be poulated if this is a BundleOrderItem
    @XmlElement(name = "bundleItem")
    @XmlElementWrapper(name = "bundleItems")
    protected List<OrderItemWrapper> bundleItems;
    //

    @Override
    public void wrap(OrderItem model, HttpServletRequest request) {
        this.id = model.getId();
        this.name = model.getName();
        this.quantity = model.getQuantity();

        if (model.getCategory() != null) {
            CategorySummaryWrapper categoryWrapper = (CategorySummaryWrapper) context.getBean(CategorySummaryWrapper.class.getName());
            categoryWrapper.wrap(model.getCategory(), request);
            this.category = categoryWrapper;
        }

        this.orderId = model.getOrder().getId();
        
        Map<String, OrderItemAttribute> itemAttributes = model.getOrderItemAttributes();
        if (itemAttributes != null && ! itemAttributes.isEmpty()) {
            this.orderItemAttributes = new ArrayList<OrderItemAttributeWrapper>();
            Set<String> keys = itemAttributes.keySet();
            for (String key : keys) {
                OrderItemAttributeWrapper orderItemAttributeWrapper = 
                        (OrderItemAttributeWrapper) context.getBean(OrderItemAttributeWrapper.class.getName());
                orderItemAttributeWrapper.wrap(itemAttributes.get(key), request);
                this.orderItemAttributes.add(orderItemAttributeWrapper);
            }
        }
        if (model.getOrderItemPriceDetails() != null && !model.getOrderItemPriceDetails().isEmpty()) {
            this.orderItemPriceDetails = new ArrayList<OrderItemPriceDetailWrapper>();
            for (OrderItemPriceDetail orderItemPriceDetail : model.getOrderItemPriceDetails()) {
                OrderItemPriceDetailWrapper orderItemPriceDetailWrapper =
                        (OrderItemPriceDetailWrapper) context.getBean(OrderItemPriceDetailWrapper.class.getName());
                orderItemPriceDetailWrapper.wrap(orderItemPriceDetail, request);
                this.orderItemPriceDetails.add(orderItemPriceDetailWrapper);
            }
        }
        
        if (model instanceof DiscreteOrderItem) {
            this.retailPrice = model.getRetailPrice();
            this.salePrice = model.getSalePrice();
            DiscreteOrderItem doi = (DiscreteOrderItem) model;

            SkuWrapper skuWrapper = (SkuWrapper) context.getBean(SkuWrapper.class.getName());
            skuWrapper.wrap(doi.getSku(), request);
            this.sku = skuWrapper;
            
            ProductSummaryWrapper productWrapper = (ProductSummaryWrapper) context.getBean(ProductSummaryWrapper.class.getName());
            productWrapper.wrap(doi.getProduct(), request);
            this.product = productWrapper;
        } else if (model instanceof BundleOrderItem) {
            BundleOrderItem boi = (BundleOrderItem) model;
            this.bundleRetailPrice = boi.getRetailPrice();
            this.bundleSalePrice = boi.getSalePrice();
            SkuWrapper skuWrapper = (SkuWrapper) context.getBean(SkuWrapper.class.getName());
            skuWrapper.wrap(boi.getSku(), request);
            this.sku = skuWrapper;

            //Wrap up all the discrete order items for this bundle order item
            List<DiscreteOrderItem> discreteItems = boi.getDiscreteOrderItems();
            if (discreteItems != null && !discreteItems.isEmpty()) {
                this.bundleItems = new ArrayList<OrderItemWrapper>();
                for (DiscreteOrderItem doi : discreteItems) {
                    OrderItemWrapper doiWrapper = (OrderItemWrapper) context.getBean(OrderItemWrapper.class.getName());
                    doiWrapper.wrap(doi, request);
                    this.bundleItems.add(doiWrapper);
                }
            }

            ProductBundleSummaryWrapper productWrapper = (ProductBundleSummaryWrapper) context.getBean(ProductBundleSummaryWrapper.class.getName());
            productWrapper.wrap(boi.getProduct(), request);
            this.product = productWrapper;
        }
    }
}
