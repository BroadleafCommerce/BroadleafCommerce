package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.converter;

/**
 * @author Jeff Fischer
 */
public interface FilterValueConverter<T> {

    T convert(String stringValue);

}
