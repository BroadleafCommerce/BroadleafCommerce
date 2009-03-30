package org.broadleafcommerce.profile.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Phone;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("phoneDao")
public class PhoneDaoJpa implements PhoneDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    public Phone maintainPhone(Phone phone) {
        if (phone.getId() == null) {
            em.persist(phone);
        } else {
            phone = em.merge(phone);
        }
        return phone;
    }

    @SuppressWarnings("unchecked")
    public List<Phone> readPhoneByUserId(Long customerId) {
        Query query = em.createNamedQuery("BC_READ_PHONE_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public Phone readPhoneById(Long phoneId) {
        return (Phone) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.Phone"), phoneId);
    }
}
