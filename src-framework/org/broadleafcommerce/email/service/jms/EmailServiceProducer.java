package org.broadleafcommerce.email.service.jms;

import java.util.HashMap;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.broadleafcommerce.email.service.info.EmailInfo;
import org.broadleafcommerce.email.service.message.EmailPropertyType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * @author jfischer
 *
 */
public class EmailServiceProducer {

    private JmsTemplate emailServiceTemplate;

    private Destination emailServiceDestination;

    @SuppressWarnings("unchecked")
    public void send(final HashMap props) {
        emailServiceTemplate.send(emailServiceDestination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage message = session.createObjectMessage(props);
                EmailInfo info = (EmailInfo) props.get(EmailPropertyType.INFO.toString());
                message.setJMSPriority(Integer.parseInt(info.getSendAsyncPriority()));
                return message;
            }
        });
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
