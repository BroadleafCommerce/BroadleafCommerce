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

package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.openadmin.dto.SortDirection;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author Jeff Fischer
 */
@Service("blCriteriaTranslator")
public class CriteriaTranslatorImpl implements CriteriaTranslator {

    @Override
    public TypedQuery<Serializable> translateCountQuery(DynamicEntityDao dynamicEntityDao, String ceilingEntity, List<FilterMapping> filterMappings) {
        return constructQuery(dynamicEntityDao, ceilingEntity, filterMappings, true, null, null);
    }

    @Override
    public TypedQuery<Serializable> translateQuery(DynamicEntityDao dynamicEntityDao, String ceilingEntity, List<FilterMapping> filterMappings, Integer firstResult, Integer maxResults) {
        return constructQuery(dynamicEntityDao, ceilingEntity, filterMappings, false, firstResult, maxResults);
    }

    protected TypedQuery<Serializable> constructQuery(DynamicEntityDao dynamicEntityDao, String ceilingEntity, List<FilterMapping> filterMappings, boolean isCount, Integer firstResult, Integer maxResults) {
        CriteriaBuilder criteriaBuilder = dynamicEntityDao.getStandardEntityManager().getCriteriaBuilder();
        Class<Serializable> ceilingMarker;
        try {
            ceilingMarker = (Class<Serializable>) Class.forName(ceilingEntity);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Class<?>[] polyEntities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(ceilingMarker);
        Class<Serializable> ceilingClass = (Class<Serializable>) polyEntities[polyEntities.length-1];

        CriteriaQuery<Serializable> criteria = criteriaBuilder.createQuery(ceilingMarker);
        Root<Serializable> original = criteria.from(ceilingClass);
        if (isCount) {
            criteria.select(criteriaBuilder.count(original));
        } else {
            criteria.select(original);
        }

        List<Predicate> restrictions = new ArrayList<Predicate>();
        List<Order> sorts = new ArrayList<Order>();
        addRestrictions(ceilingEntity, filterMappings, criteriaBuilder, original, restrictions, sorts);

        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        if (!isCount) {
            criteria.orderBy(sorts.toArray(new Order[sorts.size()]));
        }
        TypedQuery<Serializable> response = dynamicEntityDao.getStandardEntityManager().createQuery(criteria);

        if (!isCount) {
            addPaging(response, firstResult, maxResults);
        }

        return response;
    }

    protected void addPaging(Query response, Integer firstResult, Integer maxResults) {
        if (firstResult != null) {
            response.setFirstResult(firstResult);
        }
        if (maxResults != null) {
            response.setMaxResults(maxResults);
        }
    }

    protected void addRestrictions(String ceilingEntity, List<FilterMapping> filterMappings, CriteriaBuilder criteriaBuilder,
                                   Root original, List<Predicate> restrictions, List<Order> sorts) {
        for (FilterMapping filterMapping : filterMappings) {
            Path explicitPath = null;
            if (filterMapping.getFieldPath() != null) {
                explicitPath = filterMapping.getRestriction().getFieldPathBuilder().getPath(original, filterMapping.getFieldPath(), criteriaBuilder);
            }

            if (filterMapping.getRestriction() != null) {
                List directValues = null;
                boolean shouldConvert = true;
                if (CollectionUtils.isNotEmpty(filterMapping.getFilterValues())) {
                    directValues = filterMapping.getFilterValues();
                } else if (CollectionUtils.isNotEmpty(filterMapping.getDirectFilterValues())) {
                    directValues = filterMapping.getDirectFilterValues();
                    shouldConvert = false;
                }
                
                if (directValues != null) {
                    Predicate predicate = filterMapping.getRestriction().buildRestriction(criteriaBuilder, original,
                            ceilingEntity, filterMapping.getFullPropertyName(), explicitPath, directValues, shouldConvert);
                    restrictions.add(predicate);
                }
            }

            if (filterMapping.getSortDirection() != null) {
                Path sortPath = explicitPath;
                if (sortPath == null && !StringUtils.isEmpty(filterMapping.getFullPropertyName())) {
                    sortPath = filterMapping.getRestriction().getFieldPathBuilder().getPath(original, filterMapping.getFullPropertyName(), criteriaBuilder);
                }
                if (sortPath != null) {
                    addSorting(criteriaBuilder, sorts, filterMapping, sortPath);
                }
            }
        }
    }

    protected void addSorting(CriteriaBuilder criteriaBuilder, List<Order> sorts, FilterMapping filterMapping, Path path) {
        if (SortDirection.ASCENDING == filterMapping.getSortDirection()) {
            sorts.add(criteriaBuilder.asc(path));
        } else {
            sorts.add(criteriaBuilder.desc(path));
        }
    }

}
