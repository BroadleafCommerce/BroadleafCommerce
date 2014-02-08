/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.common.email.service;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.broadleafcommerce.test.BaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Resource;

public class EmailTest extends BaseTest {

    @Resource
    EmailService emailService;

    private GreenMail greenMail;

    @BeforeClass
    protected void setupEmailTest() {
        greenMail = new GreenMail(
                new ServerSetup[] {
                        new ServerSetup(30000, "127.0.0.1", ServerSetup.PROTOCOL_SMTP)
                }
        );
        greenMail.start();
    }

    @AfterClass
    protected void tearDownEmailTest() {
        greenMail.stop();
    }

    @Test
    public void testSynchronousEmail() throws Exception {
        EmailInfo info = new EmailInfo();
        info.setFromAddress("me@test.com");
        info.setSubject("test");
        info.setEmailTemplate("org/broadleafcommerce/common/email/service/template/default.vm");
        info.setSendEmailReliableAsync("false");

        emailService.sendTemplateEmail("to@localhost", info, null);
    }

}
