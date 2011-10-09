/*
 * Copyright 2008-20011 the original author or authors.
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
import org.broadleafcommerce.openadmin.server.domain.SandBoxImpl;
import org.broadleafcommerce.persistence.EntityConfiguration;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bpolster.
 */
@Repository("blStaticAssetDao")
public class StaticAssetDaoImpl implements StaticAssetDao {

    private static SandBox DUMMY_SANDBOX = new SandBoxImpl();
    {
        DUMMY_SANDBOX.setId(-1l);
    }

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    protected String queryCacheableKey = "org.hibernate.cacheable";

    @Override
    public StaticAssetFolder readStaticAssetById(Long id) {
        return (StaticAssetFolder) em.find(entityConfiguration.lookupEntityClass(StaticAssetFolder.class.getName()), id);
    }

    @Override
    public StaticAsset readStaticAssetByFullUrl(String fullUrl, SandBox targetSandBox) {
        Query query2 = em.createNamedQuery("BC_READ_STATIC_ASSET_BY_FULL_URL");
        query2.setParameter("targetSandbox", targetSandBox);
        query2.setParameter("fullUrl", fullUrl);

        List<StaticAsset> results = query2.getResultList();
        if (CollectionUtils.isEmpty(results)) {
            return null;
        } else {
            return results.iterator().next();
        }
    }

    @Override
    public List<StaticAssetFolder> readStaticAssetFolderChildFolders(StaticAssetFolder parentFolder) {
        String queryPrefix = "BC_READ_";
        if (parentFolder == null) {
                queryPrefix = "BC_READ_NULL_";
        }
        Query query2 = em.createNamedQuery(queryPrefix + "STATIC_ASSET_FOLDER_CHILD_FOLDERS");
        if (parentFolder != null) {
            query2.setParameter("parentFolder", parentFolder);
        }
        List<StaticAssetFolder> childFolders = query2.getResultList();

        return childFolders;
    }

    @Override
    public List<StaticAsset> readStaticAssetFolderChildren(StaticAssetFolder parentFolder, SandBox userSandBox, SandBox productionSandBox) {
        String queryPrefix = "BC_READ_";
        if (parentFolder == null) {
                queryPrefix = "BC_READ_NULL_";
        }
        Query query = em.createNamedQuery(queryPrefix + "STATIC_ASSET_FOLDER_CHILD_PAGES");
        if (parentFolder != null) {
            query.setParameter("parentFolder", parentFolder);
        }
        query.setParameter("userSandbox", userSandBox == null ? DUMMY_SANDBOX : userSandBox);
        query.setParameter("productionSandbox", productionSandBox == null ? DUMMY_SANDBOX : productionSandBox);

        List<StaticAsset> childAssets = query.getResultList();
        filterStaticAssetsForSandbox(userSandBox, productionSandBox, childAssets);

        return childAssets;
    }

    private void filterStaticAssetsForSandbox(SandBox userSandBox, SandBox productionSandBox, List<StaticAsset> assetList) {
        if (userSandBox != null) {
            List<Long> removeIds = new ArrayList<Long>();
            for (StaticAsset asset : assetList) {
                if (asset.getOriginalAssetId() != null) {
                    removeIds.add(asset.getOriginalAssetId());
                }

                if (asset.getDeletedFlag()) {
                    removeIds.add(asset.getId());
                }
            }

            Iterator<StaticAsset> assetIterator = assetList.iterator();

            while (assetIterator.hasNext()) {
                StaticAsset asset = assetIterator.next();
                if (removeIds.contains(asset.getId())) {
                    assetIterator.remove();
                }
            }
        }
    }

    @Override
    public StaticAsset updateStaticAsset(StaticAsset asset) {
        StaticAssetFolder parentFolder = asset.getParentFolder();
        List<String> parentFolders = new ArrayList<String>();
        while (parentFolder != null) {
            parentFolders.add(parentFolder.getName());
            parentFolder = parentFolder.getParentFolder();
        }
        Collections.reverse(parentFolders);
        StringBuffer sb = new StringBuffer();
        sb.append("/");
        for (String folderName : parentFolders) {
            sb.append(folderName);
            sb.append("/");
        }
        sb.append(asset.getName());
        sb.append(".");
        sb.append(asset.getFileExtension());
        asset.setFullUrl(sb.toString());

        return em.merge(asset);
    }

    @Override
    public void delete(StaticAsset asset) {
        if (!em.contains(asset)) {
            asset = (StaticAsset) readStaticAssetById(asset.getId());
        }
        em.remove(asset);
    }

    @Override
    public StaticAssetFolder updateStaticAssetFolder(StaticAssetFolder staticAssetFolder) {
        return em.merge(staticAssetFolder);
    }

    @Override
    public StaticAsset addStaticAsset(StaticAsset clonedAsset) {
        return (StaticAsset) em.merge(clonedAsset);
    }

    @Override
    public StaticAssetFolder addStaticAssetFolder(StaticAssetFolder staticAssetFolder) {
        return (StaticAssetFolder) em.merge(staticAssetFolder);
    }

}
