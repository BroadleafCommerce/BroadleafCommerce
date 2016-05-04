/*
 * #%L
 * BroadleafCommerce Profile Web
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
package org.broadleafcommerce.profile.web.core.util;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.profile.core.domain.Phone;
import org.springframework.stereotype.Service;

@Service("blPhoneFormatter")
public class PhoneFormatterImpl implements PhoneFormatter {
    public void formatPhoneNumber(Phone phone) {
        if(phone != null && !StringUtils.isEmpty(phone.getPhoneNumber())){
            phone.setPhoneNumber(formatTelephoneNumber(phone.getPhoneNumber()));
        }
    }

    private String formatTelephoneNumber(String pNumber) {
        if (pNumber == null) {
            return null;
        }

        String number = pNumber.replaceAll("\\D", "");

        if (number.length() == 0) {
            return null;
        }

        if (number.length() > 10) {
            number = number.substring(0, 10);
        }

        StringBuffer newNumber = new StringBuffer(number);

        if (newNumber.length() == 10) {
            newNumber.insert(6, "-");
            newNumber.insert(3, "-");
        }

        return newNumber.toString();
    }
}
