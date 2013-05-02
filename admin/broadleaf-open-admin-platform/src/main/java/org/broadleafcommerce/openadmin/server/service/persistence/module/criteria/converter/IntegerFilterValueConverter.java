package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.converter;

import org.springframework.stereotype.Component;

/**
 * @author Jeff Fischer
 */
@Component("blIntegerFilterValueConverter")
public class IntegerFilterValueConverter implements FilterValueConverter<Integer> {

    @Override
    public Integer convert(String stringValue) {
        return Integer.valueOf(stringValue);
    }
}
