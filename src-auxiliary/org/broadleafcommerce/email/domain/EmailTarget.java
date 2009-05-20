package org.broadleafcommerce.email.domain;

public interface EmailTarget {
	
	public String getEmailAddress();
	public void setEmailAddress(String emailAddress);
	public String[] getCCAddresses();
	public void setCCAddresses(String[] ccAddresses);
	public String[] getBCCAddresses();
	public void setBCCAddresses(String[] BCCAddresses);

}
