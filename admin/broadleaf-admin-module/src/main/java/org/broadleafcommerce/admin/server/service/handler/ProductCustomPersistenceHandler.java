/*
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
import org.broadleafcommerce.common.service.ParentCategoryLegacyModeService;
import org.broadleafcommerce.common.service.ParentCategoryLegacyModeServiceImpl;
import org.broadleafcommerce.common.util.dao.QueryUtils;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
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
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

/**
 * @author Jeff Fischer
 */
@Component("blProductCustomPersistenceHandler")
public class ProductCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {
    
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blProductCustomPersistenceHandlerExtensionManager")
    protected ProductCustomPersistenceHandlerExtensionManager extensionManager;
    
    @Resource(name = "blParentCategoryLegacyModeService")
    protected ParentCategoryLegacyModeService parentCategoryLegacyModeService;

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
        
        boolean legacy = parentCategoryLegacyModeService.isLegacyMode();
        
        //the following code applies when filters are present only:
        //"legacy" means that the parent category filter still utilizes Product.defaultCategory as the field to be matched
        //against the categories chosen in the listGrid filter. The default behavior up to this point, assumes the "legacy" mode. 
        //This means that one of the FilterAndSortCriterias will try to match the chosen values against "defaultCategory".
        //If "legacy" is false, we remove that FilterAndSortCriteria from the CTO, and inject a new FilterMapping in cto.additionalFilterMappings,
        //which seeks matching values in  allParentCategoryXRefs instead
        if (!legacy) {
            FilterAndSortCriteria fsc = cto.getCriteriaMap().get("defaultCategory");
            if (fsc != null) {
                List<String> filterValues = fsc.getFilterValues();
                cto.getCriteriaMap().remove("defaultCategory");
                FilterMapping filterMapping = new FilterMapping()
                        .withFieldPath(new FieldPath().withTargetProperty("allParentCategoryXrefs.category.id"))
                        .withDirectFilterValues(filterValues)
                        .withRestriction(new Restriction()
                                .withPredicateProvider(new PredicateProvider() {
                                    @Override
                                    public Predicate buildPredicate(CriteriaBuilder builder, FieldPathBuilder fieldPathBuilder,
                                            From root, String ceilingEntity,
                                            String fullPropertyName, Path explicitPath, List directValues) {

                                        //the property to be matched against (allParentCategoryXrefs.category.id) comes as "explicitPath"
                                        //the specifics of what values are acceptable (those given as filter values, that in addition are defaults)
                                        //are resolved in a sub-query
                                        Subquery<Long> sub = fieldPathBuilder.getCriteria().subquery(Long.class);
                                        Root<CategoryProductXrefImpl> subRoot = sub.from(CategoryProductXrefImpl.class);
                                        sub.select(subRoot.get("category").get("id").as(Long.class));
                                        List<Predicate> subRestrictions = new ArrayList<Predicate>();
                                        subRestrictions.add(builder.equal(subRoot.get("defaultReference"), Boolean.TRUE));
                                        subRestrictions.add(subRoot.get("category").get("id").in(directValues));
                                        //archived?
                                        QueryUtils.notArchived(builder, subRestrictions, subRoot, "archiveStatus");

                                        sub.where(subRestrictions.toArray(new Predicate[subRestrictions.size()]));

                                        return explicitPath.in(sub);
                                    }
                                }));

                cto.getAdditionalFilterMappings().add(filterMapping);
            }
        }

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
        return helper.getCompatibleModule(OperationType.BASIC).fetch(persistencePackage, cto);
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
            boolean handled = false;
            if (extensionManager != null) {
                ExtensionResultStatusType result = extensionManager.getProxy().manageParentCategoryForAdd(persistencePackage, adminInstance);
                handled = ExtensionResultStatusType.NOT_HANDLED != result;
            }
            if (!handled) {
                setupXref(adminInstance);
            }
            
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
        if (entity.getPMap().get("pricingModel") != null) {
            if (ProductBundlePricingModelType.ITEM_SUM.getType().equals(entity.getPMap().get("pricingModel").getValue())) {
                ((BasicFieldMetadata)adminProperties.get("defaultSku.retailPrice")).setRequiredOverride(false);
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
