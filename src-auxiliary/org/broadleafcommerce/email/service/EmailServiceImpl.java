package org.broadleafcommerce.email.service;

import java.util.HashMap;

import javax.annotation.Resource;

import org.broadleafcommerce.email.domain.EmailTarget;
import org.broadleafcommerce.email.domain.EmailTargetImpl;
import org.broadleafcommerce.email.service.info.EmailInfo;
import org.broadleafcommerce.email.service.info.ServerInfo;
import org.broadleafcommerce.email.service.jms.EmailServiceProducer;
import org.broadleafcommerce.email.service.message.EmailPropertyType;
import org.broadleafcommerce.email.service.message.MessageCreator;
import org.springframework.stereotype.Service;

/**
 * @author jfischer
 *
 */
@Service("blEmailService")
public class EmailServiceImpl implements EmailService {

    @Resource
    protected EmailTrackingManager emailTrackingManager;

    @Resource
    private ServerInfo serverInfo;

    protected EmailServiceProducer emailServiceProducer;

    @Resource
    protected MessageCreator messageCreator;

    public boolean sendTemplateEmail(EmailTarget emailTarget, EmailInfo emailInfo, HashMap<String,Object> props) {
        if (props == null) return false;

        props.put(EmailPropertyType.INFO.toString(), emailInfo);
        props.put(EmailPropertyType.USER.toString(), emailTarget);

        return sendBasicEmail(emailInfo, emailTarget, props);
    }

    public boolean sendTemplateEmail(String emailAddress, EmailInfo emailInfo, HashMap<String,Object> props) {
        EmailTarget emailTarget = new EmailTargetImpl();
        emailTarget.setEmailAddress(emailAddress);
        return sendTemplateEmail(emailTarget, emailInfo, props);
    }

    @Override
    public boolean sendBasicEmail(EmailInfo emailInfo, EmailTarget emailTarget, HashMap<String,Object> props) {
        if (props == null) return false;

        props.put(EmailPropertyType.INFO.toString(), emailInfo);
        props.put(EmailPropertyType.USER.toString(), emailTarget);

        if (Boolean.parseBoolean(emailInfo.getSendEmailReliableAsync())) {
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
