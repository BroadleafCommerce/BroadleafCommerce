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
package org.broadleafcommerce.common.i18n.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.i18n.dao.ISODao;
import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blISOService")
public class ISOServiceImpl implements ISOService {

    protected static final Log LOG = LogFactory.getLog(ISOServiceImpl.class);

    @Resource(name="blISODao")
    protected ISODao isoDao;

    @Override
    public List<ISOCountry> findISOCountries() {
        return isoDao.findISOCountries();
    }

    @Override
    public ISOCountry findISOCountryByAlpha2Code(String alpha2) {
        return isoDao.findISOCountryByAlpha2Code(alpha2);
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public ISOCountry save(ISOCountry isoCountry) {
        return isoDao.save(isoCountry);
    }

}
