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

import org.broadleafcommerce.common.config.dao.ModuleConfigurationDao;
import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.annotation.Resource;

@Service("blModuleConfigurationService")
@Transactional("blTransactionManager")
public class ModuleConfigurationServiceImpl implements ModuleConfigurationService {

    @Resource(name = "blModuleConfigurationDao")
    protected ModuleConfigurationDao moduleConfigDao;

    @Override
    public ModuleConfiguration findById(Long id) {
        return moduleConfigDao.readById(id);
    }

    @Override
    public ModuleConfiguration save(ModuleConfiguration config) {
        return moduleConfigDao.save(config);
    }

    @Override
    public void delete(ModuleConfiguration config) {
        moduleConfigDao.delete(config);
    }

    @Override
    public List<ModuleConfiguration> findActiveConfigurationsByType(ModuleConfigurationType type) {
        return moduleConfigDao.readActiveByType(type);
    }

    @Override
    public List<ModuleConfiguration> findAllConfigurationByType(ModuleConfigurationType type) {
        return moduleConfigDao.readAllByType(type);
    }

    @Override
    public List<ModuleConfiguration> findByType(Class<? extends ModuleConfiguration> type) {
        return moduleConfigDao.readByType(type);
    }

}
