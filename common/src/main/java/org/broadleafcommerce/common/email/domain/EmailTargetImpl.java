/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.common.email.domain;

import java.util.Arrays;

/**
 * Basic implementation of EmailTarget
 * 
 * @author bpolster
 */
public class EmailTargetImpl implements EmailTarget {

    private static final long serialVersionUID = 1L;

    protected String[] bccAddresses;
    protected String[] ccAddresses;
    protected String emailAddress;

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.common.email.domain.EmailTarget#getBCCAddresses()
     */

    public String[] getBCCAddresses() {
        return bccAddresses;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.common.email.domain.EmailTarget#getCCAddresses()
     */

    public String[] getCCAddresses() {
        return ccAddresses;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.common.email.domain.EmailTarget#getEmailAddress()
     */

    public String getEmailAddress() {
        return emailAddress;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.common.email.domain.EmailTarget#setBCCAddresses(java.lang
     * .String[])
     */

    public void setBCCAddresses(String[] bccAddresses) {
        this.bccAddresses = bccAddresses;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.common.email.domain.EmailTarget#setCCAddresses(java.lang
     * .String[])
     */

    public void setCCAddresses(String[] ccAddresses) {
        this.ccAddresses = ccAddresses;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.common.email.domain.EmailTarget#setEmailAddress(java.lang
     * .String)
     */

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(bccAddresses);
        result = prime * result + Arrays.hashCode(ccAddresses);
        result = prime * result + ((emailAddress == null) ? 0 : emailAddress.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        EmailTargetImpl other = (EmailTargetImpl) obj;
        if (!Arrays.equals(bccAddresses, other.bccAddresses))
            return false;
        if (!Arrays.equals(ccAddresses, other.ccAddresses))
            return false;
        if (emailAddress == null) {
            if (other.emailAddress != null)
                return false;
        } else if (!emailAddress.equals(other.emailAddress))
            return false;
        return true;
    }

}
