
package org.broadleafcommerce.common.util.dao;

import org.hibernate.ejb.HibernateEntityManager;

import java.util.Map;

/**
 * Provides utility methods for interacting with dynamic entities
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface DynamicDaoHelper {

    public Map<String, Object> getIdMetadata(Class<?> entityClass, HibernateEntityManager entityManager);

}