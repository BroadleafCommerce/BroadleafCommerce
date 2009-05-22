package org.broadleafcommerce.extensibility.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;

public class PropertyConfigurer extends PropertyPlaceholderConfigurer {

    @Override
    public void setLocation(Resource location) {
        // TODO Auto-generated method stub
        super.setLocation(location);
    }

    @Override
    public void setLocations(Resource[] locations) {
        // TODO Auto-generated method stub
        super.setLocations(locations);
    }

}
