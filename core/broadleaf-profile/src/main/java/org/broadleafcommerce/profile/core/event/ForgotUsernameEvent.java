package org.broadleafcommerce.profile.core.event;

import org.broadleafcommerce.common.event.BroadleafApplicationEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nick Crum ncrum
 */
public class ForgotUsernameEvent extends BroadleafApplicationEvent {

    protected String emailAddress;
    protected List<String> activeUsernames = new ArrayList<>();

    public ForgotUsernameEvent(Object source, String emailAddress, List<String> activeUsernames) {
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

    public List<String> getActiveUsernames() {
        return activeUsernames;
    }

    public void setActiveUsernames(List<String> activeUsernames) {
        this.activeUsernames = activeUsernames;
    }
}
