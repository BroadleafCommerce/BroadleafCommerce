package org.broadleafcommerce.profile.service;

import java.util.List;

import org.broadleafcommerce.profile.dao.ContactInfoDao;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class ContactInfoServiceImpl implements ContactInfoService {

    private ContactInfoDao contactInfoDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<ContactInfo> readContactInfoByUserId(Long userId) {
        return contactInfoDao.readContactInfoByUserId(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ContactInfo saveContactInfo(ContactInfo contactInfo) {
        return contactInfoDao.maintainContactInfo(contactInfo);
    }

    public ContactInfo readContactInfoById(Long contactId) {
        return contactInfoDao.readContactInfoById(contactId);
    }

    public void setContactInfoDao(ContactInfoDao contactInfoDao) {
        this.contactInfoDao = contactInfoDao;
    }
}
