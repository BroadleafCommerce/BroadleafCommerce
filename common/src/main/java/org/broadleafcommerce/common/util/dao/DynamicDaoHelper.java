
package org.broadleafcommerce.common.util.dao;

import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.type.Type;

import java.util.List;
import java.util.Map;

/**
 * Provides utility methods for interacting with dynamic entities
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface DynamicDaoHelper {

    public Map<String, Object> getIdMetadata(Class<?> entityClass, HibernateEntityManager entityManager);
    
    public List<String> getPropertyNames(Class<?> entityClass, HibernateEntityManager entityManager);
    
    public List<Type> getPropertyTypes(Class<?> entityClass, HibernateEntityManager entityManager);
    
    public SessionFactory getSessionFactory(HibernateEntityManager entityManager);

}