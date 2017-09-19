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
    
    private String fiendEntity;
    private Long siteId;
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
     * May be null, but if in a multi-tenant situation, this is the siteId for the batch.
     * @return
     */
    public Long getSiteId() {
        return siteId;
    }
    
    public void setSiteId(Long siteId) {
        this.siteId = siteId;
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
    
}
