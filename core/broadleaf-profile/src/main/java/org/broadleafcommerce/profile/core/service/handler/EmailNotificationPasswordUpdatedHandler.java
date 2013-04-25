/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
