/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
