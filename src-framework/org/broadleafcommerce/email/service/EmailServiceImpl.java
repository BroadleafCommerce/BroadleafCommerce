package org.broadleafcommerce.email.service;

import java.util.HashMap;

import javax.annotation.Resource;

import org.broadleafcommerce.email.domain.AbstractEmailTargetUser;
import org.broadleafcommerce.email.info.EmailInfo;
import org.broadleafcommerce.email.info.ServerInfo;
import org.broadleafcommerce.email.jms.EmailServiceProducer;
import org.springframework.stereotype.Service;

/**
 * @author jfischer
 *
 */
@Service("emailDeliveryService")
public class EmailServiceImpl implements EmailService {

    @Resource(name="emailTrackingManager")
    private EmailTrackingManager emailTrackingManager;

    @Resource
    private ServerInfo serverInfo;
    
    @Resource
    private EmailServiceProducer emailServiceProducer;

    /* (non-Javadoc)
     * @see com.containerstore.web.task.service.EmailService#sendTemplateEmail(com.containerstore.web.task.domain.AbstractEmailTargetUser)
     */
    @SuppressWarnings("unchecked")
    public boolean sendTemplateEmail(final HashMap props) {
    	props.put("serverInfo", serverInfo);
    	AbstractEmailTargetUser emailUser = (AbstractEmailTargetUser) props.get("user");
    	EmailInfo info = (EmailInfo) props.get("info");
    	Long emailId = emailTrackingManager.createTrackedEmail(emailUser.getEmailAddress(), info.getEmailType() , null);
    	props.put("emailTrackingId", emailId);
    	
    	emailServiceProducer.send(props);

        return true;
    }

}
