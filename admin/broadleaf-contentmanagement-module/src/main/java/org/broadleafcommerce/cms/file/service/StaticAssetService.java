/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.file.service;

import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.common.file.service.StaticAssetPathService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by bpolster.
 */
public interface StaticAssetService {


    public StaticAsset findStaticAssetById(Long id);
    
    public List<StaticAsset> readAllStaticAssets();

    public StaticAsset findStaticAssetByFullUrl(String fullUrl);

    Long findTotalStaticAssetCount();

    /**
     * <p>
     * Used when uploading a file to Broadleaf.    This method will create the corresponding 
     * asset.   
     * 
     * <p>
     * Depending on the the implementation, the actual asset may be saved to the DB or to 
     * the file system.    The default implementation {@link StaticAssetServiceImpl} has a 
     * environment properties that determine this behavior <code>asset.use.filesystem.storage</code>
     * 
     * <p>
     * The properties Map allows for implementors to update other Asset properties at the
     * same time they are uploading a file.  The default implementation uses this for an optional URL to 
     * be specified.
     * 
     * <p>
     * To actually create the physical file that this asset represents see
     * {@link StaticAssetStorageService#createStaticAssetStorageFromFile(MultipartFile, StaticAsset)}
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
     * <p>
     * Similar to {@link #createStaticAssetFromFile(MultipartFile, Map)} except not dependent upon an uploaded file from a
     * controller
     * 
     * <p>
     * To actually create the physical file that this asset represents see
     * {@link StaticAssetStorageService#createStaticAssetStorage(InputStream, StaticAsset)}
     * 
     * @param inputStream the input stream of a file to create the asset from
     * @param fileName the name of the file
     * @param fileSize the size of the file, in bytes
     * @param properties additional metadata properties
     * @return
     */
    public StaticAsset createStaticAsset(InputStream inputStream, String fileName, long fileSize, Map<String, String> properties);

    /**
     * @see StaticAssetPathService#getStaticAssetUrlPrefix()
     * @deprecated since 3.1.0. 
     */
    @Deprecated
    public String getStaticAssetUrlPrefix();

    /**
     * Return a prefixed version of the given asset url, assuming a static asset url prefix is set
     * @param assetUrl
     * @return
     */
    public String getPrefixedStaticAssetUrl(String assetUrl);

    /**
     * @see StaticAssetPathService#getStaticAssetEnvironmentUrlPrefix()
     * @deprecated since 3.1.0. 
     */
    @Deprecated
    public String getStaticAssetEnvironmentUrlPrefix();

    /**
     * @see StaticAssetPathService#getStaticAssetEnvironmentSecureUrlPrefix()
     * @deprecated since 3.1.0. 
     */
    @Deprecated
    public String getStaticAssetEnvironmentSecureUrlPrefix();

    /**
     * @see StaticAssetPathService#convertAssetPath(String, String, boolean)
     * @deprecated since 3.1.0. 
     */
    @Deprecated
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
