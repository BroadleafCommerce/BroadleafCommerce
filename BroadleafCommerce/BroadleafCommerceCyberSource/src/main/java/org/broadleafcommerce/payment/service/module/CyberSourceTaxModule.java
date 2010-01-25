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
package org.broadleafcommerce.payment.service.module;

import java.util.Currency;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.payment.service.PaymentContext;
import org.broadleafcommerce.pricing.service.module.TaxModule;
import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.vendor.cybersource.service.CyberSourceServiceManager;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceItemRequest;
import org.broadleafcommerce.vendor.cybersource.service.payment.message.CyberSourceCardRequest;
import org.broadleafcommerce.vendor.cybersource.service.payment.message.CyberSourceCardResponse;
import org.broadleafcommerce.vendor.cybersource.service.payment.type.CyberSourceTransactionType;
import org.broadleafcommerce.vendor.cybersource.service.tax.message.CyberSourceTaxRequest;

/**
 * Tax module that utilizes the Broadleaf Commerce API for CyberSource
 * tax calculation.
 * 
 * @author jfischer
 */
public class CyberSourceTaxModule implements TaxModule {

    public static final String MODULENAME = "cyberSourceTaxModule";

    protected String name = MODULENAME;
    private CyberSourceServiceManager serviceManager;

    public Order calculateTaxForOrder(Order order) {
        throw new RuntimeException("not implemented!");
        //TODO implement the module
    	//CyberSourceTaxRequest taxRequest = new CyberSourceTaxRequest();
		//setCurrency(order, taxRequest);
    }
    
    private void setCurrency(Order order, CyberSourceTaxRequest taxRequest) {
		Currency currency = order.getTotal().getCurrency();
        if (currency == null) {
        	currency = Money.defaultCurrency();
        }
        taxRequest.setCurrency(currency.getCurrencyCode());
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CyberSourceServiceManager getServiceManager() {
		return serviceManager;
	}

	public void setServiceManager(CyberSourceServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
}
