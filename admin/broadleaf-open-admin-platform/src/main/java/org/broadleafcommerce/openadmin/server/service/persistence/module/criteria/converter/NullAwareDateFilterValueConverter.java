/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
