package org.broadleafcommerce.test;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.broadleafcommerce.common.domain.Auditable;
import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderImpl;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;
import org.broadleafcommerce.profile.domain.Country;
import org.broadleafcommerce.profile.domain.CountryImpl;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerAddress;
import org.broadleafcommerce.profile.domain.CustomerAddressImpl;
import org.broadleafcommerce.profile.domain.State;
import org.broadleafcommerce.profile.domain.StateImpl;
import org.broadleafcommerce.profile.service.CountryService;
import org.broadleafcommerce.profile.service.CustomerAddressService;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.service.StateService;
import org.broadleafcommerce.util.money.Money;

public abstract class CommonSetupBaseTest extends BaseTest {
    
	@Resource
    protected CountryService countryService;
    
    @Resource
    protected StateService stateService;
    
    @Resource
    protected CustomerService customerService;
    
    @Resource
    protected CustomerAddressService customerAddressService;
    
    @Resource
    private OrderDao orderDao;

    public void createCountry() {
        Country country = new CountryImpl();
        country.setAbbreviation("US");
        country.setName("United States");
        countryService.save(country);
    }
    
    public void createState() {
        State state = new StateImpl();
        state.setAbbreviation("KY");
        state.setName("Kentucky");
        state.setCountry(countryService.findCountryByAbbreviation("US"));
        stateService.save(state);
    }
    
    public Customer createCustomer() {
    	return customerService.createCustomerFromId(null);
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
        customerAddress.setCustomer(customer);
        return saveCustomerAddress(customerAddress);
    }
    
    /**
     * Saves a customerAddress with state KY and country US.  Requires that createCountry() and createState() have been called
     * @param customerAddress
     * @return
     */
    public CustomerAddress saveCustomerAddress(CustomerAddress customerAddress) {
    	State state = stateService.findStateByAbbreviation("KY");
        customerAddress.getAddress().setState(state);
        Country country = countryService.findCountryByAbbreviation("US");
        customerAddress.getAddress().setCountry(country);
    	return customerAddressService.saveCustomerAddress(customerAddress);
    }
    
    /**
     * Create a state, country, and customer with a basic order and some addresses
     * @return
     */
    public Customer createCustomerWithBasicOrderAndAddresses() {
    	Customer customer = createCustomerWithAddresses();
        Order order = new OrderImpl();
        Auditable auditable = new Auditable();
        auditable.setDateCreated(new Date());
        order.setAuditable(auditable);
        order.setStatus(OrderStatus.IN_PROCESS);
        order.setTotal(new Money(BigDecimal.valueOf(1000)));
        
    	assert order.getId() == null;
        order.setCustomer(customer);
        order = orderDao.save(order);
        assert order.getId() != null;
        
        return customer;
    }


}
