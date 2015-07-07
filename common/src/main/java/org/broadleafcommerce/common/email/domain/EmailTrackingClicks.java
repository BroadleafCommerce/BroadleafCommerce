/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
