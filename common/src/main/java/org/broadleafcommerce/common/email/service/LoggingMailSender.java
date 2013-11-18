/*
 * #%L
 * BroadleafCommerce Common Libraries
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import java.io.ByteArrayOutputStream;
import javax.activation.DataHandler;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * @aufthor Andre Azzolini (apazzolini)
 */
public class LoggingMailSender extends JavaMailSenderImpl {
    private static final Log LOG = LogFactory.getLog(LoggingMailSender.class);

    @Override
    public void send(MimeMessagePreparator[] mimeMessagePreparators) throws MailException {
        for (MimeMessagePreparator preparator : mimeMessagePreparators) {
            try {
                MimeMessage mimeMessage = createMimeMessage();
                preparator.prepare(mimeMessage);
                LOG.info("\"Sending\" email: ");
                if (mimeMessage.getContent() instanceof MimeMultipart) {
                    MimeMultipart msg = (MimeMultipart) mimeMessage.getContent();
                    DataHandler dh = msg.getBodyPart(0).getDataHandler();
                    ByteArrayOutputStream baos = null;
                    try {
                        baos = new ByteArrayOutputStream();
                        dh.writeTo(baos);
                    } catch (Exception e) {
                        // Do nothing
                    } finally {
                        try {
                            baos.close();
                        } catch (Exception e) {
                            LOG.error("Couldn't close byte array output stream");
                        }
                    }
                } else {
                    LOG.info(mimeMessage.getContent());
                }
            } catch (Exception e) {
                LOG.error("Could not create message", e);
            }
        }
    }

}
