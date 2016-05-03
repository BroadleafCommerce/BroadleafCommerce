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
package org.broadleafcommerce.common.enumeration.service;

import javax.annotation.Resource;

import org.broadleafcommerce.common.enumeration.dao.DataDrivenEnumerationDao;
import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumeration;
import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumerationValue;
import org.springframework.stereotype.Service;


@Service("blDataDrivenEnumerationService")
public class DataDrivenEnumerationServiceImpl implements DataDrivenEnumerationService {

    @Resource(name = "blDataDrivenEnumerationDao")
    protected DataDrivenEnumerationDao dao;

    @Override
    public DataDrivenEnumeration findEnumByKey(String enumKey) {
        return dao.readEnumByKey(enumKey);
    }
    
    @Override
    public DataDrivenEnumerationValue findEnumValueByKey(String enumKey, String enumValueKey) {
        return dao.readEnumValueByKey(enumKey, enumValueKey);
    }

}
