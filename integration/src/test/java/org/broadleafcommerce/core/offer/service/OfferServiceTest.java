/*
 * #%L
 * BroadleafCommerce Integration
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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItemImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderMultishipOption;
import org.broadleafcommerce.core.order.domain.OrderMultishipOptionImpl;
import org.broadleafcommerce.core.order.fulfillment.domain.FixedPriceFulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.FixedPriceFulfillmentOptionImpl;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.test.CommonSetupBaseTest;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OfferServiceTest extends CommonSetupBaseTest {

    @Resource
    protected OfferService offerService;

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Resource
    protected CustomerService customerService;

    @Resource
    protected CatalogService catalogService;

    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;

    @Resource(name="blOrderMultishipOptionService")
    protected OrderMultishipOptionService orderMultishipOptionService;

    @Resource(name="blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    private Order createTestOrderWithOfferAndGiftWrap() throws PricingException {
        Customer customer = customerService.createCustomerFromId(null);
        Order order = orderService.createNewCartForCustomer(customer);

        customerService.saveCustomer(order.getCustomer());

        createCountry();
        createState();

        Address address = new AddressImpl();
        address.setAddressLine1("123 Test Rd");
        address.setCity("Dallas");
        address.setFirstName("Jeff");
        address.setLastName("Fischer");
        address.setPostalCode("75240");
        address.setPrimaryPhone("972-978-9067");
        address.setState(stateService.findStateByAbbreviation("KY"));
        address.setCountry(countryService.findCountryByAbbreviation("US"));

        FulfillmentGroup group = new FulfillmentGroupImpl();
        group.setAddress(address);
        group.setIsShippingPriceTaxable(true);
        List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();

        FixedPriceFulfillmentOption option = new FixedPriceFulfillmentOptionImpl();
        option.setPrice(new Money("8.50"));        
        option.setFulfillmentType(FulfillmentType.PHYSICAL_SHIP);
        group.setFulfillmentOption(option);

        group.setFulfillmentOption(option);
        group.setOrder(order);
        groups.add(group);
        order.setFulfillmentGroups(groups);
        Money total = new Money(5D);
        group.setRetailShippingPrice(total);
        group.setShippingPrice(total);

        DiscreteOrderItem item1;
        {
        item1 = new DiscreteOrderItemImpl();
        Sku sku = new SkuImpl();
        sku.setName("Test Sku");
        sku.setRetailPrice(new Money(10D));
        sku.setDiscountable(true);

        sku = catalogService.saveSku(sku);

        item1.setSku(sku);
        item1.setQuantity(2);
        item1.setOrder(order);
        item1.setOrderItemType(OrderItemType.DISCRETE);

        item1 = (DiscreteOrderItem) orderItemService.saveOrderItem(item1);

        order.addOrderItem(item1);
        FulfillmentGroupItem fgItem = new FulfillmentGroupItemImpl();
        fgItem.setFulfillmentGroup(group);
        fgItem.setOrderItem(item1);
        fgItem.setQuantity(2);
        //fgItem.setPrice(new Money(0D));
        group.addFulfillmentGroupItem(fgItem);
        }

        {
        DiscreteOrderItem item = new DiscreteOrderItemImpl();
        Sku sku = new SkuImpl();
        sku.setName("Test Product 2");
        sku.setRetailPrice(new Money(20D));
        sku.setDiscountable(true);

        sku = catalogService.saveSku(sku);

        item.setSku(sku);
        item.setQuantity(1);
        item.setOrder(order);
        item.setOrderItemType(OrderItemType.DISCRETE);

        item = (DiscreteOrderItem) orderItemService.saveOrderItem(item);

        order.addOrderItem(item);

        FulfillmentGroupItem fgItem = new FulfillmentGroupItemImpl();
        fgItem.setFulfillmentGroup(group);
        fgItem.setOrderItem(item);
        fgItem.setQuantity(1);
        //fgItem.setPrice(new Money(0D));
        group.addFulfillmentGroupItem(fgItem);
        }

        {
        GiftWrapOrderItem item = new GiftWrapOrderItemImpl();
        Sku sku = new SkuImpl();
        sku.setName("Test GiftWrap");
        sku.setRetailPrice(new Money(1D));
        sku.setDiscountable(true);

        sku = catalogService.saveSku(sku);

        item.setSku(sku);
        item.setQuantity(1);
        item.setOrder(order);
        item.getWrappedItems().add(item1);
        item.setOrderItemType(OrderItemType.GIFTWRAP);

        item = (GiftWrapOrderItem) orderItemService.saveOrderItem(item);

        order.addOrderItem(item);

        FulfillmentGroupItem fgItem = new FulfillmentGroupItemImpl();
        fgItem.setFulfillmentGroup(group);
        fgItem.setOrderItem(item);
        fgItem.setQuantity(1);
        //fgItem.setPrice(new Money(0D));
        group.addFulfillmentGroupItem(fgItem);
        }

        return order;
    }

    @Test(groups =  {"testOffersWithGiftWrap"}, dependsOnGroups = { "testShippingInsert"})
    @Transactional
    public void testOrderItemOfferWithGiftWrap() throws PricingException {
        Order order = createTestOrderWithOfferAndGiftWrap();
        OfferDataItemProvider dataProvider = new OfferDataItemProvider();
        List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"Test Sku\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.sku.name))",
            "([MVEL.eval(\"toUpperCase()\",\"Test Sku\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.sku.name))"
        );
        for (Offer offer : offers) {
            offer.setName("testOffer");
//            //reset the offer is the targets and qualifiers, otherwise the reference is incorrect
//            for (OfferItemCriteria criteria : offer.getTargetItemCriteria()) {
//                criteria.setTargetOffer(null);
//            }
//            for (OfferItemCriteria criteria : offer.getQualifyingItemCriteria()) {
//                criteria.setQualifyingOffer(null);
//            }

            offerService.save(offer);
        }

        order = orderService.save(order, false);

        Set<OrderMultishipOption> options = new HashSet<OrderMultishipOption>();
        for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
            Address address = fg.getAddress();
            for (FulfillmentGroupItem fgItem : fg.getFulfillmentGroupItems()) {
                for (int j=0;j<fgItem.getQuantity();j++) {
                    OrderMultishipOption option = new OrderMultishipOptionImpl();
                    option.setOrder(order);
                    option.setAddress(address);
                    option.setOrderItem(fgItem.getOrderItem());
                    option.setFulfillmentOption(fg.getFulfillmentOption());
                    options.add(option);
                }
            }
        }
        for (OrderMultishipOption option : options) {
            orderMultishipOptionService.save(option);
        }
        order = fulfillmentGroupService.matchFulfillmentGroupsToMultishipOptions(order, true);

        assert order.getOrderItems().size() == 3;
        assert order.getTotalTax().equals(new Money("2.00"));
        assert order.getTotalShipping().equals(new Money("8.50"));
        assert order.getSubTotal().equals(new Money("40.00"));
        assert order.getTotal().equals(new Money("50.50"));

        boolean foundGiftItemAndCorrectQuantity = false;

        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem instanceof GiftWrapOrderItem && ((GiftWrapOrderItem) orderItem).getWrappedItems().size() == 1) {
                foundGiftItemAndCorrectQuantity = true;
                break;
            }
        }

        assert foundGiftItemAndCorrectQuantity;
    }

}
