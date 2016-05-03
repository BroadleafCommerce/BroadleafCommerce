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

import java.io.Serializable;
import java.util.Date;

/**
 * @author jfischer
 *
 */
public interface EmailTracking extends Serializable {

    public abstract Long getId();

    public abstract void setId(Long id);

    /**
     * @return the emailAddress
     */
    public abstract String getEmailAddress();

    /**
     * @param emailAddress the emailAddress to set
     */
    public abstract void setEmailAddress(String emailAddress);

    /**
     * @return the dateSent
     */
    public abstract Date getDateSent();

    /**
     * @param dateSent the dateSent to set
     */
    public abstract void setDateSent(Date dateSent);

    /**
     * @return the type
     */
    public abstract String getType();

    /**
     * @param type the type to set
     */
    public abstract void setType(String type);

}
