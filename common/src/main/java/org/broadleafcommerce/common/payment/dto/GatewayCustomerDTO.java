/*
 * #%L
 * BroadleafCommerce Common Libraries
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

package org.broadleafcommerce.common.payment.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class GatewayCustomerDTO {

    protected PaymentRequestDTO parent;

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

    public GatewayCustomerDTO(PaymentRequestDTO parent) {
        this.additionalFields = new HashMap<String, Object>();
        this.parent = parent;
    }

    public PaymentRequestDTO done() {
        return parent;
    }

    public GatewayCustomerDTO additionalFields(String key, Object value) {
        additionalFields.put(key, value);
        return this;
    }

    public GatewayCustomerDTO customerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public GatewayCustomerDTO firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public GatewayCustomerDTO lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public GatewayCustomerDTO companyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public GatewayCustomerDTO phone(String phone) {
        this.phone = phone;
        return this;
    }

    public GatewayCustomerDTO mobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public GatewayCustomerDTO fax(String fax) {
        this.fax = fax;
        return this;
    }

    public GatewayCustomerDTO website(String website) {
        this.website = website;
        return this;
    }

    public GatewayCustomerDTO email(String email) {
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

    public Boolean customerPopulated() {
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
