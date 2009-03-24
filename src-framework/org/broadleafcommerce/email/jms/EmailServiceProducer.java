package org.broadleafcommerce.email.jms;

import java.util.HashMap;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.broadleafcommerce.email.info.EmailInfo;
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
                EmailInfo info = (EmailInfo) props.get("info");
                message.setJMSPriority(Integer.parseInt(info.getSendAsyncPriority()));
                return message;
            }
        });
    }
}
