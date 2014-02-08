/*
 * #%L
 * BroadleafCommerce Custom Field
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */

package com.broadleafcommerce.customfield.dao;

import com.broadleafcommerce.customfield.domain.CustomField;
import com.broadleafcommerce.customfield.domain.CustomFieldImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository("blCustomFieldDao")
public class CustomFieldDaoImpl implements CustomFieldDao {

    private static final Log LOG = LogFactory.getLog(CustomFieldDaoImpl.class);

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Override
    public CustomField retrieveById(Long id) {
       return em.find(CustomFieldImpl.class, id);
    }

    @Override
    public List<CustomField> retrieveByTargetEntityName(String targetEntityName) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<CustomField> criteria = builder.createQuery(CustomField.class);
        Root<CustomFieldImpl> customField = criteria.from(CustomFieldImpl.class);
        criteria.select(customField);
        criteria.where(builder.equal(customField.get("customFieldTarget"), targetEntityName));
        TypedQuery<CustomField> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        return em.createQuery(criteria).getResultList();
    }
}
