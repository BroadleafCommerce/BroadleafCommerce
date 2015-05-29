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
package org.broadleafcommerce.core.offer.service;

import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.i18n.domain.ISOCountryImpl;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.ProductBundleImpl;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.service.type.ProductBundlePricingModelType;
import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustmentImpl;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteriaImpl;
import org.broadleafcommerce.core.offer.domain.OfferOfferRuleXref;
import org.broadleafcommerce.core.offer.domain.OfferOfferRuleXrefImpl;
import org.broadleafcommerce.core.offer.domain.OfferQualifyingCriteriaXref;
import org.broadleafcommerce.core.offer.domain.OfferQualifyingCriteriaXrefImpl;
import org.broadleafcommerce.core.offer.domain.OfferRule;
import org.broadleafcommerce.core.offer.domain.OfferRuleImpl;
import org.broadleafcommerce.core.offer.domain.OfferTargetCriteriaXref;
import org.broadleafcommerce.core.offer.domain.OfferTargetCriteriaXrefImpl;
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustmentImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactoryImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderImpl;
import org.broadleafcommerce.core.offer.service.type.OfferDeliveryType;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;
import org.broadleafcommerce.core.offer.service.type.OfferRuleType;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.BundleOrderItemImpl;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetailImpl;
import org.broadleafcommerce.core.order.domain.OrderItemQualifier;
import org.broadleafcommerce.core.order.domain.OrderItemQualifierImpl;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.broadleafcommerce.profile.core.domain.Phone;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.domain.StateImpl;
import org.easymock.IAnswer;
import org.easymock.classextension.EasyMock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author jfischer
 *
 */
public class OfferDataItemProvider {

    public static Long orderItemId = 1L;
    public static Long orderId = 1L;
    public static Long offerId = 1L;
    
    public static Long getOfferId() {
        return offerId++;
    }

    public static Long getOrderItemId() {
        return orderItemId++;
    }
    
    public static Long getOrderId() {
        return orderId++;
    }
    
    protected static Map<Long, Order> orders = new HashMap<Long, Order>();
    
    public static IAnswer<FulfillmentGroup> getAddItemToFulfillmentGroupAnswer() {
        return new IAnswer<FulfillmentGroup>() {
            @Override
            public FulfillmentGroup answer() throws Throwable {
                FulfillmentGroupItemRequest fgItemRequest = (FulfillmentGroupItemRequest) EasyMock.getCurrentArguments()[0];
                FulfillmentGroup fg = fgItemRequest.getFulfillmentGroup();
                FulfillmentGroupItem fgItem = new FulfillmentGroupItemImpl();
                fgItem.setOrderItem(fgItemRequest.getOrderItem());
                fgItem.setQuantity(fgItemRequest.getQuantity());
                fg.getFulfillmentGroupItems().add(fgItem);
                
                return fg;
            }
        };
    }

    public static IAnswer<FulfillmentGroupItem> getCreateFulfillmentGroupItemAnswer() {
        return new IAnswer<FulfillmentGroupItem>() {
            @Override
            public FulfillmentGroupItem answer() throws Throwable {
                return new FulfillmentGroupItemImpl();
            }
        };
    }
    
    public static IAnswer<OrderItemPriceDetailAdjustment> getCreateOrderItemPriceDetailAdjustmentAnswer() {
        return new IAnswer<OrderItemPriceDetailAdjustment>() {

            @Override
            public OrderItemPriceDetailAdjustment answer() throws Throwable {
                return new OrderItemPriceDetailAdjustmentImpl();
            }
        };
    }

    public static IAnswer<OrderItemPriceDetail> getCreateOrderItemPriceDetailAnswer() {
        return new IAnswer<OrderItemPriceDetail>() {

            @Override
            public OrderItemPriceDetail answer() throws Throwable {
                return new OrderItemPriceDetailImpl();
            }
        };
    }

    public static IAnswer<OrderItemQualifier> getCreateOrderItemQualifierAnswer() {
        return new IAnswer<OrderItemQualifier>() {

            @Override
            public OrderItemQualifier answer() throws Throwable {
                return new OrderItemQualifierImpl();
            }
        };
    }

    
    
    public static IAnswer<FulfillmentGroupAdjustment> getCreateFulfillmentGroupAdjustmentAnswer() {
        return new IAnswer<FulfillmentGroupAdjustment>() {

            @Override
            public FulfillmentGroupAdjustment answer() throws Throwable {
                return new FulfillmentGroupAdjustmentImpl();
            }
        };
    }

    public static IAnswer<OrderItem> getAddOrderItemToOrderAnswer() {
        return new IAnswer<OrderItem>() {
            @Override
            public OrderItem answer() throws Throwable {
                Order order = (Order) EasyMock.getCurrentArguments()[0];
                order.getOrderItems().add((OrderItem) EasyMock.getCurrentArguments()[1]);
                if (((OrderItem) EasyMock.getCurrentArguments()[1]).getId() == null) {
                    ((OrderItem) EasyMock.getCurrentArguments()[1]).setId(OfferDataItemProvider.getOrderItemId());
                }
                return (OrderItem) EasyMock.getCurrentArguments()[1];
            }
        };
    }
    
    public static IAnswer<OrderItem> getSaveOrderItemAnswer() {
        return new IAnswer<OrderItem>() {
            @Override
            public OrderItem answer() throws Throwable {
                OrderItem orderItem = (OrderItem) EasyMock.getCurrentArguments()[0];
                if (orderItem.getId() == null) {
                    orderItem.setId(getOrderItemId());
                }
                return orderItem;
            }
        };
    }
    
    public static IAnswer<Order> getSaveOrderAnswer() {
        return new IAnswer<Order>() {
            @Override
            public Order answer() throws Throwable {
                Order order = (Order) EasyMock.getCurrentArguments()[0];
                order.setId(getOrderId());
                orders.put(order.getId(), order);
                return order;
            }
        };
    }

    public static IAnswer<Order> getSameOrderAnswer() {
        return new IAnswer<Order>() {
            @Override
            public Order answer() throws Throwable {
                return (Order) EasyMock.getCurrentArguments()[0];
            }
        };
    }

    
    public static IAnswer<FulfillmentGroupItem> getSaveFulfillmentGroupItemAnswer() {
        return new IAnswer<FulfillmentGroupItem>() {
            @Override
            public FulfillmentGroupItem answer() throws Throwable {
                return (FulfillmentGroupItem) EasyMock.getCurrentArguments()[0];
            }
        };
    }
    

    public static IAnswer<Order> getRemoveItemFromOrderAnswer() {
        return new IAnswer<Order>() {
            @Override
            public Order answer() throws Throwable {
                Long orderId = (Long) EasyMock.getCurrentArguments()[0];
                Order order = orders.get(orderId);
                
                Iterator<OrderItem> orderItemItr = order.getOrderItems().listIterator();
                while (orderItemItr.hasNext()) {
                    OrderItem item = orderItemItr.next();
                    if (item.getId().equals(EasyMock.getCurrentArguments()[1])) {
                        orderItemItr.remove();
                    }
                }
                
                for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
                    Iterator<FulfillmentGroupItem> itr = fg.getFulfillmentGroupItems().iterator();
                    while (itr.hasNext()) {
                        if (itr.next().getOrderItem().getId().equals(EasyMock.getCurrentArguments()[1])) {
                            itr.remove();
                        }
                    }
                }
                return order;
            }
        };
    }
    
    public PromotableOrder createBasicPromotableOrder() {
        Order order = createBasicOrder();
        PromotableOrder promotableOrder = new PromotableOrderImpl(order, new PromotableItemFactoryImpl(), false);
        return promotableOrder;
    }

    public Order createBasicOrder() {
        Order order = new OrderImpl();
        order.setId(getOrderId());
        
        Category category1 = new CategoryImpl();
        category1.setName("test1");
        category1.setId(1L);
        
        Product product1 = new ProductImpl();
        
        Sku sku1 = new SkuImpl();
        sku1.setName("test1");
        sku1.setId(1L);
        sku1.setDiscountable(true);
        sku1.setRetailPrice(new Money(19.99D));
        product1.setDefaultSku(sku1);

        CategoryProductXref xref1 = new CategoryProductXrefImpl();
        xref1.setProduct(product1);
        xref1.setCategory(category1);

        category1.getAllProductXrefs().add(xref1);
        
        Category category2 = new CategoryImpl();
        category2.setName("test2");
        category2.setId(2L);
        
        Product product2 = new ProductImpl();
        
        Sku sku2 = new SkuImpl();
        sku2.setName("test2");
        sku2.setId(2L);
        sku2.setDiscountable(true);
        sku2.setRetailPrice(new Money(29.99D));
        product2.setDefaultSku(sku2);

        CategoryProductXref xref2 = new CategoryProductXrefImpl();
        xref2.setProduct(product2);
        xref2.setCategory(category2);
        
        category2.getAllProductXrefs().add(xref2);
        
        DiscreteOrderItem orderItem1 = new DiscreteOrderItemImpl();
        orderItem1.setCategory(category1);
        orderItem1.setName("test1");
        orderItem1.setOrder(order);
        orderItem1.setOrderItemType(OrderItemType.DISCRETE);
        orderItem1.setProduct(product1);
        orderItem1.setQuantity(2);
        orderItem1.setSku(sku1);
        orderItem1.setId(getOrderItemId());
        orderItem1.setOrder(order);
        
        OrderItemPriceDetail priceDetail1 = new OrderItemPriceDetailImpl();
        priceDetail1.setOrderItem(orderItem1);
        priceDetail1.setQuantity(2);
        orderItem1.getOrderItemPriceDetails().add(priceDetail1);
        
        order.getOrderItems().add(orderItem1);
        
        DiscreteOrderItem orderItem2 = new DiscreteOrderItemImpl();
        orderItem2.setCategory(category2);
        orderItem2.setName("test2");
        orderItem2.setOrder(order);
        orderItem2.setOrderItemType(OrderItemType.DISCRETE);
        orderItem2.setProduct(product2);
        orderItem2.setQuantity(3);
        orderItem2.setSku(sku2);
        orderItem2.setId(getOrderItemId());
        orderItem2.setOrder(order);
        
        OrderItemPriceDetail priceDetail2 = new OrderItemPriceDetailImpl();
        priceDetail2.setOrderItem(orderItem2);
        priceDetail2.setQuantity(3);
        orderItem2.getOrderItemPriceDetails().add(priceDetail2);
        
        order.getOrderItems().add(orderItem2);
        
        Customer customer = new CustomerImpl();
        customer.setEmailAddress("test@test.com");
        customer.setFirstName("John");
        customer.setLastName("Tester");
        customer.setReceiveEmail(true);
        customer.setRegistered(true);
        
        order.setCustomer(customer);
        
        order.setEmailAddress("test@test.com");
        
        FulfillmentGroup fg1 = new FulfillmentGroupImpl();
        fg1.setId(1L);
        Address address1 = new AddressImpl();
        address1.setAddressLine1("123 Test Road");
        address1.setCity("Dallas");
        
        Country country = new CountryImpl();
        country.setAbbreviation("US");
        country.setName("United States");

        ISOCountry isoCountry = new ISOCountryImpl();
        isoCountry.setAlpha2("US");
        isoCountry.setName("UNITED STATES");

        address1.setCountry(country);
        address1.setIsoCountryAlpha2(isoCountry);
        address1.setDefault(true);
        address1.setFirstName("John");
        address1.setLastName("Tester");
        address1.setPostalCode("75244");

        Phone primary = new PhoneImpl();
        primary.setPhoneNumber("972-976-1234");
        address1.setPhonePrimary(primary);
        
        State state = new StateImpl();
        state.setAbbreviation("TX");
        state.setCountry(country);
        state.setName("Texas");
        
        address1.setState(state);
        address1.setIsoCountrySubdivision("US-TX");
        fg1.setAddress(address1);
        fg1.setOrder(order);
        fg1.setPrimary(true);
        fg1.setRetailShippingPrice(new Money(10D));
        fg1.setShippingPrice(new Money(10D));
        fg1.setType(FulfillmentType.PHYSICAL_SHIP);
        fg1.setOrder(order);
        
        FulfillmentGroupItem fgItem1 = new FulfillmentGroupItemImpl();
        fgItem1.setFulfillmentGroup(fg1);
        fgItem1.setOrderItem(orderItem1);
        fgItem1.setQuantity(2);
        //fgItem1.setRetailPrice(new Money(19.99D));
        fg1.getFulfillmentGroupItems().add(fgItem1);
        
        order.getFulfillmentGroups().add(fg1);
        
        FulfillmentGroup fg2 = new FulfillmentGroupImpl();
        fg2.setId(2L);
        Address address2 = new AddressImpl();
        address2.setAddressLine1("124 Test Road");
        address2.setCity("Dallas");
        
        Country country2 = new CountryImpl();
        country2.setAbbreviation("US");
        country2.setName("United States");

        ISOCountry isoCountry2 = new ISOCountryImpl();
        isoCountry2.setAlpha2("US");
        isoCountry2.setName("UNITED STATES");
        
        address2.setCountry(country2);
        address2.setIsoCountryAlpha2(isoCountry2);
        address2.setDefault(true);
        address2.setFirstName("John");
        address2.setLastName("Tester");
        address2.setPostalCode("75244");

        Phone primary2 = new PhoneImpl();
        primary2.setPhoneNumber("972-976-1234");
        address2.setPhonePrimary(primary2);
        
        State state2 = new StateImpl();
        state2.setAbbreviation("TX");
        state2.setCountry(country2);
        state2.setName("Texas");
        
        address2.setState(state2);
        address2.setIsoCountrySubdivision("US-TX");
        fg2.setAddress(address2);
        fg2.setOrder(order);
        fg2.setPrimary(true);
        fg2.setRetailShippingPrice(new Money(20D));
        fg2.setShippingPrice(new Money(20D));
        fg2.setType(FulfillmentType.PHYSICAL_SHIP);
        fg2.setOrder(order);
        
        FulfillmentGroupItem fgItem2 = new FulfillmentGroupItemImpl();
        fgItem2.setFulfillmentGroup(fg2);
        fgItem2.setOrderItem(orderItem2);
        fgItem2.setQuantity(3);
        //fgItem2.setRetailPrice(new Money(29.99D));
        fg2.getFulfillmentGroupItems().add(fgItem2);
        
        order.getFulfillmentGroups().add(fg2);
        
        order.setSubTotal(new Money((2 * 19.99D) + (3 * 29.99D)));
        
        orders.put(order.getId(), order);
        return order;
    }
    
    /**
     * Create order with a bundle with two items.  Bundle has a quantity of 2.   
     * Bundle item 1 has quantity of 2, bundle item 2 has quantity of 3
     * @return
     */
    public Order createOrderWithBundle() {
        Order order = new OrderImpl();
        order.setId(getOrderId());

        Category category1 = new CategoryImpl();
        category1.setName("test1");
        category1.setId(1L);

        Product product1 = new ProductImpl();

        Sku sku1 = new SkuImpl();
        sku1.setName("test1");
        sku1.setId(1L);
        sku1.setDiscountable(true);
        sku1.setRetailPrice(new Money(10D));
        product1.setDefaultSku(sku1);

        CategoryProductXref xref1 = new CategoryProductXrefImpl();
        xref1.setProduct(product1);
        xref1.setCategory(category1);
        category1.getAllProductXrefs().add(xref1);

        Category category2 = new CategoryImpl();
        category2.setName("test2");
        category2.setId(2L);

        Product product2 = new ProductImpl();

        Sku sku2 = new SkuImpl();
        sku2.setName("test2");
        sku2.setId(2L);
        sku2.setDiscountable(true);
        sku2.setRetailPrice(new Money(10D));
        product2.setDefaultSku(sku2);

        CategoryProductXref xref2 = new CategoryProductXrefImpl();
        xref2.setProduct(product2);
        xref2.setCategory(category2);
        category2.getAllProductXrefs().add(xref2);

        ProductBundle pb = new ProductBundleImpl();
        pb.setPricingModel(ProductBundlePricingModelType.ITEM_SUM);

        BundleOrderItem bundleOrderItem = new BundleOrderItemImpl();
        bundleOrderItem.setCategory(category1);
        bundleOrderItem.setName("test1");
        bundleOrderItem.setOrder(order);
        bundleOrderItem.setOrderItemType(OrderItemType.DISCRETE);
        bundleOrderItem.setQuantity(2);
        bundleOrderItem.setId(getOrderItemId());
        bundleOrderItem.setOrder(order);
        bundleOrderItem.setRetailPrice(new Money(10D));
        bundleOrderItem.setProductBundle(pb);

        OrderItemPriceDetail priceDetail = new OrderItemPriceDetailImpl();
        priceDetail.setOrderItem(bundleOrderItem);
        priceDetail.setQuantity(2);
        bundleOrderItem.getOrderItemPriceDetails().add(priceDetail);

        order.getOrderItems().add(bundleOrderItem);

        DiscreteOrderItem orderItem1 = new DiscreteOrderItemImpl();
        orderItem1.setCategory(category1);
        orderItem1.setName("test1");
        orderItem1.setOrder(order);
        orderItem1.setOrderItemType(OrderItemType.DISCRETE);
        orderItem1.setProduct(product1);
        orderItem1.setQuantity(2);
        orderItem1.setSku(sku1);
        orderItem1.setId(getOrderItemId());
        orderItem1.setOrder(order);

        OrderItemPriceDetail priceDetail1 = new OrderItemPriceDetailImpl();
        priceDetail1.setOrderItem(orderItem1);
        priceDetail1.setQuantity(2);
        orderItem1.getOrderItemPriceDetails().add(priceDetail1);

        bundleOrderItem.getDiscreteOrderItems().add(orderItem1);

        DiscreteOrderItem orderItem2 = new DiscreteOrderItemImpl();
        orderItem2.setCategory(category2);
        orderItem2.setName("test2");
        orderItem2.setOrder(order);
        orderItem2.setOrderItemType(OrderItemType.DISCRETE);
        orderItem2.setProduct(product2);
        orderItem2.setQuantity(3);
        orderItem2.setSku(sku2);
        orderItem2.setId(getOrderItemId());
        orderItem2.setOrder(order);

        OrderItemPriceDetail priceDetail2 = new OrderItemPriceDetailImpl();
        priceDetail2.setOrderItem(orderItem2);
        priceDetail2.setQuantity(3);
        orderItem2.getOrderItemPriceDetails().add(priceDetail2);

        bundleOrderItem.getDiscreteOrderItems().add(orderItem2);

        Customer customer = new CustomerImpl();
        customer.setEmailAddress("test@test.com");
        customer.setFirstName("John");
        customer.setLastName("Tester");
        customer.setReceiveEmail(true);
        customer.setRegistered(true);

        order.setCustomer(customer);

        order.setEmailAddress("test@test.com");

        FulfillmentGroup fg1 = new FulfillmentGroupImpl();
        fg1.setId(1L);
        Address address1 = new AddressImpl();
        address1.setAddressLine1("123 Test Road");
        address1.setCity("Dallas");

        Country country = new CountryImpl();
        country.setAbbreviation("US");
        country.setName("United States");

        ISOCountry isoCountry = new ISOCountryImpl();
        isoCountry.setAlpha2("US");
        isoCountry.setName("UNITED STATES");

        address1.setCountry(country);
        address1.setIsoCountryAlpha2(isoCountry);
        address1.setDefault(true);
        address1.setFirstName("John");
        address1.setLastName("Tester");
        address1.setPostalCode("75244");

        Phone primary = new PhoneImpl();
        primary.setPhoneNumber("972-976-1234");
        address1.setPhonePrimary(primary);

        State state = new StateImpl();
        state.setAbbreviation("TX");
        state.setCountry(country);
        state.setName("Texas");

        address1.setState(state);
        address1.setIsoCountrySubdivision("US-TX");
        fg1.setAddress(address1);
        fg1.setOrder(order);
        fg1.setPrimary(true);
        fg1.setRetailShippingPrice(new Money(10D));
        fg1.setShippingPrice(new Money(10D));
        fg1.setType(FulfillmentType.PHYSICAL_SHIP);
        fg1.setOrder(order);

        FulfillmentGroupItem fgItem1 = new FulfillmentGroupItemImpl();
        fgItem1.setFulfillmentGroup(fg1);
        fgItem1.setOrderItem(orderItem1);
        fgItem1.setQuantity(2);
        //fgItem1.setRetailPrice(new Money(19.99D));
        fg1.getFulfillmentGroupItems().add(fgItem1);

        order.getFulfillmentGroups().add(fg1);

        FulfillmentGroup fg2 = new FulfillmentGroupImpl();
        fg2.setId(2L);
        Address address2 = new AddressImpl();
        address2.setAddressLine1("124 Test Road");
        address2.setCity("Dallas");

        Country country2 = new CountryImpl();
        country2.setAbbreviation("US");
        country2.setName("United States");

        ISOCountry isoCountry2 = new ISOCountryImpl();
        isoCountry2.setAlpha2("US");
        isoCountry2.setName("UNITED STATES");

        address2.setCountry(country2);
        address2.setIsoCountryAlpha2(isoCountry2);
        address2.setDefault(true);
        address2.setFirstName("John");
        address2.setLastName("Tester");
        address2.setPostalCode("75244");

        Phone primary2 = new PhoneImpl();
        primary2.setPhoneNumber("972-976-1234");
        address2.setPhonePrimary(primary2);

        State state2 = new StateImpl();
        state2.setAbbreviation("TX");
        state2.setCountry(country2);
        state2.setName("Texas");

        address2.setState(state2);
        address2.setIsoCountrySubdivision("US-TX");
        fg2.setAddress(address2);
        fg2.setOrder(order);
        fg2.setPrimary(true);
        fg2.setRetailShippingPrice(new Money(20D));
        fg2.setShippingPrice(new Money(20D));
        fg2.setType(FulfillmentType.PHYSICAL_SHIP);
        fg2.setOrder(order);

        FulfillmentGroupItem fgItem2 = new FulfillmentGroupItemImpl();
        fgItem2.setFulfillmentGroup(fg2);
        fgItem2.setOrderItem(orderItem2);
        fgItem2.setQuantity(3);
        //fgItem2.setRetailPrice(new Money(29.99D));
        fg2.getFulfillmentGroupItems().add(fgItem2);

        order.getFulfillmentGroups().add(fg2);

        order.setSubTotal(new Money((2 * 19.99D) + (3 * 29.99D)));

        orders.put(order.getId(), order);
        return order;
    }

    public OfferOfferRuleXref createXref(OfferRule offerRule, Offer offer, String key) {
        return new OfferOfferRuleXrefImpl(offer, offerRule, key);
    }

    public Offer createOffer(
        String appliesToCustomerRules, 
        String appliesToFulfillmentGroupRules, 
        String appliesToRules, 
        boolean applyToSalePrice,
        boolean combinableWithOtherOffers,
        OfferDeliveryType deliveryType,
        OfferDiscountType type,
        Date endDate,
        int maxUses,
        OfferItemRestrictionRuleType qualifierType,
        OfferItemRestrictionRuleType targetType,
        int priority,
            Set<OfferQualifyingCriteriaXref> qualifyingItemCriteriaXref,
        boolean stackable,
        Date startDate,
            Set<OfferTargetCriteriaXref> targetItemCriteriaXref,
        boolean totalitarianOffer,
        OfferType offerType,
        BigDecimal value
    ) {
        Offer offer = new OfferImpl();
        OfferRule customerRule = new OfferRuleImpl();
        customerRule.setMatchRule(appliesToCustomerRules);
        offer.getOfferMatchRulesXref().put(OfferRuleType.CUSTOMER.getType(), createXref(customerRule, offer,
                OfferRuleType.CUSTOMER.getType()));
        OfferRule fgRule = new OfferRuleImpl();
        fgRule.setMatchRule(appliesToFulfillmentGroupRules);
        offer.getOfferMatchRulesXref().put(OfferRuleType.FULFILLMENT_GROUP.getType(), createXref(fgRule, offer,
                OfferRuleType.FULFILLMENT_GROUP.getType()));
        OfferRule orderRule = new OfferRuleImpl();
        orderRule.setMatchRule(appliesToRules);
        offer.getOfferMatchRulesXref().put(OfferRuleType.ORDER.getType(), createXref(orderRule, offer,
                OfferRuleType.ORDER.getType()));
        offer.setApplyDiscountToSalePrice(applyToSalePrice);
        offer.setCombinableWithOtherOffers(combinableWithOtherOffers);
        offer.setDeliveryType(deliveryType);
        offer.setAutomaticallyAdded(OfferDeliveryType.AUTOMATIC==deliveryType);
        offer.setDiscountType(type);
        offer.setEndDate(endDate);
        offer.setMaxUses(maxUses);
        offer.setOfferItemQualifierRuleType(qualifierType);
        offer.setOfferItemTargetRuleType(targetType);
        offer.setPriority(priority);
        offer.setQualifyingItemCriteriaXref(qualifyingItemCriteriaXref);
        offer.setStackable(stackable);
        offer.setStartDate(startDate);
        offer.setTargetItemCriteriaXref(targetItemCriteriaXref);
        offer.setTotalitarianOffer(totalitarianOffer);
        offer.setType(offerType);
        offer.setValue(value);
        offer.setTreatAsNewFormat(true);
        offer.setId(getOfferId());
        return offer;
    }
    
    public Date yesterday() {
        long now = System.currentTimeMillis();
        long then = now - (1000 * 60 * 60 * 24);
        return new Date(then);
    }
    
    public Date tomorrow() {
        long now = System.currentTimeMillis();
        long then = now + (1000 * 60 * 60 * 24);
        return new Date(then);
    }
    
    public List<Offer> createCustomerBasedOffer(String customerRule, Date startDate, Date endDate, OfferDiscountType discountType) {
        Offer offer = createOffer(customerRule, null, null, true, true, OfferDeliveryType.AUTOMATIC, discountType, endDate, 0, OfferItemRestrictionRuleType.NONE, OfferItemRestrictionRuleType.NONE, 1, null, true, startDate, null, false, OfferType.ORDER, BigDecimal.valueOf(10));
        List<Offer> offers = new ArrayList<Offer>();
        offers.add(offer);
        
        return offers;
    }
    
    public List<Offer> createOrderBasedOffer(String orderRule, OfferDiscountType discountType) {
        Offer offer = createOffer(null, null, orderRule, true, true, OfferDeliveryType.AUTOMATIC, discountType, tomorrow(), 0, OfferItemRestrictionRuleType.NONE, OfferItemRestrictionRuleType.NONE, 1, null, true, yesterday(), null, false, OfferType.ORDER, BigDecimal.valueOf(10));
        List<Offer> offers = new ArrayList<Offer>();
        offers.add(offer);
        
        return offers;
    }
    
    public List<Offer> createFGBasedOffer(String orderRule, String fgRule, OfferDiscountType discountType) {
        Offer offer = createOffer(null, fgRule, orderRule, true, true, OfferDeliveryType.AUTOMATIC, discountType, tomorrow(), 0, OfferItemRestrictionRuleType.NONE, OfferItemRestrictionRuleType.NONE, 1, null, true, yesterday(), null, false, OfferType.FULFILLMENT_GROUP, BigDecimal.valueOf(10));
        List<Offer> offers = new ArrayList<Offer>();
        offers.add(offer);
        
        return offers;
    }
    
    public List<Offer> createItemBasedOffer(String orderRule, String targetRule, OfferDiscountType discountType) {
        List<Offer> offers = createOrderBasedOffer(orderRule, discountType);
        offers.get(0).setType(OfferType.ORDER_ITEM);
        
        if (targetRule != null) {
            Offer offer = offers.get(0);

            OfferItemCriteria targetCriteria = new OfferItemCriteriaImpl();
            //targetCriteria.setQualifyingOffer(offers.get(0));
            targetCriteria.setQuantity(1);
            targetCriteria.setMatchRule(targetRule);
            
            OfferTargetCriteriaXref targetXref = new OfferTargetCriteriaXrefImpl();
            targetXref.setOffer(offer);
            targetXref.setOfferItemCriteria(targetCriteria);

            offer.setTargetItemCriteriaXref(Collections.singleton(targetXref));
        }
        
        return offers;
    }
    
    public List<Offer> createOrderBasedOfferWithItemCriteria(String orderRule, OfferDiscountType discountType, String orderItemMatchRule) {
        List<Offer> offers = createOrderBasedOffer(orderRule, discountType);
        
        Offer firstOffer = offers.get(0);

        OfferItemCriteria qualCriteria = new OfferItemCriteriaImpl();
        //qualCriteria.setQualifyingOffer(offers.get(0));
        qualCriteria.setQuantity(1);
        qualCriteria.setMatchRule(orderItemMatchRule);
        Set<OfferQualifyingCriteriaXref> criterias = new HashSet<OfferQualifyingCriteriaXref>();
        OfferQualifyingCriteriaXref xref = new OfferQualifyingCriteriaXrefImpl();
        xref.setOffer(firstOffer);
        xref.setOfferItemCriteria(qualCriteria);
        criterias.add(xref);
        
        firstOffer.setQualifyingItemCriteriaXref(criterias);
        
        return offers;
    }
    
    public List<Offer> createFGBasedOfferWithItemCriteria(String orderRule, String fgRule, OfferDiscountType discountType, String orderItemMatchRule) {
        List<Offer> offers = createFGBasedOffer(orderRule, fgRule, discountType);
        Offer firstOffer = offers.get(0);
        
        OfferItemCriteria qualCriteria = new OfferItemCriteriaImpl();
        qualCriteria.setQuantity(1);
        qualCriteria.setMatchRule(orderItemMatchRule);
        Set<OfferQualifyingCriteriaXref> criterias = new HashSet<OfferQualifyingCriteriaXref>();
        OfferQualifyingCriteriaXref xref = new OfferQualifyingCriteriaXrefImpl();
        xref.setOffer(firstOffer);
        xref.setOfferItemCriteria(qualCriteria);
        criterias.add(xref);
        
        firstOffer.setQualifyingItemCriteriaXref(criterias);
        
        return offers;
    }
    
    public List<Offer> createItemBasedOfferWithItemCriteria(String orderRule, OfferDiscountType discountType, String qualRule, String targetRule) {
        List<Offer> offers = createItemBasedOffer(orderRule, targetRule, discountType);
        
        if (qualRule != null) {
            Offer firstOffer = offers.get(0);

            OfferItemCriteria qualCriteria = new OfferItemCriteriaImpl();
            //qualCriteria.setQualifyingOffer(offers.get(0));
            qualCriteria.setQuantity(1);
            qualCriteria.setMatchRule(qualRule);
            Set<OfferQualifyingCriteriaXref> criterias = new HashSet<OfferQualifyingCriteriaXref>();
            OfferQualifyingCriteriaXref xref = new OfferQualifyingCriteriaXrefImpl();
            xref.setOffer(firstOffer);
            xref.setOfferItemCriteria(qualCriteria);
            criterias.add(xref);
            
            firstOffer.setQualifyingItemCriteriaXref(criterias);
        }
        
        return offers;
    }
}
