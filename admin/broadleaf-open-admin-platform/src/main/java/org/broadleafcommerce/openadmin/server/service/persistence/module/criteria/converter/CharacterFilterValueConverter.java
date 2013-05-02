package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.converter;

import org.springframework.stereotype.Component;

/**
 * @author Jeff Fischer
 */
@Component("blCharacterFilterValueConverter")
public class CharacterFilterValueConverter implements FilterValueConverter<Character> {

    @Override
    public Character convert(String stringValue) {
        if ("true".equals(stringValue)) {
            return 'Y';
        } else if ("false".equals(stringValue)) {
            return 'N';
        }
        return stringValue.charAt(0);
    }
}
