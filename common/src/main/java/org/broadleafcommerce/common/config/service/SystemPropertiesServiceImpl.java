/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.config.service;

import org.broadleafcommerce.common.config.dao.SystemPropertiesDao;
import org.broadleafcommerce.common.config.domain.SystemProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * To change this template use File | Settings | File Templates.
 * <p/>
 * User: Kelly Tisdell
 * Date: 6/25/12
 */
@Service("blSystemPropertiesService")
public class SystemPropertiesServiceImpl implements SystemPropertiesService{

    @Resource(name="blSystemPropertiesDao")
    protected SystemPropertiesDao systemPropertiesDao;

    @Override
    @Transactional("blTransactionManager")
    public SystemProperty saveSystemProperty(SystemProperty systemProperty) {
        return systemPropertiesDao.saveSystemProperty(systemProperty);
    }

    @Override
    @Transactional("blTransactionManager")
    public void deleteSystemProperty(SystemProperty systemProperty) {
        systemPropertiesDao.deleteSystemProperty(systemProperty);
    }

    @Override
    @Transactional("blTransactionManager")
    public List<SystemProperty> findAllSystemProperties() {
        return systemPropertiesDao.readAllSystemProperties();
    }

    @Override
    @Transactional("blTransactionManager")
    public SystemProperty findSystemPropertyByName(String name) {
        return systemPropertiesDao.readSystemPropertyByName(name);
    }

    @Override
    @Transactional("blTransactionManager")
    public SystemProperty createNewSystemProperty() {
        return systemPropertiesDao.createNewSystemProperty();
    }
}
