package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.CriteriaTranslatorImpl;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.web.form.entity.DefaultEntityFormActions;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.service.FormBuilderServiceImpl;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Provides row-level security to the various CRUD operations in the admin
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @author Brian Polster (bpolster)
 */
public interface RowLevelSecurityService {

    /**
     * <p>
     * Used to further restrict a result set in the admin
     * 
     * <p>
     * Existing {@link Predicate} that have already been applied can be retrieved with {@link CriteriaQuery#getRestriction()}
     * and existing sorts that have already been applied can be retrieved with {@link CriteriaQuery#getOrderList()}
     * 
     * @param ceilingEntity the entity currently being queried from
     * @param entityRoot the JPA root for <b>ceilingEntity</b>
     * @param criteria the built and populated JPA critieria with all {@link FilterMapping}s and 
     * @param criteriaBuilder used to append additional restrictions to the given <b>criteria</b>
     * @see {@link CriteriaTranslatorImpl#addRestrictions}
     */
    public void addFetchRestrictions(String ceilingEntity, Root entityRoot, CriteriaQuery criteria, CriteriaBuilder criteriaBuilder);
    
    /**
     * Hook to determine if the given <b>entity</b> can be updated or not. This is used to drive the form displayed in the
     * admin frontend to remove modifier actions and set the entire {@link EntityForm} as readonly.
     * 
     * @param entity the {@link Entity} DTO that is attempting to be updated
     * @return <b>true</b> if the given <b>entity</b> can be updated, <b>false</b> otherwise
     * @see {@link FormBuilderServiceImpl#setReadOnlyState}
     */
    public boolean canUpdate(Entity entity);
    
    /**
     * Hook to determine if the given <b>entity</b> can be updated or not. This is used to drive the {@link DefaultEntityFormActions#DELETE}
     * button from appearing on the admin frontend.
     * 
     * @param entity
     * @return <b>true</b> if the given <b>entity</b> can be deleted, <b>false</b> otherwise
     * @see {@link FormBuilderServiceImpl#addDeleteActionIfAllowed}
     */
    public boolean canDelete(Entity entity);
    
    /**
     * <p>
     * Allows friendly validation errors to occur if the user performed an incorrect update
     * 
     * <p>
     * 
     * @param entity the DTO representation of <b>instance</b> that is being added. If your validator cares about specific
     * properties, then you can find out which properties changed with {@link Property#getIsDirty()}
     * @param instance the populated instance being saved
     * @param entityFieldMetadata all of the property metadata for <b>entity</b>. Use this map if you need any metadata
     * information about the properties from <b>entity</b>.
     * 
     * @return <b>false</b> if validation failed, <b>true</b> otherwise
     */
    public boolean validateUpdateRequest(Entity entity, Serializable instance, Map<String, FieldMetadata> entityFieldMetadata);
    
    /**
     * <p>
     * Validates whether a user has permissions to actually perform an add
     * 
     * @param entity the DTO representation of <b>instance</b> that is being added. If your validator cares about specific
     * properties, then you can find out which properties changed with {@link Property#getIsDirty()}
     * @param instance the populated instance being saved
     * @param entityFieldMetadata all of the property metadata for <b>entity</b>. Use this map if you need any metadata
     * information about the properties from <b>entity</b>.
     * @return <b>false</b> if validation failed, <b>true</b> otherwise
     */
    public boolean validateAddRequest(Entity entity, Serializable instance, Map<String, FieldMetadata> entityFieldMetadata);
    
}
