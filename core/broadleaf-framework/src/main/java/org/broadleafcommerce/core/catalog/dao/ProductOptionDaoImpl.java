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

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionImpl;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValueImpl;
import org.broadleafcommerce.core.catalog.domain.SkuProductOptionValueXrefImpl;
import org.broadleafcommerce.core.catalog.domain.dto.AssignedProductOptionDTO;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Repository("blProductOptionDao")
public class ProductOptionDaoImpl implements ProductOptionDao {
    
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
        List<AssignedProductOptionDTO> results = new ArrayList<AssignedProductOptionDTO>();
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

        List<Predicate> restrictions = new ArrayList<Predicate>();
        List<Long> mergedIds = sandBoxHelper.mergeCloneIds(ProductOptionImpl.class, productOptionId);
        restrictions.add(root.get("productOption").in(mergedIds));
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

        TypedQuery<Long> query = em.createQuery(criteria);
        return query.getSingleResult();
    }

}
