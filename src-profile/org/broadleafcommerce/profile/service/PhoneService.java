package org.broadleafcommerce.profile.service;

import org.broadleafcommerce.profile.domain.Phone;

public interface PhoneService {

    public Phone savePhone(Phone phone);

    public Phone readPhoneById(Long phoneId);
}
