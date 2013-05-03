package org.broadleafcommerce.common.util.dao;

import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;


@Repository("blDynamicDaoHelperImpl")
public class DynamicDaoHelperImpl implements DynamicDaoHelper {
    
    @Override
    public Map<String, Object> getIdMetadata(Class<?> entityClass, HibernateEntityManager entityManager) {
        Map<String, Object> response = new HashMap<String, Object>();
        SessionFactory sessionFactory = entityManager.getSession().getSessionFactory();
        
        ClassMetadata metadata = sessionFactory.getClassMetadata(entityClass);
        if (metadata == null) {
            return null;
        }
        
        String idProperty = metadata.getIdentifierPropertyName();
        response.put("name", idProperty);
        Type idType = metadata.getIdentifierType();
        response.put("type", idType);

        return response;
    }

}
