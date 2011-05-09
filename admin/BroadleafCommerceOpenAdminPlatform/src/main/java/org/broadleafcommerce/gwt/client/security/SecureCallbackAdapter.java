package org.broadleafcommerce.gwt.client.security;

import com.smartgwt.client.util.SC;

public abstract class SecureCallbackAdapter {

	abstract void succeed();
	
	public void fail() {
		SC.say("Insufficient Privileges. Permission is required to execute this action.");
	} 
}
