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
package org.broadleafcommerce.cms.file.dao;

import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.domain.StaticAssetFolder;
import org.broadleafcommerce.openadmin.server.domain.SandBox;

import java.util.List;

/**
 * Created by bpolster.
 */
public interface StaticAssetDao {

    public StaticAssetFolder readStaticAssetById(Long id);

    public List<StaticAsset> readStaticAssetFolderChildren(StaticAssetFolder parentFolder, SandBox userSandbox, SandBox productionSandBox);

    public StaticAsset updateStaticAsset(StaticAsset asset);

    public void delete(StaticAsset asset);

    public StaticAssetFolder updateStaticAssetFolder(StaticAssetFolder staticAssetFolder);

    public StaticAsset addStaticAsset(StaticAsset clonedAsset);

    public StaticAssetFolder addStaticAssetFolder(StaticAssetFolder staticAssetFolder);

    public List<StaticAssetFolder> readStaticAssetFolderChildFolders(StaticAssetFolder parentFolder);

    public StaticAsset readStaticAssetByFullUrl(String fullUrl, SandBox targetSandBox);

}
