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

package org.broadleafcommerce.common.email.service.message;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.common.email.domain.EmailTarget;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.util.Map;

import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

public abstract class MessageCreator {

    private JavaMailSender mailSender;

    public MessageCreator(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMessage(final Map<String, Object> props) throws MailException {
        MimeMessagePreparator preparator = buildMimeMessagePreparator(props);
        this.mailSender.send(preparator);
    }

    public abstract String buildMessageBody(EmailInfo info, Map<String, Object> props);

    public MimeMessagePreparator buildMimeMessagePreparator(final Map<String, Object> props) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                EmailTarget emailUser = (EmailTarget) props.get(EmailPropertyType.USER.getType());
                EmailInfo info = (EmailInfo) props.get(EmailPropertyType.INFO.getType());
                boolean isMultipart = CollectionUtils.isNotEmpty(info.getAttachments());
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, info.getEncoding());
                message.setTo(emailUser.getEmailAddress());
                message.setFrom(info.getFromAddress());
                message.setSubject(info.getSubject());
                if (emailUser.getBCCAddresses() != null && emailUser.getBCCAddresses().length > 0) {
                    message.setBcc(emailUser.getBCCAddresses());
                }
                if (emailUser.getCCAddresses() != null && emailUser.getCCAddresses().length > 0) {
                    message.setCc(emailUser.getCCAddresses());
                }
                String messageBody = info.getMessageBody();
                if (messageBody == null) {
                    messageBody = buildMessageBody(info, props);
                }
                message.setText(messageBody, true);
                for (Attachment attachment : info.getAttachments()) {
                    ByteArrayDataSource dataSource = new ByteArrayDataSource(attachment.getData(), attachment.getMimeType());
                    message.addAttachment(attachment.getFilename(), dataSource);
                }
            }
        };
        return preparator;

    }

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
}
