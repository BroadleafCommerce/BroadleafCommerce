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

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.common.cloud.Aliases;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexServiceImpl;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.MapUtils;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * <p>
 * Provides a class that will statically hold the Solr server.
 * 
 * <p>
 * This is initialized in {@link SolrSearchServiceImpl} and used in {@link SolrIndexServiceImpl}
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class SolrContext {

    public static final String PRIMARY = "primary";
    public static final String REINDEX = "reindex";

    protected static SolrClient adminServer = null;
    protected static SolrClient primaryServer = null;
    protected static SolrClient reindexServer = null;
    protected static String siteAliasBase = null;
    protected static String siteCollectionBase = null;
    protected static boolean siteCollections = false;
    protected static int solrCloudNumShards = 2;
    protected static String solrCloudConfigName = null;


    /**
     * Sets the primary SolrClient instance to communicate with Solr.  This is typically one of the following:
     * <code>org.apache.solr.client.solrj.embedded.EmbeddedSolrClient</code>,
     * <code>org.apache.solr.client.solrj.impl.HttpSolrClient</code>,
     * <code>org.apache.solr.client.solrj.impl.LBHttpSolrClient</code>,
     * or <code>org.apache.solr.client.solrj.impl.CloudSolrClient</code>
     * 
     * @param server
     */
    public static void setPrimaryServer(SolrClient server) {
        if (server != null && CloudSolrClient.class.isAssignableFrom(server.getClass())) {
            CloudSolrClient cs = (CloudSolrClient) server;
            if (StringUtils.isBlank(cs.getDefaultCollection())) {
                cs.setDefaultCollection(PRIMARY);
            }

            if (reindexServer != null) {
                //If we already have a reindex server set, make sure it's not the same instance as the primary
                if (server == reindexServer) {
                    throw new IllegalArgumentException("The primary and reindex CloudSolrServers are the same instances. "
                            + "They must be different instances. Each instance must have a different defaultCollection or "
                            + "the defaultCollection must be unspecified and Broadleaf will set it.");
                }
                
                if (CloudSolrClient.class.isAssignableFrom(reindexServer.getClass())) {
                    //Make sure that the primary and reindex servers are not using the same default collection name
                    if (Objects.equals(cs.getDefaultCollection(), ((CloudSolrClient) reindexServer).getDefaultCollection())) {
                        throw new IllegalStateException("Primary and Reindex servers cannot have the same defaultCollection: "
                                + cs.getDefaultCollection());
                    }
                }
            }
        }

        primaryServer = server;
    }

    /**
     * Sets the SolrClient instance that points to the reindex core for the purpose of doing a full reindex, while the
     * primary core is still serving serving requests.  This is typically one of the following: 
     * <code>org.apache.solr.client.solrj.embedded.EmbeddedSolrServer</code>, 
     * <code>org.apache.solr.client.solrj.impl.HttpSolrServer</code>, 
     * <code>org.apache.solr.client.solrj.impl.LBHttpSolrServer</code>, 
     * or <code>org.apache.solr.client.solrj.impl.CloudSolrClient</code>
     * 
     * @param server
     */
    public static void setReindexServer(SolrClient server) {
        if (server != null && CloudSolrClient.class.isAssignableFrom(server.getClass())) {
            CloudSolrClient cs = (CloudSolrClient) server;
            if (StringUtils.isBlank(cs.getDefaultCollection())) {
                cs.setDefaultCollection(REINDEX);
            }

            if (primaryServer != null) {
                //If we already have a reindex server set, make sure it's not the same instance as the primary
                if (server == primaryServer) {
                    throw new IllegalArgumentException("The primary and reindex CloudSolrServers are the same instances. "
                            + "They must be different instances. Each instance must have a different defaultCollection or "
                            + "the defaultCollection must be unspecified and Broadleaf will set it.");
                }

                if (CloudSolrClient.class.isAssignableFrom(primaryServer.getClass())) {
                    //Make sure that the primary and reindex servers are not using the same default collection name
                    if (Objects.equals(cs.getDefaultCollection(), ((CloudSolrClient) primaryServer).getDefaultCollection())) {
                        throw new IllegalStateException("Primary and Reindex servers cannot have the same defaultCollection: "
                                + cs.getDefaultCollection());
                    }
                }
            }
        }
        reindexServer = server;
    }

    /**
     * Sets the admin SolrClient instance to communicate with Solr for administrative reasons, like swapping cores.
     * This is typically one of the following: 
     * <code>org.apache.solr.client.solrj.embedded.EmbeddedSolrServer</code>, 
     * <code>org.apache.solr.client.solrj.impl.HttpSolrServer</code>, 
     * <code>org.apache.solr.client.solrj.impl.LBHttpSolrServer</code>, 
     * or <code>org.apache.solr.client.solrj.impl.CloudSolrClient</code>
     * 
     * This should not typically need to be set unless using a stand-alone configuration, where the path to the 
     * /admin URI is different than the core URI.  This should not typically be set for EmbeddedSolrServer or 
     * CloudSolrClient.
     * 
     * @param server
     */
    public static void setAdminServer(SolrClient server) {
        adminServer = server;
    }

    /**
     * The adminServer is just a reference to a SolrClient component for connecting to Solr.  In newer
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
    public static SolrClient getAdminServer() {
        if (adminServer != null) {
            return adminServer;
        }
        //If the admin server hasn't been set, return the primary server.
        return getServer();
    }

    /**
     * @return the primary Solr server
     */
    public static SolrClient getServer() {
        if (isSiteCollections() && isSolrCloudMode()) {
            return getSiteServer();
        }

        return primaryServer;
    }

    /**
     * @return the site specific server
     */
    public static SolrClient getSiteServer() {
        BroadleafRequestContext ctx = BroadleafRequestContext.getBroadleafRequestContext();
        Site site = ctx.getNonPersistentSite();

        CloudSolrClient client = (CloudSolrClient) primaryServer;

        client.connect();

        String aliasName = getSiteAliasName(site);

        if (aliasName != null) {
            //Get a list of existing collections so we don't overwrite one
            Set<String> collectionNames = client.getZkStateReader().getClusterState().getCollections();
            Aliases aliases = client.getZkStateReader().getAliases();
            Map<String, String> aliasCollectionMap = aliases.getCollectionAliasMap();

            String collectionName = getSiteCollectionName(site);

            if (!CollectionUtils.contains(collectionNames.iterator(), collectionName)) {
                try {
                    new CollectionAdminRequest.Create().setCollectionName(collectionName).setNumShards(getSolrCloudNumShards())
                            .setMaxShardsPerNode(2).setConfigName(getSolrCloudConfigName()).process(client);
                } catch (SolrServerException e) {
                    throw ExceptionHelper.refineException(e);
                } catch (IOException e) {
                    throw ExceptionHelper.refineException(e);
                }
            }

            if (MapUtils.isEmpty(aliasCollectionMap) || !MapUtils.containsKey(aliasCollectionMap, aliasName)) {
                try {
                    new CollectionAdminRequest.CreateAlias().setAliasName(aliasName)
                            .setAliasedCollections(collectionName).process(client);
                } catch (SolrServerException e) {
                    throw ExceptionHelper.refineException(e);
                } catch (IOException e) {
                    throw ExceptionHelper.refineException(e);
                }
            }

        }

        return client;
    }

    /**
     * @return the primary server if {@link #isSingleCoreMode()}, else the reindex server
     */
    public static SolrClient getReindexServer() {
        if (isSiteCollections() && isSolrCloudMode()) {
            return getSiteReindexServer();
        }

        return isSingleCoreMode() ? primaryServer : reindexServer;
    }

    public static SolrClient getSiteReindexServer() {
        BroadleafRequestContext ctx = BroadleafRequestContext.getBroadleafRequestContext();
        Site site = ctx.getNonPersistentSite();

        CloudSolrClient client = (CloudSolrClient) reindexServer;

        client.connect();

        String aliasName = getSiteReindexAliasName(site);

        if (aliasName != null) {
            //Get a list of existing collections so we don't overwrite one
            Set<String> collectionNames = client.getZkStateReader().getClusterState().getCollections();
            Aliases aliases = client.getZkStateReader().getAliases();
            Map<String, String> aliasCollectionMap = aliases.getCollectionAliasMap();

            String collectionName = getSiteReindexCollectionName(site);

            if (!CollectionUtils.contains(collectionNames.iterator(), collectionName)) {
                try {
                    new CollectionAdminRequest.Create().setCollectionName(collectionName).setNumShards(getSolrCloudNumShards())
                            .setMaxShardsPerNode(2).setConfigName(getSolrCloudConfigName()).process(client);
                } catch (SolrServerException e) {
                    throw ExceptionHelper.refineException(e);
                } catch (IOException e) {
                    throw ExceptionHelper.refineException(e);
                }
            }

            if (MapUtils.isEmpty(aliasCollectionMap) || !MapUtils.containsKey(aliasCollectionMap, aliasName)) {
                try {
                    new CollectionAdminRequest.CreateAlias().setAliasName(aliasName)
                            .setAliasedCollections(collectionName).process(client);
                } catch (SolrServerException e) {
                    throw ExceptionHelper.refineException(e);
                } catch (IOException e) {
                    throw ExceptionHelper.refineException(e);
                }
            }

        }

        return client;
    }

    /**
     * @param site the Site
     * @return the alias name for the given Site
     */
    protected static String getSiteAliasName(Site site) {
        if (site == null) {
            return null;
        }

        return getSiteAliasBase() + site.getId();
    }

    /**
     * @param site the Site
     * @return the collection name for the given Site
     */
    protected static String getSiteCollectionName(Site site) {
        if (site == null) {
            return null;
        }

        return getSiteCollectionBase() + site.getId();
    }

    /**
     * @param site the Site
     * @return the reindex alias name for the given Site
     */
    protected static String getSiteReindexAliasName(Site site) {
        if (site == null) {
            return null;
        }

        return getSiteAliasName(site) + "R";
    }

    /**
     * @param site the Site
     * @return the reindex collection name for the given Site
     */
    protected static String getSiteReindexCollectionName(Site site) {
        if (site == null) {
            return null;
        }

        return getSiteCollectionName(site) + "R";
    }

    protected static String getSiteAliasBase() {
        return siteAliasBase;
    }

    protected static String getSiteCollectionBase() {
        return siteCollectionBase;
    }

    /**
     * @return whether to index a separate collection per site
     */
    public static boolean isSiteCollections() {
        return siteCollections;
    }

    public static int getSolrCloudNumShards() {
        return solrCloudNumShards;
    }

    public static String getSolrCloudConfigName() {
        return solrCloudConfigName;
    }

    public static void setSiteAliasBase(String siteAliasBase) {
        SolrContext.siteAliasBase = siteAliasBase;
    }

    public static void setSiteCollectionBase(String siteCollectionBase) {
        SolrContext.siteCollectionBase = siteCollectionBase;
    }

    public static void setSiteCollections(boolean siteCollections) {
        SolrContext.siteCollections = siteCollections;
    }

    public static void setSolrCloudNumShards(int solrCloudNumShards) {
        SolrContext.solrCloudNumShards = solrCloudNumShards;
    }

    public static void setSolrCloudConfigName(String solrCloudConfigName) {
        SolrContext.solrCloudConfigName = solrCloudConfigName;
    }

    /**
     * @return if this Solr context has a reindex server set not
     */
    public static boolean isSingleCoreMode() {
        return reindexServer == null;
    }

    /**
     * Indicates if we are communicating with SolrCloud, rather than embedded, or Stand-alone 
     * Solr server(s).  This is important to know because Solr cloud is clustered, uses sharding, uses 
     * Zookeeper to manage state, and manages individual "collections" rather than "cores" for searching and reindexing.
     * 
     * @return
     */
    public static boolean isSolrCloudMode() {
        return CloudSolrClient.class.isAssignableFrom(primaryServer.getClass());
    }

}
