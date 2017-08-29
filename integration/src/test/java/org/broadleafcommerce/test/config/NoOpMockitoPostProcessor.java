/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.test.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.mock.mockito.MockitoPostProcessor;

/**
 * So, this is a long story and exists as a workaround for a known Broadleaf problem with
 * LTW. This BeanFactoryPostProcessor actually scans every bean definition early to look for
 * appropriate configuration classes for mocks. It is automatically registered as part of "spring-boot-starter-test"
 * artifact inclusion. This ends up loading (but not initializing) any entity class along the way
 * (e.g. a entity class may be mentioned in a @Service class's import block). As a result,
 * our LTW process complains about un-transformed entities.
 *
 * This hack is to disable this post processor, since we don't need @Mock support for this MVC integration test.
 * A longer term fix is to find a earlier integration point for registering class transformers with the
 * ClassLoader during the Spring startup lifecycle.
 *
 * @author Jeff Fischer
 */
public class NoOpMockitoPostProcessor extends MockitoPostProcessor {

    public NoOpMockitoPostProcessor() {
        super(null);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //TODO Find a earlier integration point for registering class transformers with the ClassLoader during the Spring startup lifecycle
        //do nothing and avoid the scanning impact
    }
}
