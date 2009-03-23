package org.broadleafcommerce.email.jms;

import java.util.HashMap;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

/**
 * @author jfischer
 *
 */
@Component("emailServiceProducer")
public class EmailServiceProducer {

    //@Resource
    private JmsTemplate emailServiceTemplate;

    //@Resource(name="emailServiceQueue")
    private Destination emailServiceDestination;

    @SuppressWarnings("unchecked")
    public void send(final HashMap props) {
        emailServiceTemplate.send(emailServiceDestination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage message = session.createObjectMessage(props);
                return message;
            }
        });
    }
}
