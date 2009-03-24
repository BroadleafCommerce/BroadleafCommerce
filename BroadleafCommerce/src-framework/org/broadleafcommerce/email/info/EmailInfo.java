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
    
    private String siteHttpServerName;
    private String siteHttpServerPort;
    private String siteSecureHttpServerPort;
    
    private String sendEmailReliableAsync;
    private String sendAsyncPriority;

    public EmailInfo(String propertiesPath) throws IOException {
        Properties defaults = new Properties();
        defaults.load(EmailInfo.class.getResourceAsStream("/org/broadleafcommerce/email/props/defaultEmail.properties"));
        Properties props = new Properties(defaults);
        props.load(EmailInfo.class.getResourceAsStream(propertiesPath));
        //TODO find out where the subject is coming from in the current system
        setEmailType(props.getProperty("emailType"));
        setEmailTemplate(props.getProperty("emailTemplate"));
        setSubject(props.getProperty("subject"));
        setFromAddress(props.getProperty("fromAddress"));
        setSiteHttpServerName(props.getProperty("siteHttpServerName"));
        setSiteHttpServerPort(props.getProperty("siteHttpServerPort"));
        setSiteSecureHttpServerPort(props.getProperty("siteSecureHttpServerPort"));
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
	 * @return the siteHttpServerName
	 */
	public String getSiteHttpServerName() {
		return siteHttpServerName;
	}

	/**
	 * @param siteHttpServerName the siteHttpServerName to set
	 */
	public void setSiteHttpServerName(String siteHttpServerName) {
		this.siteHttpServerName = siteHttpServerName;
	}

	/**
	 * @return the siteHttpServerPort
	 */
	public String getSiteHttpServerPort() {
		return siteHttpServerPort;
	}

	/**
	 * @param siteHttpServerPort the siteHttpServerPort to set
	 */
	public void setSiteHttpServerPort(String siteHttpServerPort) {
		this.siteHttpServerPort = siteHttpServerPort;
	}

	/**
	 * @return the siteSecureHttpServerPort
	 */
	public String getSiteSecureHttpServerPort() {
		return siteSecureHttpServerPort;
	}

	/**
	 * @param siteSecureHttpServerPort the siteSecureHttpServerPort to set
	 */
	public void setSiteSecureHttpServerPort(String siteSecureHttpServerPort) {
		this.siteSecureHttpServerPort = siteSecureHttpServerPort;
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
