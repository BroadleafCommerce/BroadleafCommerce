package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Jeff Fischer
 */
@Component("blDecimalFilterValueConverter")
public class DecimalFilterValueConverter implements FilterValueConverter<BigDecimal> {

    @Override
    public BigDecimal convert(String stringValue) {
        if (StringUtils.isEmpty(stringValue)) {
            return null;
        }
        try {
            return new BigDecimal(stringValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
