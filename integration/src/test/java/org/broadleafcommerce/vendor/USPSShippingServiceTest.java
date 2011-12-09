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

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.util.UnitOfMeasureUtil;
import org.broadleafcommerce.profile.util.WeightUnitOfMeasureType;
import org.broadleafcommerce.test.BaseTest;
import org.broadleafcommerce.vendor.usps.service.USPSShippingCalculationService;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItem;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItemRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceResponse;
import org.broadleafcommerce.vendor.usps.service.type.USPSContainerShapeType;
import org.broadleafcommerce.vendor.usps.service.type.USPSContainerSizeType;
import org.broadleafcommerce.vendor.usps.service.type.USPSServiceType;
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
        itemRequest.setService(USPSServiceType.ALL);
        itemRequest.setContainerSize(USPSContainerSizeType.LARGE);
        itemRequest.setPackageId("0");
        itemRequest.setWeight(BigDecimal.valueOf(10L).add(UnitOfMeasureUtil.convertOuncesToPounds(BigDecimal.valueOf(5L))));
        itemRequest.setWeightUnitOfMeasureType(WeightUnitOfMeasureType.POUNDS);
        itemRequest.setZipDestination("20008");
        itemRequest.setZipOrigination("10022");
        itemRequest.setMachineSortable(true);
        request.getContainerItems().add(itemRequest);

        USPSShippingPriceResponse response = shippingCalculationService.retrieveShippingRates(request);
        assert(response.getResponses().peek().getPostage().size() > 0);

        USPSShippingPriceRequest request2 = new USPSShippingPriceRequest();
        USPSContainerItemRequest itemRequest2 = new USPSContainerItem();
        itemRequest2.setService(USPSServiceType.PRIORITY);
        itemRequest2.setContainerSize(USPSContainerSizeType.REGULAR);
        itemRequest2.setContainerShape(USPSContainerShapeType.FLATRATEBOX);
        itemRequest2.setPackageId("0");
        itemRequest2.setWeight(BigDecimal.valueOf(10L).add(UnitOfMeasureUtil.convertOuncesToPounds(BigDecimal.valueOf(5L))));
        itemRequest2.setWeightUnitOfMeasureType(WeightUnitOfMeasureType.POUNDS);
        itemRequest2.setZipDestination("20008");
        itemRequest2.setZipOrigination("10022");
        request2.getContainerItems().add(itemRequest2);

        USPSShippingPriceResponse response2 = shippingCalculationService.retrieveShippingRates(request2);
        assert(response2.getResponses().peek().getPostage().size() > 0);

        //the following are only compatible with the v3 schema - production

        /*USPSShippingPriceRequest request3 = new USPSShippingPriceRequest();
        USPSContainerItemRequest itemRequest3_1 = new USPSContainerItem();
        itemRequest3_1.setService(USPSServiceType.FIRSTCLASS);
        itemRequest3_1.setFirstClassType(USPSFirstClassType.LETTER);
        itemRequest3_1.setContainerSize(USPSContainerSizeType.REGULAR);
        itemRequest3_1.setPackageId("0");
        itemRequest3_1.setWeight(UnitOfMeasureUtil.convertOuncesToPounds(BigDecimal.valueOf(3.5)));
        itemRequest3_1.setWeightUnitOfMeasureType(WeightUnitOfMeasureType.POUNDS);
        itemRequest3_1.setZipDestination("20770");
        itemRequest3_1.setZipOrigination("44106");
        itemRequest3_1.setMachineSortable(true);
        request3.getContainerItems().add(itemRequest3_1);

        USPSContainerItemRequest itemRequest3_2 = new USPSContainerItem();
        itemRequest3_2.setService(USPSServiceType.PRIORITY);
        itemRequest3_2.setContainerSize(USPSContainerSizeType.LARGE);
        itemRequest3_2.setContainerShape(USPSContainerShapeType.NONRECTANGULAR);
        itemRequest3_2.setPackageId("1");
        itemRequest3_2.setWeight(BigDecimal.valueOf(1L).add(UnitOfMeasureUtil.convertOuncesToPounds(BigDecimal.valueOf(8L))));
        itemRequest3_2.setWeightUnitOfMeasureType(WeightUnitOfMeasureType.POUNDS);
        itemRequest3_2.setZipDestination("20770");
        itemRequest3_2.setZipOrigination("44106");
        itemRequest3_2.setWidth(BigDecimal.valueOf(15));
        itemRequest3_2.setHeight(BigDecimal.valueOf(15));
        itemRequest3_2.setDepth(BigDecimal.valueOf(30));
        itemRequest3_2.setGirth(BigDecimal.valueOf(55));
        itemRequest3_2.setDimensionUnitOfMeasureType(DimensionUnitOfMeasureType.INCHES);
        request3.getContainerItems().add(itemRequest3_2);

        USPSContainerItemRequest itemRequest3_3 = new USPSContainerItem();
        itemRequest3_3.setService(USPSServiceType.ALL);
        itemRequest3_3.setContainerSize(USPSContainerSizeType.REGULAR);
        itemRequest3_3.setPackageId("2");
        itemRequest3_3.setWeight(BigDecimal.valueOf(8L).add(UnitOfMeasureUtil.convertOuncesToPounds(BigDecimal.valueOf(32L))));
        itemRequest3_3.setWeightUnitOfMeasureType(WeightUnitOfMeasureType.POUNDS);
        itemRequest3_3.setZipDestination("96698");
        itemRequest3_3.setZipOrigination("90210");
        itemRequest3_3.setMachineSortable(true);
        request3.getContainerItems().add(itemRequest3_3);

        USPSShippingPriceResponse response3 = shippingCalculationService.retrieveShippingRates(request3);
        assert(response3.getResponses().peek().getPostage().size() > 0);

        USPSShippingPriceRequest request4 = new USPSShippingPriceRequest();
        USPSContainerItemRequest itemRequest4_1 = new USPSContainerItem();
        itemRequest4_1.setService(USPSServiceType.ALL);
        itemRequest4_1.setContainerSize(USPSContainerSizeType.REGULAR);
        itemRequest4_1.setPackageId("0");
        itemRequest4_1.setWeight(UnitOfMeasureUtil.convertOuncesToPounds(BigDecimal.valueOf(3.5)));
        itemRequest4_1.setWeightUnitOfMeasureType(WeightUnitOfMeasureType.POUNDS);
        itemRequest4_1.setZipDestination("20770");
        itemRequest4_1.setZipOrigination("44106");
        itemRequest4_1.setMachineSortable(true);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 2);
        itemRequest4_1.setShipDate(cal.getTime());
        itemRequest4_1.setReturnLocations(true);

        request4.getContainerItems().add(itemRequest4_1);
        USPSShippingPriceResponse response4 = shippingCalculationService.retrieveShippingRates(request4);
        assert(response4.getResponses().peek().getPostage().size() > 0);*/
    }

}
