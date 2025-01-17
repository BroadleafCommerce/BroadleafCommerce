/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.search.service.solr.indexer;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.core.search.dao.CatalogStructure;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexCachedOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Command service for issuing (queuing) Catalog (re)index commands.
 * 
 * @author Kelly Tisdell
 *
 */
@Service("blCatalogSolrIndexUpdateService")
public class CatalogSolrIndexUpdateServiceImpl extends AbstractSolrIndexUpdateServiceImpl implements CatalogSolrIndexUpdateService {

    @Autowired
    public CatalogSolrIndexUpdateServiceImpl(
            @Qualifier("blSolrIndexQueueProvider") SolrIndexQueueProvider queueProvider, 
            @Qualifier("blCatalogSolrUpdateCommandHandler") CatalogSolrIndexCommandHandler commandHandler) {
        super("catalog", queueProvider, commandHandler);
    }

    @Override
    public void rebuildIndex(Catalog catalog) throws ServiceException {
        CatalogReindexCommand cmd = new CatalogReindexCommand(catalog.getId());
        scheduleCommand(cmd);
    }

    @Override
    public void rebuildIndex(Site site) throws ServiceException {
        SiteReindexCommand cmd = new SiteReindexCommand(site.getId());
        scheduleCommand(cmd);
    }

    @Override
    public void rebuildIndex() throws ServiceException {
        FullReindexCommand cmd = new FullReindexCommand();
        scheduleCommand(cmd);
    }

    @Override
    public void performCachedOperation(SolrIndexCachedOperation.CacheOperation cacheOperation) throws ServiceException {
        try {
            CatalogStructure cache = new CatalogStructure();
            SolrIndexCachedOperation.setCache(cache);
            cacheOperation.execute();
        } finally {
            SolrIndexCachedOperation.clearCache();
        }
    }
}
