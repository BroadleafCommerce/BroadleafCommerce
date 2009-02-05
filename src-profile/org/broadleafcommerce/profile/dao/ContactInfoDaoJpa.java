package org.broadleafcommerce.profile.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.springframework.stereotype.Repository;

@Repository("contactInfoDao")
public class ContactInfoDaoJpa implements ContactInfoDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext
    private EntityManager em;


    @Override
    public ContactInfo maintainContactInfo(ContactInfo contactInfo) {
        if (contactInfo.getId() == null) {
            em.persist(contactInfo);
        } else {
            contactInfo = em.merge(contactInfo);
        }
        return contactInfo;
    }

    @SuppressWarnings("unchecked")
    public List<ContactInfo> readContactInfoByUserId(Long userId) {
        Query query = em.createQuery("SELECT contactInfo FROM org.broadleafcommerce.profile.domain.ContactInfo contactInfo WHERE contactInfo.user.id = :userId");
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public ContactInfo readContactInfoById(Long contactId){
        return em.find(ContactInfo.class, contactId);
    }
}
