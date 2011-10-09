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
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.locale.domain.Locale;
import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.domain.StructuredContentImpl;
import org.broadleafcommerce.cms.structure.service.StructuredContentService;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.presentation.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.service.SandBoxContext;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.SandBoxService;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.hibernate.Criteria;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/23/11
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredContentCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private Log LOG = LogFactory.getLog(StructuredContentCustomPersistenceHandler.class);

    private static Map<String, FieldMetadata> mergedProperties;

    @Resource(name="blStructuredContentService")
	protected StructuredContentService structuredContentService;

    @Resource(name="blSandBoxService")
    protected SandBoxService sandBoxService;

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return StructuredContent.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
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

    protected SandBox getSandBox() {
        return sandBoxService.retrieveSandboxById(SandBoxContext.getSandBoxContext().getSandBoxId());
    }

    protected synchronized Map<String, FieldMetadata> getModifiedProperties() {
        return mergedProperties;
    }

    protected synchronized void createModifiedProperties(DynamicEntityDao dynamicEntityDao, InspectHelper helper, PersistencePerspective persistencePerspective, Class<?>[] entityClasses) throws InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, ServiceException {
        mergedProperties = helper.getSimpleMergedProperties(StructuredContent.class.getName(), persistencePerspective, entityClasses);

        FieldMetadata fieldMetadata = new FieldMetadata();
        fieldMetadata.setFieldType(SupportedFieldType.EXPLICIT_ENUMERATION);
        fieldMetadata.setMutable(true);
        fieldMetadata.setInheritedFromType(StructuredContentImpl.class.getName());
        fieldMetadata.setAvailableToTypes(new String[]{StructuredContentImpl.class.getName()});
        fieldMetadata.setCollection(false);
        fieldMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);

        PersistencePackage fetchPackage = new PersistencePackage();
        fetchPackage.setCeilingEntityFullyQualifiedClassname(Locale.class.getName());
        PersistencePerspective fetchPerspective = new PersistencePerspective();
        fetchPackage.setPersistencePerspective(fetchPerspective);
        fetchPerspective.setAdditionalForeignKeys(new ForeignKey[]{});
        fetchPerspective.setOperationTypes(new OperationTypes(OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY));
        fetchPerspective.setAdditionalNonPersistentProperties(new String[]{});
        DynamicResultSet resultSet = ((PersistenceManager) helper).fetch(fetchPackage, new CriteriaTransferObject());

        String[][] enums = new String[resultSet.getRecords().length][2];
        int j=0;
        for (Entity entity : resultSet.getRecords()) {
            enums[j][0] = entity.findProperty("id").getValue();
            enums[j][1] = entity.findProperty("friendlyName").getValue();
            j++;
        }

        fieldMetadata.setEnumerationValues(enums);
        FieldPresentationAttributes attributes = new FieldPresentationAttributes();
        fieldMetadata.setPresentationAttributes(attributes);
        attributes.setName("locale");
        attributes.setFriendlyName("Locale");
        attributes.setGroup("Description");
        attributes.setOrder(3);
        attributes.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        attributes.setProminent(true);
        attributes.setBroadleafEnumeration("");
        attributes.setReadOnly(false);
        attributes.setHidden(false);
        attributes.setRequiredOverride(true);

        mergedProperties.put("locale", fieldMetadata);

        FieldMetadata iconMetadata = new FieldMetadata();
        iconMetadata.setFieldType(SupportedFieldType.ARTIFACT);
        iconMetadata.setMutable(true);
        iconMetadata.setInheritedFromType(StructuredContentImpl.class.getName());
        iconMetadata.setAvailableToTypes(new String[]{StructuredContentImpl.class.getName()});
        iconMetadata.setCollection(false);
        iconMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
        FieldPresentationAttributes iconAttributes = new FieldPresentationAttributes();
        iconMetadata.setPresentationAttributes(iconAttributes);
        iconAttributes.setName("picture");
        iconAttributes.setFriendlyName(" ");
        iconAttributes.setGroup("Locked Details");
        iconAttributes.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        iconAttributes.setProminent(true);
        iconAttributes.setBroadleafEnumeration("");
        iconAttributes.setReadOnly(false);
        iconAttributes.setHidden(false);
        iconAttributes.setFormHidden(FormHiddenEnum.HIDDEN);
        iconAttributes.setColumnWidth("25");
        iconAttributes.setOrder(0);
        iconAttributes.setRequiredOverride(true);

        mergedProperties.put("locked", iconMetadata);
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
		try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
            Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StructuredContent.class);

            createModifiedProperties(dynamicEntityDao, helper, persistencePerspective, entityClasses);
            Map<String, FieldMetadata> originalProps = getModifiedProperties();

			allMergedProperties.put(MergedPropertyType.PRIMARY, originalProps);
			ClassMetadata mergedMetadata = helper.getMergedClassMetadata(entityClasses, allMergedProperties);
			DynamicResultSet results = new DynamicResultSet(mergedMetadata, null, null);

			return results;
		} catch (Exception e) {
            ServiceException ex = new ServiceException("Unable to retrieve inspection results for " + persistencePackage.getCeilingEntityFullyQualifiedClassname(), e);
            LOG.error("Unable to retrieve inspection results for " + persistencePackage.getCeilingEntityFullyQualifiedClassname(), ex);
            throw ex;
		}
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StructuredContent.class);
            Map<String, FieldMetadata> originalProps = helper.getSimpleMergedProperties(StructuredContent.class.getName(), persistencePerspective, entities);
            BaseCtoConverter ctoConverter = helper.getCtoConverter(persistencePerspective, cto, StructuredContent.class.getName(), originalProps);
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, StructuredContent.class.getName());
            PersistentEntityCriteria countCriteria = ctoConverter.convert(new CriteriaTransferObjectCountWrapper(cto).wrap(), StructuredContent.class.getName());
            Criteria criteria = dynamicEntityDao.getCriteria(queryCriteria, StructuredContent.class);
            Criteria count = dynamicEntityDao.getCriteria(countCriteria, StructuredContent.class);

            List<StructuredContent> contents = structuredContentService.findContentItems(getSandBox(), criteria);
            Long totalRecords = structuredContentService.countContentItems(getSandBox(), count);
            List<Serializable> convertedList = new ArrayList<Serializable>();
            convertedList.addAll(contents);

            Entity[] pageEntities = helper.getRecords(originalProps, convertedList);

            for (Entity entity : pageEntities) {
                if ("true".equals(entity.findProperty("lockedFlag").getValue())) {
                    Property property = new Property();
                    property.setName("locked");
                    property.setValue("[ISOMORPHIC]/../admin/images/lock_page.png");
                    entity.addProperty(property);
                }
            }

            DynamicResultSet response = new DynamicResultSet(pageEntities, totalRecords.intValue());

            return response;
        } catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity  = persistencePackage.getEntity();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			StructuredContent adminInstance = (StructuredContent) Class.forName(entity.getType()[0]).newInstance();
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StructuredContent.class);
			Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(StructuredContent.class.getName(), persistencePerspective, entityClasses);
			adminInstance = (StructuredContent) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            adminInstance = structuredContentService.addStructuredContent(adminInstance, getSandBox());

			Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

			return adminEntity;
		} catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
			throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
		}
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StructuredContent.class);
			Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(StructuredContent.class.getName(), persistencePerspective, entityClasses);
			Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
			StructuredContent adminInstance = (StructuredContent) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            adminInstance.getStructuredContentFields().size();
            //detach page from the session so that our changes are not persisted here (we want to let the service take care of this)
            adminInstance = (StructuredContent) SerializationUtils.clone(adminInstance);
			adminInstance = (StructuredContent) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            adminInstance = structuredContentService.updateStructuredContent(adminInstance, getSandBox());

			Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

			return adminEntity;
		} catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
			throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
		}
    }

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		Entity entity = persistencePackage.getEntity();
        try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StructuredContent.class);
			Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(StructuredContent.class.getName(), persistencePerspective, entityClasses);
			Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
			StructuredContent adminInstance = (StructuredContent) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);

            structuredContentService.deleteStructuredContent(adminInstance, getSandBox());
		} catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
			throw new ServiceException("Unable to remove entity for " + entity.getType()[0], e);
		}
    }
}
