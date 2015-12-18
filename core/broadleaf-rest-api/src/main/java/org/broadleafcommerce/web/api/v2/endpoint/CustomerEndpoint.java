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
package org.broadleafcommerce.web.api.v2.endpoint;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.broadleafcommerce.core.web.api.BroadleafWebServicesException;
import org.broadleafcommerce.core.web.api.endpoint.BaseEndpoint;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.domain.CustomerAttribute;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.core.service.CustomerAddressService;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.web.api.v2.wrapper.CustomerAddressWrapper;
import org.broadleafcommerce.web.api.v2.wrapper.CustomerAttributeWrapper;
import org.broadleafcommerce.web.api.v2.wrapper.CustomerPaymentWrapper;
import org.broadleafcommerce.web.api.v2.wrapper.CustomerWrapper;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

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
        CustomerAddress address = customerAddressService.readCustomerAddressById(customerAddressId);
        Customer customer = customerService.readCustomerById(customerId);
        if (customer == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
        }
        if (address == null || !address.getCustomer().getId().equals(customerId)) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_ADDRESS_NOT_FOUND);
        }
        address.setAddressName(wrapper.getAddressName());
        address.setAddress(wrapper.getAddress().unwrap(request, context));
        address = customerAddressService.saveCustomerAddress(address);
        CustomerAddressWrapper response = (CustomerAddressWrapper) context.getBean(CustomerAddressWrapper.class.getName());
        response.wrapDetails(address, request);

        return response;
    }

    public List<CustomerAddressWrapper> removeAllAddresses(HttpServletRequest request, Long customerId) {
        List<CustomerAddress> addresses = customerAddressService.readActiveCustomerAddressesByCustomerId(customerId);
        if (!CollectionUtils.isEmpty(addresses)) {
            for (CustomerAddress address : addresses) {
                customerAddressService.deleteCustomerAddressById(address.getId());
            }
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
    
    public CustomerPayment addCustomerPayment(HttpServletRequest request, Long customerId, CustomerPaymentWrapper wrapper) {
        // TODO implement
        return null;
    }
    
    public List<CustomerPayment> removeAllCustomerPayments(HttpServletRequest request, Long customerId) {
        // TODO implement
        return null;
    }
    
    public CustomerPayment removeCustomerPayment(HttpServletRequest request, Long customerId, Long paymentId) {
        // TODO implement
        return null;
    }
    
    public CustomerPayment findCustomerPayment(HttpServletRequest request, Long customerId, Long paymentId) {
        // TODO implement
        return null;
    }
    
    public List<CustomerPayment> findAllCustomerPayments(HttpServletRequest request, Long customerId) {
        // TODO implement
        return null;
    }


}
