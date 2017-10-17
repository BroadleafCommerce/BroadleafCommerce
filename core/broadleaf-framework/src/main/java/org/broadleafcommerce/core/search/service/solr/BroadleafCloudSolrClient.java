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
package org.broadleafcommerce.core.search.service.solr;

import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.web.BroadleafRequestContext;

/**
 * @author Nick Crum (ncrum)
 */
public class BroadleafCloudSolrClient extends CloudSolrClient {

    private static final long serialVersionUID = 1L;
    protected SolrConfiguration solrConfig;
    protected boolean reindexClient;

    public BroadleafCloudSolrClient(Builder builder) {
        super(builder);
    }

    @Override
    public String getDefaultCollection() {
        if (solrConfig != null && solrConfig.isSiteCollections()) {
            Site site = BroadleafRequestContext.getBroadleafRequestContext().getNonPersistentSite();
            return (reindexClient) ? solrConfig.getSiteReindexAliasName(site) : solrConfig.getSiteAliasName(site);
        }

        return super.getDefaultCollection();
    }

    public boolean isReindexClient() {
        return reindexClient;
    }

    public void setReindexClient(boolean reindexClient) {
        this.reindexClient = reindexClient;
    }
    
    public SolrConfiguration getSolrConfig() {
        return solrConfig;
    }
    
    public void setSolrConfig(SolrConfiguration solrConfig) {
        this.solrConfig = solrConfig;
    }
}
