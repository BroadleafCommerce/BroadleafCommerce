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

package org.broadleafcommerce.common.email.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.common.email.dao.EmailReportingDao;
import org.broadleafcommerce.common.email.domain.EmailTarget;
import org.broadleafcommerce.common.email.service.exception.EmailException;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.broadleafcommerce.common.email.service.info.NullEmailInfo;
import org.broadleafcommerce.common.email.service.info.ServerInfo;
import org.broadleafcommerce.common.email.service.message.EmailPropertyType;
import org.broadleafcommerce.common.email.service.message.EmailServiceProducer;
import org.broadleafcommerce.common.email.service.message.MessageCreator;
import org.springframework.stereotype.Service;

/**
 * @author jfischer
 */
@Service("blEmailService")
public class EmailServiceImpl implements EmailService {

    @Resource(name = "blEmailTrackingManager")
    protected EmailTrackingManager emailTrackingManager;

    @Resource(name = "blServerInfo")
    protected ServerInfo serverInfo;

    protected EmailServiceProducer emailServiceProducer;

    @Resource(name = "blMessageCreator")
    protected MessageCreator messageCreator;

    @Resource(name = "blEmailReportingDao")
    protected EmailReportingDao emailReportingDao;

    public boolean sendTemplateEmail(EmailTarget emailTarget, EmailInfo emailInfo, Map<String, Object> props) {
        if (props == null) {
            props = new HashMap<String, Object>();
        }
        if (emailInfo == null) {
            emailInfo = new EmailInfo();
        }

        props.put(EmailPropertyType.INFO.getType(), emailInfo);
        props.put(EmailPropertyType.USER.getType(), emailTarget);
        Long emailId = emailTrackingManager.createTrackedEmail(emailTarget.getEmailAddress(), emailInfo.getEmailType(), null);
        props.put("emailTrackingId", emailId);

        return sendBasicEmail(emailInfo, emailTarget, props);
    }

    public boolean sendTemplateEmail(String emailAddress, EmailInfo emailInfo, Map<String, Object> props) {
        if (!(emailInfo instanceof NullEmailInfo)) {
            EmailTarget emailTarget = emailReportingDao.createTarget();
            emailTarget.setEmailAddress(emailAddress);
            return sendTemplateEmail(emailTarget, emailInfo, props);
        } else {
            return true;
        }
    }

    public boolean sendBasicEmail(EmailInfo emailInfo, EmailTarget emailTarget, Map<String, Object> props) {
        if (props == null) {
            props = new HashMap<String, Object>();
        }
        if (emailInfo == null) {
            emailInfo = new EmailInfo();
        }

        props.put(EmailPropertyType.INFO.getType(), emailInfo);
        props.put(EmailPropertyType.USER.getType(), emailTarget);
        // This is a temporary fix for a bug with Thymeleaf 2.0.17 where it tries to get a RequestContext from a theme variable name
        Object themes = props.get("themes");
        if (themes == null) {
            props.put("themes", "");
        }


        if (Boolean.parseBoolean(emailInfo.getSendEmailReliableAsync())) {
            if (emailServiceProducer == null) {
                throw new EmailException("The property sendEmailReliableAsync on EmailInfo is true, but the EmailService does not have an instance of JMSEmailServiceProducer set.");
            }
            emailServiceProducer.send(props);
        } else {
            messageCreator.sendMessage(props);
        }

        return true;
    }

    /**
     * @return the emailTrackingManager
     */
    public EmailTrackingManager getEmailTrackingManager() {
        return emailTrackingManager;
    }

    /**
     * @param emailTrackingManager the emailTrackingManager to set
     */
    public void setEmailTrackingManager(EmailTrackingManager emailTrackingManager) {
        this.emailTrackingManager = emailTrackingManager;
    }

    /**
     * @return the serverInfo
     */
    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    /**
     * @param serverInfo the serverInfo to set
     */
    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    /**
     * @return the emailServiceProducer
     */
    public EmailServiceProducer getEmailServiceProducer() {
        return emailServiceProducer;
    }

    /**
     * @param emailServiceProducer the emailServiceProducer to set
     */
    public void setEmailServiceProducer(EmailServiceProducer emailServiceProducer) {
        this.emailServiceProducer = emailServiceProducer;
    }

    /**
     * @return the messageCreator
     */
    public MessageCreator getMessageCreator() {
        return messageCreator;
    }

    /**
     * @param messageCreator the messageCreator to set
     */
    public void setMessageCreator(MessageCreator messageCreator) {
        this.messageCreator = messageCreator;
    }

}
