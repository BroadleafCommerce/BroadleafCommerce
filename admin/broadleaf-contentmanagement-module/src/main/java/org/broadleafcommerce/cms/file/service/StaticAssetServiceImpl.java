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

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.file.dao.StaticAssetDao;
import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.openadmin.server.dao.SandBoxItemDao;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItemType;
import org.broadleafcommerce.openadmin.server.domain.SandBoxOperationType;
import org.broadleafcommerce.openadmin.server.domain.SandBoxType;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

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
    public StaticAsset findStaticAssetById(Long id) {
        return staticAssetDao.readStaticAssetById(id);
    }

    @Override
    public StaticAsset findStaticAssetByFullUrl(String fullUrl, SandBox targetSandBox) {
        try {
            fullUrl = URLDecoder.decode(fullUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding to decode fullUrl", e);
        }
        return staticAssetDao.readStaticAssetByFullUrl(fullUrl, targetSandBox);
    }

    @Override
    public StaticAsset addStaticAsset(StaticAsset staticAsset, SandBox destinationSandbox) {
        staticAsset.setSandbox(destinationSandbox);
        staticAsset.setArchivedFlag(false);
        staticAsset.setDeletedFlag(false);
        StaticAsset newAsset = staticAssetDao.addOrUpdateStaticAsset(staticAsset, true);
        if (! isProductionSandBox(destinationSandbox)) {
            sandBoxItemDao.addSandBoxItem(destinationSandbox, SandBoxOperationType.ADD, SandBoxItemType.STATIC_ASSET, newAsset.getFullUrl(), newAsset.getId(), null);
        }
        return newAsset;
    }

    @Override
    public StaticAsset updateStaticAsset(StaticAsset staticAsset, SandBox destSandbox) {
        if (staticAsset.getLockedFlag()) {
            throw new IllegalArgumentException("Unable to update a locked record");
        }

        if (checkForSandboxMatch(staticAsset.getSandbox(), destSandbox)) {
            if (staticAsset.getDeletedFlag()) {
                SandBoxItem item = sandBoxItemDao.retrieveBySandboxAndTemporaryItemId(staticAsset.getSandbox(), SandBoxItemType.STATIC_ASSET, staticAsset.getId());
                if (staticAsset.getOriginalAssetId() == null) {
                    // This page was added in this sandbox and now needs to be deleted.
                    staticAsset.setArchivedFlag(true);
                    item.setArchivedFlag(true);
                } else {
                    // This page was being updated but now is being deleted - so change the
                    // sandbox operation type to deleted
                    item.setSandBoxOperationType(SandBoxOperationType.DELETE);
                    sandBoxItemDao.updateSandBoxItem(item);
                }
            }
            return staticAssetDao.addOrUpdateStaticAsset(staticAsset, true);
        } else if (isProductionSandBox(staticAsset.getSandbox())) {
            // Move from production to destSandbox
            StaticAsset clonedAsset = staticAsset.cloneEntity();
            clonedAsset.setOriginalAssetId(staticAsset.getId());
            clonedAsset.setSandbox(destSandbox);
            StaticAsset returnAsset = staticAssetDao.addOrUpdateStaticAsset(clonedAsset, true);

            StaticAsset prod = findStaticAssetById(staticAsset.getId());
            prod.setLockedFlag(true);
            staticAssetDao.addOrUpdateStaticAsset(prod, false);

            SandBoxOperationType type = SandBoxOperationType.UPDATE;
            if (clonedAsset.getDeletedFlag()) {
                type = SandBoxOperationType.DELETE;
            }

            sandBoxItemDao.addSandBoxItem(destSandbox, type, SandBoxItemType.STATIC_ASSET, returnAsset.getFullUrl(), returnAsset.getId(), returnAsset.getOriginalAssetId());
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

    // Returns true if the dest sandbox is production.
    private boolean isProductionSandBox(SandBox dest) {
        if (dest == null) {
            return true;
        } else {
            return SandBoxType.PRODUCTION.equals(dest.getSandBoxType());
        }
    }

    @Override
    public void deleteStaticAsset(StaticAsset staticAsset, SandBox destinationSandbox) {
        staticAsset.setDeletedFlag(true);
        updateStaticAsset(staticAsset, destinationSandbox);
    }

    @Override
    public Long countAssets(SandBox sandbox, Criteria criteria) {
        criteria.add(Restrictions.eq("archivedFlag", false));
        criteria.setProjection(Projections.rowCount());

        if (sandbox == null) {
            // Query is hitting the production sandbox.
            criteria.add(Restrictions.isNull("sandbox"));
            return (Long) criteria.uniqueResult();
        } else {
            Criterion originalSandboxExpression = Restrictions.eq("originalSandBox", sandbox);
            Criterion currentSandboxExpression = Restrictions.eq("sandbox", sandbox);
            Criterion productionSandboxExpression;
            if (sandbox.getSite() == null || sandbox.getSite().getProductionSandbox() == null) {
                productionSandboxExpression = Restrictions.isNull("sandbox");
            } else {
                // Query is hitting the production sandbox.
                if (sandbox.getId().equals(sandbox.getSite().getProductionSandbox().getId())) {
                    return (Long) criteria.uniqueResult();
                }
                productionSandboxExpression = Restrictions.eq("sandbox", sandbox.getSite().getProductionSandbox());
            }

            criteria.add(Restrictions.or(Restrictions.or(currentSandboxExpression, productionSandboxExpression), originalSandboxExpression));

            Long resultCount = (Long) criteria.list().get(0);
            Long updatedCount = 0L;
            Long deletedCount = 0L;

            // count updated items
            criteria.add(Restrictions.and(Restrictions.isNotNull("originalAssetId"),Restrictions.or(currentSandboxExpression, originalSandboxExpression)));
            updatedCount = (Long) criteria.list().get(0);

            // count deleted items
            criteria.add(Restrictions.and(Restrictions.eq("deletedFlag", true),Restrictions.or(currentSandboxExpression, originalSandboxExpression)));
            deletedCount = (Long) criteria.list().get(0);

            return resultCount - updatedCount - deletedCount;
        }
    }

    @Override
    public List<StaticAsset> findAssets(SandBox sandbox, Criteria criteria) {
        criteria.add(Restrictions.eq("archivedFlag", false));

        if (sandbox == null) {
            // Query is hitting the production sandbox.
            criteria.add(Restrictions.isNull("sandbox"));
            return (List<StaticAsset>) criteria.list();
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
                criteria.add(Restrictions.or(Restrictions.or(currentSandboxExpression, productionSandboxExpression), originalSandboxExpression));
            } else {
                criteria.add(Restrictions.or(currentSandboxExpression, originalSandboxExpression));
            }

            List<StaticAsset> resultList = (List<StaticAsset>) criteria.list();

            // Iterate once to build the map
            LinkedHashMap returnItems = new LinkedHashMap<Long,Page>();
            for (StaticAsset page : resultList) {
                returnItems.put(page.getId(), page);
            }

            // Iterate to remove items from the final list
            for (StaticAsset asset : resultList) {
                if (asset.getOriginalAssetId() != null) {
                    returnItems.remove(asset.getOriginalAssetId());
                }

                if (asset.getDeletedFlag()) {
                    returnItems.remove(asset.getId());
                }
            }
            return new ArrayList<StaticAsset>(returnItems.values());
        }
    }

    @Override
    public void itemPromoted(SandBoxItem sandBoxItem, SandBox destinationSandBox) {
        if (! SandBoxItemType.STATIC_ASSET.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }
        StaticAsset asset = staticAssetDao.readStaticAssetById(sandBoxItem.getTemporaryItemId());

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
                StaticAsset originalAsset = staticAssetDao.readStaticAssetById(sandBoxItem.getTemporaryItemId());
                originalAsset.setArchivedFlag(Boolean.TRUE);
                staticAssetDao.addOrUpdateStaticAsset(originalAsset, false);
                asset.setOriginalAssetId(null);

                if (asset.getDeletedFlag()) {
                    asset.setArchivedFlag(Boolean.TRUE);
                }
            }
        }
        if (asset.getOriginalSandBox() == null) {
            asset.setOriginalSandBox(asset.getSandbox());
        }
        asset.setSandbox(destinationSandBox);
        staticAssetDao.addOrUpdateStaticAsset(asset, false);
    }

    @Override
    public void itemRejected(SandBoxItem sandBoxItem, SandBox destinationSandBox) {
        if (! SandBoxItemType.STATIC_ASSET.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }
        StaticAsset asset = staticAssetDao.readStaticAssetById(sandBoxItem.getTemporaryItemId());

        if (asset != null) {
            asset.setSandbox(destinationSandBox);
            asset.setOriginalSandBox(null);
            asset.setLockedFlag(false);
            staticAssetDao.addOrUpdateStaticAsset(asset, false);
        }
    }

    @Override
    public void itemReverted(SandBoxItem sandBoxItem) {
        if (! SandBoxItemType.STATIC_ASSET.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }
        StaticAsset asset = staticAssetDao.readStaticAssetById(sandBoxItem.getTemporaryItemId());

        if (asset != null) {
            asset.setArchivedFlag(Boolean.TRUE);
            asset.setLockedFlag(false);
            staticAssetDao.addOrUpdateStaticAsset(asset, false);

            StaticAsset originalAsset = staticAssetDao.readStaticAssetById(sandBoxItem.getOriginalItemId());
            originalAsset.setLockedFlag(false);
            staticAssetDao.addOrUpdateStaticAsset(originalAsset, false);
        }
    }
}
