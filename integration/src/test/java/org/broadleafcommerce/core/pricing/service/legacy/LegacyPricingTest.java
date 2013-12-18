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
package org.broadleafcommerce.core.pricing.service.legacy;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.domain.OfferCodeImpl;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.offer.service.type.OfferDeliveryType;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.pricing.ShippingRateDataProvider;
import org.broadleafcommerce.core.pricing.domain.ShippingRate;
import org.broadleafcommerce.core.pricing.service.ShippingRateService;
import org.broadleafcommerce.core.pricing.service.workflow.type.ShippingServiceType;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.IdGeneration;
import org.broadleafcommerce.profile.core.domain.IdGenerationImpl;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.domain.StateImpl;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.broadleafcommerce.test.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressWarnings("deprecation")
public class LegacyPricingTest extends BaseTest {

    @Resource
    private CustomerService customerService;

    @Resource(name = "blOrderService")
    private OrderService orderService;

    @Resource
    private ShippingRateService shippingRateService;
    
    @Resource
    private CatalogService catalogService;
    
    @Resource(name = "blOrderItemService")
    private OrderItemService orderItemService;
    
    @Resource
    private OfferService offerService;
    
    @Resource
    private CountryService countryService;
    
    @Resource
    private StateService stateService;

    @Test(groups =  {"testShippingInsertLegacy"}, dataProvider = "basicShippingRates", dataProviderClass = ShippingRateDataProvider.class)
    @Rollback(false)
    public void testShippingInsert(ShippingRate shippingRate, ShippingRate sr2) throws Exception {
        shippingRate = shippingRateService.save(shippingRate);
        sr2 = shippingRateService.save(sr2);
    }

    @Test(groups = {"testPricingLegacy"}, dependsOnGroups = { "testShippingInsertLegacy", "createCustomerIdGenerationLegacy" })
    @Transactional
    public void testPricing() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        
        customerService.saveCustomer(order.getCustomer());

        Country country = new CountryImpl();
        country.setAbbreviation("US");
        country.setName("United States");

        country = countryService.save(country);

        State state = new StateImpl();
        state.setAbbreviation("TX");
        state.setName("Texas");
        state.setCountry(country);

        state = stateService.save(state);
        
        Address address = new AddressImpl();
        address.setAddressLine1("123 Test Rd");
        address.setCity("Dallas");
        address.setFirstName("Jeff");
        address.setLastName("Fischer");
        address.setPostalCode("75240");
        address.setPrimaryPhone("972-978-9067");
        address.setState(state);
        address.setCountry(country);
        
        FulfillmentGroup group = new FulfillmentGroupImpl();
        group.setAddress(address);
        List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();
        group.setMethod("standard");
        group.setService(ShippingServiceType.BANDED_SHIPPING.getType());
        group.setOrder(order);
        groups.add(group);
        order.setFulfillmentGroups(groups);
        Money total = new Money(8.5D);
        group.setShippingPrice(total);

        {
        DiscreteOrderItem item = new DiscreteOrderItemImpl();
        Sku sku = new SkuImpl();
        sku.setName("Test Sku");
        sku.setRetailPrice(new Money(10D));
        sku.setDiscountable(true);
        
        sku = catalogService.saveSku(sku);
        
        item.setSku(sku);
        item.setQuantity(2);
        item.setOrder(order);
        
        item = (DiscreteOrderItem) orderItemService.saveOrderItem(item);
        
        order.addOrderItem(item);
        FulfillmentGroupItem fgItem = new FulfillmentGroupItemImpl();
        fgItem.setFulfillmentGroup(group);
        fgItem.setOrderItem(item);
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
        
        item = (DiscreteOrderItem) orderItemService.saveOrderItem(item);
        
        order.addOrderItem(item);
        
        FulfillmentGroupItem fgItem = new FulfillmentGroupItemImpl();
        fgItem.setFulfillmentGroup(group);
        fgItem.setOrderItem(item);
        fgItem.setQuantity(1);
        //fgItem.setPrice(new Money(0D));
        group.addFulfillmentGroupItem(fgItem);
        }
        
        order.addOfferCode(createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, null, "discreteOrderItem.sku.name==\"Test Sku\""));
        order.addOfferCode(createOfferCode("3 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 3, null, "discreteOrderItem.sku.name!=\"Test Sku\""));
        order.addOfferCode(createOfferCode("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, null));
        order.setTotalShipping(new Money(0D));
        
        orderService.save(order, true);

        assert order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(31.80D));
        assert (order.getTotal().greaterThan(order.getSubTotal()));
        // Distribute Order Savings Activity is on.
        assert (order.getTotalTax().equals((order.getSubTotal().subtract(order.getOrderAdjustmentsValue())).multiply(0.05D))); // Shipping is not taxable
        assert (order.getTotal().equals(order.getSubTotal().add(order.getTotalTax()).add(order.getTotalShipping()).subtract(order.getOrderAdjustmentsValue())));
    }



    @Test(groups = { "testShippingLegacy" }, dependsOnGroups = { "testShippingInsertLegacy", "createCustomerIdGenerationLegacy"})
    @Transactional
    public void testShipping() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        
        customerService.saveCustomer(order.getCustomer());
        
        FulfillmentGroup group1 = new FulfillmentGroupImpl();
        FulfillmentGroup group2 = new FulfillmentGroupImpl();

        // setup group1 - standard
        group1.setMethod("standard");
        group1.setService(ShippingServiceType.BANDED_SHIPPING.getType());

        Country country = new CountryImpl();
        country.setAbbreviation("US");
        country.setName("United States");

        country = countryService.save(country);

        State state = new StateImpl();
        state.setAbbreviation("TX");
        state.setName("Texas");
        state.setCountry(country);

        state = stateService.save(state);
        
        Address address = new AddressImpl();
        address.setAddressLine1("123 Test Rd");
        address.setCity("Dallas");
        address.setFirstName("Jeff");
        address.setLastName("Fischer");
        address.setPostalCode("75240");
        address.setPrimaryPhone("972-978-9067");

        address.setState(state);
        address.setCountry(country);
        group1.setAddress(address);
        group1.setOrder(order);

        // setup group2 - truck
        group2.setMethod("truck");
        group2.setService(ShippingServiceType.BANDED_SHIPPING.getType());
        group2.setOrder(order);

        List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();
        groups.add(group1);
        //groups.add(group2);
        order.setFulfillmentGroups(groups);
        Money total = new Money(8.5D);
        group1.setShippingPrice(total);
        group2.setShippingPrice(total);
        //group1.setTotalTax(new Money(1D));
        //group2.setTotalTax(new Money(1D));
        order.setSubTotal(total);
        order.setTotal(total);

        DiscreteOrderItem item = new DiscreteOrderItemImpl();
        Sku sku = new SkuImpl();
        sku.setRetailPrice(new Money(15D));
        sku.setDiscountable(true);
        sku.setName("Test Sku");
        
        sku = catalogService.saveSku(sku);
        
        item.setSku(sku);
        item.setQuantity(1);
        item.setOrder(order);
        
        item = (DiscreteOrderItem) orderItemService.saveOrderItem(item);
        
        List<OrderItem> items = new ArrayList<OrderItem>();
        items.add(item);
        order.setOrderItems(items);
        for (OrderItem orderItem : items) {
            FulfillmentGroupItem fgi = new FulfillmentGroupItemImpl();
            fgi.setOrderItem(orderItem);
            fgi.setQuantity(orderItem.getQuantity());
            fgi.setFulfillmentGroup(group1);
            //fgi.setRetailPrice(new Money(15D));
            group1.addFulfillmentGroupItem(fgi);
        }
        order.setTotalShipping(new Money(0D));
        
        orderService.save(order, true);

        assert (order.getTotal().greaterThan(order.getSubTotal()));
        assert (order.getTotalTax().equals(order.getSubTotal().multiply(0.05D))); // Shipping price is not taxable
        assert (order.getTotal().equals(order.getSubTotal().add(order.getTotalTax().add(order.getTotalShipping()))));
    }
    
    @Test(groups = { "createCustomerIdGenerationLegacy" })
    @Rollback(false)
    public void createCustomerIdGeneration() {
        IdGeneration idGeneration = new IdGenerationImpl();
        idGeneration.setType("org.broadleafcommerce.profile.core.domain.Customer");
        idGeneration.setBatchStart(1L);
        idGeneration.setBatchSize(10L);
        em.persist(idGeneration);
    }

    public Customer createCustomer() {
        Customer customer = customerService.createCustomerFromId(null);
        return customer;
    }

    private OfferCode createOfferCode(String offerName, OfferType offerType, OfferDiscountType discountType, double value, String customerRule, String orderRule) {
        OfferCode offerCode = new OfferCodeImpl();
        Offer offer = createOffer(offerName, offerType, discountType, value, customerRule, orderRule);
        offerCode.setOffer(offer);
        offerCode.setOfferCode("OPRAH");
        offerCode = offerService.saveOfferCode(offerCode);
        return offerCode;
    }

    private Offer createOffer(String offerName, OfferType offerType, OfferDiscountType discountType, double value, String customerRule, String orderRule) {
        Offer offer = new OfferImpl();
        offer.setName(offerName);
        offer.setStartDate(SystemTime.asDate());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        offer.setStartDate(calendar.getTime());
        calendar.add(Calendar.DATE, 2);
        offer.setEndDate(calendar.getTime());
        offer.setType(offerType);
        offer.setDiscountType(discountType);
        offer.setValue(BigDecimal.valueOf(value));
        offer.setDeliveryType(OfferDeliveryType.CODE);
        offer.setStackable(true);
        offer.setAppliesToOrderRules(orderRule);
        offer.setAppliesToCustomerRules(customerRule);
        offer.setCombinableWithOtherOffers(true);
        offer = offerService.save(offer);
        offer.setMaxUses(50);
        return offer;
    }
}
