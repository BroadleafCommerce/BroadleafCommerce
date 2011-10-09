/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.cms.file.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.file.dao.StaticAssetStorageDao;
import org.broadleafcommerce.cms.file.domain.StaticAssetStorage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Blob;

/**
 * Created by jfischer.
 */
@Service("blStaticAssetStorageService")
public class StaticAssetStorageServiceImpl implements StaticAssetStorageService {

    private static final Log LOG = LogFactory.getLog(StaticAssetStorageServiceImpl.class);

    @Resource(name="blStaticAssetStorageDao")
    protected StaticAssetStorageDao staticAssetStorageDao;

    @Override
    public StaticAssetStorage findStaticAssetStorageById(Long id) {
        return staticAssetStorageDao.readStaticAssetStorageById(id);
    }

    @Override
    public StaticAssetStorage create() {
        return staticAssetStorageDao.create();
    }

    @Override
    public StaticAssetStorage readStaticAssetStorageByStaticAssetId(Long id) {
        return staticAssetStorageDao.readStaticAssetStorageByStaticAssetId(id);
    }

    @Override
    public StaticAssetStorage save(StaticAssetStorage assetStorage) {
        return staticAssetStorageDao.save(assetStorage);
    }

    @Override
    public void delete(StaticAssetStorage assetStorage) {
        staticAssetStorageDao.delete(assetStorage);
    }

    @Override
    public Blob createBlob(MultipartFile uploadedFile) throws IOException {
        return staticAssetStorageDao.createBlob(uploadedFile);
    }
}
