package org.broadleafcommerce.email.service;

import java.util.HashMap;

import javax.annotation.Resource;

import org.broadleafcommerce.email.domain.EmailTarget;
import org.broadleafcommerce.email.info.EmailInfo;
import org.broadleafcommerce.email.jms.EmailServiceProducer;
import org.broadleafcommerce.email.message.MessageCreator;
import org.springframework.stereotype.Service;

/**
 * @author jfischer
 *
 */
@Service("emailDeliveryServiceBLC")
public class EmailServiceImpl implements EmailService {

    @Resource(name="emailTrackingManagerBLC")
    private EmailTrackingManager emailTrackingManager;
    
    private EmailServiceProducer emailServiceProducer;
    
    @Resource
    private MessageCreator messageCreator;

    /* (non-Javadoc)
     * @see com.containerstore.web.task.service.EmailService#sendTemplateEmail(com.containerstore.web.task.domain.AbstractEmailTargetUser)
     */
    @SuppressWarnings("unchecked")
    public boolean sendTemplateEmail(final HashMap props) {
    	EmailTarget emailUser = (EmailTarget) props.get("user");
    	EmailInfo info = (EmailInfo) props.get("info");
    	Long emailId = emailTrackingManager.createTrackedEmail(emailUser.getEmailAddress(), info.getEmailType() , null);
    	props.put("emailTrackingId", emailId);
    	
    	if (Boolean.parseBoolean(info.getSendEmailReliableAsync())) {
    		emailServiceProducer.send(props);
    	} else {
    		messageCreator.sendMessage(props);
    	}

        return true;
    }

}
