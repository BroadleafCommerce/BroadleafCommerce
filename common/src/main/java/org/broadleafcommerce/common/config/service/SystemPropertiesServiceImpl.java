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
package org.broadleafcommerce.common.config.service;

import org.broadleafcommerce.common.config.RuntimeEnvironmentPropertiesManager;
import org.broadleafcommerce.common.config.dao.SystemPropertiesDao;
import org.broadleafcommerce.common.config.domain.SystemProperty;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Service that retrieves property settings from the database.   If not set in 
 * the DB then returns the value from property files.
 *  
 * @author bpolster
 *
 */
@Service("blSystemPropertiesService")
public class SystemPropertiesServiceImpl implements SystemPropertiesService{

    @Resource(name="blSystemPropertiesDao")
    protected SystemPropertiesDao systemPropertiesDao;

    @Resource(name = "blSystemPropertyServiceExtensionManager")
    protected SystemPropertyServiceExtensionManager extensionManager;

    @Autowired
    protected RuntimeEnvironmentPropertiesManager propMgr;

    @Override
    public String resolveSystemProperty(String name) {
        if (extensionManager != null) {
            ExtensionResultHolder holder = new ExtensionResultHolder();
            extensionManager.getProxy().resolveProperty(name, holder);
            if (holder.getResult() != null) {
                if (holder.getResult() instanceof String) {
                    return holder.getResult().toString();
                } else if (holder.getResult() instanceof SystemProperty) {
                    return ((SystemProperty) holder.getResult()).getValue();
                }
            }
        }

        SystemProperty property = systemPropertiesDao.readSystemPropertyByName(name);
        if (property == null || property.getValue() == null) {
            return propMgr.getProperty(name);
        } else {
            return property.getValue();
        }
    }

    @Override
    public int resolveIntSystemProperty(String name) {
        String systemProperty = resolveSystemProperty(name);
        if (systemProperty == null) {
            return 0;
        }

        return Integer.valueOf(systemProperty).intValue();
    }

    @Override
    public boolean resolveBooleanSystemProperty(String name) {
        String systemProperty = resolveSystemProperty(name);
        if (systemProperty == null) {
            return false;
        }
        return Boolean.valueOf(systemProperty).booleanValue();
    }

    @Override
    public long resolveLongSystemProperty(String name) {
        String systemProperty = resolveSystemProperty(name);
        if (systemProperty == null) {
            return 0l;
        }

        return Long.valueOf(systemProperty).longValue();
    }
}
