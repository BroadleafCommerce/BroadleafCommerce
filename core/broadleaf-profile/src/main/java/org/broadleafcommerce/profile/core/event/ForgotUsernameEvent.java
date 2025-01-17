/*-
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.profile.core.event;

import org.broadleafcommerce.common.event.BroadleafApplicationEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nick Crum ncrum
 */
public class ForgotUsernameEvent extends BroadleafApplicationEvent {

    protected String emailAddress;
    protected String phoneNumber;
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
