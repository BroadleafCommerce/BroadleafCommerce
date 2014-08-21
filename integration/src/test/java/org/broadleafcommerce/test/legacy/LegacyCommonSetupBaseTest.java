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
package org.broadleafcommerce.test.legacy;

import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.i18n.domain.ISOCountryImpl;
import org.broadleafcommerce.common.i18n.service.ISOService;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.order.dao.OrderDao;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.pricing.domain.ShippingRate;
import org.broadleafcommerce.core.pricing.domain.ShippingRateImpl;
import org.broadleafcommerce.core.pricing.service.ShippingRateService;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.domain.CustomerAddressImpl;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.domain.StateImpl;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.CustomerAddressService;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.broadleafcommerce.test.BaseTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Calendar;

public abstract class LegacyCommonSetupBaseTest extends BaseTest {

    @Resource
    protected ISOService isoService;

    @Resource
    protected CountryService countryService;
    
    @Resource
    protected StateService stateService;
    
    @Resource
    protected CustomerService customerService;
    
    @Resource
    protected CustomerAddressService customerAddressService;
    
    @Resource
    protected CatalogService catalogService;
    
    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    @Resource
    protected ShippingRateService shippingRateService;

    @Resource
    private OrderDao orderDao;
    

    public void createCountry() {
        Country country = new CountryImpl();
        country.setAbbreviation("US");
        country.setName("United States");
        countryService.save(country);

        ISOCountry isoCountry = new ISOCountryImpl();
        isoCountry.setAlpha2("US");
        isoCountry.setName("UNITED STATES");
        isoService.save(isoCountry);
    }
    
    public void createState() {
        State state = new StateImpl();
        state.setAbbreviation("KY");
        state.setName("Kentucky");
        state.setCountry(countryService.findCountryByAbbreviation("US"));
        stateService.save(state);
    }
    
    public Customer createCustomer() {
        Customer customer = customerService.createCustomerFromId(null);
        return customer;
    }
    
    /**
     * Creates a country, state, and customer with some CustomerAddresses
     * @return customer created
     */
    public Customer createCustomerWithAddresses() {
        createCountry();
        createState();
        CustomerAddress ca1 = new CustomerAddressImpl();
        Address address1 = new AddressImpl();
        address1.setAddressLine1("1234 Merit Drive");
        address1.setCity("Bozeman");
        address1.setPostalCode("75251");
        ca1.setAddress(address1);
        ca1.setAddressName("address1");
        CustomerAddress caResult = createCustomerWithAddress(ca1);
        assert caResult != null;
        assert caResult.getCustomer() != null;
        Customer customer = caResult.getCustomer();

        CustomerAddress ca2 = new CustomerAddressImpl();
        Address address2 = new AddressImpl();
        address2.setAddressLine1("12 Testing Drive");
        address2.setCity("Portland");
        address2.setPostalCode("75251");
        ca2.setAddress(address2);
        ca2.setAddressName("address2");
        ca2.setCustomer(customer);
        CustomerAddress addResult = saveCustomerAddress(ca2);
        assert addResult != null;
        return customer;
    }
    
    /**
     * Creates a country, state, and customer with the supplied customerAddress
     * @param customerAddress
     * @return customer created
     */
    public CustomerAddress createCustomerWithAddress(CustomerAddress customerAddress) {
        createCountry();
        createState();
        Customer customer = createCustomer();
        customer.setUsername(String.valueOf(customer.getId()));
        customerAddress.setCustomer(customer);
        return saveCustomerAddress(customerAddress);
    }
    
    /**
     * Saves a customerAddress with state KY and country US.  Requires that createCountry() and createState() have been called
     * @param customerAddress
     */
    public CustomerAddress saveCustomerAddress(CustomerAddress customerAddress) {
        State state = stateService.findStateByAbbreviation("KY");
        customerAddress.getAddress().setState(state);
        Country country = countryService.findCountryByAbbreviation("US");
        customerAddress.getAddress().setCountry(country);

        customerAddress.getAddress().setIsoCountrySubdivision("US-KY");
        ISOCountry isoCountry = isoService.findISOCountryByAlpha2Code("US");
        customerAddress.getAddress().setIsoCountryAlpha2(isoCountry);

        return customerAddressService.saveCustomerAddress(customerAddress);
    }
    
    /**
     * Create a state, country, and customer with a basic order and some addresses
     */
    public Customer createCustomerWithBasicOrderAndAddresses() {
        Customer customer = createCustomerWithAddresses();
        Order order = new OrderImpl();
        order.setStatus(OrderStatus.IN_PROCESS);
        order.setTotal(new Money(BigDecimal.valueOf(1000)));
        
        assert order.getId() == null;
        order.setCustomer(customer);
        order = orderDao.save(order);
        assert order.getId() != null;
        
        return customer;
    }
    
    public Product addTestProduct(String productName, String categoryName) {
        return addTestProduct(productName, categoryName, true);
    }
    
    public Product addTestProduct(String productName, String categoryName, boolean active) {
        Calendar activeStartCal = Calendar.getInstance();
        activeStartCal.add(Calendar.DAY_OF_YEAR, -2);
        
        Calendar activeEndCal = Calendar.getInstance();
        activeEndCal.add(Calendar.DAY_OF_YEAR, -1);
        
        Category category = new CategoryImpl();
        category.setName(categoryName);
        category.setActiveStartDate(activeStartCal.getTime());
        category = catalogService.saveCategory(category);
        
        Sku newSku = new SkuImpl();
        newSku.setName(productName);
        newSku.setRetailPrice(new Money(44.99));
        newSku.setActiveStartDate(activeStartCal.getTime());  
        if (!active) {
            newSku.setActiveEndDate(activeEndCal.getTime());
        }
        newSku.setDiscountable(true);
        newSku = catalogService.saveSku(newSku);
        
        Product newProduct = new ProductImpl();
        newProduct.setDefaultCategory(category);
        newProduct.setDefaultSku(newSku);
        newProduct = catalogService.saveProduct(newProduct);

        return newProduct;
    }

    public void createShippingRates() {
        ShippingRate sr = new ShippingRateImpl();
        sr.setFeeType("SHIPPING");
        sr.setFeeSubType("ALL");
        sr.setFeeBand(1);
        sr.setBandUnitQuantity(BigDecimal.valueOf(29.99));
        sr.setBandResultQuantity(BigDecimal.valueOf(8.5));
        sr.setBandResultPercent(0);
        ShippingRate sr2 = new ShippingRateImpl();
        
        sr2.setFeeType("SHIPPING");
        sr2.setFeeSubType("ALL");
        sr2.setFeeBand(2);
        sr2.setBandUnitQuantity(BigDecimal.valueOf(999999.99));
        sr2.setBandResultQuantity(BigDecimal.valueOf(8.5));
        sr2.setBandResultPercent(0);
        
        shippingRateService.save(sr);
        shippingRateService.save(sr2);
    }

}
