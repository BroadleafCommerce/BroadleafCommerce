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
public class AddressDTO<T> {

    protected T parent;

    protected Map<String, Object> additionalFields;

    protected String addressFirstName;
    protected String addressLastName;
    protected String addressFullName;
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

    public AddressDTO<T> addressFullName(String addressFullName) {
        this.addressFullName = addressFullName;
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

    public String getAddressFullName() {
        return addressFullName;
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
