package org.broadleafcommerce.cms.admin.server.handler;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.detector.ExtensionMimeDetector;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;
import org.apache.commons.lang.SerializationUtils;
import org.broadleafcommerce.cms.file.domain.*;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.file.service.StaticAssetStorageService;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.presentation.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.artifact.image.ImageArtifactProcessor;
import org.broadleafcommerce.openadmin.server.service.artifact.image.ImageMetadata;
import org.broadleafcommerce.openadmin.server.service.artifact.upload.UploadedFile;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Blob;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jfischer
 */
public class StaticAssetCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static Map<String, FieldMetadata> mergedProperties;

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
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage) || canHandleInspect(persistencePackage);
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
            String extension = upload.getOriginalFilename().substring(upload.getOriginalFilename().lastIndexOf(".") + 1, upload.getOriginalFilename().length()).toLowerCase();
            adminInstance.setFileExtension(extension);

            adminInstance = staticAssetService.addStaticAsset(adminInstance, adminInstance.getParentFolder(), null);

			Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

            StaticAssetStorage storage = staticAssetStorageService.create();
            storage.setFullUrl(adminInstance.getFullUrl());
            storage.setStaticAssetId(adminInstance.getId());
            Blob uploadBlob = staticAssetStorageService.createBlob(upload);
            storage.setFileData(uploadBlob);
            storage = staticAssetStorageService.save(storage);

			return addImageRecords(adminEntity);
		} catch (Exception e) {
			throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
		}
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        if (canHandleAdd(persistencePackage)) {
            if (!persistencePackage.getEntity().isMultiPartAvailableOnThread()) {
                throw new ServiceException("Could not detect an uploaded file.");
            }
            MultipartFile upload = UploadedFile.getUpload().get("file");
            Entity entity  = persistencePackage.getEntity();
            try {
                PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();

			    Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StaticAssetFolder.class);
                Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(StaticAssetFolder.class.getName(), persistencePerspective, dynamicEntityDao, entityClasses);
			    Long primaryKey = (Long) helper.getPrimaryKey(entity, adminProperties);
			    StaticAsset adminInstance = (StaticAsset) staticAssetService.findStaticAssetById(primaryKey);

                //detach page from the session so that our changes are not persisted here (we want to let the service take care of this)
                adminInstance = (StaticAsset) SerializationUtils.clone(adminInstance);
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
                String extension = upload.getOriginalFilename().substring(upload.getOriginalFilename().lastIndexOf(".") + 1, upload.getOriginalFilename().length()).toLowerCase();
                adminInstance.setFileExtension(extension);

                adminInstance = staticAssetService.updateStaticAsset(adminInstance, null);

                Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

                StaticAssetStorage storage = staticAssetStorageService.readStaticAssetStorageByStaticAssetId(adminInstance.getId());
                if (storage != null) {
                    staticAssetStorageService.delete(storage);
                }

                storage = staticAssetStorageService.create();
                storage.setFullUrl(adminInstance.getFullUrl());
                storage.setStaticAssetId(adminInstance.getId());
                Blob uploadBlob = staticAssetStorageService.createBlob(upload);
                storage.setFileData(uploadBlob);
                storage = staticAssetStorageService.save(storage);

                return addImageRecords(adminEntity);
            } catch (Exception e) {
                throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
            }
        } else {
            Entity entity = persistencePackage.getEntity();
            try {
                PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
                Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StaticAssetFolder.class);
                Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(StaticAssetFolder.class.getName(), persistencePerspective, dynamicEntityDao, entityClasses);
                Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
                StaticAsset adminInstance = (StaticAsset) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
                //detach page from the session so that our changes are not persisted here (we want to let the service take care of this)
                adminInstance = (StaticAsset) SerializationUtils.clone(adminInstance);
                adminInstance = (StaticAsset) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

                adminInstance = staticAssetService.updateStaticAsset(adminInstance, null);

                Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

                return addImageRecords(adminEntity);
            } catch (Exception e) {
                throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
            }
        }
    }

    protected Entity addImageRecords(Entity entity) {
        if (entity.getType()[0].equals(ImageStaticAssetImpl.class.getName())) {
            Property key = entity.findProperty("id");
            Property extension = entity.findProperty("fileExtension");
            Property property = new Property();
            property.setName("picture");
            property.setValue("cms/staticasset/" + key.getValue() + "." + extension.getValue() + "?filterType=resize&resize-width-amount=20&resize-height-amount=20&resize-high-quality=false&resize-maintain-aspect-ratio=true&resize-reduce-only=true");
            entity.addProperty(property);
            Property property2 = new Property();
            property2.setName("pictureLarge");
            property2.setValue("../cms/staticasset/" + key.getValue() + "." + extension.getValue() + "?filterType=resize&resize-width-amount=60&resize-height-amount=60&resize-high-quality=false&resize-maintain-aspect-ratio=true&resize-reduce-only=true");
            entity.addProperty(property2);
        } else {
            Property property = new Property();
            property.setName("picture");
            property.setValue("[ISOMORPHIC]/../admin/images/Mimetype-binary-icon-16.png");
            entity.addProperty(property);
            Property property2 = new Property();
            property2.setName("pictureLarge");
            property2.setValue("[ISOMORPHIC]/../admin/images/Mimetype-binary-icon-64.png");
            entity.addProperty(property2);
        }

        return entity;
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            /*String parentCategoryId = cto.get(StaticAssetsTreeDataSourceFactory.parentFolderForeignKey).getFilterValues().length==0?null:cto.get(StaticAssetsTreeDataSourceFactory.parentFolderForeignKey).getFilterValues()[0];
            StaticAssetFolder pageOrFolder = null;
            if (parentCategoryId != null) {
                pageOrFolder = staticAssetService.findStaticAssetById(Long.valueOf(parentCategoryId));
            }
            List<StaticAssetFolder> folders = staticAssetService.findStaticAssetFolderChildren(null, pageOrFolder);
            List<Serializable> convertedList = new ArrayList<Serializable>();
            convertedList.addAll(folders);*/
            /*
            TODO once the staticAssetService is code stabilized, convert it to use criteria created below and
            add any additional parameters it needs rather than use named queries. This will allow users to enter
            filter terms in the UI and have those filters impact the results. This will also enable data paging.
             */
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            BaseCtoConverter ctoConverter = helper.getCtoConverter(persistencePerspective, cto, StaticAsset.class.getName(), getMergedProperties());
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, StaticAsset.class.getName());
			List<Serializable> records = dynamicEntityDao.query(queryCriteria, StaticAsset.class);

            Entity[] pageEntities = helper.getRecords(getMergedProperties(), records);

            for (Entity entity : pageEntities) {
                entity = addImageRecords(entity);
            }

            DynamicResultSet response = new DynamicResultSet(pageEntities, pageEntities.length);

            return response;
        } catch (Exception e) {
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    protected synchronized Map<String, FieldMetadata> getMergedProperties() {
        return mergedProperties;
    }

    protected synchronized void createMergedProperties(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, Class<?>[] entityClasses) throws InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
		PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();

        mergedProperties = dynamicEntityDao.getMergedProperties(
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
        fieldMetadata.setAvailableToTypes(new String[] {StaticAssetImpl.class.getName(), ImageStaticAssetImpl.class.getName()});
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

        FieldMetadata iconMetadata = new FieldMetadata();
        iconMetadata.setFieldType(SupportedFieldType.ARTIFACT);
        iconMetadata.setMutable(true);
        iconMetadata.setInheritedFromType(StaticAssetImpl.class.getName());
        iconMetadata.setAvailableToTypes(new String[]{StaticAssetImpl.class.getName(), ImageStaticAssetImpl.class.getName()});
        iconMetadata.setCollection(false);
        iconMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
        FieldPresentationAttributes iconAttributes = new FieldPresentationAttributes();
        iconMetadata.setPresentationAttributes(iconAttributes);
        iconAttributes.setName("picture");
        iconAttributes.setFriendlyName(" ");
        iconAttributes.setGroup("Asset Details");
        iconAttributes.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        iconAttributes.setProminent(true);
        iconAttributes.setBroadleafEnumeration("");
        iconAttributes.setReadOnly(false);
        iconAttributes.setHidden(false);
        iconAttributes.setFormHidden(true);
        iconAttributes.setColumnWidth("25");
        iconAttributes.setOrder(0);
        iconAttributes.setRequiredOverride(true);

        mergedProperties.put("picture", iconMetadata);

        FieldMetadata iconLargeMetadata = new FieldMetadata();
        iconLargeMetadata.setFieldType(SupportedFieldType.ARTIFACT);
        iconLargeMetadata.setMutable(true);
        iconLargeMetadata.setInheritedFromType(StaticAssetImpl.class.getName());
        iconLargeMetadata.setAvailableToTypes(new String[]{StaticAssetImpl.class.getName(), ImageStaticAssetImpl.class.getName()});
        iconLargeMetadata.setCollection(false);
        iconLargeMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
        FieldPresentationAttributes iconLargeAttributes = new FieldPresentationAttributes();
        iconLargeMetadata.setPresentationAttributes(iconLargeAttributes);
        iconLargeAttributes.setName("pictureLarge");
        iconLargeAttributes.setFriendlyName("Preview");
        iconLargeAttributes.setGroup("Preview");
        iconLargeAttributes.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        iconLargeAttributes.setProminent(false);
        iconLargeAttributes.setBroadleafEnumeration("");
        iconLargeAttributes.setReadOnly(false);
        iconLargeAttributes.setHidden(false);
        iconLargeAttributes.setRequiredOverride(true);
        iconLargeAttributes.setOrder(0);
        iconLargeAttributes.setGroupOrder(0);

        mergedProperties.put("pictureLarge", iconLargeMetadata);

        mergedProperties.put("callbackName", createHiddenField("callbackName"));
        mergedProperties.put("operation", createHiddenField("operation"));
        mergedProperties.put("sandbox", createHiddenField("sandbox"));
        mergedProperties.put("ceilingEntityFullyQualifiedClassname", createHiddenField("ceilingEntityFullyQualifiedClassname"));
        mergedProperties.put("parentFolder", createHiddenField("parentFolder"));
	}

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, Map<String, FieldMetadata> metadataOverrides, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
		try {
			Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
            Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StaticAssetFolder.class);
            createMergedProperties(persistencePackage, dynamicEntityDao, entityClasses);

			allMergedProperties.put(MergedPropertyType.PRIMARY, getMergedProperties());
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
