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

import org.apache.http.client.HttpClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.LBHttpSolrClient;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.web.BroadleafRequestContext;

import java.util.Collection;

/**
 * @author Nick Crum (ncrum)
 */
public class BroadleafCloudSolrClient extends CloudSolrClient {

    protected SolrConfiguration solrConfig;
    protected boolean reindexClient;

    public BroadleafCloudSolrClient(String zkHost) {
        super(zkHost);
    }

    public BroadleafCloudSolrClient(String zkHost, HttpClient httpClient) {
        super(zkHost, httpClient);
    }

    public BroadleafCloudSolrClient(Collection<String> zkHosts, String chroot) {
        super(zkHosts, chroot);
    }

    public BroadleafCloudSolrClient(Collection<String> zkHosts, String chroot, HttpClient httpClient) {
        super(zkHosts, chroot, httpClient);
    }

    public BroadleafCloudSolrClient(String zkHost, boolean updatesToLeaders) {
        super(zkHost, updatesToLeaders);
    }

    public BroadleafCloudSolrClient(String zkHost, boolean updatesToLeaders, HttpClient httpClient) {
        super(zkHost, updatesToLeaders, httpClient);
    }

    public BroadleafCloudSolrClient(String zkHost, LBHttpSolrClient lbClient) {
        super(zkHost, lbClient);
    }

    public BroadleafCloudSolrClient(String zkHost, LBHttpSolrClient lbClient, boolean updatesToLeaders) {
        super(zkHost, lbClient, updatesToLeaders);
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
