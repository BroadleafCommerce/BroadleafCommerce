/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.service.solr.index;

import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * @author Jeff Fischer
 */
@Service("blSolrIndexStatusService")
public class SolrIndexStatusServiceImpl implements SolrIndexStatusService {

    @Resource(name="blSolrIndexStatusProviders")
    List<SolrIndexStatusProvider> providers;

    @Override
    public synchronized void setIndexStatus(IndexStatusInfo status) {
        for (SolrIndexStatusProvider provider : providers) {
            provider.handleUpdateIndexStatus(status);
        }
    }

    @Override
    public synchronized IndexStatusInfo getIndexStatus() {
        IndexStatusInfo status = getSeedStatusInstance();
        for (SolrIndexStatusProvider provider : providers) {
            provider.readIndexStatus(status);
        }
        return status;
    }

    @Override
    public IndexStatusInfo getSeedStatusInstance() {
        return new IndexStatusInfoImpl();
    }
}
