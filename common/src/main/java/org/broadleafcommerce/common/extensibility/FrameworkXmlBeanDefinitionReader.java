/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.extensibility;

import org.broadleafcommerce.common.extensibility.FrameworkBeanDefinitionPostProcessor;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;

/**
 * @author Jeff Fischer
 */
public class FrameworkXmlBeanDefinitionReader extends XmlBeanDefinitionReader {

    protected BeanDefinitionRegistryPostProcessor frameworkRolePostProcessor = new FrameworkBeanDefinitionPostProcessor();

    public FrameworkXmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public int registerBeanDefinitions(Document doc, Resource resource) throws BeanDefinitionStoreException {
        int processedCount = super.registerBeanDefinitions(doc, resource);
        frameworkRolePostProcessor.postProcessBeanDefinitionRegistry(getRegistry());
        return processedCount;
    }
}
