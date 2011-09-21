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
package org.broadleafcommerce.cms.file.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.file.dao.StaticAssetDao;
import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.domain.StaticAssetFolder;
import org.broadleafcommerce.openadmin.server.dao.SandBoxItemDao;
import org.broadleafcommerce.openadmin.server.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by bpolster.
 */
@Service("blStaticAssetService")
public class StaticAssetServiceImpl implements StaticAssetService {

    private static final Log LOG = LogFactory.getLog(StaticAssetServiceImpl.class);

    @Resource(name="blStaticAssetDao")
    protected StaticAssetDao staticAssetDao;

    @Resource(name="blSandBoxItemDao")
    protected SandBoxItemDao sandBoxItemDao;

    @Resource(name="blStaticAssetStorageService")
    protected StaticAssetStorageService staticAssetStorageService;

    @Override
    public StaticAssetFolder findStaticAssetById(Long id) {
        return staticAssetDao.readStaticAssetById(id);
    }

    @Override
    public StaticAsset findStaticAssetByFullUrl(String fullUrl, SandBox targetSandBox) {
        return staticAssetDao.readStaticAssetByFullUrl(fullUrl, targetSandBox);
    }

    @Override
    public List<StaticAsset> findStaticAssetFolderChildren(SandBox sandbox, StaticAssetFolder parentFolder) {
        SandBox productionSandbox = null;
        SandBox userSandbox = sandbox;

        if (sandbox != null && sandbox.getSite() != null && sandbox.getSite().getProductionSandbox() != null) {
            productionSandbox = sandbox.getSite().getProductionSandbox();
            if (userSandbox.getId().equals(productionSandbox.getId())) {
                userSandbox = null;
            }
        }

        List<StaticAsset> staticAssetFolders =  staticAssetDao.readStaticAssetFolderChildren(parentFolder, userSandbox, productionSandbox);
        return staticAssetFolders;
    }

    @Override
    public List<StaticAssetFolder> findStaticAssetFolderChildFolders(StaticAssetFolder parentFolder) {
        List<StaticAssetFolder> staticAssetFolders =  staticAssetDao.readStaticAssetFolderChildFolders(parentFolder);
        return staticAssetFolders;
    }

    @Override
    public StaticAsset addStaticAsset(StaticAsset staticAsset, StaticAssetFolder parentFolder, SandBox destinationSandbox) {
        staticAsset.setSandbox(destinationSandbox);
        staticAsset.setParentFolder(parentFolder);
        String parentUrl = (parentFolder == null ? "" : parentFolder.getFullUrl());
        staticAsset.setFullUrl(parentUrl + "/" + staticAsset.getName());
        StaticAsset newAsset =  staticAssetDao.updateStaticAsset(staticAsset);
        if (! isProductionSandBox(destinationSandbox)) {
            sandBoxItemDao.addSandBoxItem(destinationSandbox, SandBoxOperationType.ADD, SandBoxItemType.STATIC_ASSET, newAsset.getFullUrl(), newAsset.getId(), null);
        }
        return newAsset;
    }

    @Override
    public StaticAsset updateStaticAsset(StaticAsset staticAsset, SandBox destSandbox) {
        String parentUrl = (staticAsset.getParentFolder() == null ? "" : staticAsset.getParentFolder().getFullUrl());

        if (checkForSandboxMatch(staticAsset.getSandbox(), destSandbox)) {
            staticAsset.setFullUrl(parentUrl + "/" + staticAsset.getName());
            return staticAssetDao.updateStaticAsset(staticAsset);
        } else if (checkForProductionSandbox(staticAsset.getSandbox())) {
            // Moving from production to destSandbox
            StaticAsset clonedAsset = staticAsset.cloneEntity();
            clonedAsset.setOriginalAssetId(staticAsset.getId());
            clonedAsset.setSandbox(destSandbox);
            clonedAsset.setFullUrl(parentUrl + "/" + clonedAsset.getName());
            StaticAsset returnAsset =  staticAssetDao.addStaticAsset(clonedAsset);

            sandBoxItemDao.addSandBoxItem(destSandbox, SandBoxOperationType.UPDATE, SandBoxItemType.STATIC_ASSET, returnAsset.getFullUrl(), returnAsset.getId(), returnAsset.getOriginalAssetId());
            return returnAsset;
        } else {
            // This should happen via a promote, revert, or reject in the sandbox service
            throw new IllegalArgumentException("Update called when promote or reject was expected.");
        }
    }

    // Returns true if the src and dest sandbox are the same.
    private boolean checkForSandboxMatch(SandBox src, SandBox dest) {
        if (src != null) {
            if (dest != null) {
                return src.getId().equals(dest.getId());
            }
        }
        return (src == null && dest == null);
    }

    // Returns true if the dest sandbox is production.
    private boolean checkForProductionSandbox(SandBox dest) {
        boolean productionSandbox = false;

        if (dest == null) {
            productionSandbox = true;
        } else {
            if (dest.getSite() != null && dest.getSite().getProductionSandbox() != null && dest.getSite().getProductionSandbox().getId() != null) {
                productionSandbox = dest.getSite().getProductionSandbox().getId().equals(dest.getId());
            }
        }

        return productionSandbox;
    }

    @Override
    public void deleteStaticAsset(StaticAsset staticAsset, SandBox destinationSandbox) {
        staticAsset.setDeletedFlag(true);
        updateStaticAsset(staticAsset, destinationSandbox);
    }

    @Override
    public void deleteStaticAssetFolder(StaticAssetFolder staticAssetFolder) {
        if (!staticAssetFolder.getSubFolders().isEmpty()) {
            throw new UnsupportedOperationException("Cannot delete folder with children.");
        } else {
            staticAssetFolder.setDeletedFlag(true);
            staticAssetDao.updateStaticAssetFolder(staticAssetFolder);
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public StaticAssetFolder addStaticAssetFolder(StaticAssetFolder staticAssetFolder, StaticAssetFolder parentStaticAssetFolder) {
        staticAssetFolder.setParentFolder(parentStaticAssetFolder);
        return staticAssetDao.addStaticAssetFolder(staticAssetFolder);
    }

    // Returns true if the dest sandbox is production.
    private boolean isProductionSandBox(SandBox dest) {
        if (dest == null) {
            return true;
        } else {
            return SandBoxType.PRODUCTION.equals(dest.getSandBoxType());
        }
    }

    @Override
    public void itemPromoted(SandBoxItem sandBoxItem, SandBox destinationSandBox) {
        if (! SandBoxItemType.STATIC_ASSET.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }
        StaticAsset asset = (StaticAsset) staticAssetDao.readStaticAssetById(sandBoxItem.getTemporaryItemId());

        if (asset == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Asset not found " + sandBoxItem.getTemporaryItemId());
            }
        } else {
            if (isProductionSandBox(destinationSandBox) && asset.getOriginalAssetId() != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Asset promoted to production.  " + asset.getId() + ".  Archiving original asset " + asset.getOriginalAssetId());
                }
                StaticAsset originalAsset = (StaticAsset) staticAssetDao.readStaticAssetById(sandBoxItem.getTemporaryItemId());
                originalAsset.setArchivedFlag(Boolean.TRUE);
                staticAssetDao.updateStaticAsset(originalAsset);
                asset.setOriginalAssetId(null);
            }
        }
        asset.setSandbox(destinationSandBox);
        staticAssetDao.updateStaticAsset(asset);
    }

    @Override
    public void itemRejected(SandBoxItem sandBoxItem, SandBox destinationSandBox) {
        if (! SandBoxItemType.STATIC_ASSET.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }
        StaticAsset asset = (StaticAsset) staticAssetDao.readStaticAssetById(sandBoxItem.getTemporaryItemId());


        if (asset != null) {
            asset.setSandbox(destinationSandBox);
            staticAssetDao.updateStaticAsset(asset);
        }
    }

    @Override
    public void itemReverted(SandBoxItem sandBoxItem) {
        if (! SandBoxItemType.STATIC_ASSET.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }
        StaticAsset asset = (StaticAsset) staticAssetDao.readStaticAssetById(sandBoxItem.getTemporaryItemId());

        if (asset != null) {
            asset.setArchivedFlag(Boolean.TRUE);
            staticAssetDao.updateStaticAsset(asset);
        }
    }
}
