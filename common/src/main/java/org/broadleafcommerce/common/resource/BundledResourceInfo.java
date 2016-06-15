package org.broadleafcommerce.common.resource;

import java.util.List;

import org.springframework.core.io.Resource;

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
