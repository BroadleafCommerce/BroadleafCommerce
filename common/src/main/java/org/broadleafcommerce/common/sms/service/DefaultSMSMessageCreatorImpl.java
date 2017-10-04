package org.broadleafcommerce.common.sms.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.sms.service.type.SMSMessage;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * @author Nick Crum ncrum
 */
@Service("blSMSMessageCreator")
public class DefaultSMSMessageCreatorImpl implements SMSMessageCreator {

    protected final Log LOG = LogFactory.getLog(DefaultSMSMessageCreatorImpl.class);

    @Override
    public void sendMessage(SMSMessage message, Map<String, Object> props) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Attempt to send SMS message to " + message.getTo() + " from " + message.getFrom() + " with message body:\n" + message.getBody());
        }
    }
}
