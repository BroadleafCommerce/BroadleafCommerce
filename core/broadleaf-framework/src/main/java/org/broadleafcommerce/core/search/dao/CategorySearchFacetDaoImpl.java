package org.broadleafcommerce.core.search.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.search.domain.CategorySearchFacet;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author Nathan Moore (nathandmoore)
 */
@Repository("blCategorySearchFacetDao")
public class CategorySearchFacetDaoImpl implements CategorySearchFacetDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public List<CategorySearchFacet> readCategorySearchFacetsBySearchFacet(Long id) {

        TypedQuery<CategorySearchFacet> query = em.createNamedQuery("BC_READ_DISTINCT_CATEGORY_SEARCH_FACETS_BY_SEARCH_FACET", CategorySearchFacet.class);
        query.setParameter("searchFacetId", id);

        query.setHint(QueryHints.HINT_CACHEABLE, true);

        return query.getResultList();
    }

}
