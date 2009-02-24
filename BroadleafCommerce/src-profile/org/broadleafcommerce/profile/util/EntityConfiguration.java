package org.broadleafcommerce.profile.util;

import java.util.HashMap;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class EntityConfiguration implements ApplicationContextAware {

    @SuppressWarnings("unchecked")
    private final HashMap<String, Class> entityMap = new HashMap<String, Class>();

    private ApplicationContext applicationcontext;

    @SuppressWarnings("unchecked")
    public Class lookupEntityClass(String beanId) {
        Class clazz = null;
        if (entityMap.containsKey(beanId)) {
            clazz = entityMap.get(beanId);
        } else {
            Object object = applicationcontext.getBean(beanId);
            clazz = object.getClass();
            entityMap.put(beanId, clazz);
        }
        return clazz;
    }

    @SuppressWarnings("unchecked")
    public Object createEntityInstance(String beanId) {
        Class clazz = lookupEntityClass(beanId);
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException iae) {
            // TODO: handle exception
            System.out.println("IllegalAccessException in EntityConfiguration.createEntityInsetance(" + beanId + ")");
        } catch (InstantiationException ie) {
            // TODO: handle exception
            System.out.println("InstantiationException in EntityConfiguration.createEntityInsetance(" + beanId + ")");
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationcontext) throws BeansException {
        this.applicationcontext = applicationcontext;
    }
}
