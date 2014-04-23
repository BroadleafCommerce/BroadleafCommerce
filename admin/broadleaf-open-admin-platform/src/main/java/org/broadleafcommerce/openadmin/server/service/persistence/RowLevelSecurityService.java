package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.CriteriaTranslatorEventHandler;

import java.io.Serializable;
import java.util.Map;

/**
 * Provides row-level security to the various CRUD operations in the admin
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @author Brian Polster (bpolster)
 */
public interface RowLevelSecurityService extends CriteriaTranslatorEventHandler {

    
    /**
     * Allows friendly validation errors to occur if the user performed an incorrect update
     * 
     * @return <b>false</b> if validation failed, <b>true</b> otherwise
     * //TODO: dto return or add validation errors to the entity?
     */
    public boolean validateUpdateRequest(Entity entity, Serializable instance, Map<String, FieldMetadata> entityFieldMetadata);
    
    //TODO: how can we differnentiate between an ADD and UPDATE request?
    public boolean validateAddRequest(Entity entity, Serializable instance, Map<String, FieldMetadata> entityFieldMetadata);
    
}
