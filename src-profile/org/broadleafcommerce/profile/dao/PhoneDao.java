package org.broadleafcommerce.profile.dao;

import org.broadleafcommerce.profile.domain.Phone;

public interface PhoneDao {

    public Phone save(Phone phone);

    public Phone readPhoneById(Long phoneId);
}
