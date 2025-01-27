/*-
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.email.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jfischer
 */
public interface EmailTracking extends Serializable {

    Long getId();

    void setId(Long id);

    /**
     * @return the emailAddress
     */
    String getEmailAddress();

    /**
     * @param emailAddress the emailAddress to set
     */
    void setEmailAddress(String emailAddress);

    /**
     * @return the dateSent
     */
    Date getDateSent();

    /**
     * @param dateSent the dateSent to set
     */
    void setDateSent(Date dateSent);

    /**
     * @return the type
     */
    String getType();

    /**
     * @param type the type to set
     */
    void setType(String type);

}
