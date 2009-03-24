package org.broadleafcommerce.email.test;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

import org.broadleafcommerce.email.domain.AbstractEmailTarget;
import org.broadleafcommerce.email.domain.EmailTarget;
import org.broadleafcommerce.email.info.EmailInfo;
import org.broadleafcommerce.email.service.EmailService;
import org.broadleafcommerce.test.integration.BaseTest;
import org.testng.annotations.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

public class EmailTest extends BaseTest {
	
	@Resource(name="emailDeliveryServiceBLC")
	EmailService emailService;
	
	@Test
	public void testSynchronousEmail() throws Exception {
		GreenMail greenMail = new GreenMail(
				new ServerSetup[] {
					new ServerSetup(30000, "127.0.0.1", ServerSetup.PROTOCOL_SMTP)
				}
		);
	    greenMail.start();
	
	    EmailTarget target = new AbstractEmailTarget(){};
	    target.setEmailAddress("to@localhost");
	    EmailInfo info = new EmailInfo(null);
	    
	    HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("user", target);
        map.put("info", info);

        emailService.sendTemplateEmail(map);
        
        assert (1 == greenMail.getReceivedMessages().length);
        
        greenMail.stop();
	}

}
