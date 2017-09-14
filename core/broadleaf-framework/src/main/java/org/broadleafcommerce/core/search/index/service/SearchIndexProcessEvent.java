package org.broadleafcommerce.core.search.index.service;

import org.broadleafcommerce.common.event.BroadleafApplicationEvent;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.springframework.util.Assert;

/**
 * Abstract Spring event.  Allows us to raise events through Spring (and ultimately via a distributed approach, if needed).
 * 
 * @author Kelly Tisdell
 *
 */
public abstract class SearchIndexProcessEvent extends BroadleafApplicationEvent {

    private static final long serialVersionUID = 1L;
    private final String processId;
    private final FieldEntity fieldEntity;

    public SearchIndexProcessEvent(String processId, FieldEntity fieldEntity) {
        super(processId);
        Assert.notNull(processId, "Process ID cannot be null.");
        this.processId = processId;
        this.fieldEntity = fieldEntity;
    }

    public String getProcessId() {
        return processId;
    }
    
    public FieldEntity getFieldEntity() {
        return fieldEntity;
    }
}
