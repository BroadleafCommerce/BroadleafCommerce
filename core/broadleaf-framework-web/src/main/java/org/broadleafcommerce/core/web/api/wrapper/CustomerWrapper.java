/*
 * #%L
 * BroadleafCommerce Framework Web
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

package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerAttribute;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around FulfillmentGroupItem.
 * 
 * @deprecated - use {@link com.broadleafcommerce.core.rest.api.v2.wrapper.CustomerWrapper}
 * 
 * User: Elbert Bautista
 * Date: 4/18/12
 */
@Deprecated
@XmlRootElement(name = "customer")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class CustomerWrapper extends BaseWrapper implements APIWrapper<Customer>, APIUnwrapper<Customer> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected String firstName;

    @XmlElement
    protected String lastName;

    @XmlElement
    protected String emailAddress;

    @XmlElement(name = "customerAttribute")
    @XmlElementWrapper(name = "customerAttributes")
    protected List<CustomerAttributeWrapper> customerAttributes;

    @Override
    public void wrapDetails(Customer model, HttpServletRequest request) {
        this.id = model.getId();
        this.firstName = model.getFirstName();
        this.lastName = model.getLastName();
        this.emailAddress = model.getEmailAddress();
        if (model.getCustomerAttributes() != null && !model.getCustomerAttributes().isEmpty()) {
            Map<String, CustomerAttribute> itemAttributes = model.getCustomerAttributes();
            this.customerAttributes = new ArrayList<CustomerAttributeWrapper>();
            Set<String> keys = itemAttributes.keySet();
            for (String key : keys) {
                CustomerAttributeWrapper customerAttributeWrapper =
                        (CustomerAttributeWrapper) context.getBean(CustomerAttributeWrapper.class.getName());
                customerAttributeWrapper.wrapDetails(itemAttributes.get(key), request);
                this.customerAttributes.add(customerAttributeWrapper);
            }
        }
    }

    @Override
    public void wrapSummary(Customer model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    @Override
    public Customer unwrap(HttpServletRequest request, ApplicationContext context) {
        CustomerService customerService = (CustomerService) context.getBean("blCustomerService");
        Customer customer = customerService.createCustomerFromId(this.id);
        customer.setFirstName(this.firstName);
        customer.setLastName(this.lastName);
        customer.setEmailAddress(this.emailAddress);
        if (customerAttributes != null) {
            for (CustomerAttributeWrapper customerAttributeWrapper : customerAttributes) {
                CustomerAttribute attribute = customerAttributeWrapper.unwrap(request, context);
                attribute.setCustomer(customer);
                customer.getCustomerAttributes().put(attribute.getName(), attribute);
            }
        }
        return customer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<CustomerAttributeWrapper> getCustomerAttributes() {
        return customerAttributes;
    }

    public void setCustomerAttributes(List<CustomerAttributeWrapper> customerAttributes) {
        this.customerAttributes = customerAttributes;
    }
}
