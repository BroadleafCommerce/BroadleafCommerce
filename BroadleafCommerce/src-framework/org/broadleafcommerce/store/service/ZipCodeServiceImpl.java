package org.broadleafcommerce.store.service;

import javax.annotation.Resource;

import org.broadleafcommerce.store.dao.ZipCodeDao;
import org.broadleafcommerce.store.domain.ZipCode;
import org.springframework.stereotype.Service;

@Service("blZipCodeService")
public class ZipCodeServiceImpl implements ZipCodeService {

    @Resource
    private ZipCodeDao zipCodeDao;

    @Override
    public ZipCode findZipCodeByZipCode(Integer zipCode) {
        return zipCodeDao.findZipCodeByZipCode(zipCode);
    }
}
