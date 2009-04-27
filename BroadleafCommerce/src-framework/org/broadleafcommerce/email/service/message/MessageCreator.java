package org.broadleafcommerce.email.service.message;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.NumberTool;
import org.broadleafcommerce.email.domain.EmailTarget;
import org.broadleafcommerce.email.service.info.EmailInfo;
import org.broadleafcommerce.email.service.info.ServerInfo;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

@Component("messageCreatorBLC")
public class MessageCreator {

	@Resource
    private VelocityEngine velocityEngine;

    @Resource
    private JavaMailSender mailSender;
    
    @SuppressWarnings("unchecked")
	public void sendMessage(final HashMap props) throws MailException {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                EmailTarget emailUser = (EmailTarget) props.get("user");
                EmailInfo info = (EmailInfo) props.get("info");
                ServerInfo serverInfo = (ServerInfo) props.get("serverInfo");
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(emailUser.getEmailAddress());
                message.setFrom(info.getFromAddress());
                message.setSubject(info.getSubject());
                if (emailUser.getBCCAddresses() != null && emailUser.getBCCAddresses().length > 0) {
                	message.setBcc(emailUser.getBCCAddresses());
                }
                if (emailUser.getCCAddresses() != null && emailUser.getCCAddresses().length > 0) {
                	message.setCc(emailUser.getCCAddresses());
                }
                //TODO: need a way to configure the Velocity Tools more generically
                HashMap copy = (HashMap) props.clone();
                copy.put("number", new NumberTool());
                copy.put("serverInfo", serverInfo);
                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, info.getEmailTemplate(), copy);
                message.setText(text, true);
            }
        };
        this.mailSender.send(preparator);
    }
}
