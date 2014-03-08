/*
 * Broadleaf Commerce Confidential
 * _______________________________
 *
 * [2009] - [2013] Broadleaf Commerce, LLC
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 */
package org.broadleafcommerce.core.search.dao;

import org.springframework.stereotype.Repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * @see org.broadleafcommerce.core.search.dao.SolrIndexDao
 * @author Jeff Fischer
 */
@Repository("blSolrIndexDao")
public class SolrIndexDaoImpl implements SolrIndexDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Override
    public List<Long> readProductIdsByCategory(Long categoryId) {
        TypedQuery<Long> query = em.createNamedQuery("BC_READ_PRODUCT_IDS_BY_CATEGORY", Long.class);
        query.setParameter("categoryId", categoryId);
        //don't cache query results - it's a waste of ehcache since we're caching this in another way at the caller

        return query.getResultList();
    }

}
