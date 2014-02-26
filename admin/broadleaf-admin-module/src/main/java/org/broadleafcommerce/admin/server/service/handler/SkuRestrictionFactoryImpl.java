/*
 * Copyright 2008-2013 the original author or authors.
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

package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPath;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPathBuilder;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.Restriction;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.RestrictionFactory;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.BetweenDatePredicateProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.BetweenPredicateProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.CollectionSizeEqualPredicateProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.EqPredicateProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.IsNullPredicateProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.LikePredicateProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.PredicateProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

/**
 * This class takes into account that filters should be applied on either the root Sku property itself OR the defaultSku
 * for this particular Sku.
 *
 * @author Jeff Fischer
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blSkuRestrictionFactory")
public class SkuRestrictionFactoryImpl implements RestrictionFactory {

    @Resource(name="blRestrictionFactory")
    protected RestrictionFactory delegate;

    protected static final String DEFAULT_SKU_PATH_PREFIX = "product.defaultSku.";

    protected String skuPropertyPrefix = "";

    @Override
    public Restriction getRestriction(final String type, String propertyId) {
        final Restriction delegateRestriction = delegate.getRestriction(type, propertyId);
        return new Restriction()
            .withFilterValueConverter(delegateRestriction.getFilterValueConverter())
            .withPredicateProvider(new PredicateProvider() {
                @Override
                public Predicate buildPredicate(CriteriaBuilder builder, FieldPathBuilder fieldPathBuilder,
                                                From root, String ceilingEntity, String fullPropertyName,
                                                Path explicitPath, List directValues) {
                    FieldPath fieldPath = fieldPathBuilder.getFieldPath(root, fullPropertyName);
                    if ((StringUtils.isNotEmpty(skuPropertyPrefix) && fullPropertyName.startsWith(skuPropertyPrefix))
                                            || (StringUtils.isEmpty(skuPropertyPrefix) && CollectionUtils.isEmpty(fieldPath.getAssociationPath()))) {
                        
                        Path skuIdPath = fieldPathBuilder.getPath(root, getSkuPropertyPrefix() + "id", builder);
                        
                        // Build the first subquery based on searching from product.defaultSku.name
                        Subquery<Long> additionalSkusSubQuery = fieldPathBuilder.getCriteria().subquery(Long.class);
                        Root<SkuImpl> additionalSkusRoot = additionalSkusSubQuery.from(SkuImpl.class);
                        additionalSkusSubQuery.select(additionalSkusRoot.get("id").as(Long.class));
                        Path additionalSkusTargetPropertyPath = fieldPathBuilder.getPath(additionalSkusRoot, fullPropertyName.replace(getSkuPropertyPrefix(),  ""), builder);
                        // The path to the defaultSku property from a sku, available via the 'product' attribute
                        Path defaultSkuPropertyPath = fieldPathBuilder.getPath(additionalSkusRoot, 
                                DEFAULT_SKU_PATH_PREFIX + fullPropertyName.replace(getSkuPropertyPrefix(),  ""), builder);
                        Path additionalSkusProductPath = fieldPathBuilder.getPath(additionalSkusRoot, "product", builder);
                        
                        //Build the second subquery based on the value being set on the Sku itself, like just 'name'
                        Subquery<Long> hardcodedPropertySubquery = fieldPathBuilder.getCriteria().subquery(Long.class);
                        Root<SkuImpl> hardcodedPropertyRoot = hardcodedPropertySubquery.from(SkuImpl.class);
                        hardcodedPropertySubquery.select(hardcodedPropertyRoot.get("id").as(Long.class));
                        Path hardcodedPropertyTargetPath = fieldPathBuilder.getPath(hardcodedPropertyRoot, fullPropertyName.replace(getSkuPropertyPrefix(),  ""), builder);
                        
                        Predicate propertyExpression;
                        Predicate defaultSkuExpression;
                        if (delegateRestriction.getPredicateProvider() instanceof LikePredicateProvider) {
                                propertyExpression = builder.like(builder.lower(hardcodedPropertyTargetPath),
                                        ((String) directValues.get(0)).toLowerCase());
                                defaultSkuExpression = builder.like(builder.lower(defaultSkuPropertyPath),
                                        ((String) directValues.get(0)).toLowerCase());
                        } else if (delegateRestriction.getPredicateProvider() instanceof IsNullPredicateProvider) {
                            propertyExpression = builder.isNull(hardcodedPropertyTargetPath);
                            defaultSkuExpression = builder.isNull(defaultSkuPropertyPath);
                        } else if (delegateRestriction.getPredicateProvider() instanceof BetweenDatePredicateProvider) {
                            if (directValues.size() == 2) {
                                if (directValues.get(0) == null) {
                                    propertyExpression = builder.lessThan(hardcodedPropertyTargetPath, (Comparable) directValues.get(1));
                                    defaultSkuExpression = builder.lessThan(defaultSkuPropertyPath, (Comparable) directValues.get(1));
                                } else if (directValues.get(1) == null) {
                                    propertyExpression = builder.greaterThanOrEqualTo(hardcodedPropertyTargetPath,
                                            (Comparable) directValues.get(0));
                                    defaultSkuExpression = builder.greaterThanOrEqualTo(defaultSkuPropertyPath,
                                            (Comparable) directValues.get(0));
                                } else {
                                    propertyExpression = builder.between(hardcodedPropertyTargetPath, (Comparable) directValues.get(0),
                                            (Comparable) directValues.get(1));
                                    defaultSkuExpression = builder.between(defaultSkuPropertyPath, (Comparable) directValues.get(0),
                                            (Comparable) directValues.get(1));
                                }
                            } else {
                                propertyExpression = builder.equal(hardcodedPropertyTargetPath, directValues.get(0));
                                defaultSkuExpression = builder.equal(defaultSkuPropertyPath, directValues.get(0));
                            }
                        } else if (delegateRestriction.getPredicateProvider() instanceof BetweenPredicateProvider) {
                            if (directValues.size() > 1) {
                                propertyExpression = builder.between(hardcodedPropertyTargetPath, (Comparable) directValues.get(0),
                                        (Comparable) directValues.get(1));
                                defaultSkuExpression = builder.between(defaultSkuPropertyPath, (Comparable) directValues.get(0),
                                        (Comparable) directValues.get(1));
                            } else {
                                propertyExpression = builder.equal(hardcodedPropertyTargetPath, directValues.get(0));
                                defaultSkuExpression = builder.equal(defaultSkuPropertyPath, directValues.get(0));
                            }
                        } else if (delegateRestriction.getPredicateProvider() instanceof CollectionSizeEqualPredicateProvider) {
                            propertyExpression = builder.equal(builder.size(hardcodedPropertyTargetPath), directValues.get(0));
                            defaultSkuExpression = builder.equal(builder.size(defaultSkuPropertyPath), directValues.get(0));
                        } else if (delegateRestriction.getPredicateProvider() instanceof EqPredicateProvider) {
                            propertyExpression = hardcodedPropertyTargetPath.in(directValues);
                            defaultSkuExpression = defaultSkuPropertyPath.in(directValues);
                        } else {
                            throw new IllegalArgumentException("Unknown PredicateProvider instance: " +
                                    delegateRestriction.getPredicateProvider().getClass().getName());
                        }
                        
                        // First do a subquery to find all of the additional Skus that match the given expression
                        // This subquery will return all of the sku IDs that match the additional Sku expression. The WHERE clause of
                        // this is basically something like:
                        //  WHERE sku.name == null AND sku.product != null AND sku.product.defaultSku.name LIKE %<val>%
                        List<Predicate> subRestrictions = new ArrayList<Predicate>();
                        subRestrictions.add(builder.and(
                                                builder.isNull(additionalSkusTargetPropertyPath),
                                                builder.isNotNull(additionalSkusProductPath),
                                                defaultSkuExpression
                                            ));
                        additionalSkusSubQuery.where(subRestrictions.toArray(new Predicate[subRestrictions.size()]));
                        
                        // Now do another sub query to get all the Skus that actually have the name explicitly set. This will return
                        // all of the default Skus or additional Skus where the name has been explicitly set
                        // This query is something like:
                        // WHERE sku.name != null AND sku.name LIKE %<val>%
                        List<Predicate> hardcodedPropertyRestrictions = new ArrayList<Predicate>();
                        hardcodedPropertyRestrictions.add(builder.and(
                                builder.isNotNull(hardcodedPropertyTargetPath),
                                propertyExpression
                            ));
                        hardcodedPropertySubquery.where(hardcodedPropertyRestrictions.toArray(new Predicate[hardcodedPropertyRestrictions.size()]));
                        
                        // Now that we've built the subqueries, do an OR to select all Skus whose IDs are apart of the additional Skus
                        // or defaultSkus subquery
                        return builder.or(
                            skuIdPath.in(additionalSkusSubQuery),
                            skuIdPath.in(hardcodedPropertySubquery)
                        );
                    }
                    return delegateRestriction.getPredicateProvider().buildPredicate(builder, fieldPathBuilder, root,
                            ceilingEntity, fullPropertyName, explicitPath, directValues);
                }
        });
    }
    
    public RestrictionFactory getDelegate() {
        return delegate;
    }

    public void setDelegate(RestrictionFactory delegate) {
        this.delegate = delegate;
    }

    public String getSkuPropertyPrefix() {
        return skuPropertyPrefix;
    }

    public void setSkuPropertyPrefix(String skuPropertyPrefix) {
        if (StringUtils.isNotEmpty(skuPropertyPrefix) && !skuPropertyPrefix.endsWith(".")) {
            skuPropertyPrefix += ".";
        }
        this.skuPropertyPrefix = skuPropertyPrefix;
    }
}
