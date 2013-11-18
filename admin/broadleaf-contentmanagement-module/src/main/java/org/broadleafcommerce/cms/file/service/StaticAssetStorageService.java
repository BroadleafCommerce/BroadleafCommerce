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
import org.broadleafcommerce.cms.file.domain.StaticAssetStorage;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.site.domain.Site;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.util.Map;

/**
 * @author Jeff Fischer
 *
 */
public interface StaticAssetStorageService {

    StaticAssetStorage findStaticAssetStorageById(Long id);

    /**
     * @deprecated   Use createStaticAssetStorageFromFile instead.
     * @return
     */
    StaticAssetStorage create();

    StaticAssetStorage readStaticAssetStorageByStaticAssetId(Long id);

    StaticAssetStorage save(StaticAssetStorage assetStorage);

    void delete(StaticAssetStorage assetStorage);

    /**
     * @deprecated  Use createStaticAssetStorageFromFile instead.
     * 
     * @param uploadedFile
     * @return
     * @throws IOException
     */
    Blob createBlob(MultipartFile uploadedFile) throws IOException;

    /**
     * Stores the file on the filesystem by performing an MD5 hash of the 
     * the staticAsset.fullUrl.
     * 
     * To ensure that files can be stored and accessed in an efficient manner, the 
     * system creates directories based on the characters in the hash.   
     * 
     * For example, if the URL is /product/myproductimage.jpg, then the MD5 would be
     * 35ec52a8dbd8cf3e2c650495001fe55f resulting in the following file on the filesystem
     * {assetFileSystemPath}/35/ec/myproductimage.jpg.
     * 
     * If there is a "siteId" in the BroadleafRequestContext then the site is also distributed
     * using a similar algorithm but the system attempts to keep images for sites in their own
     * directory resulting in an extra two folders required to reach any given product.   So, for
     * site with id 125, the system will MD5 "/site-125" in order to build the URL string.   "/site-125" has an md5
     * string of "7fde295edac6ca7f85d0368ea741b241".    
     * 
     * So, in this case with the above product URL in site125, the full URL on the filesystem
     * will be:
     * 
     * {assetFileSystemPath}/7f/site-125/35/ec/myproductimage.jpg.
     * 
     * This algorithm has the following benefits:
     * - Efficient file-system storage with
     * - Balanced tree of files that supports 10 million files
     * 
     * If support for more files is needed, implementors should consider one of the following approaches:
     * 1.  Overriding the maxGeneratedFileSystemDirectories property from its default of 2 to 3
     * 2.  Overriding this method to introduce an alternate approach
     * 
     * @param fullUrl The URL used to represent an asset for which a name on the fileSystem is desired.
     * @param useSharedPath If false, the system will generate a path using {@link Site} information if available.
     * 
     * @return
     */
    String generateStorageFileName(String fullUrl, boolean useSharedPath);

    /**
     * By default, delegates a call to {@link #generateStorageFileName(String)} using <code>staticAsset.getFullUrl()</code>
     * as the passed in argument. 
     * 
     * @param staticAsset StaticAsset for which a filename is desired.
     * @param useSharedPath If false, the system will generate a path using {@link Site} information if available.
     * @return
     */
    String generateStorageFileName(StaticAsset staticAsset, boolean useSharedPath);

    Map<String, String> getCacheFileModel(String fullUrl, SandBox sandBox, Map<String, String> parameterMap) throws Exception;

    /**
     * Persists the file being based in according to the staticAsset's StorageType.
     *  
     * @param file
     * @param id
     * @throws IOException
     */
    void createStaticAssetStorageFromFile(MultipartFile file, StaticAsset staticAsset) throws IOException;

}
