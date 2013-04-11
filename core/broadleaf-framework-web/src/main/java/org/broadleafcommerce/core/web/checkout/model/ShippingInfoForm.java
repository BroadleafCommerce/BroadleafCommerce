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

	private static final long serialVersionUID = -7895489234675056031L;
	protected Address address = new AddressImpl();
    protected String addressName;
    protected FulfillmentOption fulfillmentOption;
    protected Long fulfillmentOptionId;
    protected PersonalMessage personalMessage = new PersonalMessageImpl();
    protected String deliveryMessage;

    public ShippingInfoForm() {
        address.setPhonePrimary(new PhoneImpl());
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
    
}
