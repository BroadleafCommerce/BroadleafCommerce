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
public interface EmailTrackingClicks extends Serializable {

    /**
     * @return the emailId
     */
    public abstract Long getId();

    /**
     * @param id the i to set
     */
    public abstract void setId(Long id);

    /**
     * @return the dateClicked
     */
    public abstract Date getDateClicked();

    /**
     * @param dateClicked the dateClicked to set
     */
    public abstract void setDateClicked(Date dateClicked);

    /**
     * @return the destinationUri
     */
    public abstract String getDestinationUri();

    /**
     * @param destinationUri the destinationUri to set
     */
    public abstract void setDestinationUri(String destinationUri);

    /**
     * @return the queryString
     */
    public abstract String getQueryString();

    /**
     * @param queryString the queryString to set
     */
    public abstract void setQueryString(String queryString);

    /**
     * @return the emailTracking
     */
    public abstract EmailTracking getEmailTracking();

    /**
     * @param emailTracking the emailTracking to set
     */
    public abstract void setEmailTracking(EmailTracking emailTracking);

    public abstract String getCustomerId();

    /**
     * @param customerId the customer to set
     */
    public abstract void setCustomerId(String customerId);

}
