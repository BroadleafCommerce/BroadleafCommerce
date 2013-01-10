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

package org.broadleafcommerce.vendor.cybersource.service.payment.message;

import java.util.List;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.vendor.cybersource.service.message.AutoNumberMemberIdList;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceBillingRequest;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceItemRequest;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceRequest;
import org.broadleafcommerce.vendor.cybersource.service.payment.type.CyberSourceMethodType;
import org.broadleafcommerce.vendor.cybersource.service.payment.type.CyberSourceTransactionType;
import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceServiceType;

/**
 * 
 * @author jfischer
 *
 */
public abstract class CyberSourcePaymentRequest extends CyberSourceRequest {
    
    private static final long serialVersionUID = 1L;
    
    protected CyberSourceBillingRequest billingRequest;
    protected String currency;
    protected List<CyberSourceItemRequest> itemRequests = new AutoNumberMemberIdList();
    protected CyberSourceTransactionType transactionType;
    protected CyberSourceMethodType methodType;
    protected Money grandTotal;
    protected Boolean useGrandTotal;

    public CyberSourcePaymentRequest(CyberSourceMethodType methodType) {
        super(CyberSourceServiceType.PAYMENT);
        this.methodType = methodType;
    }

    public CyberSourceTransactionType getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(CyberSourceTransactionType transactionType) {
        this.transactionType = transactionType;
    }
    
    public CyberSourceMethodType getMethodType() {
        return methodType;
    }
    
    public CyberSourceBillingRequest getBillingRequest() {
        return billingRequest;
    }
    
    public void setBillingRequest(CyberSourceBillingRequest billingRequest) {
        this.billingRequest = billingRequest;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public List<CyberSourceItemRequest> getItemRequests() {
        return itemRequests;
    }
    
    public void setItemRequests(List<CyberSourceItemRequest> itemRequests) {
        this.itemRequests = itemRequests;
    }

    public Money getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Money grandTotal) {
        this.grandTotal = grandTotal;
    }

    public Boolean getUseGrandTotal() {
        return useGrandTotal;
    }

    public void setUseGrandTotal(Boolean useGrandTotal) {
        this.useGrandTotal = useGrandTotal;
    }
    
}
