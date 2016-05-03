/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.url.domain;

import org.broadleafcommerce.cms.url.type.URLRedirectType;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;

public interface URLHandler extends Serializable, MultiTenantCloneable<URLHandler> {

    public abstract Long getId();

    public abstract void setId(Long id);

    public abstract String getIncomingURL();

    public abstract void setIncomingURL(String incomingURL);

    public abstract String getNewURL();

    public abstract void setNewURL(String newURL);

    public abstract URLRedirectType getUrlRedirectType();

    public void setUrlRedirectType(URLRedirectType redirectType);

}
