/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.profile.core.service.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.email.service.EmailService;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.broadleafcommerce.common.security.util.PasswordReset;
import org.broadleafcommerce.profile.core.domain.Customer;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @deprecated - This email approach is no longer recommended.   See documentation for BroadleafCommerce    
 * @author bpolster
 */
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
        org.broadleafcommerce.common.locale.domain.Locale blLocale = customer.getCustomerLocale();
        if (blLocale != null) {
            String[] splitLocale = blLocale.getLocaleCode().split("_");
            if (splitLocale.length > 1) {
                localeToUse = new Locale(splitLocale[0], splitLocale[1]);
            } else {
                localeToUse = new Locale(splitLocale[0]);
            }
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
