package org.broadleafcommerce.gwt.client.service;


import org.broadleafcommerce.gwt.client.security.AdminUser;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AdminSecurityServiceAsync {

	public void getAdminUser(AsyncCallback<AdminUser> cb);
	
}
