package org.broadleafcommerce.openadmin.server.dao.provider.property;

import org.broadleafcommerce.openadmin.server.dao.provider.property.request.PropertyRequest;

import java.lang.reflect.Field;

/**
 * @author Jeff Fischer
 */
public class PropertyProviderAdapter extends AbstractPropertyProvider {

    @Override
    public void buildProperty(PropertyRequest propertyRequest) {
        //do nothing
    }

    @Override
    public boolean canHandleField(Field field) {
        return false;
    }
}
