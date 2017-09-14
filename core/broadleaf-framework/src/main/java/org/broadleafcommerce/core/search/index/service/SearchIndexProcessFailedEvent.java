package org.broadleafcommerce.core.search.index.service;

import org.broadleafcommerce.core.search.domain.FieldEntity;

public class SearchIndexProcessFailedEvent extends SearchIndexProcessEvent {

    private static final long serialVersionUID = 1L;
    private final Throwable error;

    public SearchIndexProcessFailedEvent(String processId, FieldEntity fieldEntity, Throwable t) {
        super(processId, fieldEntity);
        this.error = t;
    }

    public Throwable getError() {
        return error;
    }
}
