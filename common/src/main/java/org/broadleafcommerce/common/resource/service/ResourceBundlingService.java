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

import org.broadleafcommerce.common.web.processor.ResourceBundleProcessor;
import org.broadleafcommerce.common.web.processor.ResourcePreloadProcessor;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * This service is responsible for interaction with
 * {@link ResourceBundleProcessor} and
 * {@link ResourcePreloadProcessor} to generate bundles for static resources.
 * @author Andre Azzolini (apazzolini)
 * @author Brian Polster (bpolster)
 */
public interface ResourceBundlingService {

    /**
     * Returns a newly rebuilt bundled resource given that the passed requestedBundleName had previously been built and cached/persisted via
     * the {@link #resolveBundleResourceName(String, String, List)} method.
     * @param requestedBundleName the requested bundle name
     * @return the rebuilt bundled resource
     */
    Resource rebuildBundledResource(String requestedBundleName);

    /**
     * Returns a file name representing a versioned copy of the bundle.
     * <p>
     * First computes the bundle version by examining the files in the bundle. If the bundle does not exist, this method
     * will create it.
     * @param requestedBundleName the requested bundle name
     * @param mappingPrefix       the path prefix for the bundle
     * @param files               the files the bundle should contain
     * @return the bundle's resource name
     */
    String resolveBundleResourceName(String requestedBundleName, String mappingPrefix, List<String> files);

    /**
     * Returns a file name representing a versioned copy of the bundle with the <code>bundleAppend</code> text appended
     * to the end of the bundle.
     * <p>
     * First computes the bundle version by examining the files in the bundle. If the bundle does not exist, this method
     * will create it.
     * @param requestedBundleName the requested bundle name
     * @param mappingPrefix       the path prefix for the bundle
     * @param files               the files the bundle should contain
     * @param bundleAppend        the text to append at the end of the bundle
     * @return the bundle's resource name
     */
    String resolveBundleResourceName(String requestedBundleName, String mappingPrefix, List<String> files, String bundleAppend);

    /**
     * Returns a Resource for passed in versionedBundleResourceName.
     * <p>
     * If the bundle does not exist, this method will attempt to create it by using the list
     * of files that were registered with the initial call to
     * {@link #resolveBundleResourceName(String, String, List)}
     * @param versionedBundleResourceName the versioned bundle resource name
     * @return the bundle resource
     */
    Resource resolveBundleResource(String versionedBundleResourceName);

    /**
     * Through configuration, you can provide additional files that will be automatically included for any bundle. This
     * method gets the list of configured additional files.
     * @param bundleName the name of the bundle to get additional files for
     * @return list of additional files
     */
    List<String> getAdditionalBundleFiles(String bundleName);

    /**
     * Tells if the given versioned bundle exists
     * @param versionedBundleName the versioned bundle name
     * @return true if the bundle exists, false otherwise
     */
    boolean checkForRegisteredBundleFile(String versionedBundleName);

    /**
     * Returns names of bundles which contain the given file
     * @param fileName file to find in the bungle
     * @return first bundle name that has file, null otherwise
     */
    List<String> findBundlesNameByResourceFileName(String fileName);

    boolean removeBundle(String bundleName);
}
