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
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.payment.PaymentType;
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
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.OrderPaymentImpl;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.domain.PaymentTransactionImpl;
import org.broadleafcommerce.core.payment.domain.secure.CreditCardPayment;
import org.broadleafcommerce.core.payment.domain.secure.Referenced;
import org.broadleafcommerce.core.payment.service.SecureOrderPaymentService;
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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

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

    @Resource(name = "blSecureOrderPaymentService")
    private SecureOrderPaymentService securePaymentInfoService;

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
        addPaymentToOrder(order, address);
        
        //execute pricing for this order
        orderService.save(order, true);
        CheckoutResponse response = checkoutService.performCheckout(order);
        
        //confirm that the secure payment info items are not persisted
        Referenced referenced = null;
        try {
            referenced = securePaymentInfoService.findSecurePaymentInfo("abc123", PaymentType.CREDIT_CARD);
        } catch (Exception e) {
            //do nothing
        }
        try {
            referenced = securePaymentInfoService.findSecurePaymentInfo("1234", PaymentType.CREDIT_CARD);
        } catch (Exception e) {
            //do nothing
        }
        assert(referenced == null);

        assert (order.getTotal().greaterThan(order.getSubTotal()));
    }



    private OrderPayment addPaymentToOrder(Order order, Address address) {
        OrderPayment payment = new OrderPaymentImpl();
        payment.setBillingAddress(address);
        payment.setAmount(new Money(15D + (15D * 0.05D)));
        payment.setReferenceNumber("1234");
        payment.setType(PaymentType.CREDIT_CARD);
        payment.setOrder(order);
        PaymentTransaction tx = new PaymentTransactionImpl();
        tx.setAmount(payment.getAmount());
        tx.setType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE);

        CreditCardPayment cc = new CreditCardPayment() {

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
            public String getNameOnCard() {
                return "Cardholder Name";
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
            public void setNameOnCard(String nameOnCard) {
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

        };

        order.getPayments().add(payment);
        return payment;
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
