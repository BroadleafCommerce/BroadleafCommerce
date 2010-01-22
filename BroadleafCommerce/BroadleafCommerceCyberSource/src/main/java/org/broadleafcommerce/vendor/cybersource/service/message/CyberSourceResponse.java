/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.vendor.cybersource.service.message;

import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceServiceType;
import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceTransactionType;
import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceMethodType;

public class CyberSourceResponse implements java.io.Serializable {

private static final long serialVersionUID = 1L;
	
	private CyberSourceTransactionType transactionType;
	private CyberSourceServiceType serviceType;
	private CyberSourceMethodType methodType;
	
	public CyberSourceTransactionType getTransactionType() {
		return transactionType;
	}
	
	public void setTransactionType(CyberSourceTransactionType transactionType) {
		this.transactionType = transactionType;
	}
	
	public CyberSourceServiceType getServiceType() {
		return serviceType;
	}
	
	public void setServiceType(CyberSourceServiceType serviceType) {
		this.serviceType = serviceType;
	}

	public CyberSourceMethodType getMethodType() {
		return methodType;
	}

	public void setMethodType(CyberSourceMethodType venueType) {
		this.methodType = venueType;
	}
	
}
