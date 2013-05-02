package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author Jeff Fischer
 */
@Component("blNullAwareLongFilterValueConverter")
public class NullAwareLongFilterValueConverter implements FilterValueConverter<Long> {

    @Override
    public Long convert(String stringValue) {
        if (StringUtils.isEmpty(stringValue) || stringValue.equals("null")) {
            return null;
        }
        try {
            return Long.valueOf(stringValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
