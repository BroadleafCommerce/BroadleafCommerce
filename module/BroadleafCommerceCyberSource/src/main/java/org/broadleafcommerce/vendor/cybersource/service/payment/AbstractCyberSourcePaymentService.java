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
package org.broadleafcommerce.vendor.cybersource.service.payment;

import org.broadleafcommerce.vendor.cybersource.service.AbstractCyberSourceService;
import org.broadleafcommerce.vendor.cybersource.service.api.PurchaseTotals;
import org.broadleafcommerce.vendor.cybersource.service.api.RequestMessage;
import org.broadleafcommerce.vendor.cybersource.service.payment.message.CyberSourcePaymentRequest;

public abstract class AbstractCyberSourcePaymentService extends AbstractCyberSourceService {
    
    protected RequestMessage buildRequestMessage(CyberSourcePaymentRequest paymentRequest) {
        RequestMessage request = new RequestMessage();
        request.setMerchantID(getMerchantId());
        request.setMerchantReferenceCode(getIdGenerationService().findNextId("org.broadleafcommerce.vendor.cybersource.service.CyberSourcePaymentService").toString());
        request.setClientLibrary("Java Axis WSS4J");
        request.setClientLibraryVersion(getLibVersion());
        request.setClientEnvironment(
          System.getProperty("os.name") + "/" +
          System.getProperty("os.version") + "/" +
          System.getProperty("java.vendor") + "/" +
          System.getProperty("java.version")
        );
        
        PurchaseTotals purchaseTotals = new PurchaseTotals();
        purchaseTotals.setCurrency(paymentRequest.getCurrency());
        if (paymentRequest.getGrandTotal() != null && paymentRequest.getUseGrandTotal().booleanValue()) {
            purchaseTotals.setGrandTotalAmount(paymentRequest.getGrandTotal().toString());
        }
        request.setPurchaseTotals(purchaseTotals);
        
        return request;
    }

}
