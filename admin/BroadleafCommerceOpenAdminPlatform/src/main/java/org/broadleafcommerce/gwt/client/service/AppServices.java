package org.broadleafcommerce.gwt.client.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class AppServices {

    //public static final AdminSecurityServiceAsync SECURITY = GWT.create(AdminSecurityService.class);
    public static final DynamicEntityServiceAsync DYNAMIC_ENTITY = GWT.create(DynamicEntityService.class);
    static {
    	ServiceDefTarget endpoint = (ServiceDefTarget) DYNAMIC_ENTITY;
        endpoint.setServiceEntryPoint("dynamic.entity.service");
    }
    
}
