/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.cms.web;

import org.broadleafcommerce.common.config.RuntimeEnvironmentPropertiesConfigurer;
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
