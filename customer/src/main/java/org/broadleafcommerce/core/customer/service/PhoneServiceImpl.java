/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.profile.core.dao.PhoneDao;
import org.broadleafcommerce.profile.core.domain.Phone;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("blPhoneService")
public class PhoneServiceImpl implements PhoneService {

    @Resource(name="blPhoneDao")
    protected PhoneDao phoneDao;

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public Phone savePhone(Phone phone) {
        return phoneDao.save(phone);
    }

    @Override
    public Phone readPhoneById(Long phoneId) {
        return phoneDao.readPhoneById(phoneId);
    }

    @Override
    public Phone create() {
        return phoneDao.create();
    }

    @Override
    public Phone copyPhone(Phone orig) {
        return copyPhone(null, orig);
    }

    @Override
    public Phone copyPhone(Phone dest, Phone orig) {
        if (dest == null) {
            dest = create();
        }

        if (orig != null) {
            dest.setPhoneNumber(orig.getPhoneNumber());
            dest.setCountryCode(orig.getCountryCode());
            dest.setExtension(orig.getExtension());
            return dest;
        }

        return null;
    }

}

