/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.browsertest.page

import geb.Page

/**
 * Root page that other admin pages should extend from; basically all admin pages have these properties
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
class AdminPage extends Page {
    
    static at = { header.find('.logout') }
    
    static content = {
        // Not required as this does not display in a modal
        header(required: false) { $('header.top-bar') }
        logoutLink(to: AdminLoginPage, required: false) { header.find('.logout a') }
        leftNavItems(required: false) { $('#sideMenu .blc-module') }
        breadcrumbs(required: false) { $('ul.breadcrumbs li') }
    }

}
