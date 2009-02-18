package org.broadleafcommerce.catalog.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import org.broadleafcommerce.catalog.domain.BasePrice;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;

@Repository("basePriceDao")
public class BasePriceDaoJpa implements BasePriceDao {

    private JpaTemplate jpaTemplate;

    private EntityConfiguration entityConfiguration;

    @Override
    public BasePrice maintainBasePrice(final BasePrice basePrice) {
        return (BasePrice) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                BasePrice retBasePrice = basePrice;
                if (retBasePrice.getId() == null) {
                    em.persist(retBasePrice);
                } else {
                    retBasePrice = em.merge(retBasePrice);
                }
                return retBasePrice;
            }
        });
    }

    @Override
    public BasePrice readBasePriceById(final Long basePriceId) {
        return (BasePrice) this.jpaTemplate.execute(new JpaCallback() {
            @SuppressWarnings("unchecked")
            public Object doInJpa(EntityManager em) throws PersistenceException {
                return em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.catalog.domain.BasePrice"), basePriceId);
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
