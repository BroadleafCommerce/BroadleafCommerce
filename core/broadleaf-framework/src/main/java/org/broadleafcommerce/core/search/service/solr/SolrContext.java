/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.search.service.solr;

import org.apache.solr.client.solrj.SolrServer;

/**
 * Provides a class that will statically hold the Solr server
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class SolrContext {

    public static final String PRIMARY = "primary";
    public static final String REINDEX = "reindex";

    protected static SolrServer adminServer = null;
    protected static SolrServer primaryServer = null;
    protected static SolrServer reindexServer = null;

    public static void setPrimaryServer(SolrServer server) {
        primaryServer = server;
    }

    public static void setReindexServer(SolrServer server) {
        reindexServer = server;
    }

    public static void setAdminServer(SolrServer server) {
        adminServer = server;
    }

    /**
     * The adminServer is just a reference to a SolrServer component for connecting to Solr.  In newer 
     * versions of Solr, 4.4 and beyond, auto discovery of cores is  
     * provided.  When using a stand-alone server or server cluster, 
     * the admin server, for swapping cores, is a different URL. For example, 
     * one needs to use http://solrserver:8983/solr, which formerly acted as the admin server 
     * AND as the primary core.  As of Solr 4.4, one needs to specify the cores separately: 
     * http://solrserver:8983/solr/primary and http://solrserver:8983/solr/reindex, 
     * and use http://solrserver:8983/solr for swapping cores.
     * 
     * By default, this method attempts to return an admin server if configured. Otherwise, 
     * it returns the primary server if the admin server is null, which is backwards compatible 
     * with the way that BLC worked prior to this change.
     * 
     * @return
     */
    public static SolrServer getAdminServer() {
        if (adminServer != null) {
            return adminServer;
        }
        //If the admin server hasn't been set, return the primary server.
        return getServer();
    }

    public static SolrServer getServer() {
        return primaryServer;
    }

    public static SolrServer getReindexServer() {
        return isSingleCoreMode() ? primaryServer : reindexServer;
    }

    public static boolean isSingleCoreMode() {
        return reindexServer == null;
    }

}
