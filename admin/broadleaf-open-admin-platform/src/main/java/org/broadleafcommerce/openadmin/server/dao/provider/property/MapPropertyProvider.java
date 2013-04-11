package org.broadleafcommerce.openadmin.server.dao.provider.property;

import org.broadleafcommerce.openadmin.server.dao.provider.property.request.PropertyRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Jeff Fischer
 */
@Component("blMapPropertyProvider")
@Scope("prototype")
public class MapPropertyProvider extends AdvancedPropertyProvider {

    @Override
    public void buildProperty(PropertyRequest propertyRequest) {

        //do nothing but add the property without manipulation
        propertyRequest.getRequestedProperties().put(propertyRequest.getRequestedPropertyName(),
                propertyRequest.getPresentationAttribute());
    }
}
