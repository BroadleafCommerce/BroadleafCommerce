package org.broadleafcommerce.core.search.service.solr.index;

import java.util.Date;

/**
 * Error status information about for a (embedded) Solr instance's index
 *
 * @author Daniel Colgrove
 */
public interface IndexStatusError {
    /**
     * The ID of the {@Link SystemEvent{
     * @param eventId
     */
    void setEventId(Long eventId);

    Long getEventId();

    /**
     * The current retry counter
     * @param retryCount
     */
    void setRetryCount(Integer retryCount);

    Integer getRetryCount();

    /**
     * The date of the event error
     * @param errorDate
     */
    void setErrorDate(Date errorDate);
    Date getErrorDate();
}
