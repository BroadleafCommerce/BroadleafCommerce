package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Service("blRowLevelSecurityService")
public class RowLevelSecurityServiceImpl implements RowLevelSecurityService {

    @Override
    public void addFetchRestrictions(String ceilingEntity, Root entityRoot, CriteriaQuery criteria, CriteriaBuilder criteriaBuilder) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean validateUpdateRequest(Entity entity, Serializable instance, Map<String, FieldMetadata> entityFieldMetadata) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean validateAddRequest(Entity entity, Serializable instance, Map<String, FieldMetadata> entityFieldMetadata) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean canUpdate(Entity entity) {
        // TODO Auto-generated method stub
        return true;
    }

}
