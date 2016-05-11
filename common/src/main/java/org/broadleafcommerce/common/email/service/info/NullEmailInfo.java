/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.email.service.info;

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
