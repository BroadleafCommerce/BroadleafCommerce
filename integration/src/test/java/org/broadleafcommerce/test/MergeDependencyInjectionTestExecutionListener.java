/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.test;

import org.broadleafcommerce.common.extensibility.context.MergeClassPathXMLApplicationContext;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@SuppressWarnings("deprecation")
public class MergeDependencyInjectionTestExecutionListener extends DependencyInjectionTestExecutionListener {

    @Override
    protected void injectDependencies(TestContext testContext) throws Exception {
        MergeClassPathXMLApplicationContext context = BaseTest.getContext();
        Object bean = testContext.getTestInstance();
        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
        beanFactory.autowireBeanProperties(bean, AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT, true);
        beanFactory.initializeBean(bean, testContext.getTestClass().getName());
        testContext.removeAttribute(REINJECT_DEPENDENCIES_ATTRIBUTE);
    }

}
