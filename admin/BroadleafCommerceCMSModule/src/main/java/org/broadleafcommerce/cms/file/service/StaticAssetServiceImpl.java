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
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bpolster.
 */
@Service("blStaticAssetService")
public class StaticAssetServiceImpl implements StaticAssetService {

    private static final Log LOG = LogFactory.getLog(StaticAssetServiceImpl.class);

    @Resource(name="blStaticAssetDao")
    protected StaticAssetDao staticAssetDao;

    @Override
    public StaticAssetFolder findStaticAssetById(Long id) {
        return staticAssetDao.readStaticAssetById(id);
    }

    @Override
    public List<StaticAssetFolder> findStaticAssetFolderChildren(SandBox sandbox, StaticAssetFolder parentFolder, String localeName) {
        SandBox productionSandbox = null;
        SandBox userSandbox = sandbox;

        if (localeName == null) {
            localeName = "default";
        }

        if (sandbox != null && sandbox.getSite() != null && sandbox.getSite().getProductionSandbox() != null) {
            productionSandbox = sandbox.getSite().getProductionSandbox();
            if (userSandbox.getId().equals(productionSandbox.getId())) {
                userSandbox = null;
            }
        }

        List<StaticAssetFolder> staticAssetFolders =  staticAssetDao.readStaticAssetFolderChildren(parentFolder, localeName, userSandbox, productionSandbox);
        return staticAssetFolders;
    }

    @Override
    public StaticAsset addStaticAsset(StaticAsset staticAsset, StaticAssetFolder parentFolder, SandBox destinationSandbox) {
        staticAsset.setSandbox(destinationSandbox);

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
        sb.append(staticAsset.getName());
        staticAsset.setFullUrl(sb.toString());

        return staticAssetDao.updateStaticAsset(staticAsset);
    }

    @Override
    public StaticAsset updateStaticAsset(StaticAsset staticAsset, SandBox destSandbox) {
        if (checkForSandboxMatch(staticAsset.getSandbox(), destSandbox)) {
            return staticAssetDao.updateStaticAsset(staticAsset);
        } else if (checkForProductionSandbox(staticAsset.getSandbox())) {
            // Moving from production to destSandbox
            StaticAsset clonedAsset = staticAsset.cloneEntity();
            clonedAsset.setOriginalAssetId(staticAsset.getId());
            clonedAsset.setSandbox(destSandbox);
            return staticAssetDao.addStaticAsset(clonedAsset);
        } else if (checkForProductionSandbox(destSandbox)) {
            // Moving to production
            StaticAsset existingAsset = (StaticAsset) findStaticAssetById(staticAsset.getOriginalAssetId());
            existingAsset.setArchivedFlag(true);
            staticAssetDao.updateStaticAsset(existingAsset);

            if (staticAsset.getDeletedFlag() == true) {
                staticAssetDao.delete(staticAsset);
            }
            return null;
        } else {
            staticAsset.setSandbox(destSandbox);
            return staticAssetDao.updateStaticAsset(staticAsset);
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

}
