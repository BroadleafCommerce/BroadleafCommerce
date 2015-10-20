/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.common.extensibility.jpa;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Jeff Fischer
 */
public class ConditionalORMConfigPersistenceUnitPostProcessor implements org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor, BeanFactoryAware {

    @Resource(name="blConditionalEntities")
    protected Map<String, ConditionalORMConfigDto> entities;

    protected Map<String, ConditionalORMConfigDto> enabledEntities = new HashMap<String, ConditionalORMConfigDto>();

    @Resource(name="blConditionalOrmFiles")
    protected Map<String, ConditionalORMConfigDto> ormFiles;

    protected Map<String, ConditionalORMConfigDto> enabledOrmFiles = new HashMap<String, ConditionalORMConfigDto>();

    protected ConfigurableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @PostConstruct
    public void init() {
        for (Map.Entry<String, ConditionalORMConfigDto> entry : entities.entrySet()) {
            if (isPropertyEnabled(entry.getValue().getConditionalProperty())) {
                enabledEntities.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<String, ConditionalORMConfigDto> entry : ormFiles.entrySet()) {
            if (isPropertyEnabled(entry.getValue().getConditionalProperty())) {
                enabledOrmFiles.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        String puName = pui.getPersistenceUnitName();
        for (Map.Entry<String, ConditionalORMConfigDto> entry : enabledEntities.entrySet()) {
            if (puName.equals(entry.getValue().getPuName())) {
                pui.getManagedClassNames().add(entry.getKey());
            }
        }
        for (Map.Entry<String, ConditionalORMConfigDto> entry : enabledOrmFiles.entrySet()) {
            if (puName.equals(entry.getValue().getPuName())) {
                pui.getMappingFileNames().add(entry.getKey());
            }
        }
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
