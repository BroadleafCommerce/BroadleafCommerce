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
package org.broadleafcommerce.core.checkout.service;

import org.broadleafcommerce.common.encryption.EncryptionModule;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutResponse;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.fulfillment.domain.FixedPriceFulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.FixedPriceFulfillmentOptionImpl;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.core.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentInfoImpl;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.SecurePaymentInfoService;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.domain.StateImpl;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.test.BaseTest;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutTest extends BaseTest {

    @Resource(name="blCheckoutService")
    private CheckoutService checkoutService;
    
    @Resource(name="blEncryptionModule")
    private EncryptionModule encryptionModule;

    @Resource(name = "blCustomerService")
    private CustomerService customerService;
    
    @Resource(name = "blOrderService")
    private OrderService orderService;

    @Resource(name = "blCatalogService")
    private CatalogService catalogService;
    
    @Resource(name = "blOrderItemService")
    private OrderItemService orderItemService;

    @Resource(name = "blSecurePaymentInfoService")
    private SecurePaymentInfoService securePaymentInfoService;

    @Test(groups = { "checkout" }, dependsOnGroups = { "createCartForCustomer", "testShippingInsert" })
    @Transactional
    public void testCheckout() throws Exception {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Order order = orderService.createNewCartForCustomer(customer);

        Address address = buildAddress();
        FulfillmentGroup group = buildFulfillmentGroup(order, address);
        addSampleItemToOrder(order, group);
        order.setTotalShipping(new Money(0D));
        Map<PaymentInfo, Referenced> map = addPaymentToOrder(order, address);

        //execute pricing for this order
        orderService.save(order, true);
        CheckoutResponse response = checkoutService.performCheckout(order, map);
        //The DummyCreditCardModule changed the reference Number - make sure it's represented
        for(PaymentInfo paymentInfo : response.getInfos().keySet()) {
            assert(paymentInfo.getReferenceNumber().equals("abc123"));
            assert(response.getInfos().get(paymentInfo).getReferenceNumber().equals("abc123"));
        }

        //confirm that the secure payment info items are not persisted
        Referenced referenced = null;
        try {
            referenced = securePaymentInfoService.findSecurePaymentInfo("abc123", PaymentInfoType.CREDIT_CARD);
        } catch (Exception e) {
            //do nothing
        }
        try {
            referenced = securePaymentInfoService.findSecurePaymentInfo("1234", PaymentInfoType.CREDIT_CARD);
        } catch (Exception e) {
            //do nothing
        }
        assert(referenced == null);

        assert (order.getTotal().greaterThan(order.getSubTotal()));
        //Removed by Jeff to facilitate merge : assert (order.getTotalTax().equals(order.getSubTotal().add(order.getTotalShipping()).multiply(0.05D)));
        assert (response.getPaymentResponse().getResponseItems().size() > 0);
        //assert (order.getTotal().equals(order.getSubTotal().add(order.getTotalTax()).add(order.getTotalShipping())));
    }

/*
    @SuppressWarnings("serial")
    @Test(groups = { "checkout" }, dependsOnGroups = { "createCartForCustomer", "testShippingInsert" })
    @Transactional
    public void testCustomerMaxUsesPromo() throws Exception {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Order order = cartService.createNewCartForCustomer(customer);
        CreateOfferUtility createOfferUtility = new CreateOfferUtility(offerDao, offerCodeDao, offerService);

        OfferCode code = createOfferUtility.createOfferCode("20.5 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20.5, null, null, true, true, 10);
        Offer offer = createOfferUtility.updateOfferCodeMaxCustomerUses(code, 2l);

        Address address = buildAddress();

        FulfillmentGroup group = buildFulfillmentGroup(order, address);
        addSampleItemToOrder(order, group);
        cartService.addOfferCode(order, code, false);
        order.setTotalShipping(new Money(0D));
        Map<PaymentInfo, Referenced> map = addPaymentToOrder(order, address);
        CheckoutResponse response = checkoutService.performCheckout(order, map);
        
        assert(offerAuditDao.countUsesByCustomer(customer.getId(), offer.getId()) == 1L);
        assert(offerService.verifyMaxCustomerUsageThreshold(customer, offer) == false);

        // Now enter a 2nd order
        Order order2 = cartService.createNewCartForCustomer(customer);        
        cartService.addOfferCode(order2, code, true);
        
        Address address2 = buildAddress();
        FulfillmentGroup group2 = buildFulfillmentGroup(order2, address2);
        addSampleItemToOrder(order2, group2);
        order2.setTotalShipping(new Money(0D));
        Map<PaymentInfo, Referenced> map2 = addPaymentToOrder(order2, address2);
        CheckoutResponse response2 = checkoutService.performCheckout(order2, map2);
        
        assert(offerAuditDao.countUsesByCustomer(customer.getId(), offer.getId()) == 2L);
        assert(offerService.verifyMaxCustomerUsageThreshold(customer, offer) == true);

        // Now, try a 3rd use of the promo (should fail)
        Order order3 = cartService.createNewCartForCustomer(customer);

        boolean exceptionCaught = false;
        try {
            cartService.addOfferCode(order3, code, true);
        } catch (OfferMaxUseExceededException e) {            
            exceptionCaught = true;
        }

        // Add the code to the order directly and bypass the exception
        order3.addOfferCode(code);
        Address address3 = buildAddress();
        FulfillmentGroup group3 = buildFulfillmentGroup(order3, address3);
        addSampleItemToOrder(order3, group3);
        order3.setTotalShipping(new Money(0D));
        Map<PaymentInfo, Referenced> map3 = addPaymentToOrder(order3, address3);

        exceptionCaught = false;
        try {
            // Exception should get generated in the checkout workflow.
            CheckoutResponse response3 = checkoutService.performCheckout(order2, map2);
        } catch (OfferMaxUseExceededException e) {
            exceptionCaught = true;
        }
        assert(exceptionCaught);

        // Finally, create an order for a 2nd customer - should be no problem.
        String userName2 = "customer2";
        Customer customer2 = customerService.readCustomerByUsername(userName2);
        Order order4 = cartService.createNewCartForCustomer(customer2);
        cartService.addOfferCode(order4, code, true);

        Address address4 = buildAddress();
        FulfillmentGroup group4 = buildFulfillmentGroup(order4, address4);
        addSampleItemToOrder(order4, group4);
        order4.setTotalShipping(new Money(0D));
        Map<PaymentInfo, Referenced> map4 = addPaymentToOrder(order4, address4);
        CheckoutResponse response4 = checkoutService.performCheckout(order4, map4);

        assert(offerAuditDao.countUsesByCustomer(customer2.getId(), offer.getId()) == 1L);
        assert(offerService.verifyMaxCustomerUsageThreshold(customer2, offer) == false);
    }
    */


    private Map<PaymentInfo, Referenced> addPaymentToOrder(Order order, Address address) {
        PaymentInfo payment = new PaymentInfoImpl();
        payment.setAddress(address);
        payment.setAmount(new Money(15D + (15D * 0.05D)));
        payment.setReferenceNumber("1234");
        payment.setType(PaymentInfoType.CREDIT_CARD);
        payment.setOrder(order);

        CreditCardPaymentInfo cc = new CreditCardPaymentInfo() {

            private static final long serialVersionUID = 1L;
            private String referenceNumber = "1234";

            @Override
            public String getCvvCode() {
                return "123";
            }

            @Override
            public Integer getExpirationMonth() {
                return 11;
            }

            @Override
            public Integer getExpirationYear() {
                return 2011;
            }

            @Override
            public Long getId() {
                return null;
            }

            @Override
            public String getPan() {
                return "1111111111111111";
            }

            @Override
            public void setCvvCode(String cvvCode) {
                //do nothing
            }

            @Override
            public void setExpirationMonth(Integer expirationMonth) {
                //do nothing
            }

            @Override
            public void setExpirationYear(Integer expirationYear) {
                //do nothing
            }

            @Override
            public void setId(Long id) {
                //do nothing
            }

            @Override
            public void setPan(String pan) {
                //do nothing
            }

            @Override
            public EncryptionModule getEncryptionModule() {
                return encryptionModule;
            }

            @Override
            public String getReferenceNumber() {
                return referenceNumber;
            }

            @Override
            public void setEncryptionModule(EncryptionModule encryptionModule) {
                //do nothing
            }

            @Override
            public void setReferenceNumber(String referenceNumber) {
                this.referenceNumber = referenceNumber;
            }

            @Override
            public String getNameOnCard() {
                return "Cardholder Name";
            }

            @Override
            public void setNameOnCard(String nameOnCard) {
                // do nothing
            }

        };

        order.getPaymentInfos().add(payment);
        Map<PaymentInfo, Referenced> map = new HashMap<PaymentInfo, Referenced>();
        map.put(payment, cc);
        return map;
    }

    private void addSampleItemToOrder(Order order, FulfillmentGroup group) {
        DiscreteOrderItem item = new DiscreteOrderItemImpl();
        item.setOrder(order);
        item.setQuantity(1);

        Sku newSku = new SkuImpl();
        newSku.setName("Under Armor T-Shirt -- Red");
        newSku.setRetailPrice(new Money(14.99));
        newSku.setActiveStartDate(SystemTime.asDate());
        newSku.setDiscountable(false);
        newSku = catalogService.saveSku(newSku);
        item.setSku(newSku);

        item = (DiscreteOrderItem) orderItemService.saveOrderItem(item);

        List<OrderItem> items = new ArrayList<OrderItem>();
        items.add(item);
        order.setOrderItems(items);

        FulfillmentGroupItem fgItem = new FulfillmentGroupItemImpl();
        fgItem.setFulfillmentGroup(group);
        fgItem.setOrderItem(item);
        fgItem.setQuantity(1);
        //fgItem.setPrice(new Money(0D));
        group.addFulfillmentGroupItem(fgItem);
    }

    private FulfillmentGroup buildFulfillmentGroup(Order order, Address address) {
        FulfillmentGroup group = new FulfillmentGroupImpl();
        group.setIsShippingPriceTaxable(true);
        group.setOrder(order);
        group.setAddress(address);
        List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();
        groups.add(group);
        order.setFulfillmentGroups(groups);
        Money total = new Money(5D);
        group.setShippingPrice(total);
        FixedPriceFulfillmentOption option = new FixedPriceFulfillmentOptionImpl();
        option.setPrice(new Money(0));
        option.setFulfillmentType(FulfillmentType.PHYSICAL_SHIP);
        group.setFulfillmentOption(option);
        return group;
    }

    private Address buildAddress() {
        Address address = new AddressImpl();
        address.setAddressLine1("123 Test Rd");
        address.setCity("Dallas");
        address.setFirstName("Jeff");
        address.setLastName("Fischer");
        address.setPostalCode("75240");
        address.setPrimaryPhone("972-978-9067");
        State state = new StateImpl();
        state.setAbbreviation("ALL");
        state.setName("ALL");
        address.setState(state);
        Country country = new CountryImpl();
        country.setAbbreviation("US");
        country.setName("United States");
        state.setCountry(country);
        address.setCountry(country);
        return address;
    }
}
