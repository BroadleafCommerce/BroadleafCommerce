/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.rest.api.v2.endpoint.customer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.core.rest.api.exception.BroadleafWebServicesException;
import org.broadleafcommerce.core.rest.api.v2.wrapper.CustomerAddressWrapper;
import org.broadleafcommerce.core.rest.api.v2.wrapper.CustomerAttributeWrapper;
import org.broadleafcommerce.core.rest.api.v2.wrapper.CustomerPaymentWrapper;
import org.broadleafcommerce.core.rest.api.v2.wrapper.CustomerWrapper;
import org.broadleafcommerce.core.web.api.endpoint.BaseEndpoint;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.domain.CustomerAttribute;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.core.service.CustomerAddressService;
import org.broadleafcommerce.profile.core.service.CustomerPaymentService;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * This endpoint depends on JAX-RS.  It should be extended by components that actually wish 
 * to provide an endpoint.  The annotations such as @Path, @Scope, @Context, @PathParam, @QueryParam, 
 * @GET, @POST, @PUT, and @DELETE are purposely not provided here to allow implementors finer control over 
 * the details of the endpoint.
 * <p/>
 * User: Kelly Tisdell
 * Date: 4/10/12
 */
public abstract class CustomerEndpoint extends BaseEndpoint {

    @Resource(name="blCustomerService")
    protected CustomerService customerService;

    @Resource(name="blCustomerAddressService")
    protected CustomerAddressService customerAddressService;
    
    @Resource(name="blCustomerPaymentService")
    protected CustomerPaymentService customerPaymentService;

    public CustomerWrapper findCustomer(HttpServletRequest request, String emailAddress) {
        Customer customer = customerService.readCustomerByEmail(emailAddress);
        if (customer == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
        }

        CustomerWrapper response = (CustomerWrapper) context.getBean(CustomerWrapper.class.getName());
        response.wrapDetails(customer, request);

        return response;
    }

    public CustomerWrapper findCustomer(HttpServletRequest request, Long id) {
        Customer customer = customerService.readCustomerById(id);
        if (customer == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
        }

        CustomerWrapper response = (CustomerWrapper) context.getBean(CustomerWrapper.class.getName());
        response.wrapDetails(customer, request);

        return response;
    }

    public CustomerWrapper addCustomer(HttpServletRequest request, CustomerWrapper wrapper) {
        Customer customer = wrapper.unwrap(request, context);
        if (StringUtils.isEmpty(customer.getUsername())) {
            String userName = (StringUtils.isNotBlank(customer.getFirstName()) ? customer.getFirstName() : "") +
                              (StringUtils.isNotBlank(customer.getLastName()) ? customer.getLastName() : "") +
                              (StringUtils.isNotBlank(customer.getEmailAddress()) ? customer.getEmailAddress() : "");
            customer.setUsername(userName);
        }
        customer = customerService.saveCustomer(customer);

        CustomerWrapper response = (CustomerWrapper) context.getBean(CustomerWrapper.class.getName());
        response.wrapDetails(customer, request);

        return response;
    }

    public CustomerWrapper updateCustomer(HttpServletRequest request, Long id, CustomerWrapper wrapper) {
        Customer customer = customerService.readCustomerById(id);
        if (customer == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
        }
        Customer update = wrapper.unwrap(request, context);
        customer.setFirstName(update.getFirstName());
        customer.setLastName(update.getLastName());
        customer.setEmailAddress(update.getEmailAddress());
        if (!MapUtils.isEmpty(update.getCustomerAttributes())) {
            customer.getCustomerAttributes().clear();
            customer.getCustomerAttributes().putAll(update.getCustomerAttributes());
        }
        customer = customerService.saveCustomer(customer);

        CustomerWrapper response = (CustomerWrapper) context.getBean(CustomerWrapper.class.getName());
        response.wrapDetails(customer, request);

        return response;
    }

    public CustomerWrapper removeAllAttributes(HttpServletRequest request, Long id) {
        Customer customer = customerService.readCustomerById(id);
        if (customer == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
        }
        customer.getCustomerAttributes().clear();
        customer = customerService.saveCustomer(customer);

        CustomerWrapper response = (CustomerWrapper) context.getBean(CustomerWrapper.class.getName());
        response.wrapDetails(customer, request);

        return response;
    }

    public CustomerWrapper addAttribute(HttpServletRequest request, Long id, CustomerAttributeWrapper wrapper) {
        Customer customer = customerService.readCustomerById(id);
        if (customer == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
        }
        CustomerAttribute attribute = wrapper.unwrap(request, context);
        attribute.setCustomer(customer);
        customer.getCustomerAttributes().put(attribute.getName(), attribute);
        customer = customerService.saveCustomer(customer);

        CustomerWrapper response = (CustomerWrapper) context.getBean(CustomerWrapper.class.getName());
        response.wrapDetails(customer, request);

        return response;
    }

    public CustomerWrapper removeAttribute(HttpServletRequest request, Long id, String attributeName) {
        Customer customer = customerService.readCustomerById(id);
        if (customer == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
        }
        customer.getCustomerAttributes().remove(attributeName);
        customer = customerService.saveCustomer(customer);

        CustomerWrapper response = (CustomerWrapper) context.getBean(CustomerWrapper.class.getName());
        response.wrapDetails(customer, request);

        return response;
    }

    public CustomerAddressWrapper findAddress(HttpServletRequest request, Long customerId, String addressName) {
        List<CustomerAddress> addresses = customerAddressService.readActiveCustomerAddressesByCustomerId(customerId);
        CustomerAddressWrapper response = null;
        if (!CollectionUtils.isEmpty(addresses)) {
            for (CustomerAddress address : addresses) {
                if (address.getAddressName().equals(addressName)) {
                    response = (CustomerAddressWrapper) context.getBean(CustomerAddressWrapper.class.getName());
                    response.wrapDetails(address, request);
                    break;
                }
            }
        }
        if (response == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                    .addMessage(BroadleafWebServicesException.CUSTOMER_ADDRESS_NOT_FOUND);
        }
        return response;
    }

    public List<CustomerAddressWrapper> findAllAddress(HttpServletRequest request, Long customerId) {
        List<CustomerAddress> addresses = customerAddressService.readActiveCustomerAddressesByCustomerId(customerId);
        List<CustomerAddressWrapper> wrappers = new ArrayList<CustomerAddressWrapper>();
        if (!CollectionUtils.isEmpty(addresses)) {
            for (CustomerAddress address : addresses) {
                CustomerAddressWrapper wrapper = (CustomerAddressWrapper) context.getBean(CustomerAddressWrapper.class.getName());
                wrapper.wrapDetails(address, request);
                wrappers.add(wrapper);
            }
        }
        return wrappers;
    }

    public CustomerAddressWrapper addAddress(HttpServletRequest request, Long customerId, CustomerAddressWrapper wrapper) {
        Customer customer = customerService.readCustomerById(customerId);
        if (customer == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
        }
        CustomerAddress address = wrapper.unwrap(request, context);
        address.setCustomer(customer);
        address = customerAddressService.saveCustomerAddress(address);
        CustomerAddressWrapper response = (CustomerAddressWrapper) context.getBean(CustomerAddressWrapper.class.getName());
        response.wrapDetails(address, request);

        return response;
    }

    public CustomerAddressWrapper updateAddress(HttpServletRequest request, Long customerId, Long customerAddressId, CustomerAddressWrapper wrapper) {
        CustomerAddress customerAddress = customerAddressService.readCustomerAddressById(customerAddressId);
        Customer customer = customerService.readCustomerById(customerId);
        if (customer == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
        }
        if (customerAddress == null || !customerAddress.getCustomer().getId().equals(customerId)) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_ADDRESS_NOT_FOUND);
        }
        customerAddress.setAddressName(wrapper.getAddressName());
        Address newAddress = wrapper.getAddress().unwrap(request, context);
        Address oldAddress = customerAddress.getAddress();
        // Workaround for a hibernate issue where when replacing the country on the oldAddress with the same country causes a persisting detached entity error
        if (Objects.equals(newAddress.getCountry(), oldAddress.getCountry())) {
            newAddress.setCountry(oldAddress.getCountry());
        }
        customerAddress.setAddress(newAddress);
        customerAddress = customerAddressService.saveCustomerAddress(customerAddress);
        CustomerAddressWrapper response = (CustomerAddressWrapper) context.getBean(CustomerAddressWrapper.class.getName());
        response.wrapDetails(customerAddress, request);

        return response;
    }

    public CustomerWrapper removeAllAddresses(HttpServletRequest request, Long customerId) {
        List<CustomerAddress> addresses = customerAddressService.readActiveCustomerAddressesByCustomerId(customerId);
        if (!CollectionUtils.isEmpty(addresses)) {
            for (CustomerAddress address : addresses) {
                customerAddressService.deleteCustomerAddressById(address.getId());
            }
        }
        Customer customer = customerService.readCustomerById(customerId);
        CustomerWrapper wrapper = (CustomerWrapper) context.getBean(CustomerWrapper.class.getName());
        if (customer != null) {
            wrapper.wrapSummary(customer, request);
        }
        return wrapper;
    }

    public List<CustomerAddressWrapper> removeAddress(HttpServletRequest request, Long customerId, String addressName) {
        List<CustomerAddress> addresses = customerAddressService.readActiveCustomerAddressesByCustomerId(customerId);
        CustomerAddress hit = null;
        if (!CollectionUtils.isEmpty(addresses)) {
            for (CustomerAddress address : addresses) {
                if (address.getAddressName().equals(addressName)) {
                    hit = address;
                    customerAddressService.deleteCustomerAddressById(address.getId());
                    break;
                }
            }
        }
        if (hit == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                    .addMessage(BroadleafWebServicesException.CUSTOMER_ADDRESS_NOT_FOUND);
        }
        addresses = customerAddressService.readActiveCustomerAddressesByCustomerId(customerId);
        List<CustomerAddressWrapper> wrappers = new ArrayList<CustomerAddressWrapper>();
        if (!CollectionUtils.isEmpty(addresses)) {
            for (CustomerAddress address : addresses) {
                CustomerAddressWrapper wrapper = (CustomerAddressWrapper) context.getBean(CustomerAddressWrapper.class.getName());
                wrapper.wrapDetails(address, request);
                wrappers.add(wrapper);
            }
        }
        return wrappers;
    }
    
    public CustomerPaymentWrapper findCustomerPayment(HttpServletRequest request, Long customerId, Long paymentId) {
        Customer customer = customerService.readCustomerById(customerId);
        if (customer == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
        }
        CustomerPayment payment = customerPaymentService.readCustomerPaymentById(paymentId);
        if (payment == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_PAYMENT_NOT_FOUND);
        }
        
        if (ObjectUtils.notEqual(payment.getCustomer(), customer)) {
            throw BroadleafWebServicesException.build(HttpStatus.BAD_REQUEST.value())
                .addMessage(BroadleafWebServicesException.INVALID_CUSTOMER_FOR_PAYMENT);
        }
        
        CustomerPaymentWrapper response = (CustomerPaymentWrapper) context.getBean(CustomerPaymentWrapper.class.getName());
        response.wrapDetails(payment, request);
        return response;
    }
    
    public List<CustomerPaymentWrapper> findAllCustomerPayments(HttpServletRequest request, Long customerId) {
        Customer customer = customerService.readCustomerById(customerId);
        if (customer == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
        }
        
        List<CustomerPaymentWrapper> payments = new ArrayList<>();
        for (CustomerPayment payment : customer.getCustomerPayments()) {
            CustomerPaymentWrapper response = (CustomerPaymentWrapper) context.getBean(CustomerPaymentWrapper.class.getName());
            response.wrapDetails(payment, request);
            payments.add(response);
        }
        return payments;
    }
    
    public CustomerPaymentWrapper addCustomerPayment(HttpServletRequest request, Long customerId, CustomerPaymentWrapper wrapper) {
        CustomerPayment customerPayment = wrapper.unwrap(request, context);
        Customer customer = customerService.readCustomerById(customerId);
        
        if (customer == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
        }
        
        customerPayment.setCustomer(customer);
        customerPayment = customerPaymentService.saveCustomerPayment(customerPayment);
        customer.getCustomerPayments().add(customerPayment);
        customerService.saveCustomer(customer);
        CustomerPaymentWrapper response = (CustomerPaymentWrapper) context.getBean(CustomerPaymentWrapper.class.getName());
        response.wrapDetails(customerPayment, request);
        return response;
    }
    
    public CustomerWrapper removeAllCustomerPayments(HttpServletRequest request, Long customerId) {
        Customer customer = customerService.readCustomerById(customerId);
        if (customer == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
        }
        List<CustomerPayment> payments = customerPaymentService.readCustomerPaymentsByCustomerId(customerId);
        for (CustomerPayment payment : payments) {
            customerPaymentService.deleteCustomerPaymentFromCustomer(customer, payment);
        }
        CustomerWrapper wrapper = (CustomerWrapper) context.getBean(CustomerWrapper.class.getName());
        if (customer != null) {
            wrapper.wrapSummary(customer, request);
        }
        return wrapper;
    }
    
    public List<CustomerPaymentWrapper> removeCustomerPayment(HttpServletRequest request, Long customerId, Long paymentId) {
        Customer customer = customerService.readCustomerById(customerId);
        if (customer == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
        }
        CustomerPayment payment = customerPaymentService.readCustomerPaymentById(paymentId);
        if (payment == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_PAYMENT_NOT_FOUND);
        }
        customerPaymentService.deleteCustomerPaymentFromCustomer(customer, payment);
        List<CustomerPaymentWrapper> wrapper = new ArrayList<>();
        customer = customerService.readCustomerById(customerId);
        for (CustomerPayment cp : customer.getCustomerPayments()) {
            CustomerPaymentWrapper response = (CustomerPaymentWrapper) context.getBean(CustomerPaymentWrapper.class.getName());
            response.wrapDetails(cp, request);
            wrapper.add(response);
        }
        return wrapper;
    }
    
}
