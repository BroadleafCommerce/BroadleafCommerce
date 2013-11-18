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
package org.broadleafcommerce.common.email.service.info;

import org.broadleafcommerce.common.email.service.message.Attachment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jfischer
 *
 */
public class EmailInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String emailType;
    private String emailTemplate;
    private String subject;
    private String fromAddress;
    private String messageBody;
    private String encoding;
    private List<Attachment> attachments = new ArrayList<Attachment>();

    private String sendEmailReliableAsync;
    private String sendAsyncPriority;

    /**
     * @return the emailType
     */
    public String getEmailType() {
        return emailType;
    }

    /**
     * @param emailType the emailType to set
     */
    public void setEmailType(String emailType) {
        this.emailType = emailType;
    }

    /**
     * @return the emailTemplate
     */
    public String getEmailTemplate() {
        return emailTemplate;
    }

    /**
     * @param emailTemplate the emailTemplate to set
     */
    public void setEmailTemplate(String emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the fromAddress
     */
    public String getFromAddress() {
        return fromAddress;
    }

    /**
     * @param fromAddress the fromAddress to set
     */
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    /**
     * @return the sendEmailReliableAsync
     */
    public String getSendEmailReliableAsync() {
        return sendEmailReliableAsync;
    }

    /**
     * @param sendEmailReliableAsync the sendEmailReliableAsync to set
     */
    public void setSendEmailReliableAsync(String sendEmailReliableAsync) {
        this.sendEmailReliableAsync = sendEmailReliableAsync;
    }

    /**
     * @return the sendAsyncPriority
     */
    public String getSendAsyncPriority() {
        return sendAsyncPriority;
    }

    /**
     * @param sendAsyncPriority the sendAsyncPriority to set
     */
    public void setSendAsyncPriority(String sendAsyncPriority) {
        this.sendAsyncPriority = sendAsyncPriority;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public synchronized EmailInfo clone() {
        EmailInfo info = new EmailInfo();
        info.setAttachments(attachments);
        info.setEmailTemplate(emailTemplate);
        info.setEmailType(emailType);
        info.setFromAddress(fromAddress);
        info.setMessageBody(messageBody);
        info.setSendAsyncPriority(sendAsyncPriority);
        info.setSendEmailReliableAsync(sendEmailReliableAsync);
        info.setSubject(subject);
        info.setEncoding(encoding);

        return info;
    }
}
