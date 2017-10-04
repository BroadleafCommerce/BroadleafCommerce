package org.broadleafcommerce.common.sms.service;

import java.util.Map;

/**
 * @author Nick Crum ncrum
 */
public interface SMSMessageCreator {

    void sendMessage(String to, String from, String body, Map<String, Object> props);
}
