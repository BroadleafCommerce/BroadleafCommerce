/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.cms.file.service;

import org.broadleafcommerce.cms.file.domain.StaticAssetStorage;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
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

    StaticAssetStorage create();

    StaticAssetStorage readStaticAssetStorageByStaticAssetId(Long id);

    StaticAssetStorage save(StaticAssetStorage assetStorage);

    void delete(StaticAssetStorage assetStorage);

    public Blob createBlob(MultipartFile uploadedFile) throws IOException;

    public Map<String, String> getCacheFileModel(String fullUrl, SandBox sandBox, Map<String, String> parameterMap) throws Exception;

}
