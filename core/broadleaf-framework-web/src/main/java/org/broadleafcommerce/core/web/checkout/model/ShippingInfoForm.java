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
package org.broadleafcommerce.core.web.checkout.model;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.domain.PersonalMessage;
import org.broadleafcommerce.core.order.domain.PersonalMessageImpl;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;

import java.io.Serializable;

/**
 * A form to model adding a shipping address with shipping options.
 * 
 * @author Elbert Bautista (ebautista)
 * @author Andre Azzolini (apazzolini)
 */
public class ShippingInfoForm implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Address address = new AddressImpl();
    protected String addressName;
    protected FulfillmentOption fulfillmentOption;
    protected Long fulfillmentOptionId;
    protected PersonalMessage personalMessage = new PersonalMessageImpl();
    protected String deliveryMessage;
    protected boolean useBillingAddress = false;
    protected boolean saveAsDefault = false;

    public ShippingInfoForm() {
        address.setPhonePrimary(new PhoneImpl());
        address.setPhoneSecondary(new PhoneImpl());
        address.setPhoneFax(new PhoneImpl());
    }

    public Long getFulfillmentOptionId() {
        return fulfillmentOptionId;
    }
    
    public void setFulfillmentOptionId(Long fulfillmentOptionId) {
        this.fulfillmentOptionId = fulfillmentOptionId;
    }
    
    public FulfillmentOption getFulfillmentOption() {
        return fulfillmentOption;
    }

    public void setFulfillmentOption(FulfillmentOption fulfillmentOption) {
        this.fulfillmentOption = fulfillmentOption;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    } 
    
    public String getDeliveryMessage() {
        return deliveryMessage;
    }
    
    public void setDeliveryMessage(String deliveryMessage) {
        this.deliveryMessage = deliveryMessage;
    }
    
    public void setPersonalMessage(PersonalMessage personalMessage) {
        this.personalMessage = personalMessage;
    }
    
    public PersonalMessage getPersonalMessage() {
        return personalMessage;
    }

    @Deprecated
    public boolean isUseBillingAddress() {
        return useBillingAddress;
    }

    public boolean shouldUseBillingAddress() {
        return useBillingAddress;
    }

    public void setUseBillingAddress(boolean useBillingAddress) {
        this.useBillingAddress = useBillingAddress;
    }

    public boolean isSaveAsDefault() {
        return saveAsDefault;
    }

    public void setSaveAsDefault(boolean saveAsDefault) {
        this.saveAsDefault = saveAsDefault;
    }

    /**
     * NOTE: this looks for all of {@link Address}'s database required fields
     */
    public boolean hasValidAddress() {
        return getAddress() != null
                && StringUtils.isNotBlank(getAddress().getAddressLine1())
                && StringUtils.isNotBlank(getAddress().getCity());
    }
}
