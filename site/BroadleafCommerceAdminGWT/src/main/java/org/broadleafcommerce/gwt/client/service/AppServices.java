package org.broadleafcommerce.gwt.client.service;

import org.broadleafcommerce.gwt.client.service.security.AdminSecurityService;
import org.broadleafcommerce.gwt.client.service.security.AdminSecurityServiceAsync;

import com.google.gwt.core.client.GWT;

public class AppServices {

    public static final AdminSecurityServiceAsync SECURITY = GWT.create(AdminSecurityService.class);
    public static final DynamicEntityServiceAsync DYNAMIC_ENTITY = GWT.create(DynamicEntityService.class);
    
}
