package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

@Repository("blProductOptionValueDao")
public class ProductOptionValueDaoImpl implements ProductOptionValueDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public Sku readSkuForProductOptionsAndValues(Long productId, Map<String, String> attributeNameValuePair) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Sku> criteriaQuery = criteriaBuilder.createQuery(Sku.class);
        Root<SkuImpl> skuRoot = criteriaQuery.from(SkuImpl.class);

        criteriaQuery.select(skuRoot);
        criteriaQuery.distinct(true);
        criteriaQuery.from(SkuImpl.class);

        List<Predicate> predicates = getPredicates(productId, attributeNameValuePair, criteriaBuilder, skuRoot);
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[]{})));

        Query query = em.createQuery(criteriaQuery);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        try {
            return (Sku) query.getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    @Override
    public List<ProductOptionValue> readMatchingProductOptionsForValues(Long productId, Map<String, String> attributeNameValuePair) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ProductOptionValue> criteriaQuery = criteriaBuilder.createQuery(ProductOptionValue.class);
        Root<SkuImpl> skuRoot = criteriaQuery.from(SkuImpl.class);

        criteriaQuery.select((Selection)skuRoot.get("productOptionValues"));
        criteriaQuery.distinct(true);
        criteriaQuery.from(SkuImpl.class);

        List<Predicate> predicates = getPredicates(productId, attributeNameValuePair, criteriaBuilder, skuRoot);
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));

        Query query = em.createQuery(criteriaQuery);

        return query.getResultList();
    }

    private List<Predicate> getPredicates(Long productId, Map<String, String> attributeNameValuePair,
                                          CriteriaBuilder criteriaBuilder, Root<SkuImpl> skuRoot) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        Join<Sku, Product> skuProduct = skuRoot.join("product");
        predicates.add(criteriaBuilder.equal(skuProduct.get("id"), productId));

        for (String attributeName : attributeNameValuePair.keySet()) {

            Join<Sku, ProductOptionValue> skuProductOptionValue = skuRoot.join("productOptionValues");
            Join<ProductOptionValue, ProductOption> productOptionValueProductOption = skuProductOptionValue.join("productOption");

            predicates.add(criteriaBuilder.equal(skuProductOptionValue.get("attributeValue"), attributeNameValuePair.get(attributeName)));
            predicates.add(criteriaBuilder.equal(productOptionValueProductOption.get("attributeName"), attributeName));

        }
        return predicates;
    }

}
