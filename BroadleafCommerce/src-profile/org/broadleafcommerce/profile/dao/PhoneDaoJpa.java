package org.broadleafcommerce.profile.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    public Phone readPhoneById(Long phoneId) {
        return (Phone) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.Phone"), phoneId);
    }
}
