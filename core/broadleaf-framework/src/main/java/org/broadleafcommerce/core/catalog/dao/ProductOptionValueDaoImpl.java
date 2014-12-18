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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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
    public Map<ProductOption, Set<ProductOptionValue>> readApplicableProductOptionsAndValuesForProduct(
            Long productId) {
        TypedQuery<ProductOptionValue> query =
                em.createNamedQuery("BC_READ_ALL_PRODUCT_OPTION_VALUES_FOR_PRODUCT", ProductOptionValue.class);
        query.setParameter("productId", productId);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");
        List<ProductOptionValue> values = query.getResultList();
        HashMap<ProductOption, Set<ProductOptionValue>> out = new HashMap<ProductOption, Set<ProductOptionValue>>();
        if (values != null && !values.isEmpty()) {
            for (ProductOptionValue value : values) {
                if (!out.containsKey(value.getProductOption())) {
                    out.put(value.getProductOption(), new LinkedHashSet<ProductOptionValue>());
                }
                out.get(value.getProductOption()).add(value);
            }
        }

        return out;
    }

    @Override
    public Sku readSkuForProductOptionsAndValues(Long productId, Map<String, String> attributeNameValuePair) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Sku> criteriaQuery = criteriaBuilder.createQuery(Sku.class);
        Root<SkuImpl> skuRoot = criteriaQuery.from(SkuImpl.class);

        criteriaQuery.select(skuRoot);
        criteriaQuery.distinct(true);
        criteriaQuery.from(SkuImpl.class);

        Join<Sku, Product> skuProduct = skuRoot.join("product");

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.equal(skuProduct.get("id"), productId));

        for (String attributeName : attributeNameValuePair.keySet()) {

            Join<Sku, ProductOptionValue> skuProductOptionValue = skuRoot.join("productOptionValues");
            Join<ProductOptionValue, ProductOption> productOptionValueProductOption = skuProductOptionValue.join("productOption");

            predicates.add(criteriaBuilder.equal(skuProductOptionValue.get("attributeValue"), attributeNameValuePair.get(attributeName)));
            predicates.add(criteriaBuilder.equal(productOptionValueProductOption.get("attributeName"), attributeName));
        }

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));

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
    public List<ProductOptionValue> readMatchingProductOptionsForValues(Long productId, String attributeName,
                                                                        String attributeValue) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ProductOptionValue> criteriaQuery = criteriaBuilder.createQuery(ProductOptionValue.class);
        Root<SkuImpl> skuRoot = criteriaQuery.from(SkuImpl.class);

        criteriaQuery.select((Selection)skuRoot.get("productOptionValues"));
        criteriaQuery.distinct(true);
        criteriaQuery.from(SkuImpl.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        Join<Sku, Product> skuProduct = skuRoot.join("product");
        predicates.add(criteriaBuilder.equal(skuProduct.get("id"), productId));

        Join<Sku, ProductOptionValue> skuProductOptionValue = skuRoot.join("productOptionValues");
        Join<ProductOptionValue, ProductOption> productOptionValueProductOption = skuProductOptionValue.join("productOption");

        predicates.add(criteriaBuilder.equal(skuProductOptionValue.get("attributeValue"), attributeValue));
        predicates.add(criteriaBuilder.equal(productOptionValueProductOption.get("attributeName"), attributeName));

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));

        Query query = em.createQuery(criteriaQuery);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getResultList();
    }

}
