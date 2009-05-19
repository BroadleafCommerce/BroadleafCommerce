package org.broadleafcommerce.email.service;

import java.util.HashMap;

/**
 * @author jfischer
 *
 */
public interface EmailService {

    @SuppressWarnings("unchecked")
    public boolean sendTemplateEmail(final HashMap props);

    @SuppressWarnings("unchecked")
    public boolean sendBasicEmail(HashMap props);

}
