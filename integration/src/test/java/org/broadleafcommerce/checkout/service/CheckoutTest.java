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
package org.broadleafcommerce.checkout.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.domain.SkuImpl;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.broadleafcommerce.checkout.service.workflow.CheckoutResponse;
import org.broadleafcommerce.encryption.EncryptionModule;
import org.broadleafcommerce.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.order.domain.FulfillmentGroupItemImpl;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.CartService;
import org.broadleafcommerce.order.service.OrderItemService;
import org.broadleafcommerce.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.PaymentInfoImpl;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.pricing.service.module.BandedShippingModule;
import org.broadleafcommerce.pricing.service.workflow.type.ShippingServiceType;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;
import org.broadleafcommerce.profile.domain.Country;
import org.broadleafcommerce.profile.domain.CountryImpl;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.State;
import org.broadleafcommerce.profile.domain.StateImpl;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.BaseTest;
import org.broadleafcommerce.time.SystemTime;
import org.broadleafcommerce.util.money.Money;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

public class CheckoutTest extends BaseTest {

    @Resource(name="blCheckoutService")
    private CheckoutService checkoutService;
    
    @Resource(name="blEncryptionModule")
    private EncryptionModule encryptionModule;
    
    @Resource
    private CustomerService customerService;
    
    @Resource
    private CartService cartService;
    
    @Resource
    private CatalogService catalogService;
    
    @Resource
    private OrderItemService orderItemService;

    @SuppressWarnings("serial")
    @Test(groups = { "checkout" }, dependsOnGroups = { "createCartForCustomer", "testShippingInsert" })
    @Transactional
    public void testCheckout() throws Exception {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Order order = cartService.createNewCartForCustomer(customer);
        
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
         
        FulfillmentGroup group = new FulfillmentGroupImpl();
        group.setOrder(order);
        group.setAddress(address);
        List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();
        groups.add(group);
        order.setFulfillmentGroups(groups);
        Money total = new Money(5D);
        group.setShippingPrice(total);
        group.setMethod("standard");
        group.setService(ShippingServiceType.BANDED_SHIPPING.getType());

        DiscreteOrderItem item = new DiscreteOrderItemImpl();
        item.setOrder(order);
        item.setPrice(new Money(14.99D));
        item.setRetailPrice(new Money(14.99D));
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
        fgItem.setPrice(new Money(0D));
        group.addFulfillmentGroupItem(fgItem);

        order.setTotalShipping(new Money(0D));

        PaymentInfo payment = new PaymentInfoImpl();
        payment.setAddress(address);
        payment.setAmount(new Money(15D + (15D * 0.05D)));
        payment.setReferenceNumber("1234");
        payment.setType(PaymentInfoType.CREDIT_CARD);
        payment.setOrder(order);

        CreditCardPaymentInfo cc = new CreditCardPaymentInfo() {

            public String getCvvCode() {
                return "123";
            }

            public Integer getExpirationMonth() {
                return 11;
            }

            public Integer getExpirationYear() {
                return 2011;
            }

            public Long getId() {
                return null;
            }

            public String getPan() {
                return "1111111111111111";
            }

            public void setCvvCode(String cvvCode) {
                //do nothing
            }

            public void setExpirationMonth(Integer expirationMonth) {
                //do nothing
            }

            public void setExpirationYear(Integer expirationYear) {
                //do nothing
            }

            public void setId(Long id) {
                //do nothing
            }

            public void setPan(String pan) {
                //do nothing
            }

            public EncryptionModule getEncryptionModule() {
                return encryptionModule;
            }

            public String getReferenceNumber() {
                return "1234";
            }

            public void setEncryptionModule(EncryptionModule encryptionModule) {
                //do nothing
            }

            public void setReferenceNumber(String referenceNumber) {
                //do nothing
            }
            
        };

        Map<PaymentInfo, Referenced> map = new HashMap<PaymentInfo, Referenced>();
        map.put(payment, cc);

        CheckoutResponse response = checkoutService.performCheckout(order, map);

        assert (order.getTotal().greaterThan(order.getSubTotal()));
        assert (order.getTotalTax().equals(order.getSubTotal().add(order.getTotalShipping()).multiply(0.05D)));
        assert (response.getPaymentResponse().getResponseItems().size() > 0);
        //assert (order.getTotal().equals(order.getSubTotal().add(order.getTotalTax()).add(order.getTotalShipping())));
    }
}
