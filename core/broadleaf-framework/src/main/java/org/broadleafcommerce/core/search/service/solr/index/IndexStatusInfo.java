/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.search.service.solr.index;

import java.util.Date;
import java.util.Map;

/**
 * General information about the current status of a (embedded) Solr instance's index
 *
 * @author Jeff Fischer
 */
public interface IndexStatusInfo {

    /**
     * The most recent index date
     *
     * @return
     */
    Date getLastIndexDate();

    void setLastIndexDate(Date lastIndexDate);

    /**
     * Arbitrary information about the index.
     *
     * @return
     */
    Map<String, String> getAdditionalInfo();

    void setAdditionalInfo(Map<String, String> additionalInfo);

    /**
     * Error information about previously attempted events
     *
     * @return
     */
    Map<Long, Integer> getIndexErrors();

    void setIndexErrors(Map<Long, Integer> errors);

    /**
     * Events that exceeded the retry limit and are considered dead
     *
     * @return
     */
    Map<Long, Date> getDeadIndexEvents();

    void setDeadIndexEvents(Map<Long, Date> deadIndexEvents);
    
}