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
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class IndexStatusInfoImpl implements IndexStatusInfo {

    private Date lastIndexDate;
    private Map<String, String> additionalInfo = new HashMap<String, String>();
    private Map<Long, Integer> indexErrors = new HashMap<Long, Integer>();
    private Map<Long, Date> deadIndexEvents = new HashMap<Long, Date>();

    @Override
    public Date getLastIndexDate() {
        return lastIndexDate;
    }

    @Override
    public void setLastIndexDate(Date lastIndexDate) {
        this.lastIndexDate = lastIndexDate;
    }

    @Override
    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    @Override
    public void setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Override
    public Map<Long, Integer> getIndexErrors() {
        return indexErrors;
    }

    @Override
    public void setIndexErrors(Map<Long, Integer> indexErrors) {
        this.indexErrors = indexErrors;
    }

    @Override
    public Map<Long, Date> getDeadIndexEvents() {
        return deadIndexEvents;
    }

    @Override
    public void setDeadIndexEvents(Map<Long, Date> deadIndexEvents) {
        this.deadIndexEvents = deadIndexEvents;
    }
    
}