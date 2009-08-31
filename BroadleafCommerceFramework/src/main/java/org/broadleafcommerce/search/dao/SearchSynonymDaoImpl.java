package org.broadleafcommerce.search.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.search.domain.SearchSynonym;
import org.springframework.stereotype.Repository;

@Repository("blSearchSynonymDao")
public class SearchSynonymDaoImpl implements SearchSynonymDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @SuppressWarnings("unchecked")
    public List<SearchSynonym> getAllSynonyms() {
        Query query = em.createNamedQuery("BC_READ_SEARCH_SYNONYMS");
        List<SearchSynonym> result;
        try {
            result = (List<SearchSynonym>) query.getResultList();
        } catch (NoResultException e) {
            result = null;
        }
        return result;
    }

    public void createSynonym(SearchSynonym synonym) {
        em.persist(synonym);
    }

    public void deleteSynonym(SearchSynonym synonym) {
        em.remove(synonym);
    }

    public void updateSynonym(SearchSynonym synonym) {
        em.merge(synonym);
    }
}
