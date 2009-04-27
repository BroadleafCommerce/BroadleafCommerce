package org.broadleafcommerce.email.service;

import java.util.HashMap;

import javax.annotation.Resource;

import org.broadleafcommerce.email.domain.EmailTarget;
import org.broadleafcommerce.email.service.info.EmailInfo;
import org.broadleafcommerce.email.service.info.ServerInfo;
import org.broadleafcommerce.email.service.jms.EmailServiceProducer;
import org.broadleafcommerce.email.service.message.MessageCreator;
import org.springframework.stereotype.Service;

/**
 * @author jfischer
 *
 */
@Service("emailDeliveryServiceBLC")
public class EmailServiceImpl implements EmailService {

    @Resource(name="emailTrackingManagerBLC")
    protected EmailTrackingManager emailTrackingManager;
    
    @Resource
    private ServerInfo serverInfo;
    
    protected EmailServiceProducer emailServiceProducer;
    
    @Resource
    protected MessageCreator messageCreator;

    /* (non-Javadoc)
     * @see com.containerstore.web.task.service.EmailService#sendTemplateEmail(com.containerstore.web.task.domain.AbstractEmailTargetUser)
     */
    @SuppressWarnings("unchecked")
    public boolean sendTemplateEmail(HashMap props) {
    	props.put("serverInfo", serverInfo);
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

	/**
	 * @return the emailTrackingManager
	 */
	public EmailTrackingManager getEmailTrackingManager() {
		return emailTrackingManager;
	}

	/**
	 * @param emailTrackingManager the emailTrackingManager to set
	 */
	public void setEmailTrackingManager(EmailTrackingManager emailTrackingManager) {
		this.emailTrackingManager = emailTrackingManager;
	}

	/**
	 * @return the serverInfo
	 */
	public ServerInfo getServerInfo() {
		return serverInfo;
	}

	/**
	 * @param serverInfo the serverInfo to set
	 */
	public void setServerInfo(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

	/**
	 * @return the emailServiceProducer
	 */
	public EmailServiceProducer getEmailServiceProducer() {
		return emailServiceProducer;
	}

	/**
	 * @param emailServiceProducer the emailServiceProducer to set
	 */
	public void setEmailServiceProducer(EmailServiceProducer emailServiceProducer) {
		this.emailServiceProducer = emailServiceProducer;
	}

	/**
	 * @return the messageCreator
	 */
	public MessageCreator getMessageCreator() {
		return messageCreator;
	}

	/**
	 * @param messageCreator the messageCreator to set
	 */
	public void setMessageCreator(MessageCreator messageCreator) {
		this.messageCreator = messageCreator;
	}

}
