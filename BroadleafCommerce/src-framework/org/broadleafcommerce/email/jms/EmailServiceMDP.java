package org.broadleafcommerce.email.jms;

import java.util.HashMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.NumberTool;
import org.broadleafcommerce.email.domain.AbstractEmailTargetUser;
import org.broadleafcommerce.email.exception.EmailException;
import org.broadleafcommerce.email.info.EmailInfo;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * @author jfischer
 *
 */
@Component("emailServiceMessageListener")
public class EmailServiceMDP implements MessageListener {

    //@Resource
    private VelocityEngine velocityEngine;

    //@Resource
    private JavaMailSender mailSender;

    /* (non-Javadoc)
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onMessage(Message message) {
        try {
            final HashMap props = (HashMap) ((ObjectMessage) message).getObject();
            MimeMessagePreparator preparator = new MimeMessagePreparator() {
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    AbstractEmailTargetUser emailUser = (AbstractEmailTargetUser) props.get("user");
                    EmailInfo info = (EmailInfo) props.get("info");
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                    message.setTo(emailUser.getEmailAddress());
                    message.setFrom(info.getFromAddress());
                    message.setSubject(info.getSubject());
                    message.setBcc(info.getBccAddress());
                    //TODO: need a way to configure the Velocity Tools more generically
                    props.put("number", new NumberTool());
                    String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, info.getEmailTemplate(), props);
                    message.setText(text, true);
                }
            };
            this.mailSender.send(preparator);
        } catch (MailException e) {
            throw new EmailException(e);
        } catch (JMSException e) {
            throw new EmailException(e);
        }
    }

}
