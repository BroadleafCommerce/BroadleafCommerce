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
import org.broadleafcommerce.cms.file.domain.StaticAssetStorage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.Map;

/**
 * @author Jeff Fischer
 *
 */
public interface StaticAssetStorageService {

    /**
     * Returns a StaticAssetStorage object.   Assumes that the asset is stored in the Database.
     * 
     * Storing Assets in the DB is not the preferred mechanism for Broadleaf as of 3.0 so in most cases, this 
     * method would not be used by Broadleaf implementations.
     * 
     * @param id
     * @return
     */
    StaticAssetStorage findStaticAssetStorageById(Long id);

    /**
     * @deprecated   Use createStaticAssetStorageFromFile instead.
     * @return
     */
    @Deprecated
    StaticAssetStorage create();

    /**
     * Returns a StaticAssetStorage object using the id of a related StaticAsset.   
     * Assumes that the asset is stored in the Database.
     * 
     * Storing Assets in the DB is not the preferred mechanism for Broadleaf as of 3.0 so in most cases, this 
     * method would not be used by Broadleaf implementations.
     * 
     * @param id
     * @return
     */
    StaticAssetStorage readStaticAssetStorageByStaticAssetId(Long id);

    /**
     * Persists a static asset to the database.   Not typically used since Broadleaf 3.0 as the 
     * preferred method for storing assets is on a shared-filesystem.
     * 
     * @param assetStorage
     * @return
     */
    StaticAssetStorage save(StaticAssetStorage assetStorage);

    /**
     * Removes a static asset from the database.   Not typically used since Broadleaf 3.0 as the 
     * preferred method for storing assets is on a shared-filesystem.
     * 
     * @param assetStorage
     */
    void delete(StaticAssetStorage assetStorage);

    /**
     * @deprecated  Use createStaticAssetStorageFromFile instead.
     * 
     * @param uploadedFile
     * @return
     * @throws IOException
     */
    @Deprecated
    Blob createBlob(MultipartFile uploadedFile) throws IOException;

    /**
     * Creates a blob from an input stream with a given filesize.  This method wraps the call in a transactional tag.
     *
     * @param uploadedFileInputStream
     * @param fileSize
     * @return
     * @throws IOException
     */
    Blob createBlob(InputStream uploadedFileInputStream, long fileSize) throws IOException;

    /**
     * @param fullUrl
     * @param sandBox
     * @param parameterMap
     * @return
     * @throws Exception
     */
    Map<String, String> getCacheFileModel(String fullUrl, Map<String, String> parameterMap) throws Exception;

    /**
     * Persists the file to the DB or FileSystem according to the staticAsset's StorageType. Typically, the 
     * MultipartFile is passed in from a Controller like the AdminAssetUploadController 
     *  
     * @param file the uploaded file from the Spring controller
     * @param staticAsset the {@link StaticAsset} entity to obtain storage information from like file size and the file name,
     * usually created from {@link StaticAssetService#createStaticAssetFromFile(MultipartFile, Map)}
     * @throws IOException
     * @see {@link StaticAssetService#createStaticAssetFromFile(MultipartFile, Map)}
     */
    void createStaticAssetStorageFromFile(MultipartFile file, StaticAsset staticAsset) throws IOException;
    
    /**
     * Similar to {@link #createStaticAssetStorageFromFile(MultipartFile, StaticAsset)} except that this does not depend on
     * an uploaded file and can support more generic use cases
     * 
     * @param fileInputStream the input stream of the uploaded file
     * @param staticAsset the {@link StaticAsset} entity to obtain storage information from like file size and the file name,
     * usually created from {@link StaticAssetService#createStaticAsset(InputStream, String, long, Map)}
     * @throws IOException
     * @see {@link StaticAssetService#createStaticAsset(InputStream, String, long, Map)}
     */
    void createStaticAssetStorage(InputStream fileInputStream, StaticAsset staticAsset) throws IOException;

    /**
     * Validates given file with the maximum upload file size value
     * @param file
     * @throws IOException
     */
    void validateFileSize(MultipartFile file) throws IOException;

}
