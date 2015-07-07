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

import java.util.List;

import org.broadleafcommerce.cms.file.domain.StaticAsset;

/**
 * Created by bpolster.
 */
public interface StaticAssetDao {

    public StaticAsset readStaticAssetById(Long id);
    
    public List<StaticAsset> readAllStaticAssets();

    public void delete(StaticAsset asset);

    public StaticAsset addOrUpdateStaticAsset(StaticAsset asset, boolean clearLevel1Cache);

    public StaticAsset readStaticAssetByFullUrl(String fullUrl);

}
