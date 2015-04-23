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
package org.broadleafcommerce.common.resource.service;

import org.springframework.core.io.Resource;

/**
 * This service is responsible for modifying a passed in resource.    The default implementation
 * does not perform any modifications but does use an extension manager pattern to allow 
 * resources to be modified by other processes (for example with the Broadleaf Themes module)
 * 
 * @author bpolster
 */
public interface ResourceModificationService {

    /**
     * Given a resource name, returns the modified resource name if different from the original.
     * 
     * For example, if a CSS file has token place holders and an implementation changes that file, this
     * method returns a name that can be used to uniquely represent the modified resource.
     * 
     * Note that this is not necessarily the name of the file that will be included in the HTML sent
     * to a client as the file may go through further modifications including versioning and minification
     * prior to getting its final name.
     * 
     * @param originalResourceName
     * @return 
     */
    Resource getModifiedResource(String originalResourceName);

    /**
     * Returns the Resource corresponding to the passed in resourceName
     * @param originalResourceName
     * @return
     */
    Resource getNonModifiedResource(String originalResourceName);
}
