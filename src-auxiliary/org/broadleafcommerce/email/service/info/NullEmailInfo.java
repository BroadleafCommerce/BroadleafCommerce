package org.broadleafcommerce.email.service.info;

import java.io.IOException;

/**
 * Implementation of EmailInfo that will not send an Email.   The out of box configuration for
 * broadleaf does not send emails but does have hooks to send emails for use cases like
 * registration, forgot password, etc.
 *
 * The email send functionality will not send an email if the passed in EmailInfo is an instance
 * of this class.
 *
 * @author vjain
 *
 */
public class NullEmailInfo extends EmailInfo {
    private static final long serialVersionUID = 1L;

    public NullEmailInfo() throws IOException {
        super();
    }

}
