/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.common.web.request;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nonnull;

/**
 * Keeps track of the resources needed for &lt;blc:bundle&gt; and &lt;blc:bundlepreload&gt; tags so that the list
 * of files does not need to be duplicated across both tags.
 *
 * @author Jacob Mitash
 */
@RequestScope
@Component("blResourcesRequest")
public class ResourcesRequest {

    protected List<ResourcesRequestBundle> bundlesRequested = new ArrayList<>();

    /**
     * Gets the bundle for the bundle name, prefix, and file list if previously used on this request.
     * <p>
     * Note that the mapping prefix and file list will be assumed to be the same if not provided.
     *
     * @param name          the name of the bundle to search for
     * @param mappingPrefix the mapping prefix of the bundle request, or null if not provided
     * @param files         the list of files of the bundle request, or null if not provided
     * @return the bundle request if found, otherwise null
     */
    public ResourcesRequestBundle getBundle(@Nonnull String name, String mappingPrefix, List<String> files) {
        for (ResourcesRequestBundle resourcesRequestBundle : bundlesRequested) {
            //names are same
            if (name.equals(resourcesRequestBundle.getBundleName())) {

                if (mappingPrefix != null
                        && !StringUtils.equalsIgnoreCase(mappingPrefix, resourcesRequestBundle.getMappingPrefix())) {
                    //the prefixes are different
                    continue;
                }

                final List<String> bundleFiles = resourcesRequestBundle.getFiles();

                if (!CollectionUtils.isEmpty(files)
                        && !CollectionUtils.isEqualCollection(files, bundleFiles)) {
                    //files are different
                    continue;
                }

                return resourcesRequestBundle;
            }
        }

        return null;
    }

    /**
     * Saves the bundled file to the request so it can be recalled later in the template
     *
     * @param name          the name of the bundle
     * @param mappingPrefix the mapping prefix of the bundle
     * @param files         the list of files in the bundle
     * @param bundlePath    the path of the resulting bundle
     */
    public void saveBundle(String name, String mappingPrefix, List<String> files, String bundlePath) {
        ResourcesRequestBundle resourcesRequestBundle = new ResourcesRequestBundle(
                name, mappingPrefix, files, bundlePath
        );
        bundlesRequested.add(resourcesRequestBundle);
    }

    /**
     * Saves the bundled file to the request so it can be recalled later in the template
     *
     * @param name             the name of the bundle
     * @param mappingPrefix    the mapping prefix of the bundle
     * @param files            the list of files in the bundle
     * @param bundledFilePaths the list of unbundled file paths to include in the template
     */
    public void saveBundle(String name, String mappingPrefix, List<String> files, List<String> bundledFilePaths) {
        ResourcesRequestBundle resourcesRequestBundle = new ResourcesRequestBundle(
                name, mappingPrefix, files, bundledFilePaths
        );
        bundlesRequested.add(resourcesRequestBundle);
    }

}
