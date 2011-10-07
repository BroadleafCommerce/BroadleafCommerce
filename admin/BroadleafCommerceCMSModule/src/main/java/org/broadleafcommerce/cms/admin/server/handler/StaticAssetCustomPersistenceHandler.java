package org.broadleafcommerce.cms.admin.server.handler;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.detector.ExtensionMimeDetector;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.file.domain.*;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.file.service.StaticAssetStorageService;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.presentation.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.service.SandBoxContext;
import org.broadleafcommerce.openadmin.server.service.artifact.image.ImageArtifactProcessor;
import org.broadleafcommerce.openadmin.server.service.artifact.image.ImageMetadata;
import org.broadleafcommerce.openadmin.server.service.artifact.upload.UploadedFile;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.SandBoxService;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.hibernate.Criteria;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Blob;
import java.util.*;

/**
 * Created by jfischer
 */
public class StaticAssetCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(StaticAssetCustomPersistenceHandler.class);
    private static HashMap<String, FieldMetadata> mergedProperties;
    private static HashMap<String, FieldMetadata> foreignKeyReadyMergedProperties;

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

    @Resource(name="blSandBoxService")
    protected SandBoxService sandBoxService;

    protected String assetServerUrlPrefix;

    protected SandBox getSandBox() {
        return sandBoxService.retrieveSandboxById(SandBoxContext.getSandBoxContext().getSandBoxId());
    }

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return persistencePackage.getCustomCriteria() != null && persistencePackage.getCustomCriteria().length > 0 && persistencePackage.getCustomCriteria()[0].equals("assetListUi");
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
    }

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
    }

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
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
            Map<String, FieldMetadata> entityProperties = getForeignKeyReadyMergedProperties();
			adminInstance = (StaticAsset) helper.createPopulatedInstance(adminInstance, entity, entityProperties, false);

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

            adminInstance = staticAssetService.addStaticAsset(adminInstance, adminInstance.getParentFolder(), getSandBox());

			Entity adminEntity = helper.getRecord(entityProperties, adminInstance, null, null);

            try {
                StaticAssetStorage storage = staticAssetStorageService.create();
                storage.setStaticAssetId(adminInstance.getId());
                Blob uploadBlob = staticAssetStorageService.createBlob(upload);
                storage.setFileData(uploadBlob);
                storage = staticAssetStorageService.save(storage);
            } catch (Exception e) {
                /*
                the blob storage is a long-lived transaction - using a compensating transaction to cover failure
                 */
                staticAssetService.deleteStaticAsset(adminInstance, getSandBox());
                throw e;
            }

            return addImageRecords(adminEntity);
		} catch (Exception e) {
			throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
		}
    }

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StaticAssetFolder.class);
            Map<String, FieldMetadata> adminProperties = getForeignKeyReadyMergedProperties();
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            StaticAsset adminInstance = (StaticAsset) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);

            staticAssetService.deleteStaticAsset(adminInstance, getSandBox());
        } catch (Exception e) {
            throw new ServiceException("Unable to delete entity for " + entity.getType()[0], e);
        }
    }

    //do not support update at this time
    /*@Override
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
                Map<String, FieldMetadata> entityProperties = getForeignKeyReadyMergedProperties();
			    Long primaryKey = Long.valueOf(entity.findProperty("idHolder").getValue());
			    StaticAsset adminInstance = (StaticAsset) staticAssetService.findStaticAssetById(primaryKey);

                //detach page from the session so that our changes are not persisted here (we want to let the service take care of this)
                adminInstance = (StaticAsset) SerializationUtils.clone(adminInstance);
                StaticAsset originalInstance = (StaticAsset) SerializationUtils.clone(adminInstance);
			    adminInstance = (StaticAsset) helper.createPopulatedInstance(adminInstance, entity, entityProperties, false);

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

                adminInstance = staticAssetService.updateStaticAsset(adminInstance, getSandBox(persistencePackage));

                Entity adminEntity = helper.getRecord(entityProperties, adminInstance, null, null);

                StaticAssetStorage storage = staticAssetStorageService.readStaticAssetStorageByStaticAssetId(adminInstance.getId());
                if (storage != null) {
                    staticAssetStorageService.delete(storage);
                }

                try {
                    storage = staticAssetStorageService.create();
                    storage.setStaticAssetId(adminInstance.getId());
                    Blob uploadBlob = staticAssetStorageService.createBlob(upload);
                    storage.setFileData(uploadBlob);
                    storage = staticAssetStorageService.save(storage);
                } catch (Exception e) {
                    //the blob storage is a long-lived transaction - using a compensating transaction to cover failure
                    adminInstance = staticAssetService.updateStaticAsset(originalInstance, getSandBox(persistencePackage));
                    throw e;
                }

                return addImageRecords(adminEntity, persistencePackage.getSandBoxInfo());
            } catch (Exception e) {
                throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
            }
        } else {
            Entity entity = persistencePackage.getEntity();
            try {
                PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
                Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StaticAssetFolder.class);
                Map<String, FieldMetadata> entityProperties = getForeignKeyReadyMergedProperties();
                Object primaryKey = helper.getPrimaryKey(entity, entityProperties);
                StaticAsset adminInstance = (StaticAsset) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
                //detach page from the session so that our changes are not persisted here (we want to let the service take care of this)
                adminInstance = (StaticAsset) SerializationUtils.clone(adminInstance);
                adminInstance = (StaticAsset) helper.createPopulatedInstance(adminInstance, entity, entityProperties, false);

                adminInstance = staticAssetService.updateStaticAsset(adminInstance, getSandBox(persistencePackage));

                Entity adminEntity = helper.getRecord(entityProperties, adminInstance, null, null);

                return addImageRecords(adminEntity, persistencePackage.getSandBoxInfo());
            } catch (Exception e) {
                throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
            }
        }
    }*/

    protected Entity addImageRecords(Entity entity) {
        if (entity.getType()[0].equals(ImageStaticAssetImpl.class.getName())) {
            Property fullUrl = entity.findProperty("fullUrl");
            Property property = new Property();
            property.setName("picture");
            property.setValue("../" + assetServerUrlPrefix + fullUrl.getValue() + "?filterType=resize&resize-width-amount=20&resize-height-amount=20&resize-high-quality=false&resize-maintain-aspect-ratio=true&resize-reduce-only=true");
            entity.addProperty(property);
            Property property2 = new Property();
            property2.setName("pictureLarge");
            property2.setValue("../" + assetServerUrlPrefix + fullUrl.getValue() + "?filterType=resize&resize-width-amount=60&resize-height-amount=60&resize-high-quality=false&resize-maintain-aspect-ratio=true&resize-reduce-only=true");
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
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            BaseCtoConverter ctoConverter = helper.getCtoConverter(persistencePerspective, cto, StaticAsset.class.getName(), getForeignKeyReadyMergedProperties());
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, StaticAsset.class.getName());
            Criteria criteria = dynamicEntityDao.getCriteria(queryCriteria, StaticAsset.class);
            List<StaticAsset> items = staticAssetService.findStaticAssetFolderChildren(getSandBox(), criteria);
            List<Serializable> convertedList = new ArrayList<Serializable>();
            convertedList.addAll(items);

            Entity[] pageEntities = helper.getRecords(getForeignKeyReadyMergedProperties(), convertedList);

            for (Entity entity : pageEntities) {
                entity = addImageRecords(entity);
            }

            Long count = staticAssetService.countStaticAssetFolderChildren(getSandBox(), criteria);

            DynamicResultSet response = new DynamicResultSet(pageEntities, count.intValue());

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    protected synchronized Map<String, FieldMetadata> getMergedProperties() {
        return mergedProperties;
    }

    protected synchronized Map<String, FieldMetadata> getForeignKeyReadyMergedProperties() {
        return foreignKeyReadyMergedProperties;
    }

    protected synchronized void createMergedProperties(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, Class<?>[] entityClasses) throws InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
		PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();

        HashMap<String, FieldMetadata> originalProps = (HashMap<String, FieldMetadata>) dynamicEntityDao.getMergedProperties(
            StaticAssetFolder.class.getName(),
            entityClasses,
            (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY),
            persistencePerspective.getAdditionalNonPersistentProperties(),
            persistencePerspective.getAdditionalForeignKeys(),
            MergedPropertyType.PRIMARY,
            persistencePerspective.getPopulateToOneFields(),
            persistencePerspective.getIncludeFields(),
            persistencePerspective.getExcludeFields(),
            persistencePerspective.getConfigurationKey(),
            ""
        );

        mergedProperties = (HashMap<String, FieldMetadata>) SerializationUtils.clone(originalProps);
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
        iconAttributes.setFormHidden(FormHiddenEnum.HIDDEN);
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
        iconLargeAttributes.setReadOnly(true);
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
        mergedProperties.put("idHolder", createHiddenField("idHolder"));
        mergedProperties.put("customCriteria", createHiddenField("customCriteria"));

        foreignKeyReadyMergedProperties = (HashMap<String, FieldMetadata>) SerializationUtils.clone(mergedProperties);
        foreignKeyReadyMergedProperties.put("parentFolder", originalProps.get("parentFolder"));
	}

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
		try {
			Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
            Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StaticAssetFolder.class);
            createMergedProperties(persistencePackage, dynamicEntityDao, entityClasses);

			allMergedProperties.put(MergedPropertyType.PRIMARY, getMergedProperties());
			ClassMetadata mergedMetadata = helper.getMergedClassMetadata(entityClasses, allMergedProperties);
			DynamicResultSet results = new DynamicResultSet(mergedMetadata);

			return results;
		} catch (Exception e) {
            ServiceException ex = new ServiceException("Unable to retrieve inspection results for " + persistencePackage.getCeilingEntityFullyQualifiedClassname(), e);
            LOG.error("Unable to retrieve inspection results for " + persistencePackage.getCeilingEntityFullyQualifiedClassname(), ex);
            throw ex;
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

    public String getAssetServerUrlPrefix() {
        return assetServerUrlPrefix;
    }

    public void setAssetServerUrlPrefix(String assetServerUrlPrefix) {
        this.assetServerUrlPrefix = assetServerUrlPrefix;
    }
}
