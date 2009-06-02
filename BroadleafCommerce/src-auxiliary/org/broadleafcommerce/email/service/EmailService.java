package org.broadleafcommerce.email.service;

import java.util.HashMap;

import org.broadleafcommerce.email.domain.EmailTarget;
import org.broadleafcommerce.email.service.info.EmailInfo;

/**
 * @author jfischer
 *
 */
public interface EmailService {

    public boolean sendTemplateEmail(String emailAddress, EmailInfo emailInfo,  HashMap<String,Object> props);

    public boolean sendTemplateEmail(EmailTarget emailTarget, EmailInfo emailInfo, HashMap<String,Object> props);

    public boolean sendBasicEmail(EmailInfo emailInfo, EmailTarget emailTarget, HashMap<String,Object> props);

}
