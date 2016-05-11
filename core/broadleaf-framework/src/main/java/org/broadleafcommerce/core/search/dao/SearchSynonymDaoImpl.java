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
package org.broadleafcommerce.core.search.dao;

import org.broadleafcommerce.core.search.domain.SearchSynonym;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

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
