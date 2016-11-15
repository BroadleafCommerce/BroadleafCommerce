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
package org.broadleafcommerce.common.resource.service;

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
     * Returns a newly rebuilt bundled resource given that the passed requestedBundleName had previously been built and cached/persisted via 
     * the {@code resolveBundleResourceName} method
     * 
     * @param requestedBundleName
     * @return
     */
    public Resource rebuildBundledResource(String requestedBundleName);

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
