package org.broadleafcommerce.gwt.client.service;

import org.broadleafcommerce.gwt.client.service.catalog.CatalogService;
import org.broadleafcommerce.gwt.client.service.catalog.CatalogServiceAsync;
import org.broadleafcommerce.gwt.client.service.security.AdminSecurityService;
import org.broadleafcommerce.gwt.client.service.security.AdminSecurityServiceAsync;

import com.google.gwt.core.client.GWT;

public class AppServices {

    public static final CatalogServiceAsync CATALOG = GWT.create(CatalogService.class);
    public static final AdminSecurityServiceAsync SECURITY = GWT.create(AdminSecurityService.class);
    
}
