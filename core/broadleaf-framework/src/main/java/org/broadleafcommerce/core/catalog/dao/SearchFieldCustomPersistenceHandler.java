package org.broadleafcommerce.core.catalog.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.search.domain.SearchField;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.broadleafcommerce.core.search.service.type.SearchFieldType;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Chad Harchar (charchar)
 */
@Component("blSearchFieldCustomPersistenceHandler")
public class SearchFieldCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(SearchFacetRangeCustomPersistenceHandler.class);

    @Resource(name = "blSearchFieldCustomPersistenceHandlerExtensionManager")
    protected SearchFieldCustomPersistenceHandlerExtensionManager extensionManager;

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return SearchField.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(SearchField.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            SearchField adminInstance = (SearchField) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);

            return getEntity(persistencePackage, dynamicEntityDao, helper, entity, adminProperties, adminInstance);
        } catch (Exception e) {
            throw new ServiceException("Unable to perform update for entity: " + SearchField.class.getName(), e);
        }
    }

    protected Entity getEntity(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper, Entity entity, Map<String, FieldMetadata> adminProperties, SearchField adminInstance) throws ServiceException {
        adminInstance = (SearchField) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
        adminInstance = dynamicEntityDao.merge(adminInstance);

        SearchFieldType fieldType = SearchFieldType.getInstance(adminInstance.getField().getFieldType());

        List<FieldType> searchableFieldTypes = new ArrayList<FieldType>();
        if (fieldType.equals(SearchFieldType.STRING)) {
            searchableFieldTypes.add(FieldType.TEXT);
        } else if (fieldType.equals(SearchFieldType.BOOLEAN)) {
            searchableFieldTypes.add(FieldType.BOOLEAN);
        } else if (fieldType.equals(SearchFieldType.DATE)) {
            searchableFieldTypes.add(FieldType.DATE);
        } else if (fieldType.equals(SearchFieldType.DECIMAL)) {
            searchableFieldTypes.add(FieldType.DOUBLE);
        } else if (fieldType.equals(SearchFieldType.INTEGER)) {
            searchableFieldTypes.add(FieldType.INT);
        } else if (fieldType.equals(SearchFieldType.MONEY)) {
            searchableFieldTypes.add(FieldType.PRICE);
        }

        adminInstance.setSearchableFieldTypes(searchableFieldTypes);

        if (extensionManager != null) {
            extensionManager.getProxy().addtoSearchableFields(persistencePackage, adminInstance);
        }

        Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

        return adminEntity;
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            SearchField adminInstance = (SearchField) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(SearchField.class.getName(), persistencePerspective);
            return getEntity(persistencePackage, dynamicEntityDao, helper, entity, adminProperties, adminInstance);
        } catch (Exception e) {
            throw new ServiceException("Unable to perform add for entity: " + SearchField.class.getName(), e);
        }
    }

}