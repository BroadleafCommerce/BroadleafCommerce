/*
 * #%L
 * BroadleafCommerce Profile Web
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
package org.broadleafcommerce.profile.web.site.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Cookie used to protected against session fixation attacks
 * 
 * @see SessionFixationProtectionFilter
 * 
 * @author Andre Azzolini (apazzolini)
 *
 * @deprecated use either {@link https://javadoc.io/doc/org.springframework.security/spring-security-web/latest/org/springframework/security/web/authentication/session/SessionFixationProtectionStrategy.html} instead
 */
@Deprecated
public class SessionFixationProtectionCookie {
    protected final Log logger = LogFactory.getLog(getClass());

    public static final String COOKIE_NAME = "ActiveID";

}
