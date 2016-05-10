/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.weave;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component("blConditionalFieldAnnotationsTransformersManager")
public class ConditionalFieldAnnotationCopyTransformersManagerImpl implements ConditionalFieldAnnotationCopyTransformersManager, BeanFactoryAware {

    protected ConfigurableBeanFactory beanFactory;

    @Resource(name = "blConditionalFieldAnnotationCopyTransformers")
    protected Map<String, ConditionalFieldAnnotationCopyTransformMemberDTO> entityToPropertyMap;

    protected Map<String, ConditionalFieldAnnotationCopyTransformMemberDTO> enabledEntities = new HashMap<String, ConditionalFieldAnnotationCopyTransformMemberDTO>();

    @PostConstruct
    public void init() {
        for (Map.Entry<String, ConditionalFieldAnnotationCopyTransformMemberDTO> entry : entityToPropertyMap.entrySet()) {
            if (isPropertyEnabled(entry.getValue().getConditionalProperty())) {
                enabledEntities.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Boolean isEntityEnabled(String entityName) {
        return enabledEntities.containsKey(entityName);
    }

    @Override
    public ConditionalFieldAnnotationCopyTransformMemberDTO getTransformMember(String entityName) {
        return enabledEntities.get(entityName);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    protected Boolean isPropertyEnabled(String propertyName) {
        Boolean shouldProceed;
        try {
            String value = beanFactory.resolveEmbeddedValue("${" + propertyName + ":false}");
            shouldProceed = Boolean.parseBoolean(value);
        } catch (Exception e) {
            shouldProceed = false;
        }
        return shouldProceed;
    }

}
