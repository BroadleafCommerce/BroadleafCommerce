package org.broadleafcommerce.openadmin.server.dao.provider.property;

import org.broadleafcommerce.openadmin.server.dao.provider.property.request.PropertyRequest;

import java.lang.reflect.Field;

/**
 * Classes implementing this interface are capable of manipulating properties resulting from the inspection
 * phase for the admin. Providers are typically added in response to new admin presentation annotation support.
 * Implementers should generally extend <tt>PropertyProviderAdapter</tt>.
 *
 * @author Jeff Fischer
 */
public interface PropertyProvider {

    /**
     * Whether or not this provider is qualified add properties for the specified field.
     *
     * @param field the <tt>Field</tt> instance to test
     * @return whether or not this provider is qualified
     */
    boolean canHandleField(Field field);

    /**
     * Contribute to property inspection for the <tt>Field</tt> instance in the request. Implementations should
     * add values to the requestedProperties field of the request object.
     *
     * @param propertyRequest contains the requested field, properties, property name and support classes.
     */
    void buildProperty(PropertyRequest propertyRequest);
}
