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
package org.broadleafcommerce.common.resource;

import org.springframework.core.io.Resource;

import java.util.List;

/**
 * Tracks a bundle version name and path/file information for a dynamically created resource bundle.  This information can be used later to 
 * rebuild a bundle resource should that resource be found to not exist.
 * 
 * @author dcolgrove
 *
 */
public class BundledResourceInfo {
    protected Resource resource;
    protected String versionedBundleName;
    protected List<String> bundledFilePaths;

    public BundledResourceInfo(Resource resource, String versionedBundleName, List<String> bundledFilePaths) {
        this.resource = resource;
        this.versionedBundleName = versionedBundleName;
        this.bundledFilePaths = bundledFilePaths;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public String getVersionedBundleName() {
        return versionedBundleName;
    }
    
    public void setVersionedBundleName(String versionedBundleName) {
        this.versionedBundleName = versionedBundleName;
    }

    public List<String> getBundledFilePaths() {
        return bundledFilePaths;
    }
    
    public void setBundledFilePaths(List<String> bundledFilePaths) {
        this.bundledFilePaths = bundledFilePaths;
    }

        
}
