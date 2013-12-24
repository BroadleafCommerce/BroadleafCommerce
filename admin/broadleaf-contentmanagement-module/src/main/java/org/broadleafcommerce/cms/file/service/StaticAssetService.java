/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.file.service;

import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.common.file.service.StaticAssetPathService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by bpolster.
 */
public interface StaticAssetService {


    public StaticAsset findStaticAssetById(Long id);
    
    public List<StaticAsset> readAllStaticAssets();

    public StaticAsset findStaticAssetByFullUrl(String fullUrl);

    /**
     * Used when uploading a file to Broadleaf.    This method will create the corresponding 
     * asset.   
     * 
     * Depending on the the implementation, the actual asset may be saved to the DB or to 
     * the file system.    The default implementation {@link StaticAssetServiceImpl} has a 
     * environment properties that determine this behavior <code>asset.use.filesystem.storage</code>
     * 
     * The properties Map allows for implementors to update other Asset properties at the
     * same time they are uploading a file.  The default implementation uses this for an optional URL to 
     * be specified.
     * 
     * @see StaticAssetServiceImpl
     * 
     * @param file - the file being uploaded
     * @param properties - additional meta-data properties
     * @return
     * @throws IOException
     */
    public StaticAsset createStaticAssetFromFile(MultipartFile file, Map<String, String> properties);

    /**
     * @see StaticAssetPathService#getStaticAssetUrlPrefix()
     * @deprecated since 3.1.0. 
     */
    public String getStaticAssetUrlPrefix();

    /**
     * @see StaticAssetPathService#getStaticAssetEnvironmentUrlPrefix()
     * @deprecated since 3.1.0. 
     */
    public String getStaticAssetEnvironmentUrlPrefix();

    /**
     * @see StaticAssetPathService#getStaticAssetEnvironmentSecureUrlPrefix()
     * @deprecated since 3.1.0. 
     */
    public String getStaticAssetEnvironmentSecureUrlPrefix();

    /**
     * @see StaticAssetPathService#convertAssetPath(String, String, boolean)
     * @deprecated since 3.1.0. 
     */
    public String convertAssetPath(String assetPath, String contextPath, boolean secureRequest);

    /**
     * Add an asset outside of Broadleaf Admin.  
     * 
     * @param staticAsset
     * @return
     */
    public StaticAsset addStaticAsset(StaticAsset staticAsset);

    /**
     * Update an asset outside of Broadleaf Admin.  
     * 
     * @param staticAsset
     * @return
     */
    public StaticAsset updateStaticAsset(StaticAsset staticAsset);

    /**
     * Delete an asset outside of Broadleaf Admin.  
     * 
     * @param staticAsset
     * @return
     */
    public void deleteStaticAsset(StaticAsset staticAsset);

}
