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
package org.broadleafcommerce.core.offer.service.legacy;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.service.OfferDataItemProvider;
import org.broadleafcommerce.core.offer.service.OfferService;
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
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.pricing.service.workflow.type.ShippingServiceType;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.test.legacy.LegacyCommonSetupBaseTest;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class LegacyOfferServiceTest extends LegacyCommonSetupBaseTest {

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
        address.setIsoCountrySubdivision("US-KY");
        address.setIsoCountryAlpha2(isoService.findISOCountryByAlpha2Code("US"));

        FulfillmentGroup group = new FulfillmentGroupImpl();
        group.setAddress(address);
        group.setIsShippingPriceTaxable(true);
        List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();
        group.setMethod("standard");
        group.setService(ShippingServiceType.BANDED_SHIPPING.getType());
        group.setShippingPrice(new Money("0"));
        group.setOrder(order);
        groups.add(group);
        group.setTotal(new Money(0));
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

    public int countPriceDetails(Order order) {
        int count = 0;
        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem.getOrderItemPriceDetails().isEmpty()) {
                count += 1;
            } else {
                count += orderItem.getOrderItemPriceDetails().size();
            }
        }
        return count;
    }

    /*
    The offer portion of this test was commented to support price lists - without this the test is not valid
    TODO fix test if GiftWrapOrderItems will continue to be supported by offers
     */
    /*@Test(groups =  {"testOffersWithGiftWrapLegacy"}, dependsOnGroups = { "testShippingInsertLegacy"})
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
            //reset the offer is the targets and qualifiers, otherwise the reference is incorrect
            for (OfferItemCriteria criteria : offer.getTargetItemCriteria()) {
                criteria.setTargetOffer(null);
            }
            for (OfferItemCriteria criteria : offer.getQualifyingItemCriteria()) {
                criteria.setQualifyingOffer(null);
            }

            offerService.save(offer);
        }
        order = orderService.save(order, true);

        assert order.getTotalTax().equals(new Money("2.05"));
        assert order.getTotalShipping().equals(new Money("0"));
        assert order.getSubTotal().equals(new Money("41.00"));
        assert order.getTotal().equals(new Money("43.05"));
        assert countPriceDetails(order) == 3;

        boolean foundGiftItemAndCorrectQuantity = false;

        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem instanceof GiftWrapOrderItem && ((GiftWrapOrderItem) orderItem).getWrappedItems().size() == 1) {
                foundGiftItemAndCorrectQuantity = true;
                break;
            }
        }

        assert foundGiftItemAndCorrectQuantity;
    }*/

}
