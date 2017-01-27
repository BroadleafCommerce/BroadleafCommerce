/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.service.solr.index;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Jeff Fischer
 */
@Service("blSolrIndexStatusService")
public class SolrIndexStatusServiceImpl implements SolrIndexStatusService {

    @Resource(name="blSolrIndexStatusProviders")
    protected List<SolrIndexStatusProvider> providers;
    
    @Value("${solr.index.status.error.retry.count:3}")
    protected Integer solrIndexStatusErrorRetryCount;

    @Override
    public synchronized void setIndexStatus(IndexStatusInfo status) {
        clearErrorStatus(status);
        updateIndexStatus(status);
    }

    @Override
    public void addIndexStatus(Long eventId, Date eventCreatedDate) {
        IndexStatusInfo statusInfo = getSeedStatusInstance();
        statusInfo.setLastIndexDate(eventCreatedDate);
        statusInfo.getAdditionalInfo().put(String.format("SystemEventId%s", eventId), String.valueOf(eventId));
        setIndexStatus(statusInfo);
    }
    
    @Override
    public synchronized IndexStatusInfo getIndexStatus() {
        IndexStatusInfo status = getSeedStatusInstance();
        for (SolrIndexStatusProvider provider : providers) {
            provider.readIndexStatus(status);
        }
        return status;
    }

    /**
     * Will add a new IndexError entry if one does not exist.  If one exists, it will increment the retry count.  If the
     * retry count is exceeded, it will move the event to the dead events list and treat the event as if it were successful
     * 
     * @param eventId The id of the event producing the error
     * @param retryCount The retry count as defined by the event
     * @param eventCreatedDate The date that the event was created
     */
    public synchronized void addIndexErrorStatus(Long eventId, Integer eventRetryCount, Date eventCreatedDate) {
        IndexStatusInfo status = getIndexStatus();
        if (status != null) {
            Integer retryCount = status.getIndexErrors().get(eventId);
            if (retryCount != null) {
                Integer allowedRetryAttempts = eventRetryCount != null && eventRetryCount > 0 ? eventRetryCount : solrIndexStatusErrorRetryCount;
                if (retryCount >= allowedRetryAttempts) {
                    status.getDeadIndexEvents().put(eventId, new Date());
                    status.setLastIndexDate(eventCreatedDate);
                    status.getAdditionalInfo().put(String.format("SystemEventId%s", eventId), String.valueOf(eventId));
                    status.getIndexErrors().remove(eventId);
                } else {
                    status.getIndexErrors().put(eventId, retryCount + 1);
                }
            } else {
                status.setLastIndexDate(eventCreatedDate);
                status.getIndexErrors().put(eventId, 0);//start with a retry count of 0
            }
        }
        updateIndexStatus(status);
    }
    
    @Override
    public IndexStatusInfo getSeedStatusInstance() {
        return new IndexStatusInfoImpl();
    }
    /**
     * Performs the actual update process with the list of providers
     * @param status
     */
    protected void updateIndexStatus(IndexStatusInfo status) {
        for (SolrIndexStatusProvider provider : providers) {
            provider.handleUpdateIndexStatus(status);
        }        
    }

    /**
     * Removes an existing error status entry.  The assumption is that an entry had an error condition and subsequently succeeded.  
     * Upon success, the current error entry should be removed.
     * @param status
     */
    protected void clearErrorStatus(IndexStatusInfo status) {
        IndexStatusInfo persistedStatus = getIndexStatus();
        if (persistedStatus.getIndexErrors().size() > 0) {
            if (status.getAdditionalInfo().size() > 0) {
                List<Long> eventIdsToClear = isEventIdInError(status.getAdditionalInfo().values(), persistedStatus);
                for(Long eventId : eventIdsToClear) {
                    persistedStatus.getIndexErrors().remove(eventId);
                }
            }
        }
        status.setIndexErrors(persistedStatus.getIndexErrors());
    }

    /**
     * Determines which passed InfoIds already exist in the persisted statuses from the provider(s)
     * @param additionalInfoIds
     * @param persistedStatus
     * @return
     */
    protected List<Long> isEventIdInError(Collection<String> additionalInfoIds, IndexStatusInfo persistedStatus) {
        List<Long> eventIdsInError = new ArrayList<Long>();
        for (String infoValue : additionalInfoIds) {
            Long eventId = Long.valueOf(infoValue);
            for(Long entry : persistedStatus.getIndexErrors().keySet()) {
                if (eventId.equals(entry)) {
                    eventIdsInError.add(eventId);
                }
            }
        }
        return eventIdsInError;
    }
    
}
