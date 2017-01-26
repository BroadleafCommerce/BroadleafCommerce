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