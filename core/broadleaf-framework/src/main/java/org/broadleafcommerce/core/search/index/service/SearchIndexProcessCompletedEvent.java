package org.broadleafcommerce.core.search.index.service;

import org.broadleafcommerce.core.search.domain.FieldEntity;

public class SearchIndexProcessCompletedEvent extends SearchIndexProcessEvent {

    private static final long serialVersionUID = 1L;
    
    public SearchIndexProcessCompletedEvent(String processId, FieldEntity fieldEntity) {
        super(processId, fieldEntity);
    }

}
