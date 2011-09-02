package org.broadleafcommerce.cms.admin.server.service.handler;

import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.domain.StaticAssetFolder;
import org.broadleafcommerce.cms.file.domain.StaticAssetImpl;
import org.broadleafcommerce.cms.page.domain.PageTemplateImpl;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.presentation.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jfischer
 */
public class StaticAssetCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return persistencePackage.getCeilingEntityFullyQualifiedClassname().equals(StaticAsset.class.getName());
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

            FieldMetadata fieldMetadata = new FieldMetadata();
            fieldMetadata.setFieldType(SupportedFieldType.UPLOAD);
            fieldMetadata.setMutable(true);
            fieldMetadata.setInheritedFromType(StaticAssetImpl.class.getName());
            fieldMetadata.setAvailableToTypes(new String[] {PageTemplateImpl.class.getName()});
            fieldMetadata.setCollection(false);
            fieldMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
            FieldPresentationAttributes attributes = new FieldPresentationAttributes();
            fieldMetadata.setPresentationAttributes(attributes);
            attributes.setName("file");
            attributes.setFriendlyName("File");
            attributes.setGroup("Upload");
            attributes.setExplicitFieldType(SupportedFieldType.UNKNOWN);
            attributes.setProminent(false);
            attributes.setBroadleafEnumeration("");
            attributes.setReadOnly(false);

            mergedProperties.put("file", fieldMetadata);

            FieldMetadata fieldMetadata2 = new FieldMetadata();
            fieldMetadata2.setFieldType(SupportedFieldType.HIDDEN);
            fieldMetadata2.setMutable(true);
            fieldMetadata2.setInheritedFromType(StaticAssetImpl.class.getName());
            fieldMetadata2.setAvailableToTypes(new String[]{PageTemplateImpl.class.getName()});
            fieldMetadata2.setCollection(false);
            fieldMetadata2.setMergedPropertyType(MergedPropertyType.PRIMARY);
            FieldPresentationAttributes attributes2 = new FieldPresentationAttributes();
            fieldMetadata2.setPresentationAttributes(attributes2);
            attributes2.setName("callbackName");
            attributes2.setFriendlyName("callbackName");
            attributes2.setGroup("Upload");
            attributes2.setExplicitFieldType(SupportedFieldType.UNKNOWN);
            attributes2.setProminent(false);
            attributes2.setBroadleafEnumeration("");
            attributes2.setReadOnly(false);
            attributes2.setHidden(true);

            mergedProperties.put("callbackName", fieldMetadata2);

			allMergedProperties.put(MergedPropertyType.PRIMARY, mergedProperties);
			ClassMetadata mergedMetadata = helper.getMergedClassMetadata(entityClasses, allMergedProperties);
			DynamicResultSet results = new DynamicResultSet(mergedMetadata, null, null);

			return results;
		} catch (Exception e) {
			throw new ServiceException("Unable to retrieve inspection results for " + ceilingEntityFullyQualifiedClassname, e);
		}
    }
}
