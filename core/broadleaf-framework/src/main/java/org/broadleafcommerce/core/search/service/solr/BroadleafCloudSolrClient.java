/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
        if (SolrContext.isSiteCollections()) {
            Site site = BroadleafRequestContext.getBroadleafRequestContext().getNonPersistentSite();
            return (reindexClient) ? SolrContext.getSiteReindexAliasName(site) : SolrContext.getSiteAliasName(site);
        }

        return super.getDefaultCollection();
    }

    public boolean isReindexClient() {
        return reindexClient;
    }

    public void setReindexClient(boolean reindexClient) {
        this.reindexClient = reindexClient;
    }
}
