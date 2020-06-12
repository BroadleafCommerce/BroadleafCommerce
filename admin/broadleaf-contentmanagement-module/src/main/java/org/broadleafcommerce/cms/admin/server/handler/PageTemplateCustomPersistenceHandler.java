/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.cms.admin.server.handler;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.field.domain.FieldDefinition;
import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageFieldImpl;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.cms.page.domain.PageTemplateFieldGroupXref;
import org.broadleafcommerce.cms.page.domain.PageTemplateImpl;
import org.broadleafcommerce.cms.page.service.PageService;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.service.SandBoxService;
import org.broadleafcommerce.common.web.SandBoxContext;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.ClassTree;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.ValidationException;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.handler.DynamicEntityRetriever;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by jfischer
 */
@Component("blPageTemplateCustomPersistenceHandler")
public class PageTemplateCustomPersistenceHandler extends CustomPersistenceHandlerAdapter implements DynamicEntityRetriever {

    private final Log LOG = LogFactory.getLog(PageTemplateCustomPersistenceHandler.class);

    @Resource(name="blPageService")
    protected PageService pageService;

    @Resource(name="blSandBoxService")
    protected SandBoxService sandBoxService;
    
    @Resource(name = "blDynamicFieldPersistenceHandlerHelper")
    protected DynamicFieldPersistenceHandlerHelper dynamicFieldUtil;

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return
            PageTemplate.class.getName().equals(ceilingEntityFullyQualifiedClassname) &&
            persistencePackage.getCustomCriteria() != null &&
            persistencePackage.getCustomCriteria().length > 0 &&
            persistencePackage.getCustomCriteria()[0].contains("constructForm");
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        return false;
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    protected SandBox getSandBox() {
        return sandBoxService.retrieveSandBoxById(SandBoxContext.getSandBoxContext().getSandBoxId());
    }

    protected List<FieldGroup> getFieldGroups(Page page, PageTemplate template) {
        List<PageTemplateFieldGroupXref> fieldGroupXrefs = null;

        List<FieldGroup> fieldGroups = new ArrayList<FieldGroup>();
        if (template != null) {
            fieldGroupXrefs = template.getFieldGroupXrefs();
        }

        if (page.getPageTemplate() != null) {
            fieldGroupXrefs = page.getPageTemplate().getFieldGroupXrefs();
        }

        if (fieldGroupXrefs != null) {
            for (PageTemplateFieldGroupXref xref : fieldGroupXrefs) {
                fieldGroups.add(xref.getFieldGroup());
            }
        }

        return fieldGroups;
    }

    protected List<FieldGroup> getFieldGroups(PersistencePackage pp, DynamicEntityDao dynamicEntityDao) {
        String pageId = pp.getCustomCriteria()[1];
        String pageTemplateId = pp.getCustomCriteria().length > 3 ? pp.getCustomCriteria()[3] : null;

        if (pageId == null) {
            return new ArrayList<FieldGroup>(0);
        }

        Page page = pageService.findPageById(Long.valueOf(pageId));
        PageTemplate template = null;
        if (pageTemplateId != null) {
            template = pageService.findPageTemplateById(Long.valueOf(pageTemplateId));
        }
        
        return getFieldGroups(page, template);
    }
    
    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            List<FieldGroup> fieldGroups = getFieldGroups(persistencePackage, dynamicEntityDao);

            ClassMetadata metadata = new ClassMetadata();
            metadata.setCeilingType(PageTemplate.class.getName());
            ClassTree entities = new ClassTree(PageTemplateImpl.class.getName());
            metadata.setPolymorphicEntities(entities);
            Property[] properties = dynamicFieldUtil.buildDynamicPropertyList(fieldGroups, PageTemplate.class);
            metadata.setProperties(properties);
            DynamicResultSet results = new DynamicResultSet(metadata);

            return results;
        } catch (Exception e) {
            throw new ServiceException("Unable to perform inspect for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            String pageId = persistencePackage.getCustomCriteria()[1];
            Entity entity = fetchEntityBasedOnId(pageId, null);
            DynamicResultSet results = new DynamicResultSet(new Entity[]{entity}, 1);
            populateFKLookupValues(dynamicEntityDao, entity);

            return results;
        } catch (Exception e) {
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    /**
     * Some of the values in this entity might be foreign key lookups. In this case, we need to set the display
     * value appropriately
     *
     * @param dynamicEntityDao
     * @param entity
     * @throws ClassNotFoundException
     */
    protected void populateFKLookupValues(DynamicEntityDao dynamicEntityDao, Entity entity) throws ClassNotFoundException {
        for (Property prop : entity.getProperties()) {
            if (StringUtils.isNotBlank(prop.getValue()) && StringUtils.isNotBlank(prop.getMetadata().getOwningClass())) {
                Class<?> clazz = Class.forName(prop.getMetadata().getOwningClass());
                Class<?>[] lookupClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(clazz);

                int i = 0;
                Object foreignEntity = null;
                while (foreignEntity == null && i < lookupClasses.length) {
                    foreignEntity = dynamicEntityDao.find(lookupClasses[i++], Long.parseLong(prop.getValue()));
                }

                if (foreignEntity instanceof AdminMainEntity) {
                    prop.setDisplayValue(((AdminMainEntity) foreignEntity).getMainEntityName());
                }
                prop.getMetadata().setOwningClass(null);
            }
        }
    }

    @Override
    public String getFieldContainerClassName() {
        return Page.class.getName();
    }

    @Override
    public Entity fetchEntityBasedOnId(String pageId, List<String> dirtyFields) throws Exception {
        Page page = pageService.findPageById(Long.valueOf(pageId));
        //Make sure the fieldmap is refreshed from the database based on any changes introduced in addOrUpdate()
        em.refresh(page);
        return fetchDynamicEntity(page, dirtyFields, true);
    }

    @Override
    public Entity fetchDynamicEntity(Serializable root, List<String> dirtyFields, boolean includeId) throws Exception {
        Page page = (Page) root;
        Map<String, PageField> pageFieldMap = page.getPageFields();
        Entity entity = new Entity();
        entity.setType(new String[]{PageTemplateImpl.class.getName()});
        List<Property> propertiesList = new ArrayList<Property>();
        List<FieldGroup> fieldGroups = getFieldGroups(page, null);
        processFieldGroups(dirtyFields, pageFieldMap, propertiesList, fieldGroups);
        processIncludeId(includeId, page, propertiesList);

        entity.setProperties(propertiesList.toArray(new Property[]{}));

        return entity;
    }

    protected void processFieldGroups(List<String> dirtyFields, Map<String, PageField> pageFieldMap, List<Property> propertiesList, List<FieldGroup> fieldGroups) {
        for (FieldGroup fieldGroup : fieldGroups) {
            for (FieldDefinition def : fieldGroup.getFieldDefinitions()) {
                Property property = new Property();
                propertiesList.add(property);
                property.setName(def.getName());
                String value = null;
                if (!MapUtils.isEmpty(pageFieldMap)) {
                    PageField pageField = pageFieldMap.get(def.getName());
                    if (pageField == null) {
                        value = "";
                    } else {
                        value = pageField.getValue();
                    }
                }
                property.setValue(value);
                if (!CollectionUtils.isEmpty(dirtyFields) && dirtyFields.contains(property.getName())) {
                    property.setIsDirty(true);
                }
                if (StringUtils.isNotBlank(def.getAdditionalForeignKeyClass())) {
                    property.getMetadata().setOwningClass(def.getAdditionalForeignKeyClass());
                }
            }
        }
    }

    protected void processIncludeId(boolean includeId, Page page, List<Property> propertiesList) {
        if (includeId) {
            Property property = new Property();
            propertiesList.add(property);
            property.setName("id");
            property.setValue(String.valueOf(page.getId()));
        }
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        return addOrUpdate(persistencePackage, dynamicEntityDao, helper);
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        return addOrUpdate(persistencePackage, dynamicEntityDao, helper);
    }

    protected Entity addOrUpdate(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            String pageId = persistencePackage.getCustomCriteria()[1];
            
            if (StringUtils.isBlank(pageId)) {
                return persistencePackage.getEntity();
            }
            
            Page page = pageService.findPageById(Long.valueOf(pageId));

            Property[] properties = dynamicFieldUtil.buildDynamicPropertyList(getFieldGroups(page, null), PageTemplate.class);
            Map<String, FieldMetadata> md = new HashMap<String, FieldMetadata>();
            for (Property property : properties) {
                md.put(property.getName(), property.getMetadata());
            }
            
            boolean validated = helper.validate(persistencePackage.getEntity(), null, md);
            if (!validated) {
                throw new ValidationException(persistencePackage.getEntity(), "Page dynamic fields failed validation");
            }

            List<String> templateFieldNames = new ArrayList<String>(20);
            for (FieldGroup group : getFieldGroups(page, null)) {
                for (FieldDefinition def : group.getFieldDefinitions()) {
                    templateFieldNames.add(def.getName());
                }
            }
            Map<String, String> dirtyFieldsOrigVals = new HashMap<String, String>();
            List<String> dirtyFields = new ArrayList<String>();
            Map<String, PageField> pageFieldMap = page.getPageFields();
            for (Property property : persistencePackage.getEntity().getProperties()) {
                if (property.getEnabled() && templateFieldNames.contains(property.getName())) {
                    PageField pageField = pageFieldMap.get(property.getName());
                    if (pageField != null) {
                        boolean isDirty = (pageField.getValue() == null && property.getValue() != null) ||
                                (pageField.getValue() != null && property.getValue() == null);
                        if (isDirty || (pageField.getValue() != null && property.getValue() != null &&
                                !pageField.getValue().trim().equals(property.getValue().trim()))) {
                            dirtyFields.add(property.getName());
                            dirtyFieldsOrigVals.put(property.getName(), pageField.getValue());
                            pageField.setValue(property.getValue());
                            pageField = dynamicEntityDao.merge(pageField);
                        }
                    } else {
                        pageField = new PageFieldImpl();
                        pageField.setFieldKey(property.getName());
                        pageField.setValue(property.getValue());
                        pageField.setPage(page);
                        dynamicEntityDao.persist(pageField);
                        dirtyFields.add(property.getName());
                    }
                }
            }
            List<String> removeItems = new ArrayList<String>();
            for (String key : pageFieldMap.keySet()) {
                if (persistencePackage.getEntity().findProperty(key)==null) {
                    removeItems.add(key);
                }
            }
            if (removeItems.size() > 0) {
                for (String removeKey : removeItems) {
                    pageFieldMap.remove(removeKey);
                }
            }

            Collections.sort(dirtyFields);
            Entity entity = fetchEntityBasedOnId(pageId, dirtyFields);

            for (Map.Entry<String, String> entry : dirtyFieldsOrigVals.entrySet()) {
                entity.getPMap().get(entry.getKey()).setOriginalValue(entry.getValue());
                entity.getPMap().get(entry.getKey()).setOriginalDisplayValue(entry.getValue());
            }

            return entity;
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unable to perform update for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }
}
