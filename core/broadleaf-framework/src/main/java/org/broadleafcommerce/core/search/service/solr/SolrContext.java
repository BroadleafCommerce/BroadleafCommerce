/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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

import org.apache.solr.client.solrj.SolrServer;



/**
 * Provides a class that will statically hold the Solr server
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class SolrContext {

    public static final String PRIMARY = "primary";
    public static final String REINDEX = "reindex";

    protected static SolrServer primaryServer = null;
    protected static SolrServer reindexServer = null;

    public static void setPrimaryServer(SolrServer server) {
        primaryServer = server;
    }

    public static void setReindexServer(SolrServer server) {
        reindexServer = server;
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