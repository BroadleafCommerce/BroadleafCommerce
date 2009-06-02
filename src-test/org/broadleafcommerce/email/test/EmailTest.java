package org.broadleafcommerce.email.test;

import javax.annotation.Resource;

import org.broadleafcommerce.email.service.EmailService;
import org.broadleafcommerce.email.service.info.EmailInfo;
import org.broadleafcommerce.test.integration.BaseTest;
import org.testng.annotations.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

public class EmailTest extends BaseTest {

    @Resource
    EmailService emailService;

    @Test
    public void testSynchronousEmail() throws Exception {
        GreenMail greenMail = new GreenMail(
                new ServerSetup[] {
                        new ServerSetup(30000, "127.0.0.1", ServerSetup.PROTOCOL_SMTP)
                }
        );
        greenMail.start();

        emailService.sendTemplateEmail("to@localhost", new EmailInfo(), null);

        assert(greenMail.waitForIncomingEmail(10000, 1));

        greenMail.stop();
    }

}
