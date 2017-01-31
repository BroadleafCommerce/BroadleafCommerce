/*
 * #%L
 * BroadleafCommerce Export Module
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package com.broadleafcommerce.export.service;

import org.broadleafcommerce.common.util.TransactionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.broadleafcommerce.export.dao.ExportInfoDao;
import com.broadleafcommerce.export.domain.ExportInfo;

import javax.annotation.Resource;

@Service("blExportInfoService")
public class ExportInfoServiceImpl implements ExportInfoService {
    
    @Resource(name = "blExportInfoDao")
    protected ExportInfoDao exportInfoDao;
    
    @Override
    public ExportInfo create() {
        return exportInfoDao.create();
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public ExportInfo save(ExportInfo info) {
        return exportInfoDao.save(info);
    }

    @Override
    public ExportInfo findExportInfoById(Long id) {
        return exportInfoDao.readExportInfoById(id);
    }
}
