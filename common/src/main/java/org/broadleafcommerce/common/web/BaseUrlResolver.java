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

package org.broadleafcommerce.common.web;

/**
 * Responsible for providing the base url for the site / admin applications.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface BaseUrlResolver {

    /**
     * Returns the currently configured base url for the site. The default implementation of this interface
     * will return the value stored in the site.baseurl property for the current environment.
     * 
     * For example, in a development environment, this method might return: http://localhost:8080
     * 
     * @return the site baseurl, without a trailing slash
     */
    public String getSiteBaseUrl();

    /**
     * Returns the currently configured base url for the admin. The default implementation of this interface
     * will return the value stored in the admin.baseurl property for the current environment.
     * 
     * For example, in a development environment, this method might return: http://localhost:8080/admin
     * 
     * @return the admin baseurl, without a trailing slash
     */
    public String getAdminBaseUrl();

}
