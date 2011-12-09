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
import org.broadleafcommerce.profile.vendor.service.cache.ServiceResponseCacheable;
import org.broadleafcommerce.test.BaseTest;
import org.broadleafcommerce.vendor.cybersource.service.CyberSourceServiceManager;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceBillingRequest;
import org.broadleafcommerce.vendor.cybersource.service.tax.CyberSourceTaxService;
import org.broadleafcommerce.vendor.cybersource.service.tax.message.CyberSourceTaxItemRequest;
import org.broadleafcommerce.vendor.cybersource.service.tax.message.CyberSourceTaxRequest;
import org.broadleafcommerce.vendor.cybersource.service.tax.message.CyberSourceTaxResponse;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class CyberSourceTaxServiceTest extends BaseTest {

    @Resource
    private CyberSourceServiceManager serviceManager;

    @Test(groups = { "testSuccessfulCyberSourceTax" })
    @Rollback(false)
    public void testSuccessfulCyberSourceTax() throws Exception {
        if (serviceManager.getMerchantId().equals("?")) {
            return;
        }
        CyberSourceTaxRequest taxRequest = new CyberSourceTaxRequest();
        taxRequest.setCurrency(Currency.getInstance(Locale.US).getCurrencyCode());
        taxRequest.setNexus("CA");
        taxRequest.setOrderAcceptancePostalCode("94043");
        //taxRequest.setOrderOriginPostalCode("94043");
        
        CyberSourceBillingRequest billingRequest = new CyberSourceBillingRequest();
        billingRequest.setCity("Mountain View");
        billingRequest.setPostalCode("94043");
        billingRequest.setState("CA");
        billingRequest.setStreet1("1295 Charleston Road");
        billingRequest.setCountry("US");
        
        taxRequest.setBillingRequest(billingRequest);
        
        CyberSourceTaxItemRequest itemRequest1 = new CyberSourceTaxItemRequest();
        itemRequest1.setDescription("First Item");
        itemRequest1.setQuantity(2L);
        itemRequest1.setShortDescription("firstItem");
        itemRequest1.setUnitPrice(new Money(12.34));
        
        taxRequest.getItemRequests().add(itemRequest1);
        
        CyberSourceTaxService service = (CyberSourceTaxService) serviceManager.getValidService(taxRequest);
        ((ServiceResponseCacheable) service).clearCache();
        CyberSourceTaxResponse response = (CyberSourceTaxResponse) service.process(taxRequest);

        assert(response.getReasonCode().intValue() == 100);
        assert(!response.getRequestToken().equals("from-cache"));
        Money totalTaxAmount = response.getItemResponses()[0].getTotalTaxAmount();
        assert(totalTaxAmount != null && totalTaxAmount.greaterThan(new Money(0D)));
        
        //confirm that we used the cache
        CyberSourceTaxResponse response2 = (CyberSourceTaxResponse) service.process(taxRequest);
        assert(response2.getReasonCode().intValue() == 100);
        assert(response2.getRequestToken().equals("from-cache"));
        Money totalTaxAmount2 = response2.getItemResponses()[0].getTotalTaxAmount();
        assert(totalTaxAmount2 != null && totalTaxAmount2.greaterThan(new Money(0D)));
        assert(totalTaxAmount.equals(totalTaxAmount2));
    }

}
