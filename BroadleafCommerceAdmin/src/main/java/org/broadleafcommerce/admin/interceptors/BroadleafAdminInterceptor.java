package org.broadleafcommerce.admin.interceptors;

import org.springframework.flex.core.MessageInterceptor;
import org.springframework.flex.core.MessageProcessingContext;

import flex.messaging.messages.Message;

public class BroadleafAdminInterceptor implements MessageInterceptor {

    public Message postProcess(MessageProcessingContext context, Message inputMessage,
            Message outputMessage) {
//        Object result =  outputMessage.getBody();
//        outputMessage.setBody(result);
        return outputMessage;
    }

    public Message preProcess(MessageProcessingContext context, Message inputMessage) {
        return inputMessage;
    }

}
