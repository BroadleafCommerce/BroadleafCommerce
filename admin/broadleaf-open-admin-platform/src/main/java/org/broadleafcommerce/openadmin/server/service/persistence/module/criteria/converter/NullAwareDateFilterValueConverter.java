/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.converter;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.util.FormatUtil;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Jeff Fischer
 */
@Component("blNullAwareDateFilterValueConverter")
public class NullAwareDateFilterValueConverter implements FilterValueConverter<Date> {

    @Override
    public Date convert(String stringValue) {
        return parseDate(stringValue, FormatUtil.getDateFormat());
    }

    public Date parseDate(String value, SimpleDateFormat dateFormat) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            return dateFormat.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException("Error while converting '" + value + "' into Date using pattern " + dateFormat.toPattern(), e);
        }
    }
}
