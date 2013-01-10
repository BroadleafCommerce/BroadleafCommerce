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

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria;
import com.anasoft.os.daofusion.cto.server.CriteriaTransferObjectCountWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.file.domain.StaticAssetImpl;
import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.domain.StructuredContentImpl;
import org.broadleafcommerce.cms.structure.domain.StructuredContentRule;
import org.broadleafcommerce.cms.structure.domain.StructuredContentType;
import org.broadleafcommerce.cms.structure.domain.StructuredContentTypeImpl;
import org.broadleafcommerce.cms.structure.service.StructuredContentService;
import org.broadleafcommerce.cms.structure.service.type.StructuredContentRuleType;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.locale.domain.Locale;
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

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class StructuredContentCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private final Log LOG = LogFactory.getLog(StructuredContentCustomPersistenceHandler.class);

    private static Map<String, FieldMetadata> mergedProperties;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

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

    protected synchronized void createModifiedProperties(DynamicEntityDao dynamicEntityDao, InspectHelper helper, PersistencePerspective persistencePerspective) throws InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, ServiceException, NoSuchFieldException {
        mergedProperties = helper.getSimpleMergedProperties(StructuredContent.class.getName(), persistencePerspective);

        BasicFieldMetadata fieldMetadata = new BasicFieldMetadata();
        fieldMetadata.setFieldType(SupportedFieldType.EXPLICIT_ENUMERATION);
        fieldMetadata.setMutable(true);
        fieldMetadata.setInheritedFromType(StructuredContentImpl.class.getName());
        fieldMetadata.setAvailableToTypes(new String[]{StructuredContentImpl.class.getName()});
        fieldMetadata.setForeignKeyCollection(false);
        fieldMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);

        PersistencePackage fetchPackage = new PersistencePackage();
        fetchPackage.setCeilingEntityFullyQualifiedClassname(Locale.class.getName());
        PersistencePerspective fetchPerspective = new PersistencePerspective();
        fetchPackage.setPersistencePerspective(fetchPerspective);
        fetchPerspective.setAdditionalForeignKeys(new ForeignKey[]{});
        fetchPerspective.setOperationTypes(new OperationTypes(OperationType.BASIC, OperationType.BASIC, OperationType.BASIC, OperationType.BASIC, OperationType.BASIC));
        fetchPerspective.setAdditionalNonPersistentProperties(new String[]{});
        DynamicResultSet resultSet = ((PersistenceManager) helper).fetch(fetchPackage, new CriteriaTransferObject());

        String[][] enums = new String[resultSet.getRecords().length][2];
        int j=0;
        for (Entity entity : resultSet.getRecords()) {
            enums[j][0] = entity.findProperty("localeCode").getValue();
            enums[j][1] = entity.findProperty("friendlyName").getValue();
            j++;
        }

        fieldMetadata.setEnumerationValues(enums);
        fieldMetadata.setName("locale");
        fieldMetadata.setFriendlyName("StructuredContentCustomPersistenceHandler_Locale");
        fieldMetadata.setGroup("StructuredContentCustomPersistenceHandler_Description");
        fieldMetadata.setOrder(3);
        fieldMetadata.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        fieldMetadata.setProminent(true);
        fieldMetadata.setBroadleafEnumeration("");
        fieldMetadata.setReadOnly(false);
        fieldMetadata.setVisibility(VisibilityEnum.VISIBLE_ALL);
        fieldMetadata.setRequiredOverride(true);

        mergedProperties.put("locale", fieldMetadata);

        BasicFieldMetadata contentTypeFieldMetadata = new BasicFieldMetadata();
        contentTypeFieldMetadata.setFieldType(SupportedFieldType.EXPLICIT_ENUMERATION);
        contentTypeFieldMetadata.setMutable(true);
        contentTypeFieldMetadata.setInheritedFromType(StructuredContentTypeImpl.class.getName());
        contentTypeFieldMetadata.setAvailableToTypes(new String[]{StructuredContentTypeImpl.class.getName()});
        contentTypeFieldMetadata.setForeignKeyCollection(false);
        contentTypeFieldMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);

        PersistencePackage contentTypeFetchPackage = new PersistencePackage();
        contentTypeFetchPackage.setCeilingEntityFullyQualifiedClassname(StructuredContentType.class.getName());
        PersistencePerspective contentTypeFetchPerspective = new PersistencePerspective();
        contentTypeFetchPackage.setPersistencePerspective(contentTypeFetchPerspective);
        contentTypeFetchPerspective.setAdditionalForeignKeys(new ForeignKey[]{});
        contentTypeFetchPerspective.setOperationTypes(new OperationTypes(OperationType.BASIC, OperationType.BASIC, OperationType.BASIC, OperationType.BASIC, OperationType.BASIC));
        contentTypeFetchPerspective.setAdditionalNonPersistentProperties(new String[]{});
        DynamicResultSet contentTypeResultSet = ((PersistenceManager) helper).fetch(contentTypeFetchPackage, new CriteriaTransferObject());

        String[][] contentTypeEnums = new String[contentTypeResultSet.getRecords().length][2];
        int i=0;
        for (Entity entity : contentTypeResultSet.getRecords()) {
            contentTypeEnums[i][0] = entity.findProperty("id").getValue();
            contentTypeEnums[i][1] = entity.findProperty("name").getValue();
            i++;
        }

        contentTypeFieldMetadata.setEnumerationValues(contentTypeEnums);
        contentTypeFieldMetadata.setName("structuredContentType_Grid");
        contentTypeFieldMetadata.setFriendlyName("StructuredContentCustomPersistenceHandler_Content_Type");
        contentTypeFieldMetadata.setGroup("StructuredContentCustomPersistenceHandler_Description");
        contentTypeFieldMetadata.setOrder(2);
        contentTypeFieldMetadata.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        contentTypeFieldMetadata.setProminent(true);
        contentTypeFieldMetadata.setBroadleafEnumeration("");
        contentTypeFieldMetadata.setReadOnly(false);
        contentTypeFieldMetadata.setVisibility(VisibilityEnum.FORM_HIDDEN);
        contentTypeFieldMetadata.setRequiredOverride(true);

        mergedProperties.put("structuredContentType_Grid", contentTypeFieldMetadata);

        BasicFieldMetadata iconMetadata = new BasicFieldMetadata();
        iconMetadata.setFieldType(SupportedFieldType.ASSET);
        iconMetadata.setMutable(true);
        iconMetadata.setInheritedFromType(StructuredContentImpl.class.getName());
        iconMetadata.setAvailableToTypes(new String[]{StructuredContentImpl.class.getName()});
        iconMetadata.setForeignKeyCollection(false);
        iconMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
        iconMetadata.setName("picture");
        iconMetadata.setFriendlyName("StructuredContentCustomPersistenceHandler_Lock");
        iconMetadata.setGroup("StructuredContentCustomPersistenceHandler_Locked_Details");
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
            Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StructuredContent.class);
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
            if (cto.get("structuredContentType_Grid").getFilterValues().length > 0) {
                CriteriaTransferObject ctoCopy = new CriteriaTransferObject();
                for (String prop : cto.getPropertyIdSet()) {
                    String propertyId;
                    if (prop.equals("structuredContentType_Grid")) {
                        propertyId = "structuredContentType";
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
            Map<String, FieldMetadata> originalProps = helper.getSimpleMergedProperties(StructuredContent.class.getName(), persistencePerspective);
            BaseCtoConverter ctoConverter = helper.getCtoConverter(persistencePerspective, cto, StructuredContent.class.getName(), originalProps);
            PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, StructuredContent.class.getName());
            PersistentEntityCriteria countCriteria = ctoConverter.convert(new CriteriaTransferObjectCountWrapper(cto).wrap(), StructuredContent.class.getName());
            Criteria criteria = dynamicEntityDao.getCriteria(queryCriteria, StructuredContent.class);
            Criteria count = dynamicEntityDao.getCriteria(countCriteria, StructuredContent.class);

            List<StructuredContent> contents = structuredContentService.findContentItems(getSandBox(), criteria);
            Long totalRecords = structuredContentService.countContentItems(getSandBox(), count);
            List<Serializable> convertedList = new ArrayList<Serializable>();
            convertedList.addAll(contents);

            Entity[] structuredContentEntities = helper.getRecords(originalProps, convertedList);

            for (Entity entity : structuredContentEntities) {
                if ("true".equals(entity.findProperty("lockedFlag").getValue())) {
                    Property property = new Property();
                    property.setName("locked");
                    property.setValue("[ISOMORPHIC]/../admin/images/lock_page.png");
                    entity.addProperty(property);
                }
                if (entity.findProperty("structuredContentType") != null) {
                    Property property = new Property();
                    property.setName("structuredContentType_Grid");
                    property.setValue(entity.findProperty("structuredContentType").getValue());
                    entity.addProperty(property);
                }
            }

            for (int j=0;j<structuredContentEntities.length;j++) {
                addRulesToEntity(contents.get(j), structuredContentEntities[j]);
            }

            DynamicResultSet response = new DynamicResultSet(structuredContentEntities, totalRecords.intValue());

            return response;
        } catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    protected void addRulesToEntity(StructuredContent structuredContent, Entity structuredContentEntity) {
        Entity entity = structuredContentEntity;
        StructuredContent content = structuredContent;
        if (content.getStructuredContentMatchRules() != null) {
            for (String key : content.getStructuredContentMatchRules().keySet()) {
                StructuredContentRuleType type = StructuredContentRuleType.getInstance(key);
                Property prop = null;
                if (StructuredContentRuleType.CUSTOMER.equals(type)) {
                    prop = new Property();
                    prop.setName("customerRule");
                } else if (StructuredContentRuleType.PRODUCT.equals(type)) {
                    prop = new Property();
                    prop.setName("productRule");
                } else if (StructuredContentRuleType.REQUEST.equals(type)) {
                    prop = new Property();
                    prop.setName("requestRule");
                } else if (StructuredContentRuleType.TIME.equals(type)) {
                    prop = new Property();
                    prop.setName("timeRule");
                }
                if (prop != null) {
                    prop.setValue(content.getStructuredContentMatchRules().get(key).getMatchRule());
                    entity.addProperty(prop);
                }
            }
        }
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity  = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            StructuredContent adminInstance = (StructuredContent) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(StructuredContent.class.getName(), persistencePerspective);
            adminInstance = (StructuredContent) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            addRule(entity, adminInstance, "customerRule", StructuredContentRuleType.CUSTOMER);
            addRule(entity, adminInstance, "productRule", StructuredContentRuleType.PRODUCT);
            addRule(entity, adminInstance, "requestRule", StructuredContentRuleType.REQUEST);
            addRule(entity, adminInstance, "timeRule", StructuredContentRuleType.TIME);

            adminInstance = structuredContentService.addStructuredContent(adminInstance, getSandBox());

            Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

            if (adminEntity.findProperty("structuredContentType") != null) {
                Property property = new Property();
                property.setName("structuredContentType_Grid");
                property.setValue(adminEntity.findProperty("structuredContentType").getValue());
                adminEntity.addProperty(property);
            }

            addRulesToEntity(adminInstance, adminEntity);

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
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(StructuredContent.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            StructuredContent adminInstance = (StructuredContent) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            adminInstance = (StructuredContent) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            updateRule(entity, adminInstance, "customerRule", StructuredContentRuleType.CUSTOMER);
            updateRule(entity, adminInstance, "productRule", StructuredContentRuleType.PRODUCT);
            updateRule(entity, adminInstance, "requestRule", StructuredContentRuleType.REQUEST);
            updateRule(entity, adminInstance, "timeRule", StructuredContentRuleType.TIME);

            adminInstance = structuredContentService.updateStructuredContent(adminInstance, getSandBox());

            Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

            if (adminEntity.findProperty("structuredContentType") != null) {
                Property property = new Property();
                property.setName("structuredContentType_Grid");
                property.setValue(adminEntity.findProperty("structuredContentType").getValue());
                adminEntity.addProperty(property);
            }

            addRulesToEntity(adminInstance, adminEntity);

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
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(StructuredContent.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            StructuredContent adminInstance = (StructuredContent) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);

            structuredContentService.deleteStructuredContent(adminInstance, getSandBox());
        } catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
            throw new ServiceException("Unable to remove entity for " + entity.getType()[0], e);
        }
    }

    protected void addRule(Entity entity, StructuredContent structuredContentInstance, String propertyName, StructuredContentRuleType type) {
        Property ruleProperty = entity.findProperty(propertyName);
        if (ruleProperty != null && !StringUtils.isEmpty(ruleProperty.getValue())) {
            //antisamy XSS protection encodes the values in the MVEL
            //reverse this behavior
            ruleProperty.setValue(ruleProperty.getRawValue());
            StructuredContentRule rule = (StructuredContentRule) entityConfiguration.createEntityInstance(StructuredContentRule.class.getName());
            rule.setMatchRule(ruleProperty.getValue());
            structuredContentInstance.getStructuredContentMatchRules().put(type.getType(), rule);
        }
    }

    protected void updateRule(Entity entity, StructuredContent structuredContentInstance, String propertyName, StructuredContentRuleType type) {
        Property ruleProperty = entity.findProperty(propertyName);
        if (ruleProperty != null && !StringUtils.isEmpty(ruleProperty.getValue())) {
            //antisamy XSS protection encodes the values in the MVEL
            //reverse this behavior
            ruleProperty.setValue(ruleProperty.getRawValue());
            StructuredContentRule rule = structuredContentInstance.getStructuredContentMatchRules().get(type.getType());
            if (rule == null) {
                rule = (StructuredContentRule) entityConfiguration.createEntityInstance(StructuredContentRule.class.getName());
            }
            rule.setMatchRule(ruleProperty.getValue());
            structuredContentInstance.getStructuredContentMatchRules().put(type.getType(), rule);
        } else {
            structuredContentInstance.getStructuredContentMatchRules().remove(type.getType());
        }
    }
}
