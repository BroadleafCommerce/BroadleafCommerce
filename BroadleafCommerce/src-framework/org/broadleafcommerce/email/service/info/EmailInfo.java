package org.broadleafcommerce.email.service.info;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

/**
 * @author jfischer
 *
 */
public class EmailInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String emailType;
    private String emailTemplate;
    private String subject;
    private String fromAddress;

    private String sendEmailReliableAsync;
    private String sendAsyncPriority;

    public EmailInfo(String[] propertiesPath) throws IOException {
        Properties defaults = new Properties();
        defaults.load(EmailInfo.class.getResourceAsStream("/org/broadleafcommerce/email/service/props/defaultEmail.properties"));
        Properties props = null;
        if (propertiesPath != null && propertiesPath.length > 0) {
            for (int j=0;j<propertiesPath.length;j++) {
                props = new Properties(defaults);
                props.load(EmailInfo.class.getResourceAsStream(propertiesPath[j]));
                defaults = props;
            }
        } else {
            props = new Properties(defaults);
        }
        //TODO find out where the subject is coming from in the current system
        setEmailType(props.getProperty("emailType"));
        setEmailTemplate(props.getProperty("emailTemplate"));
        setSubject(props.getProperty("subject"));
        setFromAddress(props.getProperty("fromAddress"));
        setSendEmailReliableAsync(props.getProperty("sendEmailReliableAsync"));
        setSendAsyncPriority(props.getProperty("sendAsyncPriority"));
    }

    /**
     * @return the emailType
     */
    public String getEmailType() {
        return emailType;
    }

    /**
     * @param emailType the emailType to set
     */
    public void setEmailType(String emailType) {
        this.emailType = emailType;
    }

    /**
     * @return the emailTemplate
     */
    public String getEmailTemplate() {
        return emailTemplate;
    }

    /**
     * @param emailTemplate the emailTemplate to set
     */
    public void setEmailTemplate(String emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the fromAddress
     */
    public String getFromAddress() {
        return fromAddress;
    }

    /**
     * @param fromAddress the fromAddress to set
     */
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    /**
     * @return the sendEmailReliableAsync
     */
    public String getSendEmailReliableAsync() {
        return sendEmailReliableAsync;
    }

    /**
     * @param sendEmailReliableAsync the sendEmailReliableAsync to set
     */
    public void setSendEmailReliableAsync(String sendEmailReliableAsync) {
        this.sendEmailReliableAsync = sendEmailReliableAsync;
    }

    /**
     * @return the sendAsyncPriority
     */
    public String getSendAsyncPriority() {
        return sendAsyncPriority;
    }

    /**
     * @param sendAsyncPriority the sendAsyncPriority to set
     */
    public void setSendAsyncPriority(String sendAsyncPriority) {
        this.sendAsyncPriority = sendAsyncPriority;
    }
}
