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
package org.broadleafcommerce.common.config.service;

import org.broadleafcommerce.common.config.dao.ModuleConfigurationDao;
import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.annotation.Resource;

@Service("blModuleConfigurationService")
public class ModuleConfigurationServiceImpl implements ModuleConfigurationService {

    @Resource(name = "blModuleConfigurationDao")
    protected ModuleConfigurationDao moduleConfigDao;

    @Override
    public ModuleConfiguration findById(Long id) {
        return moduleConfigDao.readById(id);
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public ModuleConfiguration save(ModuleConfiguration config) {
        return moduleConfigDao.save(config);
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
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
