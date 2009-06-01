package org.broadleafcommerce.email.domain;

/**
 * The EmailTarget interface is used to specify the recipients of the email.
 *
 * @see EmailTargetImpl
 * @author bpolster
 */
public interface EmailTarget {

    public String getEmailAddress();
    public void setEmailAddress(String emailAddress);
    public String[] getCCAddresses();
    public void setCCAddresses(String[] ccAddresses);
    public String[] getBCCAddresses();
    public void setBCCAddresses(String[] BCCAddresses);

}
