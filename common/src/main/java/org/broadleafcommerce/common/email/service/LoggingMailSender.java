/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.email.service;

import java.io.ByteArrayOutputStream;

import javax.activation.DataHandler;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 * @author Andre Azzolini (apazzolini)
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
