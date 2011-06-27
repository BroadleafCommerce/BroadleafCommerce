package org.broadleafcommerce.profile.core.service.handler;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.gwt.server.security.util.PasswordReset;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.type.LocaleType;
import org.broadleafcommerce.profile.email.service.EmailService;
import org.broadleafcommerce.profile.email.service.info.EmailInfo;

public class EmailNotificationPasswordUpdatedHandler implements PasswordUpdatedHandler {

	private static final Log LOG = LogFactory.getLog(EmailNotificationPasswordUpdatedHandler.class);
	public static final String CUSTOMER_PASSWORD_TEMPLATE_VARIABLE = "customerPasswordTemplateVariable";
	
	@Resource(name="blEmailService")
	protected EmailService emailService;
	
	protected Map<Locale, String> passwordResetEmailSubject = new HashMap<Locale, String>();
	protected Map<Locale, String> passwordResetEmailTemplate = new HashMap<Locale, String>();
	protected String passwordResetEmailFromAddress;
	protected Locale passwordResetEmailDefaultLocale = Locale.US;
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void passwordChanged(PasswordReset passwordReset, Customer customer, String newPassword) {
		Locale localeToUse = null;
		LocaleType localeType = customer.getCustomerLocale();
		if (localeType != null) {
			localeToUse = localeType.getLocale();
		}
		if (localeToUse == null) {
			localeToUse = getPasswordResetEmailDefaultLocale();
		}
		String subject = getPasswordResetEmailSubject().get(localeToUse);
		if (subject == null) {
			LOG.warn("Unable to find an email subject for customer locale: "+localeToUse.toString()+". Using default locale instead.");
			subject = getPasswordResetEmailSubject().get(getPasswordResetEmailDefaultLocale());
		}
		String template = getPasswordResetEmailTemplate().get(localeToUse);
		if (template == null) {
			LOG.warn("Unable to find an email template for customer locale: "+localeToUse.toString()+". Using default locale instead.");
			template = getPasswordResetEmailTemplate().get(getPasswordResetEmailDefaultLocale());
		}
		
		EmailInfo info = new EmailInfo();
        info.setFromAddress(getPasswordResetEmailFromAddress());
        info.setSubject(subject);
        info.setEmailTemplate(template);
        info.setSendEmailReliableAsync(String.valueOf(passwordReset.isSendResetEmailReliableAsync()));
        
        HashMap vars = constructPasswordChangeEmailTemplateVariables(customer, newPassword);

        emailService.sendTemplateEmail(passwordReset.getEmail(), info, vars);
	}
	
	@SuppressWarnings("rawtypes")
	/**
	 * Override this method to add in whatever variables your custom template may require.
	 */
	protected HashMap constructPasswordChangeEmailTemplateVariables(Customer customer, String newPassword) {
		HashMap<String, String> vars = new HashMap<String, String>();
		vars.put(CUSTOMER_PASSWORD_TEMPLATE_VARIABLE, newPassword);
		
		return vars;
	}
	
	public Map<Locale, String> getPasswordResetEmailSubject() {
		return passwordResetEmailSubject;
	}

	public void setPasswordResetEmailSubject(Map<Locale, String> passwordResetEmailSubject) {
		this.passwordResetEmailSubject = passwordResetEmailSubject;
	}

	public Map<Locale, String> getPasswordResetEmailTemplate() {
		return passwordResetEmailTemplate;
	}

	public void setPasswordResetEmailTemplate(Map<Locale, String> passwordResetEmailTemplate) {
		this.passwordResetEmailTemplate = passwordResetEmailTemplate;
	}

	public String getPasswordResetEmailFromAddress() {
		return passwordResetEmailFromAddress;
	}

	public void setPasswordResetEmailFromAddress(String passwordResetEmailFromAddress) {
		this.passwordResetEmailFromAddress = passwordResetEmailFromAddress;
	}

	public Locale getPasswordResetEmailDefaultLocale() {
		return passwordResetEmailDefaultLocale;
	}

	public void setPasswordResetEmailDefaultLocale(Locale passwordResetEmailDefaultLocale) {
		this.passwordResetEmailDefaultLocale = passwordResetEmailDefaultLocale;
	}
}
