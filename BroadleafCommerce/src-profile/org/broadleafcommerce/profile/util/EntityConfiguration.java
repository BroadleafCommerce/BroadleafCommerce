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
    public Class getEntityClass(String beanId) {
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

    @Override
    public void setApplicationContext(ApplicationContext applicationcontext) throws BeansException {
        this.applicationcontext = applicationcontext;
    }
}
