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

import java.util.Collections;
import java.util.List;

/**
 * Holds information about bundles used during the current request.
 *
 * @author Jacob Mitash
 */
public class ResourcesRequestBundle {

    private final String bundleName;
    private final String mappingPrefix;
    private final List<String> files;
    private final String bundlePath;
    private final List<String> bundleFilePaths;

    public ResourcesRequestBundle(
            final String bundleName,
            final String mappingPrefix,
            final List<String> files,
            final String bundlePath
    ) {
        this.bundleName = bundleName;
        this.mappingPrefix = mappingPrefix;
        this.files = Collections.unmodifiableList(files);
        this.bundlePath = bundlePath;
        this.bundleFilePaths = null;
    }

    public ResourcesRequestBundle(
            final String bundleName,
            final String mappingPrefix,
            final List<String> files,
            final List<String> bundleFilePaths
    ) {
        this.bundleName = bundleName;
        this.mappingPrefix = mappingPrefix;
        this.files = Collections.unmodifiableList(files);
        this.bundlePath = null;
        this.bundleFilePaths = Collections.unmodifiableList(bundleFilePaths);
    }

    public String getBundleName() {
        return bundleName;
    }

    public String getMappingPrefix() {
        return mappingPrefix;
    }

    public List<String> getFiles() {
        return files;
    }

    public String getBundlePath() {
        return bundlePath;
    }

    public List<String> getBundleFilePaths() {
        return bundleFilePaths;
    }

}
