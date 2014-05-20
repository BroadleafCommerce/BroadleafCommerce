package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.common.presentation.client.ForeignKeyRestrictionType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPath;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.RestrictionType;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddSearchMappingRequest;
import org.broadleafcommerce.openadmin.server.service.type.FieldProviderResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Allows filter mappings to be created for collection fields for @ManyToMany scenarios when the collection metadata
 * is really a foreign key. Most of the logic here was lifted from ForeignKey filtering in {@link BasicFieldPersistenceProvider}
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blCollectionFieldPersistenceProvider")
@Scope("prototype")
public class CollectionFieldPersistenceProvider extends FieldPersistenceProviderAdapter {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
    
    protected boolean canHandleSearchMapping(AddSearchMappingRequest addSearchMappingRequest, List<FilterMapping> filterMappings) {
        FieldMetadata metadata = addSearchMappingRequest.getMergedProperties().get(addSearchMappingRequest.getPropertyName());
        return metadata instanceof BasicCollectionMetadata;
        //return false;
    }
    
    @Override
    public FieldProviderResponse addSearchMapping(AddSearchMappingRequest addSearchMappingRequest, List<FilterMapping> filterMappings) {
        if (!canHandleSearchMapping(addSearchMappingRequest, filterMappings)) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        
        Class clazz;
        try {
            clazz = Class.forName(addSearchMappingRequest.getMergedProperties().get(addSearchMappingRequest
                    .getPropertyName()).getInheritedFromType());
        } catch (ClassNotFoundException e) {
            throw new PersistenceException(e);
        }
        Field field = addSearchMappingRequest.getFieldManager().getField(clazz,
                addSearchMappingRequest.getPropertyName());
        Class<?> targetType = null;
        if (field != null) {
            targetType = field.getType();
        }
        
        FilterAndSortCriteria fasc = addSearchMappingRequest.getRequestedCto().get(addSearchMappingRequest.getPropertyName());

        FilterMapping filterMapping = new FilterMapping()
                .withInheritedFromClass(clazz)
                .withFullPropertyName(addSearchMappingRequest.getPropertyName())
                .withFilterValues(fasc.getFilterValues())
                .withSortDirection(fasc.getSortDirection());
        filterMappings.add(filterMapping);
        
        if (CollectionUtils.isNotEmpty(addSearchMappingRequest.getRequestedCto().get(addSearchMappingRequest.getPropertyName())
                .getFilterValues())) {
            ForeignKey foreignKey = (ForeignKey) addSearchMappingRequest.getPersistencePerspective()
                    .getPersistencePerspectiveItems()
                    .get(PersistencePerspectiveItemType.FOREIGNKEY);
            if (ForeignKeyRestrictionType.COLLECTION_SIZE_EQ.toString().equals(foreignKey
                    .getRestrictionType().toString())) {
                filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory()
                        .getRestriction(RestrictionType.COLLECTION_SIZE_EQUAL.getType(),
                                addSearchMappingRequest.getPropertyName()));
                filterMapping.setFieldPath(new FieldPath());
            } else {
                // Find out the primary key property from the foreign instance to filter appropriately
                Map<String, FieldMetadata> foreignKeyClassMd = addSearchMappingRequest.getRecordHelper().getSimpleMergedProperties(clazz.getName(), addSearchMappingRequest.getPersistencePerspective());
                String primaryKeyProperty = null;
                for (Entry<String, FieldMetadata> entry : foreignKeyClassMd.entrySet()) {
                    if (entry.getValue() instanceof BasicFieldMetadata) {
                        if (SupportedFieldType.ID.equals(((BasicFieldMetadata) entry.getValue()).getFieldType())) {
                            primaryKeyProperty = entry.getKey();
                            break;
                        }
                    }
                }
                
                filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory()
                        .getRestriction(RestrictionType.LONG.getType(),
                                addSearchMappingRequest.getPropertyName()));
                String filterPath = addSearchMappingRequest.getPropertyName() + "." + primaryKeyProperty;
                filterMapping.setFieldPath(new FieldPath().withTargetProperty(filterPath));
            }
            return FieldProviderResponse.HANDLED_BREAK;
        }
        
        return FieldProviderResponse.NOT_HANDLED;
    }
    
}
