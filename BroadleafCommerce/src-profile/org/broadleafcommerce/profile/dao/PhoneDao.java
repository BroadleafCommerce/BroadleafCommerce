package org.broadleafcommerce.profile.dao;

import java.util.List;

import org.broadleafcommerce.profile.domain.Phone;

public interface PhoneDao {

    public List<Phone> readPhoneByUserId(Long userId);

    public Phone maintainPhone(Phone phone);

    public Phone readPhoneById(Long phoneId);
}
