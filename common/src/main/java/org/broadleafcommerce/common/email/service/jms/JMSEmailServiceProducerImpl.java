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
package org.broadleafcommerce.common.email.service.jms;

import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.broadleafcommerce.common.email.service.message.EmailPropertyType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.Serializable;
import java.util.Map;

/**
 * @author jfischer
 *
 */
public class JMSEmailServiceProducerImpl implements JMSEmailServiceProducer {

    private JmsTemplate emailServiceTemplate;

    private Destination emailServiceDestination;

    public void send(final Map props) {
        if (props instanceof Serializable) {
            final Serializable sProps = (Serializable) props;
            emailServiceTemplate.send(emailServiceDestination, new MessageCreator() {
                public Message createMessage(Session session) throws JMSException {
                    ObjectMessage message = session.createObjectMessage(sProps);
                    EmailInfo info = (EmailInfo) props.get(EmailPropertyType.INFO.getType());
                    message.setJMSPriority(Integer.parseInt(info.getSendAsyncPriority()));
                    return message;
                }
            });
        } else {
            throw new IllegalArgumentException("The properties map must be Serializable");
        }
    }

    /**
     * @return the emailServiceTemplate
     */
    public JmsTemplate getEmailServiceTemplate() {
        return emailServiceTemplate;
    }

    /**
     * @param emailServiceTemplate the emailServiceTemplate to set
     */
    public void setEmailServiceTemplate(JmsTemplate emailServiceTemplate) {
        this.emailServiceTemplate = emailServiceTemplate;
    }

    /**
     * @return the emailServiceDestination
     */
    public Destination getEmailServiceDestination() {
        return emailServiceDestination;
    }

    /**
     * @param emailServiceDestination the emailServiceDestination to set
     */
    public void setEmailServiceDestination(Destination emailServiceDestination) {
        this.emailServiceDestination = emailServiceDestination;
    }

}
