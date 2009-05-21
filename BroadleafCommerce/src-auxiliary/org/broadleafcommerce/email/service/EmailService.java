package org.broadleafcommerce.email.service;

import java.util.HashMap;

import org.broadleafcommerce.email.domain.EmailTarget;
import org.broadleafcommerce.email.service.info.EmailInfo;

/**
 * @author jfischer
 *
 */
public interface EmailService {

    @SuppressWarnings("unchecked")
    public boolean sendTemplateEmail(final HashMap props);

    @SuppressWarnings("unchecked")
    public boolean sendTemplateEmail(EmailInfo emailInfo, EmailTarget emailTarget, HashMap props);

    @SuppressWarnings("unchecked")
    public boolean sendBasicEmail(HashMap props);

}
