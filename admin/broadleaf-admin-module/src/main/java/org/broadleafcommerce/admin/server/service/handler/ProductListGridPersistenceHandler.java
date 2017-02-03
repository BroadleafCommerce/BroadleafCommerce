/*
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.admin.server.service.handler;

import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.common.service.ParentCategoryLegacyModeService;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.ListGridFetchRequest;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.AbstractListGridPersistenceHandler;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

/**
 * @author Chad Harchar (charchar)
 */
@Component("blProductListGridPersistenceHandler")
public class ProductListGridPersistenceHandler extends AbstractListGridPersistenceHandler {

    @Resource(name = "blParentCategoryLegacyModeService")
    protected ParentCategoryLegacyModeService parentCategoryLegacyModeService;

    @Resource(name = "blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    public static final String DEFAULT_CATEGORY_PROPERTY = "defaultCategory";

    public static final int CATEGORY_NAME_INDEX = 1;
    public static final int CATEGORY_ID_INDEX = 2;

    List<String> handledProperties = new ArrayList() {{add(DEFAULT_CATEGORY_PROPERTY);}};
    private String idField;

    @Override
    public Boolean canHandleEntity(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return Product.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Map<String, FieldMetadata> stripHandledProperties(Map<String, FieldMetadata> properties) {
        
        Map<String, FieldMetadata> strippedProperties = new HashMap<>();
        
        for (String propertyName : properties.keySet()) {
            if (!handledProperties.contains(propertyName)) {
                strippedProperties.put(propertyName, properties.get(propertyName));
            }
        }
        return strippedProperties;
    }

    @Override
    public Entity[] handleEntities(PersistencePackage persistencePackage, Entity[] entities, DynamicEntityDao dynamicEntityDao, RecordHelper recordHelper, Map<String, FieldMetadata> filteredProperties) {

        ListGridFetchRequest listGridFetchRequest = persistencePackage.getListGridFetchRequest();
        setIdField(listGridFetchRequest.getIdField());

        entities = populateCategoryDataForEntities(listGridFetchRequest, entities, dynamicEntityDao);

        return entities;
    }

    public Entity[] populateCategoryDataForEntities(ListGridFetchRequest listGridFetchRequest, Entity[] entities, DynamicEntityDao dynamicEntityDao) {
        CriteriaBuilder criteriaBuilder = dynamicEntityDao.getStandardEntityManager().getCriteriaBuilder();
        CriteriaQuery criteria = criteriaBuilder.createQuery();

        List<Long> productIds = new ArrayList<>();


        for (Entity entity : entities) {
            String productId = entity.getPMap().get(listGridFetchRequest.getIdField()).getValue();
            productIds.add(new Long(productId));
        }

        productIds = sandBoxHelper.mergeCloneIds(ProductImpl.class, productIds.toArray(new Long[productIds.size()]));

        if (parentCategoryLegacyModeService.isLegacyMode()) {
            getLegacyCategoriesQuery(criteria, productIds);
        } else {
            getCategoriesQuery(criteriaBuilder, criteria, productIds);
        }

        TypedQuery response = dynamicEntityDao.getStandardEntityManager().createQuery(criteria);
        response.setMaxResults(entities.length);

        List results = response.getResultList();
        List<Long> categoryIds = new ArrayList<>();

        Map<String, String[]> resultMap = getProductIdToCategoryInfoMap(results, categoryIds);
        Map<String, String> categorySandboxableNames = new HashMap<>();
        if (listGridFetchRequest.getIsSandboxableEntity()) {
            categorySandboxableNames = getSandboxableCategoryNames(categoryIds, dynamicEntityDao);
        }

        entities = updateEntitiesWithCategoryInfo(entities, resultMap, categorySandboxableNames);

        return entities;
    }

    private Map<String, String> getSandboxableCategoryNames(List<Long> categoryIds, DynamicEntityDao dynamicEntityDao) {
        categoryIds = sandBoxHelper.mergeCloneIds(CategoryImpl.class, categoryIds.toArray(new Long[categoryIds.size()]));

        CriteriaBuilder criteriaBuilder = dynamicEntityDao.getStandardEntityManager().getCriteriaBuilder();
        CriteriaQuery criteria = criteriaBuilder.createQuery();

        Root root = criteria.from(CategoryImpl.class);
        Path categoryId = root.get("id");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(categoryId.in(categoryIds));
        criteria.where(predicates.toArray(new Predicate[]{}));

        criteria.multiselect(categoryId, root.get("name"), root.get("embeddableSandBoxDiscriminator").get("originalItemId"));

        TypedQuery response = dynamicEntityDao.getStandardEntityManager().createQuery(criteria);
        response.setMaxResults(categoryIds.size());

        List results = response.getResultList();

        Map<String, String> categorySandboxedNames = translateResultsIntoSandboxedNames(results);

        return categorySandboxedNames;
    }

    private Map<String, String> translateResultsIntoSandboxedNames(List results) {
        Map<String, String> categorySandboxedNames = new HashMap<>();

        for (Object result : results) {

            Object[] resultArray = (Object[])result;

            Object categoryNameResult = resultArray[1];
            Object categoryIdResult = resultArray[2];

            if (categoryNameResult != null && categoryIdResult != null) {
                categorySandboxedNames.put(categoryIdResult.toString(), categoryNameResult.toString());
            }

        }

        return categorySandboxedNames;
    }

    public Entity[] updateEntitiesWithCategoryInfo(Entity[] entities, Map<String, String[]> resultMap, Map<String, String> categorySandboxableNames) {
        for (Entity entity : entities) {

            String idValue = entity.getPMap().get(getIdField()).getValue();
            String[] resultObject = resultMap.get(idValue);
            
            if (resultObject == null) {
                SandBoxHelper.OriginalIdResponse originalId = sandBoxHelper.getOriginalId(ProductImpl.class, new Long(idValue));
                if (originalId != null) {
                    idValue = originalId.getOriginalId().toString();
                    resultObject = resultMap.get(idValue);
                }
            }

            if (resultObject != null) {
                Property property = new Property();
                property.setName(DEFAULT_CATEGORY_PROPERTY);
                
                String categoryNameDisplayValue = resultObject[0];
                
                if (categorySandboxableNames.containsKey(resultObject[1].toString())) {
                    categoryNameDisplayValue = categorySandboxableNames.get(resultObject[1].toString());
                }

                property.setDisplayValue(categoryNameDisplayValue);

                property.setValue(resultObject[1].toString());
                entity.addProperty(property);
            }
        }
        return entities;
    }

    public Map<String, String[]> getProductIdToCategoryInfoMap(List results, List<Long> categoryIds) {
        Map<String, String[]> resultMap = new HashMap<>();

        for (Object result : results) {

            Object[] resultArray = (Object[])result;
            String[] categoryInfoArray = new String[2];

            Object categoryNameResult = resultArray[CATEGORY_NAME_INDEX];
            Object categoryIdResult = resultArray[CATEGORY_ID_INDEX];

            if (categoryNameResult != null) {
                categoryInfoArray[0] = categoryNameResult.toString();
            }

            if (categoryIdResult != null) {
                categoryInfoArray[1] = categoryIdResult.toString();
            }
            
            if (categoryNameResult != null && categoryIdResult != null) {
                resultMap.put(resultArray[0].toString(), categoryInfoArray);
            }
            
            if (!categoryIds.contains(categoryIdResult.toString())) {
                categoryIds.add(new Long(categoryIdResult.toString()));
            }
        }
        return resultMap;
    }

    public void getCategoriesQuery(CriteriaBuilder criteriaBuilder, CriteriaQuery criteria, List<Long> productIds) {
        Root root = criteria.from(CategoryProductXrefImpl.class);

        List<Predicate> predicates = new ArrayList<>();

        Path productId = root.get("product").get(getIdField());

        predicates.add(productId.in(productIds));
        predicates.add(criteriaBuilder.equal(root.get("defaultReference"), true));

        criteria.where(predicates.toArray(new Predicate[]{}));

        criteria.multiselect(productId, root.get("category").get("name"), root.get("category").get("id"));
    }

    public void getLegacyCategoriesQuery(CriteriaQuery criteria, List<Long> productIds) {
        Root root = criteria.from(ProductImpl.class);
        Join categoryJoin = root.join(DEFAULT_CATEGORY_PROPERTY, JoinType.LEFT);

        Selection categoryNamePath = categoryJoin.get("name");
        Selection categoryIdPath = categoryJoin.get("id");

        criteria.multiselect(root.get(getIdField()), categoryNamePath, categoryIdPath);

        criteria.where(root.get(getIdField()).in(productIds));
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public String getIdField() {
        return idField;
    }
}
