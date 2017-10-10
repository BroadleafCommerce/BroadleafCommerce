/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.catalog.dao;

import org.apache.commons.collections4.CollectionUtils;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionImpl;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValueImpl;
import org.broadleafcommerce.core.catalog.domain.ProductOptionXref;
import org.broadleafcommerce.core.catalog.domain.ProductOptionXrefImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.domain.SkuProductOptionValueXrefImpl;
import org.broadleafcommerce.core.catalog.domain.dto.AssignedProductOptionDTO;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jpa.QueryHints;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Repository("blProductOptionDao")
public class ProductOptionDaoImpl implements ProductOptionDao {

    private static final int IN_CLAUSE_LIMIT = 999;

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name="blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;
    
    @Override
    public List<ProductOption> readAllProductOptions() {
        TypedQuery<ProductOption> query = em.createNamedQuery("BC_READ_ALL_PRODUCT_OPTIONS", ProductOption.class);
        return query.getResultList();
    }
    
    @Override
    public ProductOption saveProductOption(ProductOption option) {
        return em.merge(option);
    }

    @Override
    public ProductOption readProductOptionById(Long id) {
        return em.find(ProductOptionImpl.class, id);
    }

    @Override
    public ProductOptionValue readProductOptionValueById(Long id) {
        return em.find(ProductOptionValueImpl.class, id);
    }

    @Override
    public List<AssignedProductOptionDTO> findAssignedProductOptionsByProductId(Long productId) {
        Session session = em.unwrap(Session.class);
        Criteria criteria = session.createCriteria(SkuProductOptionValueXrefImpl.class);
        List dtoList = criteria
            .createAlias("sku", "sku")
            .createAlias("sku.product", "product")
            .createAlias("productOptionValue", "productOptionValue")
            .createAlias("productOptionValue.productOption", "productOption")
            .setProjection(Projections.distinct(
                    Projections.projectionList()
                    .add(Projections.property("product.id"), "productId")
                    .add(Projections.property("productOption.attributeName"), "productOptionAttrName")
                    .add(Projections.property("productOptionValue"), "productOptionValue")
                    .add(Projections.property("sku"), "sku")
                )
            ).setResultTransformer(Transformers.aliasToBean(AssignedProductOptionDTO.class))
            .add(Restrictions.eq("product.id", productId))
            .addOrder(Order.asc("productOption.attributeName")).list();
        List<AssignedProductOptionDTO> results = new ArrayList<>();
        for (Object o : dtoList) {
            AssignedProductOptionDTO dto = (AssignedProductOptionDTO) o;
            if (dto.getSku().isActive()) {
                results.add(dto);
            }
        }
        return results;
    }

    @Override
    public List<AssignedProductOptionDTO> findAssignedProductOptionsByProduct(Product product) {
        return findAssignedProductOptionsByProductId(product.getId());
    }

    @Override
    public Long countAllowedValuesForProductOptionById(Long productOptionId) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<ProductOptionValueImpl> root = criteria.from(ProductOptionValueImpl.class);
        criteria.select(builder.count(root));

        List<Predicate> restrictions = new ArrayList<>();
        List<Long> mergedIds = sandBoxHelper.mergeCloneIds(ProductOptionImpl.class, productOptionId);
        restrictions.add(root.get("productOption").in(mergedIds));
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

        TypedQuery<Long> query = em.createQuery(criteria);
        return query.getSingleResult();
    }

    @Override
    public List<Long> readSkuIdsForProductOptionValues(Long productId, String attributeName, String attributeValue, List<Long> possibleSkuIds) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Sku> criteria = cb.createQuery(Sku.class);
        Root<SkuProductOptionValueXrefImpl> root = criteria.from(SkuProductOptionValueXrefImpl.class);
        criteria.select(root.get("sku").as(Sku.class));

        List<Predicate> predicates = new ArrayList<>();

        // restrict to skus that match the product
        predicates.add(root.get("sku").get("product").get("id").in(sandBoxHelper.mergeCloneIds(ProductImpl.class, productId)));

        // restrict to skus that match the attributeName
        predicates.add(cb.equal(root.get("productOptionValue").get("productOption").get("attributeName"), attributeName));

        // restrict to skus that match the attributeValue
        predicates.add(cb.equal(root.get("productOptionValue").get("attributeValue"), attributeValue));

        // restrict to skus that have ids within the given list of skus ids
        if (CollectionUtils.isNotEmpty(possibleSkuIds)) {
            possibleSkuIds = sandBoxHelper.mergeCloneIds(SkuImpl.class, possibleSkuIds.toArray(new Long[possibleSkuIds.size()]));
            Predicate skuDomainPredicate = buildSkuDomainPredicate(cb, root.get("sku").get("id"), possibleSkuIds);
            if (skuDomainPredicate != null) {
                predicates.add(skuDomainPredicate);
            }
        }

        // restrict archived values
        attachArchivalConditionIfPossible(SkuProductOptionValueXrefImpl.class, root, cb, predicates);
        attachArchivalConditionIfPossible(SkuImpl.class, root.get("sku"), cb, predicates);

        criteria.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

        TypedQuery<Sku> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");
        List<Sku> candidateSkus = query.getResultList();

        return filterCandidateSkusForArchivedStatus(candidateSkus);
    }

    protected List<Long> filterCandidateSkusForArchivedStatus(final List<Sku> candidateSkus) {
        final List<Long> validCandidateSkuIds = new ArrayList<>();

        for (final Sku sku : candidateSkus) {
            if (Status.class.isAssignableFrom(sku.getClass())) {
                if (!Objects.equals(((Status) sku).getArchived(), 'Y')) {
                    validCandidateSkuIds.add(sku.getId());
                }
            } else {
                // if Sku doesn't implement Status from bytecode weaving, assume it's non-archived
                validCandidateSkuIds.add(sku.getId());
            }
        }

        return validCandidateSkuIds;
    }

    protected void attachArchivalConditionIfPossible(Class<?> clazz, Path<?> path, CriteriaBuilder cb, List<Predicate> predicates) {
        if (Status.class.isAssignableFrom(clazz)) {
            predicates.add(
                cb.or(
                    cb.isNull(path.get("archiveStatus").get("archived")),
                    cb.equal(path.get("archiveStatus").get("archived"), 'N')
                )
            );
        }
    }

    @SuppressWarnings("unchecked")
    protected Predicate buildSkuDomainPredicate(CriteriaBuilder cb, Path fieldName, List<Long> possibleSkuIds) {
        int listSize = possibleSkuIds.size();
        Predicate predicate = null;
        for (int i = 0; i < listSize; i += IN_CLAUSE_LIMIT) {
            List subList;
            if (listSize > i + IN_CLAUSE_LIMIT) {
                subList = possibleSkuIds.subList(i, (i + IN_CLAUSE_LIMIT));
            } else {
                subList = possibleSkuIds.subList(i, listSize);
            }
            if (predicate == null) {
                predicate = fieldName.in(subList);
            } else {
                predicate = cb.or(predicate, fieldName.in(subList));
            }
        }
        return predicate;
    }

    @Override
    public Long countProductsUsingProductOptionById(Long productOptionId) {
        TypedQuery<Long> query = getProductIdsUsingProductOptionByIdQuery(productOptionId, true);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getSingleResult();
    }

    @Override
    public List<Long> findProductIdsUsingProductOptionById(Long productOptionId, int start, int pageSize) {
        TypedQuery<Long> query = getProductIdsUsingProductOptionByIdQuery(productOptionId, false);
        query.setFirstResult(start);
        query.setMaxResults(pageSize);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();

    }

    private TypedQuery<Long> getProductIdsUsingProductOptionByIdQuery(Long productOptionId, boolean count) {
        // Set up the criteria query that specifies we want to return Products
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

        // The root of our search is ProductOptionXref
        Root<ProductOptionXrefImpl> productOptionXref = criteria.from(ProductOptionXrefImpl.class);
        Join<ProductOptionXref, Product> product = productOptionXref.join("product");
        Join<ProductOptionXref, ProductOption> productOption = productOptionXref.join("productOption");

        if (count) {
            criteria.select(builder.count(product));
        } else {
            // Product IDs are what we want back
            criteria.select(product.get("id").as(Long.class));
        }
        criteria.distinct(true);

        List<Predicate> restrictions = new ArrayList<Predicate>();
        restrictions.add(productOption.get("id").in(sandBoxHelper.mergeCloneIds(ProductOptionImpl.class, productOptionId)));

        // Execute the query with the restrictions
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        return em.createQuery(criteria);
    }

}
