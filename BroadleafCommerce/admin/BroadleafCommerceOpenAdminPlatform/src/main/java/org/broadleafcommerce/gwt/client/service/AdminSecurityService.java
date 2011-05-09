package org.broadleafcommerce.gwt.client.service;

import com.google.gwt.user.client.rpc.RemoteService;

public interface AdminSecurityService extends RemoteService {

	public org.broadleafcommerce.gwt.client.security.AdminUser getAdminUser();
	
}