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
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPath;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPathBuilder;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.Restriction;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.RestrictionFactory;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate .BetweenDatePredicateProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.BetweenPredicateProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate .CollectionSizeEqualPredicateProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.EqPredicateProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.IsNullPredicateProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.LikePredicateProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.PredicateProvider;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * This class takes into account that filters should be applied on either the root Sku property itself OR the defaultSku
 * for this particular Sku.
 *
 * @author Jeff Fischer
 */
@Component("blSkuRestrictionFactory")
public class SkuRestrictionFactoryImpl implements RestrictionFactory {

    @Resource(name="blRestrictionFactory")
    protected RestrictionFactory delegate;

    protected static final String DEFAULT_SKU_PATH_PREFIX = "product.defaultSku.";

    protected String skuPropertyPrefix;

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
                                            || CollectionUtils.isEmpty(fieldPath.getAssociationPath())) {
                        Path targetPropertyPath = fieldPathBuilder.getPath(root, fieldPath, builder);
                        Path defaultSkuPropertyPath = fieldPathBuilder.getPath(root,
                                DEFAULT_SKU_PATH_PREFIX + fullPropertyName, builder);
                        Path productPath = fieldPathBuilder.getPath(root, "product", builder);
                        Predicate propertyExpression;
                        Predicate defaultSkuExpression;
                        if (delegateRestriction.getPredicateProvider() instanceof LikePredicateProvider) {
                                propertyExpression = builder.like(builder.lower(targetPropertyPath),
                                        ((String) directValues.get(0)).toLowerCase());
                                defaultSkuExpression = builder.like(builder.lower(defaultSkuPropertyPath),
                                        ((String) directValues.get(0)).toLowerCase());
                        } else if (delegateRestriction.getPredicateProvider() instanceof IsNullPredicateProvider) {
                            propertyExpression = builder.isNull(targetPropertyPath);
                            defaultSkuExpression = builder.isNull(defaultSkuPropertyPath);
                        } else if (delegateRestriction.getPredicateProvider() instanceof BetweenDatePredicateProvider) {
                            if (directValues.size() == 2) {
                                if (directValues.get(0) == null) {
                                    propertyExpression = builder.lessThan(targetPropertyPath, (Comparable) directValues.get(1));
                                    defaultSkuExpression = builder.lessThan(defaultSkuPropertyPath, (Comparable) directValues.get(1));
                                } else if (directValues.get(1) == null) {
                                    propertyExpression = builder.greaterThanOrEqualTo(targetPropertyPath,
                                            (Comparable) directValues.get(0));
                                    defaultSkuExpression = builder.greaterThanOrEqualTo(defaultSkuPropertyPath,
                                            (Comparable) directValues.get(0));
                                } else {
                                    propertyExpression = builder.between(targetPropertyPath, (Comparable) directValues.get(0),
                                            (Comparable) directValues.get(1));
                                    defaultSkuExpression = builder.between(defaultSkuPropertyPath, (Comparable) directValues.get(0),
                                            (Comparable) directValues.get(1));
                                }
                            } else {
                                propertyExpression = builder.equal(targetPropertyPath, directValues.get(0));
                                defaultSkuExpression = builder.equal(defaultSkuPropertyPath, directValues.get(0));
                            }
                        } else if (delegateRestriction.getPredicateProvider() instanceof BetweenPredicateProvider) {
                            if (directValues.size() > 1) {
                                propertyExpression = builder.between(targetPropertyPath, (Comparable) directValues.get(0),
                                        (Comparable) directValues.get(1));
                                defaultSkuExpression = builder.between(defaultSkuPropertyPath, (Comparable) directValues.get(0),
                                        (Comparable) directValues.get(1));
                            } else {
                                propertyExpression = builder.equal(targetPropertyPath, directValues.get(0));
                                defaultSkuExpression = builder.equal(defaultSkuPropertyPath, directValues.get(0));
                            }
                        } else if (delegateRestriction.getPredicateProvider() instanceof CollectionSizeEqualPredicateProvider) {
                            propertyExpression = builder.equal(builder.size(targetPropertyPath), directValues.get(0));
                            defaultSkuExpression = builder.equal(builder.size(defaultSkuPropertyPath), directValues.get(0));
                        } else if (delegateRestriction.getPredicateProvider() instanceof EqPredicateProvider) {
                            propertyExpression = builder.equal(targetPropertyPath, directValues.get(0));
                            defaultSkuExpression = builder.equal(defaultSkuPropertyPath, directValues.get(0));
                        } else {
                            throw new IllegalArgumentException("Unknown PredicateProvider instance: " +
                                    delegateRestriction.getPredicateProvider().getClass().getName());
                        }

                        return buildCompositePredicate(builder, targetPropertyPath, productPath, propertyExpression,
                                defaultSkuExpression);
                    }
                    return delegateRestriction.getPredicateProvider().buildPredicate(builder, fieldPathBuilder, root,
                            ceilingEntity, fullPropertyName, explicitPath, directValues);
                }
        });
    }

    protected Predicate buildCompositePredicate(CriteriaBuilder builder, Path targetPropertyPath, Path productPath,
                                                Predicate propertyExpression, Predicate defaultSkuExpression) {
        return builder.or(
            builder.or(
                builder.and(builder.isNotNull(targetPropertyPath), propertyExpression),
                builder.and(
                    builder.and(
                        builder.isNull(targetPropertyPath),
                        builder.isNotNull(productPath)
                    ), defaultSkuExpression
                )
            ), builder.and(builder.isNull(productPath), propertyExpression)
        );
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
