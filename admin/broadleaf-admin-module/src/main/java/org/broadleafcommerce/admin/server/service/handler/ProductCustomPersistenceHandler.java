/*
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.admin.server.service.extension.ProductCustomPersistenceHandlerExtensionManager;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.common.service.ParentCategoryLegacyModeService;
import org.broadleafcommerce.common.service.ParentCategoryLegacyModeServiceImpl;
import org.broadleafcommerce.common.util.BLCCollectionUtils;
import org.broadleafcommerce.common.util.TypedTransformer;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.catalog.service.type.ProductBundlePricingModelType;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.EmptyFilterValues;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPath;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPathBuilder;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.Restriction;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.PredicateProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author Jeff Fischer
 */
@Component("blProductCustomPersistenceHandler")
public class ProductCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    public static final String DEFAULT_SKU = "defaultSku";
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blProductCustomPersistenceHandlerExtensionManager")
    protected ProductCustomPersistenceHandlerExtensionManager extensionManager;
    
    @Resource(name = "blParentCategoryLegacyModeService")
    protected ParentCategoryLegacyModeService parentCategoryLegacyModeService;

    @Resource(name="blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    @Value("${product.query.limit:500}")
    protected long queryLimit;

    @Value("${product.eager.fetch.associations.admin:true}")
    protected boolean eagerFetchAssociations = true;

    private static final Log LOG = LogFactory.getLog(ProductCustomPersistenceHandler.class);

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        String[] customCriteria = persistencePackage.getCustomCriteria();
        return !ArrayUtils.isEmpty(customCriteria) && "productDirectEdit".equals(customCriteria[0]) && Product.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        Map<String, FieldMetadata> md = getMetadata(persistencePackage, helper);

        if (!isDefaultCategoryLegacyMode()) {
            md.remove("allParentCategoryXrefs");

            BasicFieldMetadata defaultCategory = ((BasicFieldMetadata) md.get("defaultCategory"));
            defaultCategory.setFriendlyName("ProductImpl_Parent_Category");
        }

        return getResultSet(persistencePackage, helper, md);
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao
            dynamicEntityDao, RecordHelper helper) throws ServiceException {

        if (!setupNonLegacyProductCategory(cto, dynamicEntityDao)) {
            return new DynamicResultSet(null, new Entity[0], 0);
        }

        if (eagerFetchAssociations) {
            cto.getNonCountAdditionalFilterMappings().add(new FilterMapping()
                    .withDirectFilterValues(new EmptyFilterValues())
                    .withRestriction(new Restriction()
                            .withPredicateProvider(new PredicateProvider() {
                                @Override
                                public Predicate buildPredicate(CriteriaBuilder builder,
                                                                FieldPathBuilder fieldPathBuilder, From root,
                                                                String ceilingEntity,
                                                                String fullPropertyName, Path explicitPath,
                                                                List directValues) {
                                    root.fetch("defaultSku", JoinType.LEFT);
                                    root.fetch("defaultCategory", JoinType.LEFT);
                                    return null;
                                }
                            })
                    ));
        }
        if (ArrayUtils.isEmpty(persistencePackage.getSectionCrumbs()) && !hasCriteriaForId(cto)) {
            //Add special handling for product list grid fetches
            boolean hasExplicitSort = false;
            for (FilterAndSortCriteria filter : cto.getCriteriaMap().values()) {
                hasExplicitSort = filter.getSortDirection() != null;
                if (hasExplicitSort) {
                    break;
                }
            }
            if (!hasExplicitSort) {
                FilterAndSortCriteria filter = cto.get("id");
                filter.setNullsLast(false);
                filter.setSortAscending(true);
            }

            // Check if we are only sorting/filtering on Sku related fields. If so, return the filtered product list.
            List<Product> filteredProducts = filterProductsBySkuFields(cto, dynamicEntityDao, helper);
            if (isSkuOnlyFilter(cto)) {
                // Convert the filtered product list into a list of entities
                PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
                Map<String, FieldMetadata> indexFieldMetadata = helper.getSimpleMergedProperties(Product.class.getName(), persistencePerspective);
                Entity[] entities = helper.getRecords(indexFieldMetadata, filteredProducts);

                // We need to get the total number of records available because the entityList returned may not be the full set.
                Map<String, FieldMetadata> originalProps = helper.getSimpleMergedProperties(Product.class.getName(), persistencePerspective);
                List<FilterMapping> filterMappings = helper.getFilterMappings(persistencePerspective, cto, Product.class.getName(), originalProps);
                int totalRecords = helper.getTotalRecords(persistencePackage.getCeilingEntityFullyQualifiedClassname(), filterMappings);

                // Create a Dynamic Result Set from the entity list created above.
                DynamicResultSet resultSet = new DynamicResultSet(entities, totalRecords);
                return resultSet;

            } else {
                if (filteredProducts.size() != 0) {
                    clearFilteredProperties(cto);
                    addSkuFilterMapping(cto, filteredProducts);
                }
            }

            try {
                extensionManager.getProxy().initiateFetchState();
                return helper.getCompatibleModule(OperationType.BASIC).fetch(persistencePackage, cto);
            } finally {
                extensionManager.getProxy().endFetchState();
            }
        } else {
            return helper.getCompatibleModule(OperationType.BASIC).fetch(persistencePackage, cto);
        }
    }

    protected Boolean hasCriteriaForId(CriteriaTransferObject cto) {
        return cto.getCriteriaMap().containsKey("id") && !CollectionUtils.isEmpty(cto.getCriteriaMap().get("id").getFilterValues());
    }

    @SuppressWarnings("unchecked")
    protected boolean setupNonLegacyProductCategory(CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao) {
        //the following code applies when filters are present only:
        //"legacy" means that the parent category filter still utilizes Product.defaultCategory as the field to be matched
        //against the categories chosen in the listGrid filter. The default behavior up to this point, assumes the "legacy" mode.
        //This means that one of the FilterAndSortCriterias will try to match the chosen values against "defaultCategory".
        //If "legacy" is false, we remove that FilterAndSortCriteria from the CTO, and inject a new FilterMapping in cto.additionalFilterMappings,
        //which seeks matching values in  allParentCategoryXRefs instead
        if (!isDefaultCategoryLegacyMode()) {
            FilterAndSortCriteria fsc = cto.getCriteriaMap().get("defaultCategory");
            if (fsc != null) {
                List<String> filterValues = fsc.getFilterValues();
                cto.getCriteriaMap().remove("defaultCategory");

                List<Long> transformedValues = BLCCollectionUtils.collectList(filterValues, new TypedTransformer<Long>() {
                    @Override
                    public Long transform(Object input) {
                        return Long.parseLong(((String) input));
                    }
                });
                CriteriaBuilder builder = dynamicEntityDao.getStandardEntityManager().getCriteriaBuilder();
                CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
                Root<CategoryProductXrefImpl> root = criteria.from(CategoryProductXrefImpl.class);
                criteria.select(root.get("product").get("id").as(Long.class));
                List<Predicate> restrictions = new ArrayList<Predicate>();
                restrictions.add(builder.equal(root.get("defaultReference"), Boolean.TRUE));
                if (CollectionUtils.isNotEmpty(transformedValues)) {
                    restrictions.add(root.get("category").get("id").in(transformedValues));
                }

                criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
                TypedQuery<Long> query = dynamicEntityDao.getStandardEntityManager().createQuery(criteria);
                List<Long> productIds = query.getResultList();
                productIds = sandBoxHelper.mergeCloneIds(ProductImpl.class, productIds.toArray(new Long[productIds.size()]));

                if(productIds.size() == 0){
                    return false;
                }
                if (productIds.size() <= queryLimit) {
                    FilterMapping filterMapping = new FilterMapping()
                        .withFieldPath(new FieldPath().withTargetProperty("id"))
                        .withDirectFilterValues(productIds)
                        .withRestriction(new Restriction()
                            .withPredicateProvider(new PredicateProvider() {
                                   @Override
                                   public Predicate buildPredicate(CriteriaBuilder builder, FieldPathBuilder fieldPathBuilder,
                                                                   From root, String ceilingEntity, String fullPropertyName,
                                                                   Path explicitPath, List directValues) {
                                       return explicitPath.in(directValues);
                                   }
                               }
                            )
                        );
                    cto.getAdditionalFilterMappings().add(filterMapping);
                } else {
                    String joined = StringUtils.join(transformedValues, ',');
                    LOG.warn(String.format("Skipping default category filtering for product fetch query since there are " +
                            "more than " + queryLimit + " products found to belong to the selected default categories(%s). This is a " +
                            "filter query limitation.", joined));
                }
            }
        }
        return true;
    }

    protected Boolean isSkuOnlyFilter(CriteriaTransferObject cto) {
        Boolean isSkuOnly = Boolean.TRUE;
        int skuFilters = 0;
        for (String filterProperty : cto.getCriteriaMap().keySet()) {
            if (!filterProperty.startsWith(DEFAULT_SKU)
                    && !filterProperty.equals("id")
                    && !filterProperty.equals("isLookup")) {
                isSkuOnly = Boolean.FALSE;
                break;
            } else if (filterProperty.startsWith(DEFAULT_SKU)){
                skuFilters++;
            }
        }
        return isSkuOnly && (skuFilters > 0);
    }

    protected List<Product> filterProductsBySkuFields(CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) {
        // Setup the Sku query.
        CriteriaBuilder builder = dynamicEntityDao.getStandardEntityManager().getCriteriaBuilder();
        CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
        Root<SkuImpl> root = criteria.from(SkuImpl.class);
        criteria.select(root.get("defaultProduct").as(Product.class));

        // Build our list of Sku related restrictions.
        List<Predicate> restrictions = buildSkuRestrictions(cto, helper, builder, criteria, root);

        // Apply the restrictions if any.
        if (restrictions.size() > 0) {
            criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        }

        TypedQuery<Product> query = dynamicEntityDao.getStandardEntityManager().createQuery(criteria);
        List<Product> filteredProducts = query.getResultList();

        return filteredProducts;
    }

    @SuppressWarnings("unchecked")
    protected List<Predicate> buildSkuRestrictions(CriteriaTransferObject cto, RecordHelper helper, CriteriaBuilder builder, CriteriaQuery<Product> criteria, Root<SkuImpl> root) {
        List<Predicate> restrictions = new ArrayList<>();
        for (String filterProperty : cto.getCriteriaMap().keySet()) {
            if (filterProperty.startsWith(DEFAULT_SKU)) {
                FilterAndSortCriteria fasc = cto.getCriteriaMap().get(filterProperty);
                String skuField = filterProperty.replace(DEFAULT_SKU + ".","");
                Path skuFieldPath = root.get(skuField);
                Field field = helper.getFieldManager().getField(SkuImpl.class, skuField);

                // If we have filter values, create the appropriate restrictions.
                if (fasc.getFilterValues().size() > 0) {
                    if (field.getType().isAssignableFrom(String.class)) {
                        restrictions.add(builder.like(skuFieldPath.as(String.class), "%" + fasc.getFilterValues().get(0) + "%"));
                    } else if (field.getType().isAssignableFrom(BigDecimal.class)) {
                        BigDecimal decimalValue = new BigDecimal(fasc.getFilterValues().get(0));
                        String filterValue = String.format("%.5f", decimalValue);

                        if (fasc.getFilterValues().size() == 1) {
                            restrictions.add(builder.equal(skuFieldPath.as(String.class), filterValue));
                        } else {
                            BigDecimal firstValue = new BigDecimal(fasc.getFilterValues().get(0));
                            BigDecimal secondValue = new BigDecimal(fasc.getFilterValues().get(1));
                            restrictions.add(builder.between(skuFieldPath.as(BigDecimal.class), firstValue, secondValue));
                        }
                    }
                }

                // Add sorting if applicable.
                if (fasc.getSortDirection() != null) {
                    if (fasc.getSortAscending()) {
                        criteria.orderBy(builder.asc(skuFieldPath));
                    } else {
                        criteria.orderBy(builder.desc(skuFieldPath));
                    }
                }
            }
        }
        return restrictions;
    }

    @SuppressWarnings("unchecked")
    protected void addSkuFilterMapping(CriteriaTransferObject cto, List<Product> filterValues) {
        List<String> productIds = buildProductIdList(filterValues);
        cto.getAdditionalFilterMappings().add(new FilterMapping()
                .withFieldPath(new FieldPath().withTargetProperty("id"))
                .withDirectFilterValues(productIds)
                .withRestriction(new Restriction()
                        .withPredicateProvider(new PredicateProvider() {
                            @Override
                            public Predicate buildPredicate(CriteriaBuilder builder, FieldPathBuilder fieldPathBuilder,
                                                            From root, String ceilingEntity,
                                                            String fullPropertyName, Path explicitPath, List directValues) {
                                return explicitPath.in(directValues);
                            }
                        })
                ));
    }

    protected List<String> buildProductIdList(List<Product> filterValues) {
        List<String> productIds = BLCCollectionUtils.collectList(filterValues, new TypedTransformer<String>() {
            @Override
            public String transform(Object input) {
                // Make sure we have the actual product id.
                Long originalId = sandBoxHelper.getOriginalId(input);
                if (originalId != null) {
                    return originalId.toString();
                } else {
                    return ((Product)input).getId().toString();
                }
            }
        });
        return productIds;
    }

    protected void clearFilteredProperties(CriteriaTransferObject cto) {
        for (String filterProperty : cto.getCriteriaMap().keySet()) {
            if (filterProperty.startsWith(DEFAULT_SKU)) {
                FilterAndSortCriteria fasc = cto.getCriteriaMap().get(filterProperty);
                if (fasc.getFilterValues().size() > 0) {
                    cto.getCriteriaMap().get(filterProperty).setFilterValues(Collections.<String>emptyList());
                }
            }
        }
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity  = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Product adminInstance = (Product) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Product.class.getName(), persistencePerspective);

            if (adminInstance instanceof ProductBundle) {
                removeBundleFieldRestrictions((ProductBundle)adminInstance, adminProperties, entity);
            }
            
            adminInstance = (Product) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
            adminInstance = dynamicEntityDao.merge(adminInstance);

            //Since none of the Sku fields are required, it's possible that the user did not fill out
            //any Sku fields, and thus a Sku would not be created. Product still needs a default Sku so instantiate one
            if (adminInstance.getDefaultSku() == null) {
                Sku newSku = catalogService.createSku();
                dynamicEntityDao.persist(newSku);
                adminInstance.setDefaultSku(newSku);
                adminInstance = dynamicEntityDao.merge(adminInstance);
            }

            //also set the default product for the Sku
            adminInstance.getDefaultSku().setDefaultProduct(adminInstance);
            dynamicEntityDao.merge(adminInstance.getDefaultSku());

            // if this is a Pre-Add, skip the rest of the method
            if (entity.isPreAdd()) {
                return helper.getRecord(adminProperties, adminInstance, null, null);
            }

            boolean handled = false;
            if (extensionManager != null) {
                ExtensionResultStatusType result = extensionManager.getProxy().manageParentCategoryForAdd(persistencePackage, adminInstance);
                handled = ExtensionResultStatusType.NOT_HANDLED != result;
            }
            if (!handled) {
                setupXref(adminInstance);
            }

            return helper.getRecord(adminProperties, adminInstance, null, null);
        } catch (Exception e) {
            throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
        }
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();

            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Product.class.getName(), persistencePerspective);
            BasicFieldMetadata defaultCategory = ((BasicFieldMetadata) adminProperties.get("defaultCategory"));
            defaultCategory.setFriendlyName("ProductImpl_Parent_Category");
            if (entity.findProperty("defaultCategory") != null && !StringUtils.isEmpty(entity.findProperty("defaultCategory").getValue())) {
                //Change the inherited type so that this property is disconnected from the entity and validation is temporarily skipped.
                //This is useful when the defaultCategory was previously completely empty for whatever reason. Without this, such
                //a case would fail the validation, even though the property was specified in the submission.
                defaultCategory.setInheritedFromType(String.class.getName());
            }

            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            Product adminInstance = (Product) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            if (adminInstance instanceof ProductBundle) {
                removeBundleFieldRestrictions((ProductBundle)adminInstance, adminProperties, entity);
            }

            CategoryProductXref oldDefault = getCurrentDefaultXref(adminInstance);
            adminInstance = (Product) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
            adminInstance = dynamicEntityDao.merge(adminInstance);
            boolean handled = false;
            if (extensionManager != null) {
                ExtensionResultStatusType result = extensionManager.getProxy().manageParentCategoryForUpdate
                        (persistencePackage, adminInstance);
                handled = ExtensionResultStatusType.NOT_HANDLED != result;

                extensionManager.getProxy().manageFields(persistencePackage, adminInstance);
            }
            if (!handled) {
                setupXref(adminInstance);
                removeOldDefault(adminInstance, oldDefault, entity);
            }

            return helper.getRecord(adminProperties, adminInstance, null, null);
        } catch (Exception e) {
            throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
        }
    }

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Product.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            Product adminInstance = (Product) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            if (extensionManager != null) {
                extensionManager.getProxy().manageRemove(persistencePackage, adminInstance);
            }
            helper.getCompatibleModule(OperationType.BASIC).remove(persistencePackage);
        } catch (ClassNotFoundException e) {
            throw new ServiceException("Unable to remove entity for " + entity.getType()[0], e);
        }
    }

    /**
     * If the pricing model is of type item_sum, that property should not be required
     * @param adminInstance
     * @param adminProperties
     * @param entity
     */
    protected void removeBundleFieldRestrictions(ProductBundle adminInstance, Map<String, FieldMetadata> adminProperties, Entity entity) {
        //no required validation for product bundles
        ((BasicFieldMetadata)adminProperties.get("defaultSku.retailPrice")).setRequiredOverride(false);
        if (entity.getPMap().get("pricingModel") != null) {
            if (ProductBundlePricingModelType.BUNDLE.getType().equals(entity.getPMap().get("pricingModel").getValue())) {
                ((BasicFieldMetadata)adminProperties.get("defaultSku.retailPrice")).setRequiredOverride(true);
            }
        }
    }

    protected Boolean isDefaultCategoryLegacyMode() {
        ParentCategoryLegacyModeService legacyModeService = ParentCategoryLegacyModeServiceImpl.getLegacyModeService();
        if (legacyModeService != null) {
            return legacyModeService.isLegacyMode();
        }
        return false;
    }

    protected Category getExistingDefaultCategory(Product product) {
        //Make sure we get the actual field value - not something manipulated in the getter
        Category parentCategory;
        try {
            Field defaultCategory = ProductImpl.class.getDeclaredField("defaultCategory");
            defaultCategory.setAccessible(true);
            parentCategory = (Category) defaultCategory.get(product);
        } catch (NoSuchFieldException e) {
            throw ExceptionHelper.refineException(e);
        } catch (IllegalAccessException e) {
            throw ExceptionHelper.refineException(e);
        }
        return parentCategory;
    }

    protected void removeOldDefault(Product adminInstance, CategoryProductXref oldDefault, Entity entity) {
        if (!isDefaultCategoryLegacyMode()) {
            if (entity.findProperty("defaultCategory") != null && StringUtils.isEmpty(entity.findProperty("defaultCategory").getValue())) {
                adminInstance.setCategory(null);
            }
            CategoryProductXref newDefault = getCurrentDefaultXref(adminInstance);
            if (oldDefault != null && !oldDefault.equals(newDefault)) {
                adminInstance.getAllParentCategoryXrefs().remove(oldDefault);
            }
        }
    }

    protected void setupXref(Product adminInstance) {
        if (isDefaultCategoryLegacyMode()) {
            CategoryProductXref categoryXref = new CategoryProductXrefImpl();
            categoryXref.setCategory(getExistingDefaultCategory(adminInstance));
            categoryXref.setProduct(adminInstance);
            if (!adminInstance.getAllParentCategoryXrefs().contains(categoryXref) && categoryXref.getCategory() != null) {
                adminInstance.getAllParentCategoryXrefs().add(categoryXref);
            }
        }
    }

    protected CategoryProductXref getCurrentDefaultXref(Product product) {
        CategoryProductXref currentDefault = null;
        List<CategoryProductXref> xrefs = product.getAllParentCategoryXrefs();
        if (!CollectionUtils.isEmpty(xrefs)) {
            for (CategoryProductXref xref : xrefs) {
                if (xref.getCategory().isActive() && xref.getDefaultReference() != null && xref.getDefaultReference()) {
                    currentDefault = xref;
                    break;
                }
            }
        }
        return currentDefault;
    }
}
