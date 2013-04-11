package org.broadleafcommerce.openadmin.server.dao.provider.property;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Jeff Fischer
 */
public abstract class AbstractPropertyProvider implements PropertyProvider {

    protected Class<?> getBasicJavaType(SupportedFieldType fieldType) {
        Class<?> response;
        switch (fieldType) {
            case BOOLEAN:
                response = Boolean.TYPE;
                break;
            case DATE:
                response = Date.class;
                break;
            case DECIMAL:
                response = BigDecimal.class;
                break;
            case MONEY:
                response = BigDecimal.class;
                break;
            case INTEGER:
                response = Integer.TYPE;
                break;
            case UNKNOWN:
                response = null;
                break;
            default:
                response = String.class;
                break;
        }

        return response;
    }

}
