package org.broadleafcommerce.cms.admin.server.handler;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.detector.ExtensionMimeDetector;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;
import org.broadleafcommerce.cms.file.domain.*;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.file.service.StaticAssetStorageService;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.presentation.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.artifact.image.ImageArtifactProcessor;
import org.broadleafcommerce.openadmin.server.service.artifact.image.ImageMetadata;
import org.broadleafcommerce.openadmin.server.service.artifact.upload.UploadedFile;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.sql.Blob;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jfischer
 */
public class StaticAssetCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    static {
        MimeUtil.registerMimeDetector(ExtensionMimeDetector.class.getName());
        MimeUtil.registerMimeDetector(MagicMimeMimeDetector.class.getName());
    }

    @Resource(name="blStaticAssetService")
	protected StaticAssetService staticAssetService;

    @Resource(name="blStaticAssetStorageService")
	protected StaticAssetStorageService staticAssetStorageService;

    @Resource(name="blImageArtifactProcessor")
    protected ImageArtifactProcessor imageArtifactProcessor;

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return persistencePackage.getCeilingEntityFullyQualifiedClassname().equals(StaticAsset.class.getName());
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return persistencePackage.getCeilingEntityFullyQualifiedClassname().equals(StaticAssetImpl.class.getName());
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        if (!persistencePackage.getEntity().isMultiPartAvailableOnThread()) {
            throw new ServiceException("Could not detect an uploaded file.");
        }
        MultipartFile upload = UploadedFile.getUpload().get("file");
        Entity entity  = persistencePackage.getEntity();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            StaticAsset adminInstance;
            try {
                ImageMetadata metadata = imageArtifactProcessor.getImageMetadata(upload.getInputStream());
                adminInstance = new ImageStaticAssetImpl();
                ((ImageStaticAsset) adminInstance).setWidth(metadata.getWidth());
                ((ImageStaticAsset) adminInstance).setHeight(metadata.getHeight());
            } catch (Exception e) {
                //must not be an image stream
                adminInstance = new StaticAssetImpl();
            }
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StaticAssetFolder.class);
			Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(StaticAssetFolder.class.getName(), persistencePerspective, dynamicEntityDao, entityClasses);
			adminInstance = (StaticAsset) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            adminInstance.setFileSize(upload.getSize());
            Collection mimeTypes = MimeUtil.getMimeTypes(upload.getOriginalFilename());
            if (!mimeTypes.isEmpty()) {
                MimeType mimeType = (MimeType) mimeTypes.iterator().next();
                adminInstance.setMimeType(mimeType.toString());
            } else {
                mimeTypes = MimeUtil.getMimeTypes(upload.getInputStream());
                if (!mimeTypes.isEmpty()) {
                    MimeType mimeType = (MimeType) mimeTypes.iterator().next();
                    adminInstance.setMimeType(mimeType.toString());
                }
            }
            adminInstance = staticAssetService.addStaticAsset(adminInstance, adminInstance.getParentFolder(), null);

			Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

            StaticAssetStorage storage = staticAssetStorageService.create();
            storage.setFullUrl(adminInstance.getFullUrl());
            Blob uploadBlob = staticAssetStorageService.createBlob(upload);
            storage.setFileData(uploadBlob);
            storage = staticAssetStorageService.save(storage);

			return adminEntity;
		} catch (Exception e) {
			throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
		}
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
            fieldMetadata.setAvailableToTypes(new String[] {StaticAssetImpl.class.getName()});
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
            attributes.setHidden(true);
            attributes.setRequiredOverride(true);

            mergedProperties.put("file", fieldMetadata);

            mergedProperties.put("callbackName", createHiddenField("callbackName"));
            mergedProperties.put("operation", createHiddenField("operation"));
            mergedProperties.put("sandbox", createHiddenField("sandbox"));
            mergedProperties.put("ceilingEntityFullyQualifiedClassname", createHiddenField("ceilingEntityFullyQualifiedClassname"));
            mergedProperties.put("parentFolder", createHiddenField("parentFolder"));

			allMergedProperties.put(MergedPropertyType.PRIMARY, mergedProperties);
			ClassMetadata mergedMetadata = helper.getMergedClassMetadata(entityClasses, allMergedProperties);
			DynamicResultSet results = new DynamicResultSet(mergedMetadata, null, null);

			return results;
		} catch (Exception e) {
			throw new ServiceException("Unable to retrieve inspection results for " + ceilingEntityFullyQualifiedClassname, e);
		}
    }

    protected FieldMetadata createHiddenField(String name) {
        FieldMetadata fieldMetadata = new FieldMetadata();
        fieldMetadata.setFieldType(SupportedFieldType.HIDDEN);
        fieldMetadata.setMutable(true);
        fieldMetadata.setInheritedFromType(StaticAssetImpl.class.getName());
        fieldMetadata.setAvailableToTypes(new String[]{StaticAssetImpl.class.getName()});
        fieldMetadata.setCollection(false);
        fieldMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
        FieldPresentationAttributes attributes = new FieldPresentationAttributes();
        fieldMetadata.setPresentationAttributes(attributes);
        attributes.setName(name);
        attributes.setFriendlyName(name);
        attributes.setGroup("Upload");
        attributes.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        attributes.setProminent(false);
        attributes.setBroadleafEnumeration("");
        attributes.setReadOnly(false);
        attributes.setHidden(true);
        return fieldMetadata;
    }
}
