package org.broadleafcommerce.profile.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("contactInfoDao")
public class ContactInfoDaoJpa implements ContactInfoDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    public ContactInfo maintainContactInfo(ContactInfo contactInfo) {
        if (contactInfo.getId() == null) {
            em.persist(contactInfo);
        } else {
            contactInfo = em.merge(contactInfo);
        }
        return contactInfo;
    }

    @SuppressWarnings("unchecked")
    public List<ContactInfo> readContactInfoByUserId(Long customerId) {
        Query query = em.createNamedQuery("READ_CONTACT_INFO_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public ContactInfo readContactInfoById(Long contactId) {
        return (ContactInfo) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.ContactInfo"), contactId);
    }
}
