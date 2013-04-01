/*
 * Copyright 2008-2013 the original author or authors.
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

package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.openadmin.server.cto.FilterCriterionProviders;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.criteria.FilterCriterionProvider;
import com.anasoft.os.daofusion.criteria.SimpleFilterCriterionProvider;
import com.anasoft.os.daofusion.criteria.SimpleFilterCriterionProvider.FilterDataStrategy;

/**
 * This class takes into account that filters should be applied on either the root Sku property itself OR the defaultSku
 * for this particular Sku.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 *
 */
public class SkuPropertiesFilterCriterionProvider extends FilterCriterionProviders {
    
    protected static final String DEFAULT_SKU_PATH_PREFIX = "defaultSku.";

    protected String skuPropertyPrefix;
    
    public SkuPropertiesFilterCriterionProvider() {
        super();
    }

    /**
     * This should be used if you are attempting to filter on an object that could contain a Sku 'ToOne'
     * relationship that might need to be filtered on. For instance, InventoryImpl has a 'Sku' property called 'sku'. In
     * this scenario, the <b>skuPropertyPrefix</b> would be 'sku'.
     * 
     * @param skuPropertyPrefix
     */
    public SkuPropertiesFilterCriterionProvider(String skuPropertyPrefix) {
        if (StringUtils.isNotEmpty(skuPropertyPrefix) && !skuPropertyPrefix.endsWith(".")) {
            skuPropertyPrefix += ".";
        }
        this.skuPropertyPrefix = skuPropertyPrefix;
    }

    @Override
    public FilterCriterionProvider getLikeProvider(AssociationPath path, String propertyId) {
        if ((StringUtils.isNotEmpty(skuPropertyPrefix) && propertyId.startsWith(skuPropertyPrefix))
                || path.equals(AssociationPath.ROOT)) {
            return new SimpleFilterCriterionProvider(FilterDataStrategy.DIRECT, 1) {

                @Override
                public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
                    return buildCriterion(targetPropertyName,
                            Restrictions.ilike(targetPropertyName, (String) directValues[0], MatchMode.START),
                            Restrictions.ilike(DEFAULT_SKU_PATH_PREFIX + targetPropertyName, (String) directValues[0], MatchMode.START));

                }
            };
        } else {
            return super.getLikeProvider(path, propertyId);
        }
    }

    @Override
    public FilterCriterionProvider getEqProvider(AssociationPath path, String propertyId) {
        if ((StringUtils.isNotEmpty(skuPropertyPrefix) && propertyId.startsWith(skuPropertyPrefix))
                || path.equals(AssociationPath.ROOT)) {
            return new SimpleFilterCriterionProvider(FilterDataStrategy.NONE, 1) {
    
                @Override
                public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
                    return buildCriterion(targetPropertyName,
                            Restrictions.eq(targetPropertyName, directValues[0]),
                            Restrictions.eq(DEFAULT_SKU_PATH_PREFIX + targetPropertyName, directValues[0]));
                }
            };
        } else {
            return super.getEqProvider(path, propertyId);
        }

    }

    @Override
    public FilterCriterionProvider getIsNullProvider(AssociationPath path, String propertyId) {
        if ((StringUtils.isNotEmpty(skuPropertyPrefix) && propertyId.startsWith(skuPropertyPrefix))
                || path.equals(AssociationPath.ROOT)) {
            return new SimpleFilterCriterionProvider(FilterDataStrategy.NONE, 1) {
    
                @Override
                public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
                    return buildCriterion(targetPropertyName,
                            Restrictions.isNull(targetPropertyName),
                            Restrictions.isNull(DEFAULT_SKU_PATH_PREFIX + targetPropertyName));
                }
            };
        } else {
            return super.getIsNullProvider(path, propertyId);
        }

    }

    @Override
    public FilterCriterionProvider getLessThanOrEqualProvider(AssociationPath path, String propertyId) {
        if ((StringUtils.isNotEmpty(skuPropertyPrefix) && propertyId.startsWith(skuPropertyPrefix))
                || path.equals(AssociationPath.ROOT)) {
            return new SimpleFilterCriterionProvider(FilterDataStrategy.DIRECT, 1) {
    
                @Override
                public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
                    return buildCriterion(targetPropertyName,
                            Restrictions.le(targetPropertyName, directValues[0]),
                            Restrictions.le(DEFAULT_SKU_PATH_PREFIX + targetPropertyName, directValues[0]));
                }
            };
        } else {
            return super.getLessThanOrEqualProvider(path, propertyId);
        }
    }

    @Override
    public FilterCriterionProvider getBetweenProvider(AssociationPath path, String propertyId) {
        if ((StringUtils.isNotEmpty(skuPropertyPrefix) && propertyId.startsWith(skuPropertyPrefix))
                || path.equals(AssociationPath.ROOT)) {
            return new SimpleFilterCriterionProvider(FilterDataStrategy.NONE, 2) {
    
                @Override
                public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
                    if (directValues.length > 1) {
                        return buildCriterion(targetPropertyName,
                                Restrictions.between(targetPropertyName, directValues[0], directValues[1]),
                                Restrictions.between(DEFAULT_SKU_PATH_PREFIX + targetPropertyName, directValues[0], directValues[1]));
                    } else {
                        return buildCriterion(targetPropertyName,
                                Restrictions.eq(targetPropertyName, directValues[0]),
                                Restrictions.eq(DEFAULT_SKU_PATH_PREFIX + targetPropertyName, directValues[0]));
                    }
                }
            };
        } else {
            return super.getBetweenProvider(path, propertyId);
        }
    }

    @Override
    public FilterCriterionProvider getBetweenDateProvider(AssociationPath path, String propertyId) {
        if ((StringUtils.isNotEmpty(skuPropertyPrefix) && propertyId.startsWith(skuPropertyPrefix))
                || path.equals(AssociationPath.ROOT)) {
            return new SimpleFilterCriterionProvider(FilterDataStrategy.NONE, 2) {
    
                @Override
                public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
                    if (directValues.length > 2) {
                        return buildCriterion(targetPropertyName,
                                Restrictions.between(targetPropertyName, directValues[0], directValues[2]),
                                Restrictions.between(DEFAULT_SKU_PATH_PREFIX + targetPropertyName, directValues[0], directValues[2]));
                    } else if (directValues[0] == null) {
                        return buildCriterion(targetPropertyName,
                                Restrictions.lt(targetPropertyName, directValues[1]),
                                Restrictions.lt(DEFAULT_SKU_PATH_PREFIX + targetPropertyName, directValues[1]));
                    } else if (directValues[1] == null) {
                        return buildCriterion(targetPropertyName,
                                Restrictions.ge(targetPropertyName, directValues[0]),
                                Restrictions.ge(DEFAULT_SKU_PATH_PREFIX + targetPropertyName, directValues[0]));
                    } else {
                        return buildCriterion(targetPropertyName,
                                Restrictions.eq(targetPropertyName, directValues[0]),
                                Restrictions.eq(DEFAULT_SKU_PATH_PREFIX + targetPropertyName, directValues[0]));
                    }
                }
            };
        } else {
            return super.getBetweenDateProvider(path, propertyId);
        }
    }

    @Override
    public FilterCriterionProvider getCollectionSizeEqualsProvider(AssociationPath path, String propertyId) {
        if ((StringUtils.isNotEmpty(skuPropertyPrefix) && propertyId.startsWith(skuPropertyPrefix))
                || path.equals(AssociationPath.ROOT)) {
            return new SimpleFilterCriterionProvider(FilterDataStrategy.DIRECT, 1) {
    
                @Override
                public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
                    return buildCriterion(targetPropertyName,
                            Restrictions.sizeEq(targetPropertyName, (Integer) directValues[0]),
                            Restrictions.sizeEq(DEFAULT_SKU_PATH_PREFIX + targetPropertyName, (Integer) directValues[0]));
                }
            };
        } else {
            return super.getCollectionSizeEqualsProvider(path, propertyId);
        }
    }

    /**
     * @param targetPropertyName
     * @param propertyCriterion
     * @param defaultSkuCriterion
     * @return
     */
    protected Criterion buildCriterion(String targetPropertyName, Criterion propertyCriterion, Criterion defaultSkuCriterion) {
        return Restrictions.or(
                Restrictions.or(
                        Restrictions.and(Restrictions.isNotNull(targetPropertyName), propertyCriterion),
                        Restrictions.and(
                                Restrictions.and(
                                        Restrictions.isNull(targetPropertyName),
                                        Restrictions.isNotNull("product")),
                                defaultSkuCriterion
                                )
                        ),
                Restrictions.and(Restrictions.isNull("product"), propertyCriterion));
    }

}
