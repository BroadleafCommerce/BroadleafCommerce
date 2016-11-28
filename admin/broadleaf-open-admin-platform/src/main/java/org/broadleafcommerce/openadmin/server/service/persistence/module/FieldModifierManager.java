/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.openadmin.server.service.persistence.module;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

/**
 * Manages execution of field value modification for reads and writes against one or more modifiers. See {@link FieldManagerModifier}.
 *
 * @author Jeff Fischer
 */
@Service("blFieldModifierManager")
public class FieldModifierManager implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private static FieldModifierManager fieldModifierManager;

    public static FieldModifierManager getFieldModifierManager() {
        if (applicationContext == null) {
            return null;
        }
        if (fieldModifierManager == null) {
            fieldModifierManager = (FieldModifierManager) applicationContext.getBean("blFieldModifierManager");
        }
        return fieldModifierManager;
    }

    @Resource
    protected List<FieldManagerModifier> fieldManagerModifiers;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        FieldModifierManager.applicationContext = applicationContext;
    }

    public Object getModifiedWriteValue(Field field, Object value, Object newValue, EntityManager em) throws IllegalAccessException {
        Object response = newValue;
        // iterate through each modifier and if it can handle this field, receive the modified value
        if (CollectionUtils.isNotEmpty(fieldManagerModifiers)) {
            for (FieldManagerModifier modifier : fieldManagerModifiers) {
                if (modifier.canHandle(field, response, em)) {
                    response = modifier.getModifiedWriteValue(field, value, response, em);
                }
            }
        }
        return response;
    }

    public Object getModifiedReadValue(Field field, Object value, EntityManager em) throws IllegalAccessException {
        Object response = value;
        // iterate through each modifier and if it can handle this field, receive the modified value
        if (CollectionUtils.isNotEmpty(fieldManagerModifiers) && field != null) {
            for (FieldManagerModifier modifier : fieldManagerModifiers) {
                if (modifier.canHandle(field, response, em)) {
                    response = modifier.getModifiedReadValue(field, response, em);
                }
            }
        }
        return response;
    }

}
