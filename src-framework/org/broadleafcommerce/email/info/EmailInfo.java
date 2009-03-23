package org.broadleafcommerce.email.info;

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
    private String bccAddress;

    public EmailInfo(String propertiesPath) throws IOException {
        Properties defaults = new Properties();
        defaults.load(EmailInfo.class.getResourceAsStream("/com/containerstore/web/email/props/defaultEmail.properties"));
        Properties props = new Properties(defaults);
        props.load(EmailInfo.class.getResourceAsStream(propertiesPath));
        //TODO find out where the subject is coming from in the current system
        setEmailType(props.getProperty("emailType"));
        setEmailTemplate(props.getProperty("emailTemplate"));
        setSubject(props.getProperty("subject"));
        setFromAddress(props.getProperty("fromAddress"));
        setBccAddress(props.getProperty("bccAddress"));
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
     * @return the bccAddress
     */
    public String getBccAddress() {
        return bccAddress;
    }

    /**
     * @param bccAddress the bccAddress to set
     */
    public void setBccAddress(String bccAddress) {
        this.bccAddress = bccAddress;
    }
}
