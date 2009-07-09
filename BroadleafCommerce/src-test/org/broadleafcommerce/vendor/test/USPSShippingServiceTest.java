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
package org.broadleafcommerce.vendor.test;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.broadleafcommerce.test.integration.BaseTest;
import org.broadleafcommerce.util.UnitOfMeasureUtil;
import org.broadleafcommerce.vendor.service.type.WeightUnitOfMeasureType;
import org.broadleafcommerce.vendor.usps.service.USPSShippingCalculationService;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItem;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItemRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceResponse;
import org.broadleafcommerce.vendor.usps.service.type.ContainerSizeType;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class USPSShippingServiceTest extends BaseTest {

    @Resource
    private USPSShippingCalculationService shippingCalculationService;

    @Test(groups = { "testSuccessfulShippingCalc" })
    @Rollback(false)
    public void testSuccessfulShippingCalc() throws Exception {
        if (shippingCalculationService.getUspsUserName().equals("?")) {
            return;
        }
        USPSShippingPriceRequest request = new USPSShippingPriceRequest();
        USPSContainerItemRequest itemRequest = new USPSContainerItem();
        itemRequest.setContainerSize(ContainerSizeType.LARGE);
        itemRequest.setPackageId("0");
        itemRequest.setWeight(BigDecimal.valueOf(10L).add(UnitOfMeasureUtil.convertOuncesToPounds(BigDecimal.valueOf(5L))));
        itemRequest.setWeightUnitOfMeasureType(WeightUnitOfMeasureType.POUNDS);
        itemRequest.setZipDestination("20008");
        itemRequest.setZipOrigination("10022");
        request.getContainerItems().add(itemRequest);

        USPSShippingPriceResponse response = shippingCalculationService.retrieveShippingRates(request);
        assert(response.getResponses().peek().getRates().size() > 0);
    }

}
