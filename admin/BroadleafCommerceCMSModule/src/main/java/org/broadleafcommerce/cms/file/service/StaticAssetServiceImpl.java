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
import org.broadleafcommerce.cms.file.dao.StaticAssetDao;
import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.domain.StaticAssetFolder;
import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.openadmin.server.dao.SandBoxItemDao;
import org.broadleafcommerce.openadmin.server.domain.*;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

    /**
     * Retuns content items for the passed in sandbox that match the passed in criteria.
     * <p/>
     * Merges the sandbox content with the production content.
     *
     * @param sandbox      - the sandbox to find structured content items (null indicates items that are in production for
     *                     sites that are single tenant.
     * @return
     */
    @Override
    public List<StaticAsset> findStaticAssetFolderChildren(SandBox sandbox, Criteria c) {
        c.add(Restrictions.eq("archivedFlag", false));
        c.add(Restrictions.eq("folderFlag", false));

        if (sandbox == null) {
            // Query is hitting the production sandbox.
            c.add(Restrictions.isNull("sandbox"));
            return c.list();
        } else {
            Criterion originalSandboxExpression = Restrictions.eq("originalSandBox", sandbox);
            Criterion currentSandboxExpression = Restrictions.eq("sandbox", sandbox);
            Criterion productionSandboxExpression = null;
            if (sandbox.getSite() == null || sandbox.getSite().getProductionSandbox() == null) {
                productionSandboxExpression = Restrictions.isNull("sandbox");
            } else {
                if (!SandBoxType.PRODUCTION.equals(sandbox.getSandBoxType())) {
                    productionSandboxExpression = Restrictions.eq("sandbox", sandbox.getSite().getProductionSandbox());
                }
            }

            if (productionSandboxExpression != null) {
                c.add(Restrictions.or(Restrictions.or(currentSandboxExpression,productionSandboxExpression), originalSandboxExpression));
            } else {
                c.add(Restrictions.or(currentSandboxExpression, originalSandboxExpression));
            }

            List<StaticAsset> resultList = (List<StaticAsset>) c.list();

            // Iterate once to build the map
            LinkedHashMap returnItems = new LinkedHashMap<Long,StructuredContent>();
            for (StaticAsset asset : resultList) {
                returnItems.put(asset.getId(), asset);
            }

            // Iterate to remove items from the final list
            for (StaticAsset asset : resultList) {
                if (asset.getDeletedFlag()) {
                    returnItems.remove(asset.getId());
                }
            }
            return new ArrayList<StaticAsset>(returnItems.values());
        }

    }

    /**
     * Returns the count of items that match the passed in criteria.
     *
     * This counts the items in production + the new items in the sandbox - the
     * existing items that have been deleted in the sandbox.
     *
     * @return the count of items in this sandbox that match the passed in Criteria
     */
    @Override
    public Long countStaticAssetFolderChildren(SandBox sandbox, Criteria c) {
        c.add(Restrictions.eq("archivedFlag", false));
        c.add(Restrictions.eq("folderFlag", false));
        c.setProjection(Projections.rowCount());

        if (sandbox == null) {
            // Query is hitting the production sandbox.
            c.add(Restrictions.isNull("sandbox"));
            return (Long) c.uniqueResult();
        } else {
            Criterion originalSandboxExpression = Restrictions.eq("originalSandBox", sandbox);
            Criterion currentSandboxExpression = Restrictions.eq("sandbox", sandbox);
            Criterion productionSandboxExpression;
            if (sandbox.getSite() == null || sandbox.getSite().getProductionSandbox() == null) {
                productionSandboxExpression = Restrictions.isNull("sandbox");
            } else {
                // Query is hitting the production sandbox.
                if (sandbox.getId().equals(sandbox.getSite().getProductionSandbox().getId())) {
                    return (Long) c.uniqueResult();
                }
                productionSandboxExpression = Restrictions.eq("sandbox", sandbox.getSite().getProductionSandbox());
            }

            c.add(Restrictions.or(Restrictions.or(currentSandboxExpression,productionSandboxExpression), originalSandboxExpression));

            Long resultCount = (Long) c.list().get(0);
            Long deletedCount = 0L;

            // count deleted items
            c.add(Restrictions.and(Restrictions.eq("deletedFlag", true),Restrictions.eq("sandbox", sandbox)));
            deletedCount = (Long) c.list().get(0);

            return resultCount - deletedCount;
        }
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
        staticAsset.setFullUrl(parentUrl + "/" + staticAsset.getName() + "." + staticAsset.getFileExtension());
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
            staticAsset.setFullUrl(parentUrl + "/" + staticAsset.getName() + "." + staticAsset.getFileExtension());
            return staticAssetDao.updateStaticAsset(staticAsset);
        } else if (checkForProductionSandbox(staticAsset.getSandbox())) {
            // Moving from production to destSandbox
            StaticAsset clonedAsset = staticAsset.cloneEntity();
            clonedAsset.setOriginalAssetId(staticAsset.getId());
            clonedAsset.setSandbox(destSandbox);
            clonedAsset.setFullUrl(parentUrl + "/" + clonedAsset.getName() + "." + clonedAsset.getFileExtension());
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
            boolean productionSandBox = isProductionSandBox(destinationSandBox);
            if (productionSandBox) {
                asset.setLockedFlag(false);
            } else {
                asset.setLockedFlag(true);
            }
            if (productionSandBox && asset.getOriginalAssetId() != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Asset promoted to production.  " + asset.getId() + ".  Archiving original asset " + asset.getOriginalAssetId());
                }
                StaticAsset originalAsset = (StaticAsset) staticAssetDao.readStaticAssetById(sandBoxItem.getTemporaryItemId());
                originalAsset.setArchivedFlag(Boolean.TRUE);
                staticAssetDao.updateStaticAsset(originalAsset);
                asset.setOriginalAssetId(null);
            }
        }
        if (asset.getOriginalSandBox() == null) {
            asset.setOriginalSandBox(asset.getSandbox());
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
            asset.setOriginalSandBox(null);
            asset.setLockedFlag(false);
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
            asset.setLockedFlag(false);
            staticAssetDao.updateStaticAsset(asset);

            StaticAsset originalAsset = (StaticAsset) staticAssetDao.readStaticAssetById(sandBoxItem.getOriginalItemId());
            originalAsset.setLockedFlag(false);
            staticAssetDao.updateStaticAsset(originalAsset);
        }
    }
}
