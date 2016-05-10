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
package org.broadleafcommerce.core.web.controller.account;

import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;

import java.io.Serializable;

public class CustomerAddressForm implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Address address = new AddressImpl();
    protected String addressName;
    protected Long customerAddressId;

    public CustomerAddressForm() {
        address.setPhonePrimary(new PhoneImpl());
        address.setPhoneSecondary(new PhoneImpl());
        address.setPhoneFax(new PhoneImpl());
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        if (address.getPhonePrimary() == null) {
            address.setPhonePrimary(new PhoneImpl());
        }
        if (address.getPhoneSecondary() == null) {
            address.setPhoneSecondary(new PhoneImpl());
        }
        if (address.getPhoneFax() == null) {
            address.setPhoneFax(new PhoneImpl());
        }
        this.address = address;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public Long getCustomerAddressId() {
        return customerAddressId;
    }

    public void setCustomerAddressId(Long customerAddressId) {
        this.customerAddressId = customerAddressId;
    }

}
