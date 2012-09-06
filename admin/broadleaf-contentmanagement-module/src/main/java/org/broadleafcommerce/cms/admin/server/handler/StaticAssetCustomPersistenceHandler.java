/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.admin.server.handler;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.server.CriteriaTransferObjectCountWrapper;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.detector.ExtensionMimeDetector;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.file.domain.ImageStaticAsset;
import org.broadleafcommerce.cms.file.domain.ImageStaticAssetImpl;
import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.domain.StaticAssetImpl;
import org.broadleafcommerce.cms.file.domain.StaticAssetStorage;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.file.service.StaticAssetStorageService;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.web.SandBoxContext;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jfischer
 */
public class StaticAssetCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(StaticAssetCustomPersistenceHandler.class);
    private static HashMap<String, FieldMetadata> mergedProperties;

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
        return persistencePackage.getCustomCriteria() != null && persistencePackage.getCustomCriteria().length > 0 && "assetListUi".equals(persistencePackage.getCustomCriteria()[0]);
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
    
    protected String getFileName(String fullPathName) {
        int pos = fullPathName.lastIndexOf("/");
        checkPath: {
            //try a unix based path
            if (pos >= 0) {
                break checkPath;
            }
            pos = fullPathName.lastIndexOf("\\");
            //try windows path
            if (pos >= 0) {
                break checkPath;
            }
            //just take the full path name
            return fullPathName;
        }
        return fullPathName.substring(pos + 1, fullPathName.length());
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        if (!persistencePackage.getEntity().isMultiPartAvailableOnThread()) {
            throw new ServiceException("Could not detect an uploaded file.");
        }
        MultipartFile upload = UploadedFile.getUpload().get("file");
        Entity entity  = persistencePackage.getEntity();
		try {
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
            Map<String, FieldMetadata> entityProperties = getMergedProperties();
			adminInstance = (StaticAsset) helper.createPopulatedInstance(adminInstance, entity, entityProperties, false);

            String fileName = getFileName(upload.getOriginalFilename());
            if (StringUtils.isEmpty(adminInstance.getName())) {
                adminInstance.setName(fileName);
            }
            if (StringUtils.isEmpty(adminInstance.getFullUrl())) {
                adminInstance.setFullUrl("/" + fileName);
            }

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
            String extension = upload.getOriginalFilename().substring(upload.getOriginalFilename().lastIndexOf('.') + 1, upload.getOriginalFilename().length()).toLowerCase();
            adminInstance.setFileExtension(extension);
            
            String fullUrl = adminInstance.getFullUrl();
            if (!fullUrl.startsWith("/")) {
                fullUrl = '/' + fullUrl;
            }
            if (fullUrl.lastIndexOf('.') < 0) {
                fullUrl += '.' + extension;
            }
            adminInstance.setFullUrl(fullUrl);

            adminInstance = staticAssetService.addStaticAsset(adminInstance, getSandBox());

			Entity adminEntity = helper.getRecord(entityProperties, adminInstance, null, null);

            StaticAssetStorage storage = staticAssetStorageService.create();
            storage.setStaticAssetId(adminInstance.getId());
            Blob uploadBlob = staticAssetStorageService.createBlob(upload);
            storage.setFileData(uploadBlob);
            staticAssetStorageService.save(storage);

            return addImageRecords(adminEntity);
		} catch (Exception e) {
            LOG.error("Unable to perform add for entity: "+entity.getType()[0], e);
			throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
		}
    }

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            Map<String, FieldMetadata> adminProperties = getMergedProperties();
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            StaticAsset adminInstance = (StaticAsset) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);

            staticAssetService.deleteStaticAsset(adminInstance, getSandBox());
        } catch (Exception e) {
            LOG.error("Unable to perform delete for entity: "+entity.getType()[0], e);
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
            property.setValue(assetServerUrlPrefix + fullUrl.getValue() + "?smallAdminThumbnail");
            entity.addProperty(property);
            Property property2 = new Property();
            property2.setName("pictureLarge");
            property2.setValue(assetServerUrlPrefix + fullUrl.getValue() + "?largeAdminThumbnail");
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
            BaseCtoConverter ctoConverter = helper.getCtoConverter(persistencePerspective, cto, StaticAsset.class.getName(), getMergedProperties());
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, StaticAsset.class.getName());
            PersistentEntityCriteria countCriteria = ctoConverter.convert(new CriteriaTransferObjectCountWrapper(cto).wrap(), StaticAsset.class.getName());
            Criteria criteria = dynamicEntityDao.getCriteria(queryCriteria, StaticAsset.class);
            Criteria count = dynamicEntityDao.getCriteria(countCriteria, StaticAsset.class);

            SandBox sandBox;
            if (persistencePackage.getCustomCriteria().length > 1 && "prodOnly".equals(persistencePackage.getCustomCriteria()[1])) {
                sandBox = null;
            } else {
                sandBox = getSandBox();
            }
            List<StaticAsset> items = staticAssetService.findAssets(sandBox, criteria);
            Long totalRecords = staticAssetService.countAssets(sandBox, count);

            List<Serializable> convertedList = new ArrayList<Serializable>(items.size());
            convertedList.addAll(items);

            Entity[] assetEntities = helper.getRecords(getMergedProperties(), convertedList);

            for (Entity entity : assetEntities) {
                entity = addImageRecords(entity);
                if ("true".equals(entity.findProperty("lockedFlag").getValue())) {
                    Property property = new Property();
                    property.setName("picture");
                    property.setValue("[ISOMORPHIC]/../admin/images/lock_page.png");
                    entity.addProperty(property);
                }
            }

            return new DynamicResultSet(assetEntities, totalRecords.intValue());
        } catch (Exception e) {
            LOG.error("Unable to perform fetch for entity: " + ceilingEntityFullyQualifiedClassname, e);
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    protected synchronized Map<String, FieldMetadata> getMergedProperties() {
        return mergedProperties;
    }

    protected synchronized void createMergedProperties(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, Class<?>[] entityClasses) throws InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
		PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();

        HashMap<String, FieldMetadata> originalProps = (HashMap<String, FieldMetadata>) dynamicEntityDao.getMergedProperties(
            StaticAsset.class.getName(),
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
        BasicFieldMetadata fieldMetadata = new BasicFieldMetadata();
        fieldMetadata.setFieldType(SupportedFieldType.UPLOAD);
        fieldMetadata.setMutable(true);
        fieldMetadata.setInheritedFromType(StaticAssetImpl.class.getName());
        fieldMetadata.setAvailableToTypes(new String[] {StaticAssetImpl.class.getName(), ImageStaticAssetImpl.class.getName()});
        fieldMetadata.setForeignKeyCollection(false);
        fieldMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
        fieldMetadata.setName("file");
        fieldMetadata.setFriendlyName("StaticAssetCustomPersistenceHandler_File");
        fieldMetadata.setGroup("StaticAssetCustomPersistenceHandler_Upload");
        fieldMetadata.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        fieldMetadata.setProminent(false);
        fieldMetadata.setBroadleafEnumeration("");
        fieldMetadata.setReadOnly(false);
        fieldMetadata.setVisibility(VisibilityEnum.HIDDEN_ALL);
        fieldMetadata.setRequiredOverride(true);

        mergedProperties.put("file", fieldMetadata);

        BasicFieldMetadata iconMetadata = new BasicFieldMetadata();
        iconMetadata.setFieldType(SupportedFieldType.ASSET);
        iconMetadata.setMutable(true);
        iconMetadata.setInheritedFromType(StaticAssetImpl.class.getName());
        iconMetadata.setAvailableToTypes(new String[]{StaticAssetImpl.class.getName(), ImageStaticAssetImpl.class.getName()});
        iconMetadata.setForeignKeyCollection(false);
        iconMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
        iconMetadata.setName("picture");
        iconMetadata.setFriendlyName(" ");
        iconMetadata.setGroup("StaticAssetCustomPersistenceHandler_Asset_Details");
        iconMetadata.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        iconMetadata.setProminent(true);
        iconMetadata.setBroadleafEnumeration("");
        iconMetadata.setReadOnly(false);
        iconMetadata.setVisibility(VisibilityEnum.FORM_HIDDEN);
        iconMetadata.setColumnWidth("25");
        iconMetadata.setOrder(0);
        iconMetadata.setRequiredOverride(false);

        mergedProperties.put("picture", iconMetadata);

        BasicFieldMetadata iconLargeMetadata = new BasicFieldMetadata();
        iconLargeMetadata.setFieldType(SupportedFieldType.ASSET);
        iconLargeMetadata.setMutable(true);
        iconLargeMetadata.setInheritedFromType(StaticAssetImpl.class.getName());
        iconLargeMetadata.setAvailableToTypes(new String[]{StaticAssetImpl.class.getName(), ImageStaticAssetImpl.class.getName()});
        iconLargeMetadata.setForeignKeyCollection(false);
        iconLargeMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
        iconLargeMetadata.setName("pictureLarge");
        iconLargeMetadata.setFriendlyName("StaticAssetCustomPersistenceHandler_Preview");
        iconLargeMetadata.setGroup("StaticAssetCustomPersistenceHandler_Preview");
        iconLargeMetadata.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        iconLargeMetadata.setProminent(false);
        iconLargeMetadata.setBroadleafEnumeration("");
        iconLargeMetadata.setReadOnly(true);
        iconLargeMetadata.setVisibility(VisibilityEnum.VISIBLE_ALL);
        iconLargeMetadata.setRequiredOverride(false);
        iconLargeMetadata.setOrder(0);
        iconLargeMetadata.setGroupOrder(0);

        mergedProperties.put("pictureLarge", iconLargeMetadata);

        mergedProperties.put("callbackName", createHiddenField("callbackName"));
        mergedProperties.put("operation", createHiddenField("operation"));
        mergedProperties.put("sandbox", createHiddenField("sandbox"));
        mergedProperties.put("ceilingEntityFullyQualifiedClassname", createHiddenField("ceilingEntityFullyQualifiedClassname"));
        mergedProperties.put("parentFolder", createHiddenField("parentFolder"));
        mergedProperties.put("idHolder", createHiddenField("idHolder"));
        mergedProperties.put("customCriteria", createHiddenField("customCriteria"));
        mergedProperties.put("csrfToken", createHiddenField("csrfToken"));
	}

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
		try {
			Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new EnumMap<MergedPropertyType, Map<String, FieldMetadata>>(MergedPropertyType.class);
            Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StaticAsset.class);
            if (getMergedProperties() == null) {
                createMergedProperties(persistencePackage, dynamicEntityDao, entityClasses);
            }

			allMergedProperties.put(MergedPropertyType.PRIMARY, getMergedProperties());
			ClassMetadata mergedMetadata = helper.getMergedClassMetadata(entityClasses, allMergedProperties);
			return new DynamicResultSet(mergedMetadata);
		} catch (Exception e) {
            ServiceException ex = new ServiceException("Unable to retrieve inspection results for " + persistencePackage.getCeilingEntityFullyQualifiedClassname(), e);
            LOG.error("Unable to retrieve inspection results for " + persistencePackage.getCeilingEntityFullyQualifiedClassname(), ex);
            throw ex;
		}
    }

    protected FieldMetadata createHiddenField(String name) {
        BasicFieldMetadata fieldMetadata = new BasicFieldMetadata();
        fieldMetadata.setFieldType(SupportedFieldType.HIDDEN);
        fieldMetadata.setMutable(true);
        fieldMetadata.setInheritedFromType(StaticAssetImpl.class.getName());
        fieldMetadata.setAvailableToTypes(new String[]{StaticAssetImpl.class.getName()});
        fieldMetadata.setForeignKeyCollection(false);
        fieldMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
        fieldMetadata.setName(name);
        fieldMetadata.setFriendlyName(name);
        fieldMetadata.setGroup("StaticAssetCustomPersistenceHandler_Upload");
        fieldMetadata.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        fieldMetadata.setProminent(false);
        fieldMetadata.setBroadleafEnumeration("");
        fieldMetadata.setReadOnly(false);
        fieldMetadata.setVisibility(VisibilityEnum.HIDDEN_ALL);

        return fieldMetadata;
    }

    public String getAssetServerUrlPrefix() {
        return assetServerUrlPrefix;
    }

    public void setAssetServerUrlPrefix(String assetServerUrlPrefix) {
        this.assetServerUrlPrefix = assetServerUrlPrefix;
    }
}
