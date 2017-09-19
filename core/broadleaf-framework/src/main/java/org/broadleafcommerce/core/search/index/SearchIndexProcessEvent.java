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
package org.broadleafcommerce.core.search.index;

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
