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

package org.broadleafcommerce.email;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.PreDestroy;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;

public class DemoSMTP extends Thread {
	
	private static final Log LOG = LogFactory.getLog(DemoSMTP.class);

	private GreenMail greenMail;
	private Integer currentCount = 0;
	private int port;
	private String bindAddress;
    private boolean enabled = true;
	
	public DemoSMTP(int port, String bindAddress) {
		this.port = port;
		this.bindAddress = bindAddress;
		init();
		start();
	}
	
	public void init() {
        if (enabled) {
            greenMail = new GreenMail(
                new ServerSetup[] {
                    new ServerSetup(port, bindAddress, ServerSetup.PROTOCOL_SMTP)
                }
            );
            currentCount = 0;
            greenMail.start();
        }
	}

    @PreDestroy
    public void quit() {
        enabled = false;
        greenMail.stop();
    }
	
	@Override
	public void run() {
		super.run();
        while(enabled) {
        	try {
        		greenMail.waitForIncomingEmail(1000, 1);
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
