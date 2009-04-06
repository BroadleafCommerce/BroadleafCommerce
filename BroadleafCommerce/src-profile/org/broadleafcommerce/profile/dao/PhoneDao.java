package org.broadleafcommerce.profile.dao;

import org.broadleafcommerce.profile.domain.Phone;

public interface PhoneDao {

    public Phone maintainPhone(Phone phone);

    public Phone readPhoneById(Long phoneId);
}
