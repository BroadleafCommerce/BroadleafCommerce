package org.broadleafcommerce.profile.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.broadleafcommerce.profile.domain.Customer;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Repository;
import org.springframework.ui.velocity.VelocityEngineUtils;

@Repository("emailService")
public class EmailServiceImpl implements EmailService {

    protected final Log logger = LogFactory.getLog(getClass());

    @Resource(name = "mailSender")
    private JavaMailSenderImpl mailSender;

    @Resource(name = "velocityEngine")
    private VelocityEngine velocityEngine;

    public void sendEmail(Customer customer, String template, String fromAddress, String subject) {
        try {

            // TODO: need to get the url from a property file
            String url = "http://localhost:8080";
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("user", customer);
            model.put("from", fromAddress);
            model.put("baseurl", url);

            String body = null;

            try {
                body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, model);
            } catch (VelocityException e) {
                logger.error(e);
            }

            MimeMessage message = buildMessage(customer, subject, body, fromAddress);
            mailSender.send(message);
        } catch (MessagingException me) {
            logger.error(me);
        }
    }

    private MimeMessage buildMessage(Customer customer, String subject, String body, String fromAddress) throws MessagingException {
        MimeMessage message = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(customer.getEmailAddress());
        helper.setFrom(fromAddress);
        helper.setSubject(subject);
        message.setContent(body, "text/html");

        return message;
    }

    public void setMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }
}
