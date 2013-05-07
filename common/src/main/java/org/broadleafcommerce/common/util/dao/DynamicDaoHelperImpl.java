package org.broadleafcommerce.common.util.dao;

import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

    @Override
    public List<String> getPropertyNames(Class<?> entityClass, HibernateEntityManager entityManager) {
        ClassMetadata metadata = getSessionFactory(entityManager).getClassMetadata(entityClass);
        List<String> propertyNames = new ArrayList<String>();
        Collections.addAll(propertyNames, metadata.getPropertyNames());
        return propertyNames;
    }

    @Override
    public List<Type> getPropertyTypes(Class<?> entityClass, HibernateEntityManager entityManager) {
        ClassMetadata metadata = getSessionFactory(entityManager).getClassMetadata(entityClass);
        List<Type> propertyTypes = new ArrayList<Type>();
        Collections.addAll(propertyTypes, metadata.getPropertyTypes());
        return propertyTypes;
    }

    @Override
    public SessionFactory getSessionFactory(HibernateEntityManager entityManager) {
        return entityManager.getSession().getSessionFactory();
    }

}
