package org.broadleafcommerce.openadmin.server.security.service;

import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.GlobalValidationResult;

import java.io.Serializable;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Dummy implementation of a {@link RowLevelSecurityProvider}. Implementors should extend this class
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class AbstractRowLevelSecurityProvider implements RowLevelSecurityProvider {

    @Override
    public void addFetchRestrictions(AdminUser currentUser, String ceilingEntity, List<Predicate> restrictions, List<Order> sorts, Root entityRoot, CriteriaQuery criteria, CriteriaBuilder criteriaBuilder) {
        // intentionally unimplemented
    }

    @Override
    public Class<Serializable> getFetchRestrictionRoot(AdminUser currentUser, Class<Serializable> ceilingEntity, List<FilterMapping> filterMappings) {
        return null;
    }

    @Override
    public boolean canUpdate(AdminUser currentUser, Entity entity) {
        return true;
    }

    @Override
    public boolean canRemove(AdminUser currentUser, Entity entity) {
        return true;
    }

    @Override
    public GlobalValidationResult validateUpdateRequest(AdminUser currentUser, Entity entity, PersistencePackage persistencePackage) {
        return new GlobalValidationResult(true);
    }

    @Override
    public GlobalValidationResult validateRemoveRequest(AdminUser currentUser, Entity entity, PersistencePackage persistencePackage) {
        return new GlobalValidationResult(true);
    }

}
