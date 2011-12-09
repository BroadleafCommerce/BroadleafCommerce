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

package org.broadleafcommerce.vendor;

import java.util.Currency;
import java.util.Locale;

import javax.annotation.Resource;

import org.broadleafcommerce.money.Money;
import org.broadleafcommerce.test.BaseTest;
import org.broadleafcommerce.vendor.cybersource.service.CyberSourceServiceManager;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceBillingRequest;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceItemRequest;
import org.broadleafcommerce.vendor.cybersource.service.payment.CyberSourcePaymentService;
import org.broadleafcommerce.vendor.cybersource.service.payment.message.CyberSourceCardRequest;
import org.broadleafcommerce.vendor.cybersource.service.payment.message.CyberSourceCardResponse;
import org.broadleafcommerce.vendor.cybersource.service.payment.type.CyberSourceTransactionType;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class CyberSourcePaymentServiceTest extends BaseTest {

    @Resource
    private CyberSourceServiceManager serviceManager;

    @Test(groups = { "testSuccessfulCyberSourceCCPayment" })
    @Rollback(false)
    public void testSuccessfulCyberSourceCCPayment() throws Exception {
        if (serviceManager.getMerchantId().equals("?")) {
            return;
        }
        
        /*
         * authorize the amount
         */
        CyberSourceCardRequest cardRequest = new CyberSourceCardRequest();
        cardRequest.setTransactionType(CyberSourceTransactionType.AUTHORIZE);
        cardRequest.setCurrency(Currency.getInstance(Locale.US).getCurrencyCode());
        
        CyberSourceBillingRequest billingRequest = new CyberSourceBillingRequest();
        billingRequest.setCity("Mountain View");
        billingRequest.setFirstName("John");
        billingRequest.setLastName("Doe");
        billingRequest.setPostalCode("94043");
        billingRequest.setIpAddress("10.7.111.111");
        billingRequest.setState("CA");
        billingRequest.setStreet1("1295 Charleston Road");
        billingRequest.setCountry("US");
        billingRequest.setEmail("null@cybersource.com");
        
        cardRequest.setBillingRequest(billingRequest);
        
        CyberSourceItemRequest itemRequest1 = new CyberSourceItemRequest();
        itemRequest1.setDescription("First Item");
        itemRequest1.setQuantity(2L);
        itemRequest1.setShortDescription("firstItem");
        itemRequest1.setUnitPrice(new Money(12.34));
        
        cardRequest.getItemRequests().add(itemRequest1);
        
        CyberSourceItemRequest itemRequest2 = new CyberSourceItemRequest();
        itemRequest2.setDescription("Second Item");
        itemRequest2.setQuantity(1L);
        itemRequest2.setShortDescription("secondItem");
        itemRequest2.setUnitPrice(new Money(56.78));
        
        cardRequest.getItemRequests().add(itemRequest2);
        
        cardRequest.setAccountNumber("4111111111111111");
        cardRequest.setExpirationMonth(12);
        cardRequest.setExpirationYear(2020);
        
        CyberSourcePaymentService service = (CyberSourcePaymentService) serviceManager.getValidService(cardRequest);
        CyberSourceCardResponse response = (CyberSourceCardResponse) service.process(cardRequest);

        assert(response.getAuthResponse().getAmount().doubleValue() > 0D);
        assert(response.getReasonCode().intValue() == 100);
        
        /*
         * capture
         */
        CyberSourceCardRequest cardRequest2 = new CyberSourceCardRequest();
        cardRequest2.setTransactionType(CyberSourceTransactionType.CAPTURE);
        cardRequest2.setCurrency(Currency.getInstance(Locale.US).getCurrencyCode());
        cardRequest2.setRequestID(response.getRequestID());
        cardRequest2.setRequestToken(response.getRequestToken());
        cardRequest2.getItemRequests().add(itemRequest1);
        cardRequest2.getItemRequests().add(itemRequest2);
        
        CyberSourceCardResponse response2 = (CyberSourceCardResponse) service.process(cardRequest2);
        
        assert(response2.getReasonCode().intValue() == 100);
        
        /*
         * authorize and capture
         */
        cardRequest.setTransactionType(CyberSourceTransactionType.AUTHORIZEANDCAPTURE);
        CyberSourceCardResponse response3 = (CyberSourceCardResponse) service.process(cardRequest);

        assert(response3.getAuthResponse().getAmount().doubleValue() > 0D);
        assert(response3.getCaptureResponse().getAmount().doubleValue() > 0D);
        assert(response3.getReasonCode().intValue() == 100);
        
        /*
         * credit
         */
        CyberSourceCardRequest cardRequest4 = new CyberSourceCardRequest();
        cardRequest4.setTransactionType(CyberSourceTransactionType.CREDIT);
        cardRequest4.setCurrency(Currency.getInstance(Locale.US).getCurrencyCode());
        cardRequest4.setRequestID(response3.getRequestID());
        cardRequest4.setRequestToken(response3.getRequestToken());
        cardRequest4.getItemRequests().add(itemRequest1);
        cardRequest4.getItemRequests().add(itemRequest2);
        
        CyberSourceCardResponse response4 = (CyberSourceCardResponse) service.process(cardRequest4);
        
        assert(response4.getReasonCode().intValue() == 100);
        
        /*
         * void
         */
        //TODO does not appear to be working in the CyberSource test environment
        /*
        cardRequest.setTransactionType(CyberSourceTransactionType.AUTHORIZEANDCAPTURE);
        CyberSourceCardResponse response5 = (CyberSourceCardResponse) service.process(cardRequest);
        
        CyberSourceCardRequest cardRequest6 = new CyberSourceCardRequest();
        cardRequest6.setTransactionType(CyberSourceTransactionType.VOIDTRANSACTION);
        cardRequest6.setServiceType(CyberSourceServiceType.PAYMENT);
        cardRequest6.setMethodType(CyberSourceMethodType.CREDITCARD);
        cardRequest6.setCurrency(Currency.getInstance(Locale.US).getCurrencyCode());
        cardRequest6.setRequestID(response5.getRequestID());
        cardRequest6.setRequestToken(response5.getRequestToken());
        
        CyberSourceCardResponse response6 = (CyberSourceCardResponse) service.process(cardRequest6);
        
        assert(response6.getReasonCode().intValue() == 100);
        */
        
        /*
         * reverse authorize
         */
        cardRequest.setTransactionType(CyberSourceTransactionType.AUTHORIZE);
        CyberSourceCardResponse response7 = (CyberSourceCardResponse) service.process(cardRequest);
        
        CyberSourceCardRequest cardRequest7 = new CyberSourceCardRequest();
        cardRequest7.setTransactionType(CyberSourceTransactionType.REVERSEAUTHORIZE);
        cardRequest7.setCurrency(Currency.getInstance(Locale.US).getCurrencyCode());
        cardRequest7.setRequestID(response7.getRequestID());
        cardRequest7.setRequestToken(response7.getRequestToken());
        cardRequest7.getItemRequests().add(itemRequest1);
        cardRequest7.getItemRequests().add(itemRequest2);
        
        CyberSourceCardResponse response8 = (CyberSourceCardResponse) service.process(cardRequest7);
        
        assert(response8.getReasonCode().intValue() == 100);
    }

}
