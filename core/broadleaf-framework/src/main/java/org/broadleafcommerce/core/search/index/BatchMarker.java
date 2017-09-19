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

import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.search.domain.FieldEntity;

import java.io.Serializable;

/**
 * Holder to allow for receivers to batch query the specific data where the fistValue and lastValue are 
 * long values representing the lower and upper bounds of a primary keys for {@link Indexable} items. 
 * The siteId and catalogId values may be null.  The fieldEntity is the String 
 * representation of the {@link FieldEntity} object.
 * 
 * @author Kelly Tisdell
 *
 */
public class BatchMarker implements Serializable {

    private static final long serialVersionUID = 1L;
    private int expectedBatchSize;
    private String fiendEntity;
    private Long catalogId;
    private Long firstValue;
    private Long lastValue;
    
    /**
     * This should be the String representation of the {@link FieldEntity}.
     * @return
     */
    public String getFiendEntity() {
        return fiendEntity;
    }
    
    public void setFiendEntity(String fiendEntity) {
        this.fiendEntity = fiendEntity;
    }
    
    /**
     * May be null, but if in a multi-tenant situation, this is the catalogId for the batch.
     * @return
     */
    public Long getCatalogId() {
        return catalogId;
    }
    
    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }
    
    /**
     * The first value in the batch.
     * @return
     */
    public Long getFirstValue() {
        return firstValue;
    }
    
    public void setFirstValue(Long firstValue) {
        this.firstValue = firstValue;
    }
    
    /**
     * Typically the last value, or ID, in the batch.
     * @return
     */
    public Long getLastValue() {
        return lastValue;
    }
    
    public void setLastValue(Long lastValue) {
        this.lastValue = lastValue;
    }

    /**
     * Provides the expected number of items in this batch (given that we are only provided the first and last ID 
     * in the batch).
     * @return
     */
    public int getExpectedBatchSize() {
        return expectedBatchSize;
    }

    
    public void setExpectedBatchSize(int expectedBatchSize) {
        this.expectedBatchSize = expectedBatchSize;
    }
    
    
    
}
