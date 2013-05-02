package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.converter;

import org.springframework.stereotype.Component;

/**
 * @author Jeff Fischer
 */
@Component("blLongFilterValueConverter")
public class LongFilterValueConverter implements FilterValueConverter<Long> {

    @Override
    public Long convert(String stringValue) {
        return Long.valueOf(stringValue);
    }
}
