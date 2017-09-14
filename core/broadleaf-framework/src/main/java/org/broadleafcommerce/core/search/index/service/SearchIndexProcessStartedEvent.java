package org.broadleafcommerce.core.search.index.service;

import org.broadleafcommerce.core.search.domain.FieldEntity;


public class SearchIndexProcessStartedEvent extends SearchIndexProcessEvent {

    private static final long serialVersionUID = 1L;
    private final String queueName;
    
    public SearchIndexProcessStartedEvent(String processId, FieldEntity fieldEntity, String queueName) {
        super(processId, fieldEntity);
        this.queueName = queueName;
    }

    public String getQueueName() {
        return queueName;
    }
}
