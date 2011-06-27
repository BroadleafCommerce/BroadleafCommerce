package org.broadleafcommerce.email;

import java.io.ByteArrayOutputStream;

import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

public class DemoSMTP extends Thread {
	
	private static final Log LOG = LogFactory.getLog(DemoSMTP.class);

	private GreenMail greenMail;
	private Integer currentCount = 0;
	private int port;
	private String bindAddress;
	
	public DemoSMTP(int port, String bindAddress) {
		this.port = port;
		this.bindAddress = bindAddress;
		init();
		start();
	}
	
	public void init() {
		greenMail = new GreenMail(
            new ServerSetup[] {
                new ServerSetup(port, bindAddress, ServerSetup.PROTOCOL_SMTP)
            }
        );
		currentCount = 0;
		greenMail.start();
	}
	
	@Override
	public void run() {
		super.run();
        while(true) {
        	try {
        		greenMail.waitForIncomingEmail(10000, 1);
        		MimeMessage[] messages = greenMail.getReceivedMessages();
            	if (messages != null) {
            		for (int j = currentCount; j < messages.length; j++) {
            			ByteArrayOutputStream baos = new ByteArrayOutputStream();
            			messages[j].writeTo(baos);
            			baos.close();
            			LOG.info("Email Sent - See MimeMessage Output: " + new String(baos.toByteArray()));
            		}
            		currentCount = messages.length;
            	}
            	if (currentCount > 2) {
            		greenMail.stop();
            		init();
            	}
        	} catch (InterruptedException e) {
        		//do nothing
        	} catch (Exception e) {
        		LOG.warn("Unable to log demo email message", e);
        	}
        }
	}

	
}
