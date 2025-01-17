/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.exception.NoPossibleResultsException;
import org.broadleafcommerce.openadmin.dto.ClassTree;
import org.broadleafcommerce.openadmin.dto.SortDirection;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.broadleafcommerce.openadmin.server.security.service.RowLevelSecurityService;
import org.broadleafcommerce.openadmin.server.service.persistence.module.EmptyFilterValues;
import org.hibernate.type.AbstractStandardBasicType;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * @author Jeff Fischer
 */
@Service("blCriteriaTranslator")
public class CriteriaTranslatorImpl implements CriteriaTranslator {

    @Resource(name = "blCriteriaTranslatorEventHandlers")
    protected List<CriteriaTranslatorEventHandler> eventHandlers = new ArrayList<>();

    @Resource(name = "blRowLevelSecurityService")
    protected RowLevelSecurityService rowSecurityService;

    @Resource(name = "blAdminSecurityRemoteService")
    protected SecurityVerifier adminSecurityService;

    @Override
    public TypedQuery<Serializable> translateCountQuery(
            DynamicEntityDao dynamicEntityDao,
            String ceilingEntity,
            List<FilterMapping> filterMappings
    ) {
        CriteriaBuilder criteriaBuilder = dynamicEntityDao.getStandardEntityManager().getCriteriaBuilder();
        Class<Serializable> ceilingMarker = this.ceilingMarker(ceilingEntity, filterMappings);
        Class<Serializable> resultType = this.classByName(Long.class.getName());
        CriteriaQuery<Serializable> criteria = criteriaBuilder.createQuery(resultType);
        Class<Serializable> ceilingClass = this.determineRoot(dynamicEntityDao, ceilingMarker, filterMappings);
        Root<Serializable> original = criteria.from(ceilingClass);
        criteria.select(criteriaBuilder.count(original));
        List<Predicate> restrictions = this.restrictions(
                ceilingEntity, filterMappings, criteriaBuilder, original, new ArrayList<>(), criteria
        );
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        return dynamicEntityDao.getStandardEntityManager().createQuery(criteria);
    }

    @Override
    public TypedQuery<Serializable> translateMaxQuery(
            DynamicEntityDao dynamicEntityDao,
            String ceilingEntity,
            List<FilterMapping> filterMappings,
            String maxField
    ) {
        CriteriaBuilder criteriaBuilder = dynamicEntityDao.getStandardEntityManager().getCriteriaBuilder();
        Class<Serializable> ceilingMarker = this.ceilingMarker(ceilingEntity, filterMappings);
        Class<Serializable> resultType = this.classByName(BigDecimal.class.getName());
        CriteriaQuery<Serializable> criteria = criteriaBuilder.createQuery(resultType);
        Class<Serializable> ceilingClass = this.determineRoot(dynamicEntityDao, ceilingMarker, filterMappings);
        Root<Serializable> original = criteria.from(ceilingClass);
        criteria.select(criteriaBuilder.max((Path<Number>) ((Object) original.get(maxField))));
        List<Predicate> restrictions = this.restrictions(
                ceilingEntity, filterMappings, criteriaBuilder, original, new ArrayList<>(), criteria
        );
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        return dynamicEntityDao.getStandardEntityManager().createQuery(criteria);
    }

    @Override
    public TypedQuery<Serializable> translateQuery(
            DynamicEntityDao dynamicEntityDao,
            String ceilingEntity,
            List<FilterMapping> filterMappings,
            Integer firstResult,
            Integer maxResults
    ) {
        CriteriaBuilder criteriaBuilder = dynamicEntityDao.getStandardEntityManager().getCriteriaBuilder();
        Class<Serializable> ceilingMarker = this.ceilingMarker(ceilingEntity, filterMappings);
        CriteriaQuery<Serializable> criteria = criteriaBuilder.createQuery(ceilingMarker);
        Class<Serializable> ceilingClass = this.determineRoot(dynamicEntityDao, ceilingMarker, filterMappings);
        Root<Serializable> original = criteria.from(ceilingClass);
        criteria.select(original);
        List<Order> sorts = new ArrayList<>();
        List<Predicate> restrictions = this.restrictions(
                ceilingEntity, filterMappings, criteriaBuilder, original, sorts, criteria
        );
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        criteria.orderBy(sorts.toArray(new Order[sorts.size()]));

        //If someone provides a firstResult value, then there is generally pagination going on.
        //In order to produce consistent results, especially with certain databases such as PostgreSQL,
        //there has to be an "order by" clause.  We'll add one here if we can.
        if (firstResult != null && sorts.isEmpty()) {
            Map<String, Object> idMetaData = dynamicEntityDao.getIdMetadata(ceilingClass);
            if (idMetaData != null) {
                Object idFldName = idMetaData.get("name");
                Object type = idMetaData.get("type");
                if ((idFldName instanceof String) && (type instanceof AbstractStandardBasicType)) {
                    criteria.orderBy(criteriaBuilder.asc(original.get((String) idFldName)));
                }
            }
        }

        TypedQuery<Serializable> response = dynamicEntityDao.getStandardEntityManager().createQuery(criteria);
        addPaging(response, firstResult, maxResults);
        return response;
    }

    /**
     * Determines the appropriate entity in this current class tree to use as the ceiling entity for the query. Because
     * we filter with AND instead of OR, we throw an exception if an attempt to utilize properties from mutually exclusive
     * class trees is made as it would be impossible for such a query to return results.
     *
     * @param dynamicEntityDao
     * @param ceilingMarker
     * @param filterMappings
     * @return the root class
     * @throws NoPossibleResultsException
     */
    @SuppressWarnings("unchecked")
    protected Class<Serializable> determineRoot(
            DynamicEntityDao dynamicEntityDao,
            Class<Serializable> ceilingMarker,
            List<FilterMapping> filterMappings
    ) throws NoPossibleResultsException {

        Class<?>[] polyEntities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(ceilingMarker);
        ClassTree root = dynamicEntityDao.getClassTree(polyEntities);

        List<ClassTree> parents = new ArrayList<>();
        for (FilterMapping mapping : filterMappings) {
            if (mapping.getInheritedFromClass() != null) {
                root = determineRootInternal(root, parents, mapping.getInheritedFromClass());
                if (root == null) {
                    throw new NoPossibleResultsException("AND filter on different class hierarchies produces no results");
                }
            }
        }

        for (Class<?> clazz : polyEntities) {
            if (clazz.getName().equals(root.getFullyQualifiedClassname())) {
                return (Class<Serializable>) clazz;
            }
        }

        throw new IllegalStateException("Class didn't match - this should not occur");
    }

    /**
     * Because of the restriction described in {@link #determineRoot(DynamicEntityDao, Class, List)}, we must check
     * that a class lies inside of the same tree as the current known root. Consider the following situation:
     * <p>
     * Class C extends Class B, which extends Class A.
     * Class E extends Class D, which also extends Class A.
     * <p>
     * We can allow filtering on properties that are either all in C/B/A or all in E/D/A. Filtering on properties across
     * C/B and E/D will always produce no results given an AND style of joining the filtered properties.
     *
     * @param root
     * @param parents
     * @param classToCheck
     * @return the (potentially new) root or null if invalid
     */
    protected ClassTree determineRootInternal(ClassTree root, List<ClassTree> parents, Class<?> classToCheck) {
        // If the class to check is the current root or a parent of this root, we will continue to use the same root
        if (root.getFullyQualifiedClassname().equals(classToCheck.getName())) {
            return root;
        }
        for (ClassTree parent : parents) {
            if (parent.getFullyQualifiedClassname().equals(classToCheck.getName())) {
                return root;
            }
        }
        try {
            Class<?> rootClass = Class.forName(root.getFullyQualifiedClassname());
            if (classToCheck.isAssignableFrom(rootClass)) {
                return root;
            }
        } catch (ClassNotFoundException e) {
            // Do nothing - we'll continue searching
        }

        parents.add(root);

        for (ClassTree child : root.getChildren()) {
            ClassTree result = child.find(classToCheck.getName());
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    protected Class<Serializable> ceilingMarker(String ceilingEntity, List<FilterMapping> filterMappings) {
        Class<Serializable> ceilingMarker = this.classByName(ceilingEntity);

        Class<Serializable> securityRoot = rowSecurityService.getFetchRestrictionRoot(
                adminSecurityService.getPersistentAdminUser(),
                ceilingMarker,
                filterMappings
        );
        if (securityRoot != null) {
            ceilingMarker = securityRoot;
        }
        return ceilingMarker;
    }

    protected Class<Serializable> classByName(String className) {
        Class<Serializable> resultType;
        try {
            resultType = (Class<Serializable>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return resultType;
    }

    protected void addPaging(Query response, Integer firstResult, Integer maxResults) {
        if (firstResult != null) {
            response.setFirstResult(firstResult);
        }
        if (maxResults != null) {
            response.setMaxResults(maxResults);
        }
    }

    protected List<Predicate> restrictions(
            String ceilingEntity,
            List<FilterMapping> filterMappings,
            CriteriaBuilder criteriaBuilder,
            Root<?> original,
            List<Order> sorts,
            CriteriaQuery<?> criteria
    ) {
        List<Predicate> restrictions = new ArrayList<>();

        filterMappings.sort(new FilterMapping.ComparatorByOrder());

        for (FilterMapping filterMapping : filterMappings) {
            Path<?> explicitPath = this.explicitPath(filterMapping, criteriaBuilder, original);

            if (filterMapping.getRestriction() != null) {
                List<?> directValues = null;
                boolean shouldConvert = true;
                if (CollectionUtils.isNotEmpty(filterMapping.getFilterValues())) {
                    directValues = filterMapping.getFilterValues();
                } else {
                    if (CollectionUtils.isNotEmpty(filterMapping.getDirectFilterValues())
                            || (filterMapping.getDirectFilterValues() != null
                            && filterMapping.getDirectFilterValues() instanceof EmptyFilterValues)) {
                        directValues = filterMapping.getDirectFilterValues();
                        shouldConvert = false;
                    }
                }
                if (directValues != null) {
                    Predicate predicate = filterMapping.getRestriction()
                            .buildRestriction(
                                    criteriaBuilder, original, ceilingEntity, filterMapping.getFullPropertyName(),
                                    explicitPath, directValues, shouldConvert, criteria, restrictions
                            );
                    if (predicate != null) {
                        restrictions.add(predicate);
                    }
                }
            }

            if (filterMapping.getSortDirection() != null) {
                Path<?> sortPath = explicitPath;
                if (sortPath == null && !StringUtils.isEmpty(filterMapping.getFullPropertyName())) {
                    FieldPathBuilder fieldPathBuilder = filterMapping.getRestriction().getFieldPathBuilder();
                    fieldPathBuilder.setCriteria(criteria);
                    fieldPathBuilder.setRestrictions(restrictions);
                    sortPath = filterMapping.getRestriction()
                            .getFieldPathBuilder()
                            .getPath(original, filterMapping.getFullPropertyName(), criteriaBuilder);
                }
                if (sortPath != null) {
                    this.addSorting(criteriaBuilder, sorts, filterMapping, sortPath);
                }
            }
        }

        // add in the row-level security handlers to this as well
        rowSecurityService.addFetchRestrictions(
                adminSecurityService.getPersistentAdminUser(),
                ceilingEntity,
                restrictions,
                sorts,
                original,
                criteria,
                criteriaBuilder
        );

        for (CriteriaTranslatorEventHandler eventHandler : eventHandlers) {
            eventHandler.addRestrictions(
                    ceilingEntity, filterMappings, criteriaBuilder, original, restrictions, sorts, criteria
            );
        }
        return restrictions;
    }

    protected Path<?> explicitPath(FilterMapping filterMapping, CriteriaBuilder criteriaBuilder, Root<?> original) {
        Path<?> explicitPath = null;
        if (filterMapping.getFieldPath() != null) {
            explicitPath = filterMapping.getRestriction()
                    .getFieldPathBuilder()
                    .getPath(original, filterMapping.getFieldPath(), criteriaBuilder);
        }
        return explicitPath;
    }

    protected void addSorting(
            CriteriaBuilder criteriaBuilder,
            List<Order> sorts,
            FilterMapping filterMapping,
            Path<?> path
    ) {
        Expression<?> exp = path;
        if (filterMapping.getNullsLast() != null && filterMapping.getNullsLast()) {
            Object largeValue = getAppropriateLargeSortingValue(path.getJavaType());
            if (largeValue != null) {
                exp = criteriaBuilder.coalesce(path, largeValue);
            }
        }
        if (SortDirection.ASCENDING == filterMapping.getSortDirection()) {
            sorts.add(criteriaBuilder.asc(exp));
        } else {
            sorts.add(criteriaBuilder.desc(exp));
        }
    }

    protected Object getAppropriateLargeSortingValue(Class<?> javaType) {
        Object response = null;
        if (Date.class.isAssignableFrom(javaType)) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 500);
            response = calendar.getTime();
        } else if (Long.class.isAssignableFrom(javaType)) {
            response = Long.MAX_VALUE;
        } else if (Integer.class.isAssignableFrom(javaType)) {
            response = Integer.MAX_VALUE;
        } else if (BigDecimal.class.isAssignableFrom(javaType)) {
            response = new BigDecimal(String.valueOf(Long.MAX_VALUE));
        }
        return response;
    }

}
