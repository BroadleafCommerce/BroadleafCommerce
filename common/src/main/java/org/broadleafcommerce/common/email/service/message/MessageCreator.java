/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.email.service.message;

import org.broadleafcommerce.common.email.domain.EmailTarget;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.util.Map;

public abstract class MessageCreator {

    private JavaMailSender mailSender;
        
    public MessageCreator(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMessage(final Map<String,Object> props) throws MailException {
        MimeMessagePreparator preparator = buildMimeMessagePreparator(props);
        this.mailSender.send(preparator);
    }
    
    public abstract String buildMessageBody(EmailInfo info, Map<String,Object> props);
    
    public MimeMessagePreparator buildMimeMessagePreparator(final Map<String,Object> props) {
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
