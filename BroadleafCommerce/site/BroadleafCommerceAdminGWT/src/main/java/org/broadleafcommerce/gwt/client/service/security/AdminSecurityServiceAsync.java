package org.broadleafcommerce.gwt.client.service.security;

import org.broadleafcommerce.security.domain.AdminUser;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AdminSecurityServiceAsync {

	public void getAdminUser(AsyncCallback<AdminUser> callback);
	
}
