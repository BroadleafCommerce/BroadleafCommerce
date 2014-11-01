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
package org.broadleafcommerce.common.web.extensibility;

import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;

/**
 * Bootstrap listener to start up BroadleafCommerce's rootId {@link MergeWebApplicationContext}.
 * Simply delegates to {@link MergeContextLoader}.
 *
 * <p>This listener should be registered after
 * {@link org.springframework.web.util.Log4jConfigListener}
 * in <code>web.xml</code>, if the latter is used.
 *
 * @author Jeff Fischer
 */
public class MergeContextLoaderListener extends ContextLoaderListener {

    /**
     * Create the ContextLoader to use. Can be overridden in subclasses.
     * @return the new ContextLoader
     */
    protected MergeContextLoader createContextLoader() {
        return new MergeContextLoader();
    }


    @Override
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        ThreadLocalManager.remove();
    }
}
