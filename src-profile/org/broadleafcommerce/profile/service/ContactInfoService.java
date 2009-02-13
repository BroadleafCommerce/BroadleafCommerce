package org.broadleafcommerce.profile.service;

import java.util.List;

import org.broadleafcommerce.profile.domain.ContactInfo;

public interface ContactInfoService {

    public List<ContactInfo> readContactInfoByUserId(Long userId);

    public ContactInfo saveContactInfo(ContactInfo contactInfo);

    public ContactInfo readContactInfoById(Long contactId);
}
