/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.weave;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @see org.broadleafcommerce.common.weave.ConditionalDirectCopyTransformersManager
 *
 * @author Jeff Fischer
 */
@Service("blConditionalDirectCopyTransformersManager")
public class ConditionalDirectCopyTransformersManagerImpl implements BeanFactoryAware, ConditionalDirectCopyTransformersManager {

    @Resource(name="blConditionalDirectCopyTransformers")
    protected Map<String, ConditionalDirectCopyTransformMemberDto> entityToPropertyMap;
    protected Map<String, ConditionalDirectCopyTransformMemberDto> enabledEntities = new HashMap<String, ConditionalDirectCopyTransformMemberDto>();
    protected ConfigurableBeanFactory beanFactory;

    @PostConstruct
    public void init() {
        for (Map.Entry<String, ConditionalDirectCopyTransformMemberDto> entry : entityToPropertyMap.entrySet()) {
            if (!StringUtils.isEmpty(entry.getValue().getConditionalProperty())) {
                if (isPropertyEnabled(entry.getValue().getConditionalProperty())) {
                    enabledEntities.put(entry.getKey(), entry.getValue());
                }
            } else if (entry.getValue().getConditionalValue() != null && entry.getValue().getConditionalValue()) {
                enabledEntities.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @Override
    public Boolean isEntityEnabled(String entityName) {
        return enabledEntities.containsKey(entityName);
    }

    @Override
    public ConditionalDirectCopyTransformMemberDto getTransformMember(String entityName) {
        return enabledEntities.get(entityName);
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
