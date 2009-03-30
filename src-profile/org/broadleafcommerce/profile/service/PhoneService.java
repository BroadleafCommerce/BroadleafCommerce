package org.broadleafcommerce.profile.service;

import java.util.List;

import org.broadleafcommerce.profile.domain.Phone;

public interface PhoneService {

    public List<Phone> readPhoneByUserId(Long userId);

    public Phone savePhone(Phone phone);

    public Phone readPhoneById(Long phoneId);
}
