package org.broadleafcommerce.core.search.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.core.search.domain.SearchFacetImpl;
import org.broadleafcommerce.core.search.domain.SearchField;
import org.broadleafcommerce.core.search.domain.SearchFieldImpl;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @author Nick Crum (ncrum)
 */
@Repository("blSearchFieldDao")
public class SearchFieldDaoImpl implements SearchFieldDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public SearchField readSearchFieldForField(Field field) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<SearchField> criteria = builder.createQuery(SearchField.class);

        Root<SearchFieldImpl> search = criteria.from(SearchFieldImpl.class);

        criteria.select(search);
        criteria.where(
                builder.equal(search.join("field").get("id").as(Long.class), field.getId())
        );

        TypedQuery<SearchField> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
