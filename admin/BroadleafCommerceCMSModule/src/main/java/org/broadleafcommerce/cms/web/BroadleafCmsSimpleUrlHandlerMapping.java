package org.broadleafcommerce.cms.web;

import org.broadleafcommerce.config.RuntimeEnvironmentPropertiesConfigurer;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import javax.annotation.Resource;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/26/11
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class BroadleafCmsSimpleUrlHandlerMapping extends SimpleUrlHandlerMapping {

    @Resource(name="blConfiguration")
    protected RuntimeEnvironmentPropertiesConfigurer configurer;

    @Override
    public void setMappings(Properties mappings) {
        Properties clone = new Properties();
        for (Object propertyName: mappings.keySet()) {
            String newName = configurer.getStringValueResolver().resolveStringValue(propertyName.toString());
            clone.put(newName, mappings.get(propertyName));
        }
        super.setMappings(clone);
    }
}
