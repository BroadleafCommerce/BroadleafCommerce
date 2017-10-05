package org.broadleafcommerce.openadmin.server.security.event;

import org.broadleafcommerce.common.event.BroadleafApplicationEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nick Crum ncrum
 */
public class AdminForgotUsernameEvent extends BroadleafApplicationEvent {

    protected String emailAddress;
    protected String phoneNumber;
    protected List<String> activeUsernames = new ArrayList<>();

    public AdminForgotUsernameEvent(Object source, String emailAddress, String phoneNumber, List<String> activeUsernames) {
        super(source);
        this.emailAddress = emailAddress;
        this.activeUsernames = activeUsernames;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getActiveUsernames() {
        return activeUsernames;
    }

    public void setActiveUsernames(List<String> activeUsernames) {
        this.activeUsernames = activeUsernames;
    }
}
