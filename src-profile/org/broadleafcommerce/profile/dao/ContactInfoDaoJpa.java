package org.broadleafcommerce.profile.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

public class ContactInfoDaoJpa implements ContactInfoDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private JpaTemplate jpaTemplate;

    private EntityConfiguration entityConfiguration;

    public ContactInfo maintainContactInfo(final ContactInfo contactInfo) {
        return (ContactInfo) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                ContactInfo retContactInfo = contactInfo;
                if (retContactInfo.getId() == null) {
                    em.persist(retContactInfo);
                } else {
                    retContactInfo = em.merge(retContactInfo);
                }
                return retContactInfo;
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<ContactInfo> readContactInfoByUserId(final Long customerId) {
        return (List<ContactInfo>) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery("READ_CONTACT_INFO_BY_CUSTOMER_ID");
                query.setParameter("customerId", customerId);
                return query.getResultList();
            }
        });
    }

    public ContactInfo readContactInfoById(final Long contactId) {
        return (ContactInfo) this.jpaTemplate.execute(new JpaCallback() {
            @SuppressWarnings("unchecked")
            public Object doInJpa(EntityManager em) throws PersistenceException {
                return em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.ContactInfo"), contactId);
            }
        });
    }

    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.jpaTemplate = new JpaTemplate(emf);
    }

    public void setEntityConfiguration(EntityConfiguration entityConfiguration) {
        this.entityConfiguration = entityConfiguration;
    }
}
