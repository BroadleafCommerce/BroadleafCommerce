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
package org.broadleafcommerce.vendor;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.domain.SkuImpl;
import org.broadleafcommerce.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.order.domain.FulfillmentGroupItemImpl;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderImpl;
import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.PaymentInfoImpl;
import org.broadleafcommerce.pricing.service.module.CyberSourceTaxModule;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;
import org.broadleafcommerce.profile.domain.CountryImpl;
import org.broadleafcommerce.profile.domain.StateImpl;
import org.broadleafcommerce.test.BaseTest;
import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.vendor.cybersource.service.CyberSourceServiceManager;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

/**
 * @author jfischer
 *
 */
public class CyberSourceTaxModuleTest extends BaseTest {

    @Resource
    private CyberSourceServiceManager serviceManager;
    
    @Test(groups = { "testSuccessfulCyberSourceTaxModule" })
    @Rollback(false)
    public void testSuccessfulCyberSourceTaxModule() throws Exception {
        if (serviceManager.getMerchantId().equals("?")) {
            return;
        }
        
        CyberSourceTaxModule module = new CyberSourceTaxModule();
        module.setServiceManager(serviceManager);
        ArrayList<String> nexus = new ArrayList<String>();
        nexus.add("TX");
        module.setNexus(nexus);
        module.setOrderAcceptanceCity("Dallas");
        module.setOrderAcceptanceCountry("US");
        module.setOrderAcceptancePostalCode("75244");
        module.setOrderAcceptanceState("TX");
        
        PaymentInfo paymentInfo = createPaymentInfo("8335 Westchester Drive", "Dallas", "US", "John", "Test", "75225", "TX");
        Order order = new OrderImpl();
        order.setEmailAddress("null@cybersource.com");
        paymentInfo.setOrder(order);
        order.getPaymentInfos().add(paymentInfo);
        
        DiscreteOrderItem item1 = new DiscreteOrderItemImpl();
        Sku sku1 = new SkuImpl();
        sku1.setName("a72345b");
        sku1.setDescription("Test Description Product 1");
        sku1.setTaxable(true);
        item1.setSku(sku1);
        item1.setPrice(new Money(10D));
        
        FulfillmentGroup fg1 = new FulfillmentGroupImpl();
        fg1.setId(1L);
        FulfillmentGroupItem fgi1 = new FulfillmentGroupItemImpl();
        fgi1.setOrderItem(item1);
        fgi1.setQuantity(2);
        fg1.addFulfillmentGroupItem(fgi1);
        fg1.setAddress(createDestinationAddress("14930 Midway Rd", "Dallas", "US", "John", "Test", "75001", "TX"));
        order.getFulfillmentGroups().add(fg1);
        
        DiscreteOrderItem item2 = new DiscreteOrderItemImpl();
        Sku sku2 = new SkuImpl();
        sku2.setName("a72345c");
        sku2.setDescription("Test Description Product 2");
        sku2.setTaxable(true);
        item2.setSku(sku2);
        item2.setPrice(new Money(30D));
        
        FulfillmentGroup fg2 = new FulfillmentGroupImpl();
        fg2.setId(2L);
        FulfillmentGroupItem fgi2 = new FulfillmentGroupItemImpl();
        fgi2.setOrderItem(item2);
        fgi2.setQuantity(1);
        fg2.addFulfillmentGroupItem(fgi2);
        fg2.setAddress(createDestinationAddress("14999 Monfort Drive", "Dallas", "US", "John", "Test", "75254", "TX"));
        order.getFulfillmentGroups().add(fg2);
        order.setTotal(new Money(50D));
        
        assert(order.getTotalTax() == null);
        order = module.calculateTaxForOrder(order);
        assert(order.getTotalTax() != null && order.getTotalTax().greaterThan(new Money(0D)));
        assert(order.getFulfillmentGroups().get(0).getTotalTax().add(order.getFulfillmentGroups().get(1).getTotalTax()).equals(order.getTotalTax()));
    }
    
    private PaymentInfo createPaymentInfo(String line1, String city, final String country, String name, String lastName, String postalCode, final String state) {
        PaymentInfo paymentInfo = new PaymentInfoImpl();
        Address address = new AddressImpl();
        address.setAddressLine1(line1);
        address.setCity(city);
        address.setCountry(new CountryImpl() {
            @Override
            public String getAbbreviation() {
                return country;
            }
        }
        );
        address.setFirstName(name);
        address.setLastName(lastName);
        address.setPostalCode(postalCode);
        address.setState(new StateImpl() {
            @Override
            public String getAbbreviation() {
                return state;
            }
        });
        paymentInfo.setAddress(address);
        paymentInfo.setCustomerIpAddress("10.7.111.111");
        
        return paymentInfo;
    }
    
    private Address createDestinationAddress(String line1, String city, final String country, String name, String lastName, String postalCode, final String state) {
        Address address = new AddressImpl();
        address.setAddressLine1(line1);
        address.setCity(city);
        address.setCountry(new CountryImpl() {
            @Override
            public String getAbbreviation() {
                return country;
            }
        }
        );
        address.setFirstName(name);
        address.setLastName(lastName);
        address.setPostalCode(postalCode);
        address.setState(new StateImpl() {
            @Override
            public String getAbbreviation() {
                return state;
            }
        });
        return address;
    }
    
}
