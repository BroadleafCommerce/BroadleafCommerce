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

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria;
import com.anasoft.os.daofusion.cto.server.CriteriaTransferObjectCountWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageImpl;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.cms.page.domain.PageTemplateImpl;
import org.broadleafcommerce.cms.page.service.PageService;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.FieldPresentationAttributes;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.OperationType;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.SandBoxContext;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.SandBoxService;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.hibernate.Criteria;

/**
 * @author Jeff Fischer
 */
public class PagesCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(PagesCustomPersistenceHandler.class);

    @Resource(name="blPageService")
	protected PageService pageService;

    @Resource(name="blSandBoxService")
    protected SandBoxService sandBoxService;

    private static Map<String, FieldMetadata> mergedProperties;

    protected synchronized Map<String, FieldMetadata> getModifiedProperties() {
        return mergedProperties;
    }

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return Page.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    protected SandBox getSandBox() {
        return sandBoxService.retrieveSandboxById(SandBoxContext.getSandBoxContext().getSandBoxId());
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity  = persistencePackage.getEntity();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Page adminInstance = (Page) Class.forName(entity.getType()[0]).newInstance();
			Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Page.class.getName(), persistencePerspective);
			adminInstance = (Page) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            adminInstance = pageService.addPage(adminInstance, getSandBox());

			Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

            if (adminEntity.findProperty("pageTemplate") != null) {
                Property property = new Property();
                property.setName("pageTemplate_Grid");
                property.setValue(adminEntity.findProperty("pageTemplate").getValue());
                adminEntity.addProperty(property);
            }

			return adminEntity;
		} catch (Exception e) {
            LOG.error("Unable to add entity for " + entity.getType()[0], e);
			throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
		}
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            if (cto.get("pageTemplate_Grid").getFilterValues().length > 0) {
                CriteriaTransferObject ctoCopy = new CriteriaTransferObject();
                for (String prop : cto.getPropertyIdSet()) {
                    String propertyId;
                    if (prop.equals("pageTemplate_Grid")) {
                        propertyId = "pageTemplate";
                    } else {
                        propertyId = prop;
                    }
                    FilterAndSortCriteria criteria = ctoCopy.get(propertyId);
                    FilterAndSortCriteria oldCriteria = cto.get(prop);
                    criteria.setFilterValue(oldCriteria.getFilterValues()[0]);
                    criteria.setIgnoreCase(oldCriteria.getIgnoreCase());
                    criteria.setSortAscending(oldCriteria.getIgnoreCase());
                }
                cto = ctoCopy;
            }

            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> originalProps = helper.getSimpleMergedProperties(Page.class.getName(), persistencePerspective);
            BaseCtoConverter ctoConverter = helper.getCtoConverter(persistencePerspective, cto, Page.class.getName(), originalProps);
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, Page.class.getName());
            PersistentEntityCriteria countCriteria = ctoConverter.convert(new CriteriaTransferObjectCountWrapper(cto).wrap(), Page.class.getName());
            Criteria criteria = dynamicEntityDao.getCriteria(queryCriteria, Page.class);
            Criteria count = dynamicEntityDao.getCriteria(countCriteria, Page.class);

            // TODO: Add Locale to Criteria

            List<Page> pages = pageService.findPages(getSandBox(), criteria);
            Long totalRecords = pageService.countPages(getSandBox(), count);
            List<Serializable> convertedList = new ArrayList<Serializable>();
            convertedList.addAll(pages);

            Entity[] pageEntities = helper.getRecords(originalProps, convertedList);

            for (Entity entity : pageEntities) {
                if (entity.findProperty("pageTemplate") != null) {
                    Property property = new Property();
                    property.setName("pageTemplate_Grid");
                    property.setValue(entity.findProperty("pageTemplate").getValue());
                    entity.addProperty(property);
                }
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
            LOG.error("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    protected Map<String, FieldMetadata> getMergedProperties(Class<?> ceilingEntityFullyQualifiedClass, DynamicEntityDao dynamicEntityDao, Boolean populateManyToOneFields, String[] includeManyToOneFields, String[] excludeManyToOneFields, String configurationKey, ForeignKey[] additionalForeignKeys) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(ceilingEntityFullyQualifiedClass);
		Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
			ceilingEntityFullyQualifiedClass.getName(),
			entities,
			null,
			new String[]{},
			additionalForeignKeys,
			MergedPropertyType.PRIMARY,
			populateManyToOneFields,
			includeManyToOneFields,
			excludeManyToOneFields,
            configurationKey,
			""
		);

		return mergedProperties;
	}

       protected synchronized void createModifiedProperties(DynamicEntityDao dynamicEntityDao, InspectHelper helper, PersistencePerspective persistencePerspective) throws InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, ServiceException, NoSuchFieldException {
        mergedProperties = helper.getSimpleMergedProperties(Page.class.getName(), persistencePerspective);

        FieldMetadata contentTypeFieldMetadata = new FieldMetadata();
        contentTypeFieldMetadata.setFieldType(SupportedFieldType.EXPLICIT_ENUMERATION);
        contentTypeFieldMetadata.setMutable(true);
        contentTypeFieldMetadata.setInheritedFromType(PageTemplateImpl.class.getName());
        contentTypeFieldMetadata.setAvailableToTypes(new String[]{PageTemplateImpl.class.getName()});
        contentTypeFieldMetadata.setCollection(false);
        contentTypeFieldMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);

        PersistencePackage pageTemplatePersistencePackage = new PersistencePackage();
        pageTemplatePersistencePackage.setCeilingEntityFullyQualifiedClassname(PageTemplate.class.getName());
        PersistencePerspective pageTemplateFetchPerspective = new PersistencePerspective();
        pageTemplatePersistencePackage.setPersistencePerspective(pageTemplateFetchPerspective);
        pageTemplateFetchPerspective.setAdditionalForeignKeys(new ForeignKey[]{});
        pageTemplateFetchPerspective.setOperationTypes(new OperationTypes(OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY));
        pageTemplateFetchPerspective.setAdditionalNonPersistentProperties(new String[]{});
        DynamicResultSet pageTemplateResultSet = ((PersistenceManager) helper).fetch(pageTemplatePersistencePackage, new CriteriaTransferObject());

        String[][] pageTemplateEnums = new String[pageTemplateResultSet.getRecords().length][2];
        int i=0;
        for (Entity entity : pageTemplateResultSet.getRecords()) {
            pageTemplateEnums[i][0] = entity.findProperty("id").getValue();
            pageTemplateEnums[i][1] = entity.findProperty("templateName").getValue();
            i++;
        }

        contentTypeFieldMetadata.setEnumerationValues(pageTemplateEnums);
        FieldPresentationAttributes contentTypeAttributes = new FieldPresentationAttributes();
        contentTypeFieldMetadata.setPresentationAttributes(contentTypeAttributes);
        contentTypeAttributes.setName("pageTemplate_Grid");
        contentTypeAttributes.setFriendlyName("PagesCustomPersistenceHandler_Page_Template");
        contentTypeAttributes.setGroup("Description");
        contentTypeAttributes.setOrder(2);
        contentTypeAttributes.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        contentTypeAttributes.setProminent(false);
        contentTypeAttributes.setBroadleafEnumeration("");
        contentTypeAttributes.setReadOnly(false);
        contentTypeAttributes.setVisibility(VisibilityEnum.FORM_HIDDEN);
        contentTypeAttributes.setRequiredOverride(true);

        mergedProperties.put("pageTemplate_Grid", contentTypeFieldMetadata);

        FieldMetadata iconMetadata = new FieldMetadata();
        iconMetadata.setFieldType(SupportedFieldType.ASSET);
        iconMetadata.setMutable(true);
        iconMetadata.setInheritedFromType(PageImpl.class.getName());
        iconMetadata.setAvailableToTypes(new String[]{PageImpl.class.getName()});
        iconMetadata.setCollection(false);
        iconMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
        FieldPresentationAttributes iconAttributes = new FieldPresentationAttributes();
        iconMetadata.setPresentationAttributes(iconAttributes);
        iconAttributes.setName("picture");
        iconAttributes.setFriendlyName("PagesCustomPersistenceHandler_Lock");
        iconAttributes.setGroup("Locked Details");
        iconAttributes.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        iconAttributes.setProminent(true);
        iconAttributes.setBroadleafEnumeration("");
        iconAttributes.setReadOnly(false);
        iconAttributes.setVisibility(VisibilityEnum.FORM_HIDDEN);
        iconAttributes.setColumnWidth("30");
        iconAttributes.setOrder(0);
        iconAttributes.setRequiredOverride(true);

        mergedProperties.put("locked", iconMetadata);
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
		try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();

            if (getModifiedProperties() == null) {
                createModifiedProperties(dynamicEntityDao, helper, persistencePerspective);
            }
            Map<String, FieldMetadata> originalProps = getModifiedProperties();

			allMergedProperties.put(MergedPropertyType.PRIMARY, originalProps);
            Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Page.class);
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
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Page.class.getName(), persistencePerspective);
			Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            Page adminInstance = (Page) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
			adminInstance = (Page) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            adminInstance = pageService.updatePage(adminInstance, getSandBox());

			Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

            if (adminEntity.findProperty("pageTemplate") != null) {
                Property property = new Property();
                property.setName("pageTemplate_Grid");
                property.setValue(adminEntity.findProperty("pageTemplate").getValue());
                adminEntity.addProperty(property);
            }

			return adminEntity;
		} catch (Exception e) {
            LOG.error("Unable to update entity for " + entity.getType()[0], e);
			throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
		}
    }

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		Entity entity = persistencePackage.getEntity();
        try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Page.class.getName(), persistencePerspective);
			Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            Serializable persistenceObject = dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
			Page adminInstance = (Page) persistenceObject;
            pageService.deletePage(adminInstance, getSandBox());
		} catch (Exception e) {
            LOG.error("Unable to remove entity for " + entity.getType()[0], e);
			throw new ServiceException("Unable to remove entity for " + entity.getType()[0], e);
		}
    }
}
