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
package org.broadleafcommerce.email.service.message;

import java.util.HashMap;

import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.apache.velocity.app.VelocityEngine;
import org.broadleafcommerce.email.domain.EmailTarget;
import org.broadleafcommerce.email.service.info.EmailInfo;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

public class MessageCreator {

    private VelocityEngine velocityEngine;
    private JavaMailSender mailSender;
    private HashMap<String, Object> additionalConfigItems;

    public MessageCreator(VelocityEngine velocityEngine, JavaMailSender mailSender, HashMap<String, Object> additionalConfigItems) {
        this.velocityEngine = velocityEngine;
        this.mailSender = mailSender;
        this.additionalConfigItems = additionalConfigItems;
    }

    @SuppressWarnings("unchecked")
    public void sendMessage(final HashMap props) throws MailException {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                EmailTarget emailUser = (EmailTarget) props.get(EmailPropertyType.USER.getType());
                EmailInfo info = (EmailInfo) props.get(EmailPropertyType.INFO.getType());
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, (info.getAttachments() != null && info.getAttachments().size() > 0));
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
                String text;
                if (messageBody == null) {
                    HashMap copy = (HashMap) props.clone();
                    if (additionalConfigItems != null) {
                        copy.putAll(additionalConfigItems);
                    }
                    text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, info.getEmailTemplate(), copy);
                } else {
                    text = messageBody;
                }
                message.setText(text, true);
                for (Attachment attachment : info.getAttachments()) {
                    ByteArrayDataSource dataSource = new ByteArrayDataSource(attachment.getData(), attachment.getMimeType());
                    message.addAttachment(attachment.getFilename(), dataSource);
                }
            }
        };
        this.mailSender.send(preparator);
    }
}
