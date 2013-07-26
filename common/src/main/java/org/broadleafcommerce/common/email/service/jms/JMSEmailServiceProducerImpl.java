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
        }
        throw new IllegalArgumentException("The properties map must be Serializable");
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
