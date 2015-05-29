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
package org.broadleafcommerce.core.web.checkout.model;

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
    protected boolean useBillingAddress;

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

    public boolean isUseBillingAddress() {
        return useBillingAddress;
    }

    public void setUseBillingAddress(boolean useBillingAddress) {
        this.useBillingAddress = useBillingAddress;
    }
}
