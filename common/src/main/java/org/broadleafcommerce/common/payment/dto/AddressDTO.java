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
/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.payment.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class AddressDTO<T> {

    protected T parent;

    protected Map<String, Object> additionalFields;

    protected String addressFirstName;
    protected String addressLastName;
    protected String addressCompanyName;
    protected String addressLine1;
    protected String addressLine2;
    protected String addressCityLocality;
    protected String addressStateRegion;
    protected String addressPostalCode;
    protected String addressCountryCode;
    protected String addressPhone;
    protected String addressEmail;

    public AddressDTO() {
        this.additionalFields = new HashMap<String, Object>();
    }

    public AddressDTO(T parent) {
        this.additionalFields = new HashMap<String, Object>();
        this.parent = parent;
    }

    public T done() {
        return parent;
    }

    public AddressDTO<T> additionalFields(String key, Object value) {
        additionalFields.put(key, value);
        return this;
    }

    public AddressDTO<T> addressFirstName(String addressFirstName) {
        this.addressFirstName = addressFirstName;
        return this;
    }

    public AddressDTO<T> addressLastName(String addressLastName) {
        this.addressLastName = addressLastName;
        return this;
    }

    public AddressDTO<T> addressCompanyName(String addressCompanyName) {
        this.addressCompanyName = addressCompanyName;
        return this;
    }

    public AddressDTO<T> addressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
        return this;
    }

    public AddressDTO<T> addressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
        return this;
    }

    public AddressDTO<T> addressCityLocality(String addressCityLocality) {
        this.addressCityLocality = addressCityLocality;
        return this;
    }

    public AddressDTO<T> addressStateRegion(String addressStateRegion) {
        this.addressStateRegion = addressStateRegion;
        return this;
    }

    public AddressDTO<T> addressPostalCode(String addressPostalCode) {
        this.addressPostalCode = addressPostalCode;
        return this;
    }

    public AddressDTO<T> addressCountryCode(String addressCountryCode) {
        this.addressCountryCode = addressCountryCode;
        return this;
    }

    public AddressDTO<T> addressPhone(String addressPhone) {
        this.addressPhone = addressPhone;
        return this;
    }

    public AddressDTO<T> addressEmail(String addressEmail) {
        this.addressEmail = addressEmail;
        return this;
    }

    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }

    public String getAddressFirstName() {
        return addressFirstName;
    }

    public String getAddressLastName() {
        return addressLastName;
    }

    public String getAddressCompanyName() {
        return addressCompanyName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getAddressCityLocality() {
        return addressCityLocality;
    }

    public String getAddressStateRegion() {
        return addressStateRegion;
    }

    public String getAddressPostalCode() {
        return addressPostalCode;
    }

    public String getAddressCountryCode() {
        return addressCountryCode;
    }

    public String getAddressPhone() {
        return addressPhone;
    }

    public String getAddressEmail() {
        return addressEmail;
    }

    public boolean addressPopulated() {
        return ((getAdditionalFields() != null && !getAdditionalFields().isEmpty()) ||
                getAddressFirstName() != null ||
                getAddressLastName() != null ||
                getAddressCompanyName() != null ||
                getAddressLine1() != null ||
                getAddressLine2() != null ||
                getAddressCityLocality() != null ||
                getAddressStateRegion() != null ||
                getAddressPostalCode() != null ||
                getAddressCountryCode() != null ||
                getAddressPhone() != null ||
                getAddressEmail() != null);
    }
    
}
