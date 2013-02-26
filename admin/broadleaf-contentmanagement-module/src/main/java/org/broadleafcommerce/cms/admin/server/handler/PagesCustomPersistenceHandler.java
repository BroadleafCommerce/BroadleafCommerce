/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.admin.server.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.file.domain.StaticAssetImpl;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageImpl;
import org.broadleafcommerce.cms.page.domain.PageRule;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.cms.page.domain.PageTemplateImpl;
import org.broadleafcommerce.cms.page.service.PageService;
import org.broadleafcommerce.cms.page.service.type.PageRuleType;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.presentation.client.OperationType;
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
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.SandBoxService;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.hibernate.Criteria;
import org.hibernate.tool.hbm2x.StringUtils;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria;
import com.anasoft.os.daofusion.cto.server.CriteriaTransferObjectCountWrapper;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Jeff Fischer
 */
public class PagesCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(PagesCustomPersistenceHandler.class);

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name = "blPageService")
    protected PageService pageService;

    @Resource(name = "blSandBoxService")
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
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Page adminInstance = (Page) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Page.class.getName(), persistencePerspective);
            adminInstance = (Page) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            addRule(entity, adminInstance, "customerRule", PageRuleType.CUSTOMER);
            addRule(entity, adminInstance, "productRule", PageRuleType.PRODUCT);
            addRule(entity, adminInstance, "requestRule", PageRuleType.REQUEST);
            addRule(entity, adminInstance, "timeRule", PageRuleType.TIME);


            adminInstance = pageService.addPage(adminInstance, getSandBox());

            Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

            if (adminEntity.findProperty("pageTemplate") != null) {
                Property property = new Property();
                property.setName("pageTemplate_Grid");
                property.setValue(adminEntity.findProperty("pageTemplate").getValue());
                adminEntity.addProperty(property);
            }

            addRulesToEntity(adminInstance, adminEntity);

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
                Property lockedProperty = new Property();
                lockedProperty.setName("locked");
                if ("true".equals(entity.findProperty("lockedFlag").getValue())) {
                    lockedProperty.setValue("[ISOMORPHIC]/../admin/images/lock_page.png");
                }
                entity.addProperty(lockedProperty);
            }

            for (int j = 0; j < pageEntities.length; j++) {
                addRulesToEntity(pages.get(j), pageEntities[j]);
            }

            DynamicResultSet response = new DynamicResultSet(pageEntities, totalRecords.intValue());

            return response;
        } catch (Exception e) {
            LOG.error("Unable to perform fetch for entity: " + ceilingEntityFullyQualifiedClassname, e);
            throw new ServiceException("Unable to perform fetch for entity: " + ceilingEntityFullyQualifiedClassname, e);
        }
    }

    protected void addRulesToEntity(Page structuredContent, Entity structuredContentEntity) {
        Entity entity = structuredContentEntity;
        Page content = structuredContent;
        if (content.getPageMatchRules() != null) {
            for (String key : content.getPageMatchRules().keySet()) {
                PageRuleType type = PageRuleType.getInstance(key);
                Property prop = null;
                if (PageRuleType.CUSTOMER.equals(type)) {
                    prop = new Property();
                    prop.setName("customerRule");
                } else if (PageRuleType.PRODUCT.equals(type)) {
                    prop = new Property();
                    prop.setName("productRule");
                } else if (PageRuleType.REQUEST.equals(type)) {
                    prop = new Property();
                    prop.setName("requestRule");
                } else if (PageRuleType.TIME.equals(type)) {
                    prop = new Property();
                    prop.setName("timeRule");
                }
                if (prop != null) {
                    prop.setValue(content.getPageMatchRules().get(key).getMatchRule());
                    entity.addProperty(prop);
                }
            }
        }
    }

    protected Map<String, FieldMetadata> getMergedProperties(Class<?> ceilingEntityFullyQualifiedClass, DynamicEntityDao dynamicEntityDao, Boolean populateManyToOneFields, String[] includeManyToOneFields, String[] excludeManyToOneFields, String configurationKey, ForeignKey[] additionalForeignKeys) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
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

        BasicFieldMetadata contentTypeFieldMetadata = new BasicFieldMetadata();
        contentTypeFieldMetadata.setFieldType(SupportedFieldType.EXPLICIT_ENUMERATION);
        contentTypeFieldMetadata.setMutable(true);
        contentTypeFieldMetadata.setInheritedFromType(PageTemplateImpl.class.getName());
        contentTypeFieldMetadata.setAvailableToTypes(new String[]{PageTemplateImpl.class.getName()});
        contentTypeFieldMetadata.setForeignKeyCollection(false);
        contentTypeFieldMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);

        PersistencePackage pageTemplatePersistencePackage = new PersistencePackage();
        pageTemplatePersistencePackage.setCeilingEntityFullyQualifiedClassname(PageTemplate.class.getName());
        PersistencePerspective pageTemplateFetchPerspective = new PersistencePerspective();
        pageTemplatePersistencePackage.setPersistencePerspective(pageTemplateFetchPerspective);
        pageTemplateFetchPerspective.setAdditionalForeignKeys(new ForeignKey[]{});
        pageTemplateFetchPerspective.setOperationTypes(new OperationTypes(OperationType.BASIC, OperationType.BASIC, OperationType.BASIC, OperationType.BASIC, OperationType.BASIC));
        pageTemplateFetchPerspective.setAdditionalNonPersistentProperties(new String[]{});
        DynamicResultSet pageTemplateResultSet = ((PersistenceManager) helper).fetch(pageTemplatePersistencePackage, new CriteriaTransferObject());

        String[][] pageTemplateEnums = new String[pageTemplateResultSet.getRecords().length][2];
        int i = 0;
        for (Entity entity : pageTemplateResultSet.getRecords()) {
            pageTemplateEnums[i][0] = entity.findProperty("id").getValue();
            pageTemplateEnums[i][1] = entity.findProperty("templateName").getValue();
            i++;
        }

        contentTypeFieldMetadata.setEnumerationValues(pageTemplateEnums);
        contentTypeFieldMetadata.setName("pageTemplate_Grid");
        contentTypeFieldMetadata.setFriendlyName("PagesCustomPersistenceHandler_Page_Template");
        contentTypeFieldMetadata.setGroup("PagesCustomPersistenceHandler_Description");
        contentTypeFieldMetadata.setOrder(2);
        contentTypeFieldMetadata.setTab("General");
        contentTypeFieldMetadata.setTabOrder(100);
        contentTypeFieldMetadata.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        contentTypeFieldMetadata.setProminent(false);
        contentTypeFieldMetadata.setBroadleafEnumeration("");
        contentTypeFieldMetadata.setReadOnly(false);
        contentTypeFieldMetadata.setVisibility(VisibilityEnum.FORM_HIDDEN);
        contentTypeFieldMetadata.setRequiredOverride(true);

        mergedProperties.put("pageTemplate_Grid", contentTypeFieldMetadata);

        BasicFieldMetadata iconMetadata = new BasicFieldMetadata();
        iconMetadata.setFieldType(SupportedFieldType.ASSET);
        iconMetadata.setMutable(true);
        iconMetadata.setInheritedFromType(PageImpl.class.getName());
        iconMetadata.setAvailableToTypes(new String[]{PageImpl.class.getName()});
        iconMetadata.setForeignKeyCollection(false);
        iconMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
        iconMetadata.setName("picture");
        iconMetadata.setFriendlyName("PagesCustomPersistenceHandler_Lock");
        iconMetadata.setGroup("PagesCustomPersistenceHandler_Description");
        iconMetadata.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        iconMetadata.setProminent(true);
        iconMetadata.setBroadleafEnumeration("");
        iconMetadata.setReadOnly(false);
        iconMetadata.setVisibility(VisibilityEnum.FORM_HIDDEN);
        iconMetadata.setColumnWidth("30");
        iconMetadata.setOrder(0);
        iconMetadata.setRequiredOverride(true);

        mergedProperties.put("locked", iconMetadata);


        mergedProperties.put("timeRule", createHiddenField("timeRule"));
        mergedProperties.put("requestRule", createHiddenField("requestRule"));
        mergedProperties.put("customerRule", createHiddenField("customerRule"));
        mergedProperties.put("productRule", createHiddenField("productRule"));
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
        fieldMetadata.setGroup("StructuredContentCustomPersistenceHandler_Rules");
        fieldMetadata.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        fieldMetadata.setProminent(false);
        fieldMetadata.setBroadleafEnumeration("");
        fieldMetadata.setReadOnly(false);
        fieldMetadata.setVisibility(VisibilityEnum.HIDDEN_ALL);

        return fieldMetadata;
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

            updateRule(entity, adminInstance, "customerRule", PageRuleType.CUSTOMER);
            updateRule(entity, adminInstance, "productRule", PageRuleType.PRODUCT);
            updateRule(entity, adminInstance, "requestRule", PageRuleType.REQUEST);
            updateRule(entity, adminInstance, "timeRule", PageRuleType.TIME);

            adminInstance = pageService.updatePage(adminInstance, getSandBox());

            Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

            if (adminEntity.findProperty("pageTemplate") != null) {
                Property property = new Property();
                property.setName("pageTemplate_Grid");
                property.setValue(adminEntity.findProperty("pageTemplate").getValue());
                adminEntity.addProperty(property);
            }

            addRulesToEntity(adminInstance, adminEntity);

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

    protected void addRule(Entity entity, Page structuredContentInstance, String propertyName, PageRuleType type) {
        Property ruleProperty = entity.findProperty(propertyName);
        if (ruleProperty != null && !StringUtils.isEmpty(ruleProperty.getValue())) {
            //antisamy XSS protection encodes the values in the MVEL
            //reverse this behavior
            ruleProperty.setValue(ruleProperty.getRawValue());
            PageRule rule = (PageRule) entityConfiguration.createEntityInstance(PageRule.class.getName());
            rule.setMatchRule(ruleProperty.getValue());
            structuredContentInstance.getPageMatchRules().put(type.getType(), rule);
        }
    }

    protected void updateRule(Entity entity, Page structuredContentInstance, String propertyName, PageRuleType type) {
        Property ruleProperty = entity.findProperty(propertyName);
        if (ruleProperty != null && !StringUtils.isEmpty(ruleProperty.getValue())) {
            //antisamy XSS protection encodes the values in the MVEL
            //reverse this behavior
            ruleProperty.setValue(ruleProperty.getRawValue());
            PageRule rule = structuredContentInstance.getPageMatchRules().get(type.getType());
            if (rule == null) {
                rule = (PageRule) entityConfiguration.createEntityInstance(PageRule.class.getName());
            }
            rule.setMatchRule(ruleProperty.getValue());
            structuredContentInstance.getPageMatchRules().put(type.getType(), rule);
        } else {
            structuredContentInstance.getPageMatchRules().remove(type.getType());
        }
    }
}
