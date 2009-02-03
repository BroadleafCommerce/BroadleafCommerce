package org.broadleafcommerce.profile.dao;

import java.util.List;

import org.broadleafcommerce.profile.domain.ContactInfo;

public interface ContactInfoDao {
    public List<ContactInfo> readContactInfoByUserId(Long userId);
    
    public ContactInfo maintainContactInfo(ContactInfo contactInfo);
    
    public ContactInfo readContactInfoById(Long contactId);
}
