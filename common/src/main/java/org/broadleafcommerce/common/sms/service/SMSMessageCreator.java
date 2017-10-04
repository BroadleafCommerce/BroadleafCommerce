package org.broadleafcommerce.common.sms.service;

import org.broadleafcommerce.common.sms.service.type.SMSMessage;
import java.util.Map;

/**
 * @author Nick Crum ncrum
 */
public interface SMSMessageCreator {

    void sendMessage(SMSMessage message, Map<String, Object> props);
}
