package org.broadleafcommerce.catalog.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.catalog.domain.ItemAttribute;
import org.springframework.stereotype.Repository;

@Repository("itemAttributeDao")
public class ItemAttributeDaoJpa implements ItemAttributeDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public ItemAttribute maintainItemAttribute(ItemAttribute itemAttribute) {
        if (itemAttribute.getId() == null) {
            em.persist(itemAttribute);
        } else {
        	itemAttribute = em.merge(itemAttribute);
        }
        return itemAttribute;
    }

    @Override
    public ItemAttribute readItemAttributeById(Long itemAttributeId) {
        return em.find(ItemAttribute.class, itemAttributeId);
    }

}
