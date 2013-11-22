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
 * User: Elbert Bautista
 * Date: 4/18/12
 */
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
        Customer customer = customerService.readCustomerById(this.id);
        customer.setId(this.id);
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
}
