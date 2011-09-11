package org.broadleafcommerce.cms.admin.server.handler;

import org.broadleafcommerce.cms.file.domain.StaticAssetFolder;
import org.broadleafcommerce.cms.file.domain.StaticAssetFolderImpl;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jfischer
 */
public class StaticAssetFolderCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return persistencePackage.getCeilingEntityFullyQualifiedClassname().equals(StaticAssetFolder.class.getName());
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, Map<String, FieldMetadata> metadataOverrides, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StaticAssetFolder.class);
			Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
				StaticAssetFolder.class.getName(),
				entityClasses,
				(ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY),
				persistencePerspective.getAdditionalNonPersistentProperties(),
				persistencePerspective.getAdditionalForeignKeys(),
				MergedPropertyType.PRIMARY,
				persistencePerspective.getPopulateToOneFields(),
				persistencePerspective.getIncludeFields(),
				persistencePerspective.getExcludeFields(),
				null,
				""
			);
            String[] keys = mergedProperties.keySet().toArray(new String[]{});
			for (String key : keys) {
                FieldMetadata temp = mergedProperties.get(key);
                if (!temp.getInheritedFromType().equals(StaticAssetFolderImpl.class.getName())) {
                    mergedProperties.remove(key);
                }
            }
			allMergedProperties.put(MergedPropertyType.PRIMARY, mergedProperties);
			ClassMetadata mergedMetadata = helper.getMergedClassMetadata(entityClasses, allMergedProperties);
			DynamicResultSet results = new DynamicResultSet(mergedMetadata, null, null);

			return results;
		} catch (Exception e) {
			throw new ServiceException("Unable to retrieve inspection results for " + ceilingEntityFullyQualifiedClassname, e);
		}
    }
}
