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

import org.broadleafcommerce.common.web.processor.ResourceBundleProcessor;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * This service is responsible for interaction with the {@link ResourceBundleProcessor} to generate
 * bundles for static resources.
 * 
 * @author Andre Azzolini (apazzolini)
 * @author Brian Polster (bpolster)
 */
public interface ResourceBundlingService {

    /**
     * Returns a file name representing a versioned copy of the bundle.
     * 
     * <p>
     * First computes the bundle version by checking examining the files in the bundle.
     *
     * <p>
     * If the bundle does not exist, this method will make a call to create it.  
     *  
     * @param requestedBundleName
     * @return 
     */
    public String resolveBundleResourceName(String requestedBundleName, String mappingPrefix, List<String> files);

    /**
     * Returns a Resource for passed in versionedBundleResourceName.
     * 
     * If the bundle does not exist, this method will attempt to create it by using the list
     * of files that were registered with the initial call to {@link #getBundleResourceName(String, List)}
     * 
     * @param versionedBundleResourceName
     * @return 
     */
    Resource resolveBundleResource(String versionedBundleResourceName);

    /**
     * Through configuration, you can provide additional files that will be automatically included
     * for any bundle.   
     * 
     * @param bundleName
     * @return
     */
    List<String> getAdditionalBundleFiles(String bundleName);

    /**
     * Returns true if the passed in name represents a versionedBundle 
     * 
     * @param versionedBundleName
     * @return
     */
    boolean checkForRegisteredBundleFile(String versionedBundleName);

}
