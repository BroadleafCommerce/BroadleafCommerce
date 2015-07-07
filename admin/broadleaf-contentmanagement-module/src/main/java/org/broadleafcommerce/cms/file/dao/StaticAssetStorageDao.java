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
package org.broadleafcommerce.cms.file.dao;

import org.broadleafcommerce.cms.file.domain.StaticAssetStorage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;


public interface StaticAssetStorageDao {
    StaticAssetStorage create();

    StaticAssetStorage readStaticAssetStorageById(Long id);

    StaticAssetStorage readStaticAssetStorageByStaticAssetId(Long id);

    StaticAssetStorage save(StaticAssetStorage assetStorage);

    void delete(StaticAssetStorage assetStorage);

    Blob createBlob(MultipartFile uploadedFile) throws IOException;
    
    /**
     * Overloaded method for {@link #createBlob(MultipartFile)} just in case you do not have the Spring {@link MultipartFile}
     * and are dealing with an already-uploadd file
     * 
     * @param uploadedFileInputStream the input stream of the file the blob should be created from
     * @param fileSize the size of the file which is represented by <b>uploadedFileInputStream</b>, in bytes
     * @return
     * @throws IOException
     */
    Blob createBlob(InputStream uploadedFileInputStream, long fileSize) throws IOException;
    
}
