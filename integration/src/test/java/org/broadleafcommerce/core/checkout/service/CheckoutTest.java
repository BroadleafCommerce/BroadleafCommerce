/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.checkout.service;

import org.broadleafcommerce.common.currency.BroadleafCurrencyProvider;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.service.BroadleafCurrencyService;
import org.broadleafcommerce.common.encryption.EncryptionModule;
import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.i18n.domain.ISOCountryImpl;
import org.broadleafcommerce.common.money.CurrencyConversionContext;
import org.broadleafcommerce.common.money.CurrencyConversionService;
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
import org.broadleafcommerce.core.payment.service.NullIntegrationGatewayType;
import org.broadleafcommerce.core.payment.service.SecureOrderPaymentService;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.domain.StateImpl;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.test.TestNGSiteIntegrationSetup;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

public class CheckoutTest extends TestNGSiteIntegrationSetup {
    
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
    
    @Resource(name = "blCurrencyService")
    protected BroadleafCurrencyService currencyService;

    @Test(groups = { "checkout" }, dependsOnGroups = { "createCartForCustomer", "testShippingInsert" }, dataProvider = "USCurrency", dataProviderClass = BroadleafCurrencyProvider.class)
    @Transactional
    public void testCheckout(BroadleafCurrency usCurrency) throws Exception {
        HashMap currencyConsiderationContext = new HashMap();
        currencyConsiderationContext.put("aa","bb");
        CurrencyConversionContext.setCurrencyConversionContext(currencyConsiderationContext);
        CurrencyConversionContext.setCurrencyConversionService(new CurrencyConversionService() {
            @Override
            public Money convertCurrency(Money source, Currency destinationCurrency, int destinationScale) {
                return source;
            }
        });
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Order order = orderService.createNewCartForCustomer(customer);
        usCurrency = currencyService.save(usCurrency);
        order.setCurrency(usCurrency);

        Address address = buildAddress();
        FulfillmentGroup group = buildFulfillmentGroup(order, address);
        addSampleItemToOrder(order, group);
        order.setTotalShipping(new Money(0D));
        addPaymentToOrder(order, address);
        
        //execute pricing for this order
        orderService.save(order, true);
        CheckoutResponse response = checkoutService.performCheckout(order);
        
        assert (order.getTotal().greaterThan(order.getSubTotal()));
    }



    private OrderPayment addPaymentToOrder(Order order, Address address) {
        OrderPayment payment = new OrderPaymentImpl();
        payment.setBillingAddress(address);
        payment.setAmount(new Money(15D + (15D * 0.05D)));
        payment.setReferenceNumber("1234");
        payment.setType(PaymentType.CREDIT_CARD);
        payment.setPaymentGatewayType(NullIntegrationGatewayType.NULL_INTEGRATION_GATEWAY);
        payment.setOrder(order);
        PaymentTransaction tx = new PaymentTransactionImpl();
        tx.setAmount(payment.getAmount());
        tx.setType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE);
        tx.setOrderPayment(payment);
        payment.getTransactions().add(tx);

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

        List<OrderItem> items = new ArrayList<>();
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
        List<FulfillmentGroup> groups = new ArrayList<>();
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
        ISOCountry isoCountry = new ISOCountryImpl();
        isoCountry.setAlpha2("US");
        isoCountry.setName("UNITED STATES");
        address.setIsoCountryAlpha2(isoCountry);
        return address;
    }
}
