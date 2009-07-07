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
package org.broadleafcommerce.profile.test;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerImpl;
import org.broadleafcommerce.profile.domain.State;
import org.broadleafcommerce.profile.domain.StateImpl;
import org.broadleafcommerce.test.integration.BaseTest;
import org.broadleafcommerce.vendor.service.exception.AddressStandardizationException;
import org.broadleafcommerce.vendor.usps.service.USPSAddressVerificationService;
import org.broadleafcommerce.vendor.usps.service.message.USPSAddressStandardizationResponse;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

public class USPSServiceTest extends BaseTest {

    @Resource
    private USPSAddressVerificationService addressStandardizationService;
    private GreenMail greenMail;

    @BeforeClass
    protected void addressVerificationSetUp() throws Exception {
        super.setup();
        greenMail = new GreenMail(
                new ServerSetup[] {
                        new ServerSetup(30000, "127.0.0.1", ServerSetup.PROTOCOL_SMTP)
                }
        );
        greenMail.start();
    }

    @AfterClass
    protected void addressVerificationTearDown() throws Exception {
        super.tearDown();
        greenMail.stop();
    }

    private Address getValidAddress() {
        Customer customer = new CustomerImpl();
        customer.setId(new Long(7427));
        customer.setUsername("customer1");
        customer.setPassword("customer1");
        Address addr = new AddressImpl();
        addr.setAddressLine1("6406 Ivy Lane");
        addr.setCity("Greenbelt");
        State state = new StateImpl();
        state.setAbbreviation("MD");
        addr.setState(state);

        return addr;
    }

    @Test(groups = { "testBadAddress" })
    @Rollback(false)
    public void testBadAddress() throws Exception {
        if (addressStandardizationService.getUspsUserName().equals("?")) {
            return;
        }
        Address testAddress = getValidAddress();
        testAddress.setPostalCode("70057");
        State state = new StateImpl();
        state.setAbbreviation("CL");
        testAddress.setState(state);

        try {
            addressStandardizationService.standardizeAddress(testAddress);
            assert(false);
        } catch (AddressStandardizationException e) {
            assert(e.getStandardizationResponse().isErrorDetected());
        }
        assert(greenMail.waitForIncomingEmail(10000, 1));
        assert(greenMail.getReceivedMessages()[0].getSubject().contains("is reporting a status"));
    }

    @Test(groups = { "testException" }, dependsOnGroups="testBadAddress")
    @Rollback(false)
    public void testException() throws Exception {
        if (addressStandardizationService.getUspsUserName().equals("?")) {
            return;
        }
        try {
            addressStandardizationService.standardizeAddress(null);
            assert(false);
        } catch (Exception e) {
            assert(true);
        }
    }

    @Test(groups = { "testSuccessfulAddress" }, dependsOnGroups="testException")
    @Rollback(false)
    public void testSuccessfulAddress() throws Exception {
        if (addressStandardizationService.getUspsUserName().equals("?")) {
            return;
        }
        Address testAddress = getValidAddress();
        USPSAddressStandardizationResponse standardizedResponse = addressStandardizationService.standardizeAddress(testAddress);
        logger.debug("Get ZipCode: " + standardizedResponse.getAddress().getPostalCode());
        assert(!standardizedResponse.isErrorDetected());
        assert(greenMail.waitForIncomingEmail(10000, 1));
        assert(greenMail.getReceivedMessages()[0].getSubject().contains("is reporting a status"));
    }
}
