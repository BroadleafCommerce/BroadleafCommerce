/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Provides a means for classes that do not normally have access to
 * a servlet context or application context to be able to obtain
 * the current Spring ApplicationContext instance. This should be a last
 * resort, as it is unlikely this class is ever needed unless an
 * instance of ApplicationContext is required in a custom class
 * instantiated by third-party code.
 * 
 * @author jfischer
 *
 */
public class SpringAppContext implements ApplicationContextAware {
    
    private static ApplicationContext appContext;

    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        this.appContext = appContext;
    }

    public static ApplicationContext getApplicationContext() {
        return appContext;
    }
}
