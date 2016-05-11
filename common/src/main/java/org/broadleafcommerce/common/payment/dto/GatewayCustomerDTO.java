/*
 * #%L
 * BroadleafCommerce Common Libraries
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

package org.broadleafcommerce.common.payment.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class GatewayCustomerDTO<T> {

    protected T parent;

    protected Map<String, Object> additionalFields;
    protected String customerId;
    protected String firstName;
    protected String lastName;
    protected String companyName;
    protected String phone;
    protected String mobile;
    protected String fax;
    protected String website;
    protected String email;

    public GatewayCustomerDTO() {
        this.additionalFields = new HashMap<String, Object>();
    }

    public GatewayCustomerDTO(T parent) {
        this.additionalFields = new HashMap<String, Object>();
        this.parent = parent;
    }

    public T done() {
        return parent;
    }

    public GatewayCustomerDTO<T> additionalFields(String key, Object value) {
        additionalFields.put(key, value);
        return this;
    }

    public GatewayCustomerDTO<T> customerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public GatewayCustomerDTO<T> firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public GatewayCustomerDTO<T> lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public GatewayCustomerDTO<T> companyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public GatewayCustomerDTO<T> phone(String phone) {
        this.phone = phone;
        return this;
    }

    public GatewayCustomerDTO<T> mobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public GatewayCustomerDTO<T> fax(String fax) {
        this.fax = fax;
        return this;
    }

    public GatewayCustomerDTO<T> website(String website) {
        this.website = website;
        return this;
    }

    public GatewayCustomerDTO<T> email(String email) {
        this.email = email;
        return this;
    }

    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getPhone() {
        return phone;
    }

    public String getMobile() {
        return mobile;
    }

    public String getFax() {
        return fax;
    }

    public String getWebsite() {
        return website;
    }

    public String getEmail() {
        return email;
    }

    public boolean customerPopulated() {
        return ((getAdditionalFields() != null && !getAdditionalFields().isEmpty()) ||
                getCustomerId() != null ||
                getFirstName() != null ||
                getLastName() != null ||
                getCompanyName() != null ||
                getPhone() != null ||
                getMobile() != null ||
                getFax() != null ||
                getWebsite() != null ||
                getEmail() != null);
    }
}
