/*
 * Copyright 2008-2009 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.web.checkout.model;

import org.broadleafcommerce.common.web.form.CsrfProtectedForm;
import org.broadleafcommerce.core.order.domain.PersonalMessage;
import org.broadleafcommerce.core.order.domain.PersonalMessageImpl;
import org.broadleafcommerce.core.order.service.call.OrderMultishipOptionDTO;

import java.io.Serializable;

/**
 * This form is used to bind multiship options in a way that doesn't require
 * the actual objects to be instantiated -- we handle that at the controller
 * level.
 * 
 * 
 */
public class MultiShipInstructionForm extends CsrfProtectedForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected String deliveryMessage;
	protected PersonalMessage personalMessage = new PersonalMessageImpl();
	protected Long fulfillmentGroupId;
	
	public String getDeliveryMessage() {
		return deliveryMessage;
	}
	
	public void setDeliveryMessage(String deliveryMessage) {
		this.deliveryMessage = deliveryMessage;
	}
	
	public PersonalMessage getPersonalMessage() {
		return personalMessage;
	}
	
	public void setPersonalMessage(PersonalMessage personalMessage) {
		this.personalMessage = personalMessage;
	}

	public Long getfulfillmentGroupId() {
		return fulfillmentGroupId;
	}

	public void setfulfillmentGroupId(Long id) {
		this.fulfillmentGroupId = id;
	}
	
}
