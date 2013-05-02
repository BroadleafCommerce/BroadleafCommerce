package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.converter;

import org.springframework.stereotype.Component;

/**
 * @author Jeff Fischer
 */
@Component("blStringFilterValueConverter")
public class StringFilterValueConverter implements FilterValueConverter<String>{

    @Override
    public String convert(String stringValue) {
        return stringValue;
    }
}
