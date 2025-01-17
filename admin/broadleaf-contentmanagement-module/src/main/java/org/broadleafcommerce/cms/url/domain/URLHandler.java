/*-
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.cms.url.domain;

import org.broadleafcommerce.cms.url.type.URLRedirectType;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;

public interface URLHandler extends Serializable, MultiTenantCloneable<URLHandler> {

    Long getId();

    void setId(Long id);

    String getIncomingURL();

    void setIncomingURL(String incomingURL);

    String getNewURL();

    void setNewURL(String newURL);

    URLRedirectType getUrlRedirectType();

    void setUrlRedirectType(URLRedirectType redirectType);

    /**
     * Indicates if the value returned by <code>getIncomingURL()</code> is a regex expression
     * rather than a concrete URI.  Default is false.
     *
     * @return
     */
    boolean isRegexHandler();

    /**
     * Indicates if the value set by the method <code>setIncomingURL(String)</code> should be treated as a regex
     * expression rather than as a concrete URI.
     *
     * @param regexHandler
     */
    void setRegexHandler(Boolean regexHandler);

    /**
     * @Deprecated use {@link #setRegexHandler(Boolean regexHandler)}
     */
    @Deprecated
    void setRegexHandler(boolean regexHandler);

}
