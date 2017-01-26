/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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

import java.util.Date;

/**
 * @author Daniel Colgrove
 */
public class IndexStatusErrorImpl implements IndexStatusError {
    private Long eventId;
    private Integer retryCount;
    private Date errorDate;
    
    public IndexStatusErrorImpl() {}
    
    public IndexStatusErrorImpl(Long eventId) {
        this.eventId = eventId;
        this.retryCount = 0;
        this.errorDate = new Date();
    }
    
    public IndexStatusErrorImpl(Long eventId, Integer retryCount, Date errorDate) {
        this.eventId = eventId;
        this.retryCount = retryCount;
        this.errorDate = errorDate;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    public Long getEventId() {
        return eventId;
    }
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    public Integer getRetryCount() {
        return retryCount;
    }
    public void setErrorDate(Date errorDate) {
        this.errorDate = errorDate;
    }
    public Date getErrorDate() {
        return errorDate;
    }
}