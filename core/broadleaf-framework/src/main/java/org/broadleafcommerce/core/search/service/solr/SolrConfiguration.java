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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.common.cloud.Aliases;
import org.apache.solr.common.util.NamedList;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexServiceImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * Provides a class that will statically hold the Solr server.
 * 
 * <p>
 * This is initialized in {@link SolrSearchServiceImpl} and used in {@link SolrIndexServiceImpl}
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class SolrConfiguration implements InitializingBean {
    private static final Log LOG = LogFactory.getLog(SolrConfiguration.class);

    protected String primaryName = null;
    protected String reindexName = null;

    // this is a field to differentiate between collections of items and it must be non-blank
    protected String namespace = "d";

    protected SolrClient adminServer = null;
    protected SolrClient primaryServer = null;
    protected SolrClient reindexServer = null;

    //This is the name of the config that Zookeeper has associated with Solr configs
    protected String solrCloudConfigName = null;

    //This is the default number of shards that should be created if a SolrCloud collection is created via API
    protected Integer solrCloudNumShards = null;

    //This is the default number of replicas that should be created if a SolrCloud collection is created via API
    protected Integer solrCloudNumReplicas = null;

    protected String solrHomePath = null;
    
    @Value("${solr.index.site.collections:false}")
    protected boolean siteCollections;

    @Value("${solr.index.site.alias.name:site}")
    protected String siteAliasBase;

    @Value("${solr.index.site.collection.name:blcSite}")
    protected String siteCollectionBase;
    
    public String getPrimaryName() {
        return primaryName;
    }

    public void setPrimaryName(String primaryName) {
        this.primaryName = primaryName;
    }

    public String getReindexName() {
        return reindexName;
    }

    public void setReindexName(String reindex) {
        this.reindexName = reindex;
    }

    public String getSolrCloudConfigName() {
        return solrCloudConfigName;
    }

    public void setSolrCloudConfigName(String solrCloudConfigName) {
        this.solrCloudConfigName = solrCloudConfigName;
    }

    public Integer getSolrCloudNumShards() {
        return solrCloudNumShards;
    }

    public void setSolrCloudNumShards(Integer solrCloudNumShards) {
        this.solrCloudNumShards = solrCloudNumShards;
    }
    
    public Integer getSolrCloudNumReplicas() {
        return solrCloudNumReplicas;
    }
    
    public void setSolrCloudNumReplicas(Integer solrCloudNumReplicas) {
        this.solrCloudNumReplicas = solrCloudNumReplicas;
    }

    public String getSolrHomePath() {
        return solrHomePath;
    }

    public void setSolrHomePath(String solrHomePath) {
        this.solrHomePath = solrHomePath;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Sets the primary SolrClient instance to communicate with Solr.  This is typically one of the following:
     * <code>org.apache.solr.client.solrj.embedded.EmbeddedSolrClient</code>,
     * <code>org.apache.solr.client.solrj.impl.HttpSolrClient</code>,
     * <code>org.apache.solr.client.solrj.impl.LBHttpSolrClient</code>,
     * or <code>org.apache.solr.client.solrj.impl.CloudSolrClient</code>
     * 
     * @param server
     * @throws IllegalStateException 
     */
    public void setServer(SolrClient server) throws IllegalStateException {
        if (server != null && CloudSolrClient.class.isAssignableFrom(server.getClass())) {
            CloudSolrClient cs = (CloudSolrClient) server;
            if (StringUtils.isBlank(cs.getDefaultCollection())) {
                cs.setDefaultCollection(getPrimaryName());
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
     * @throws IllegalStateException 
     */
    public void setReindexServer(SolrClient server) throws IllegalStateException {
        if (server != null && CloudSolrClient.class.isAssignableFrom(server.getClass())) {
            CloudSolrClient cs = (CloudSolrClient) server;
            if (StringUtils.isBlank(cs.getDefaultCollection())) {
                cs.setDefaultCollection(getReindexName());
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
    public void setAdminServer(SolrClient server) {
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
    public SolrClient getAdminServer() {
        if (adminServer != null) {
            return adminServer;
        }
        //If the admin server hasn't been set, return the primary server.
        return getServer();
    }

    /**
     * @return the primary Solr server
     */
    public SolrClient getServer() {
        if (isSiteCollections() && isSolrCloudMode()) {
            return getSiteServer();
        }
        return primaryServer;
    }

    /**
     * @return the primary server if {@link #isSingleCoreMode()}, else the reindex server
     */
    public SolrClient getReindexServer() {
        if (isSiteCollections() && isSolrCloudMode()) {
            return getSiteReindexServer();
        }
        
        return isSingleCoreMode() ? primaryServer : reindexServer;
    }

    /**
     * @return if this Solr context has a reindex server set not
     */
    public boolean isSingleCoreMode() {
        return reindexServer == null;
    }
    
    public boolean isSolrCloudMode() {
        return CloudSolrClient.class.isAssignableFrom(primaryServer.getClass());
    }

    /**
     * Sets up Solr using multiple clients, one primary, one for reindexing, and one admin to reduce down time during
     * indexing.  This constructor should be used when setting up HttpSolrClient since no collection names are
     * being provided.
     * 
     * The adminServer is just a reference to a SolrClient component for connecting to Solr.  In newer
     * versions of Solr, 4.4 and beyond, auto discovery of cores is  
     * provided.  When using a stand-alone server or server cluster, 
     * the admin server, for swapping cores, is a different URL. For example, 
     * one needs to use http://solrserver:8983/solr, which formerly acted as the admin server 
     * AND as the primary core.  As of Solr 4.4, one needs to specify the cores separately: 
     * http://solrserver:8983/solr/primary and http://solrserver:8983/solr/reindex, 
     * and use http://solrserver:8983/solr for swapping cores.
     * 
     * @param solrServer
     * @param reindexServer
     * @param adminServer
     * @throws IllegalStateException
     */
    public SolrConfiguration(SolrClient solrServer, SolrClient reindexServer, SolrClient adminServer) throws IllegalStateException {
        //get primary and reindex names from http urls

        if (HttpSolrClient.class.isAssignableFrom(solrServer.getClass())) {
            this.setPrimaryName(determineCoreName((HttpSolrClient) solrServer));
        } else if (DelegatingHttpSolrClient.class.isAssignableFrom(solrServer.getClass())) {
            this.setPrimaryName(((DelegatingHttpSolrClient) solrServer).getDefaultCollection());
        }

        if (HttpSolrClient.class.isAssignableFrom(reindexServer.getClass())) {
            this.setReindexName(determineCoreName((HttpSolrClient) reindexServer));
        } else if (DelegatingHttpSolrClient.class.isAssignableFrom(reindexServer.getClass())) {
            this.setReindexName(((DelegatingHttpSolrClient) reindexServer).getDefaultCollection());
        }

        this.setServer(solrServer);
        this.setReindexServer(reindexServer);
        this.setAdminServer(adminServer);
    }

    /**
     * Sets up Solr using multiple clients, one primary, one for reindexing, and one admin to reduce down time during 
     * indexing.  This constructor should be used when setting up HttpSolrClient since no collection names are 
     * being provided.  Namespace can be specified if managing multiple document sets within the same cores.
     * 
     * The adminServer is just a reference to a SolrClient component for connecting to Solr.  In newer
     * versions of Solr, 4.4 and beyond, auto discovery of cores is  
     * provided.  When using a stand-alone server or server cluster, 
     * the admin server, for swapping cores, is a different URL. For example, 
     * one needs to use http://solrserver:8983/solr, which formerly acted as the admin server 
     * AND as the primary core.  As of Solr 4.4, one needs to specify the cores separately: 
     * http://solrserver:8983/solr/primary and http://solrserver:8983/solr/reindex, 
     * and use http://solrserver:8983/solr for swapping cores.
     * 
     * @param solrServer
     * @param reindexServer
     * @param adminServer
     * @param namespace
     * @throws IllegalStateException
     */
    public SolrConfiguration(SolrClient solrServer, SolrClient reindexServer, SolrClient adminServer, String namespace) throws IllegalStateException {
        this.setNamespace(namespace);
        if (HttpSolrClient.class.isAssignableFrom(solrServer.getClass())) {
            this.setPrimaryName(determineCoreName((HttpSolrClient) solrServer));
        } else if (DelegatingHttpSolrClient.class.isAssignableFrom(solrServer.getClass())) {
            if (((DelegatingHttpSolrClient)solrServer).getDefaultCollection() == null) {
                this.setReindexName(determineCoreName(((DelegatingHttpSolrClient)solrServer).getDelegate()));
            } else {
                this.setReindexName(((DelegatingHttpSolrClient)solrServer).getDefaultCollection());
            }
        }
        
        if (HttpSolrClient.class.isAssignableFrom(reindexServer.getClass())) {
            this.setReindexName(determineCoreName((HttpSolrClient) reindexServer));
        } else if (DelegatingHttpSolrClient.class.isAssignableFrom(solrServer.getClass())) {
            if (((DelegatingHttpSolrClient)reindexServer).getDefaultCollection() == null) {
                this.setReindexName(determineCoreName(((DelegatingHttpSolrClient)reindexServer).getDelegate()));
            } else {
                this.setReindexName(((DelegatingHttpSolrClient)reindexServer).getDefaultCollection());
            }
        }

        this.setServer(solrServer);
        this.setReindexServer(reindexServer);
        this.setAdminServer(adminServer);
    }

    /**
     * Sets up Solr using multiple clients, one primary, one for reindexing, and one admin to reduce down time during 
     * indexing. This constructor should be used when setting up LBHttpSolrClients because primaryCoreName and 
     * reindexCoreName need to be provided to SolrConfiguration.
     * 
     * The adminServer is just a reference to a SolrClient component for connecting to Solr.  In newer
     * versions of Solr, 4.4 and beyond, auto discovery of cores is  
     * provided.  When using a stand-alone server or server cluster, 
     * the admin server, for swapping cores, is a different URL. For example, 
     * one needs to use http://solrserver:8983/solr, which formerly acted as the admin server 
     * AND as the primary core.  As of Solr 4.4, one needs to specify the cores separately: 
     * http://solrserver:8983/solr/primary and http://solrserver:8983/solr/reindex, 
     * and use http://solrserver:8983/solr for swapping cores.
     * 
     * @param solrServer
     * @param reindexServer
     * @param adminServer
     * @param primaryCoreName
     * @param reindexCoreName
     * @throws IllegalStateException
     */
    public SolrConfiguration(SolrClient solrServer, SolrClient reindexServer, SolrClient adminServer, String primaryCoreName, String reindexCoreName) throws IllegalStateException {
        this.setPrimaryName(primaryCoreName);
        this.setReindexName(reindexCoreName);
        this.setServer(solrServer);
        this.setReindexServer(reindexServer);
        this.setAdminServer(adminServer);
    }

    /**
     * Sets up Solr using multiple clients, one primary, one for reindexing, and one admin to reduce down time during 
     * indexing. This constructor should be used when setting up LBHttpSolrClients because primaryCoreName and 
     * reindexCoreName need to be provided to SolrConfiguration. Namespace can be specified if managing multiple 
     * document sets within the same cores.
     * 
     * The adminServer is just a reference to a SolrClient component for connecting to Solr.  In newer
     * versions of Solr, 4.4 and beyond, auto discovery of cores is  
     * provided.  When using a stand-alone server or server cluster, 
     * the admin server, for swapping cores, is a different URL. For example, 
     * one needs to use http://solrserver:8983/solr, which formerly acted as the admin server 
     * AND as the primary core.  As of Solr 4.4, one needs to specify the cores separately: 
     * http://solrserver:8983/solr/primary and http://solrserver:8983/solr/reindex, 
     * and use http://solrserver:8983/solr for swapping cores.
     * 
     * @param solrServer
     * @param reindexServer
     * @param adminServer
     * @param primaryCoreName
     * @param reindexCoreName
     * @param namespace
     * @throws IllegalStateException
     */
    public SolrConfiguration(SolrClient solrServer, SolrClient reindexServer, SolrClient adminServer, String primaryCoreName, String reindexCoreName, String namespace) throws IllegalStateException {
        this.setPrimaryName(primaryCoreName);
        this.setReindexName(reindexCoreName);
        this.setNamespace(namespace);
        this.setServer(solrServer);
        this.setReindexServer(reindexServer);
        this.setAdminServer(adminServer);
    }

    /**
     * This constructor should be used to set up Solr Cloud using solr cloud config name, number of cloud shards, and 
     * multiple clients, one primary, and one for reindexing to reduce down time during indexing.  Be sure to set the 
     * defaultCollection on the SolrClients correctly before passing them to this constructor.
     * 
     * @param solrServer
     * @param reindexServer
     * @param solrCloudConfigName
     * @param solrCloudNumShards
     * @throws IllegalStateException
     */
    public SolrConfiguration(SolrClient solrServer, SolrClient reindexServer, String solrCloudConfigName, int solrCloudNumShards) throws IllegalStateException {
        this.setSolrCloudConfigName(solrCloudConfigName);
        this.setSolrCloudNumShards(solrCloudNumShards);
        this.setServer(solrServer);
        this.setReindexServer(reindexServer);
    }

    /**
     * This constructor should be used to set up Solr Cloud using solr cloud config name, number of cloud shards, and 
     * multiple clients, one primary, and one for reindexing to reduce down time during indexing.  Be sure to set the 
     * defaultCollection on the SolrClients correctly before passing them to this constructor.  Namespace can be specified 
     * if managing multiple document sets within the same collections.
     * 
     * @param solrServer
     * @param reindexServer
     * @param solrCloudConfigName
     * @param solrCloudNumShards
     * @param namespace
     * @throws IllegalStateException
     */
    public SolrConfiguration(SolrClient solrServer, SolrClient reindexServer, String solrCloudConfigName, int solrCloudNumShards, String namespace) throws IllegalStateException {
        this.setSolrCloudConfigName(solrCloudConfigName);
        this.setSolrCloudNumShards(solrCloudNumShards);
        this.setNamespace(namespace);
        this.setServer(solrServer);
        this.setReindexServer(reindexServer);
    }

    @Override
    public void afterPropertiesSet() throws SolrServerException, IOException, IllegalStateException {
        if (isSolrCloudMode()) {
            
            if (isSiteCollections()) {
                LOG.info("Solr configuration is using collection-per-site, assuming collections will be created on the fly");
                return;
            }
            
            //We want to use the Solr APIs to make sure the correct collections are set up.
            CloudSolrClient primary = (CloudSolrClient) primaryServer;
            CloudSolrClient reindex = (CloudSolrClient) reindexServer;
            if (primary == null || reindex == null) {
                throw new IllegalStateException("The primary and reindex CloudSolrServers must not be null. Check "
                        + "your configuration and ensure that you are passing a different instance for each to the "
                        + "constructor of "
                        + this.getClass().getName()
                        + " and ensure that each has a null (empty)"
                        + " defaultCollection property, or ensure that defaultCollection is unique between"
                        + " the two instances. All other things, like Zookeeper addresses should be the same.");
            }

            if (primary == reindex) {
                //These are the same object instances.  They should be separate instances, with generally 
                //the same configuration, except for the defaultCollection name.
                throw new IllegalStateException("The primary and reindex CloudSolrServers must be different instances "
                        + "and their defaultCollection property must be unique or null.  All other things like the "
                        + "Zookeeper addresses should be the same.");
            }
            
            //check if the default collection is null
            if (StringUtils.isEmpty(primary.getDefaultCollection())) {
                throw new IllegalStateException("The primary CloudSolrServer must have a defaultCollection property set.");
            } else {
                this.setPrimaryName(primary.getDefaultCollection());
            }

            //check if the default collection is null
            if (StringUtils.isEmpty(reindex.getDefaultCollection())) {
                throw new IllegalStateException("The reindex CloudSolrServer must have a defaultCollection property set.");
            } else {
                this.setReindexName(reindex.getDefaultCollection());
            }

            if (Objects.equals(primary.getDefaultCollection(), reindex.getDefaultCollection())) {
                throw new IllegalStateException("The primary and reindex CloudSolrServers must have "
                        + "unique defaultCollection properties.  All other things like the "
                        + "Zookeeper addresses should be the same.");
            }

            primary.connect(); //This is required to ensure no NPE!

            //Get a list of existing collections so we don't overwrite one
            NamedList<Object> listResponse = new CollectionAdminRequest.List().process(primary).getResponse();
            List<String> collectionNames = listResponse.get("collections") == null ? collectionNames = new ArrayList<>() : (List<String>) listResponse.get("collections");

            Aliases aliases = primary.getZkStateReader().getAliases();
            Map<String, String> aliasCollectionMap = aliases.getCollectionAliasMap();

            if (aliasCollectionMap == null || !aliasCollectionMap.containsKey(primary.getDefaultCollection())) {
                //Create a completely new collection
                String collectionName = null;
                for (int i = 0; i < 1000; i++) {
                    collectionName = "blcCollection" + i;
                    if (collectionNames.contains(collectionName)) {
                        collectionName = null;
                    } else {
                        break;
                    }
                }
                CollectionAdminRequest.createCollection(collectionName, solrCloudConfigName, solrCloudNumShards, solrCloudNumReplicas).process(primary);
                CollectionAdminRequest.createAlias(primary.getDefaultCollection(), collectionName).process(primary);
            } else {
                //Aliases can be mapped to collections that don't exist.... Make sure the collection exists
                String collectionName = aliasCollectionMap.get(primary.getDefaultCollection());
                collectionName = collectionName.split(",")[0];
                if (!collectionNames.contains(collectionName)) {
                    CollectionAdminRequest.createCollection(collectionName, solrCloudConfigName, solrCloudNumShards, solrCloudNumReplicas).process(primary);
                }
            }

            //Reload the collection names
            listResponse = new CollectionAdminRequest.List().process(primary).getResponse();
            collectionNames = listResponse.get("collections") == null ? collectionNames = new ArrayList<>() : (List<String>) listResponse.get("collections");

            //Reload these maps for the next collection.
            aliases = primary.getZkStateReader().getAliases();
            aliasCollectionMap = aliases.getCollectionAliasMap();

            if (aliasCollectionMap == null || !aliasCollectionMap.containsKey(reindex.getDefaultCollection())) {
                //Create a completely new collection
                String collectionName = null;
                for (int i = 0; i < 1000; i++) {
                    collectionName = "blcCollection" + i;
                    if (collectionNames.contains(collectionName)) {
                        collectionName = null;
                    } else {
                        break;
                    }
                }
                CollectionAdminRequest.createCollection(collectionName, solrCloudConfigName, solrCloudNumShards, solrCloudNumReplicas).process(primary);
                CollectionAdminRequest.createAlias(reindex.getDefaultCollection(), collectionName).process(primary);

            } else {
                //Aliases can be mapped to collections that don't exist.... Make sure the collection exists
                String collectionName = aliasCollectionMap.get(reindex.getDefaultCollection());
                collectionName = collectionName.split(",")[0];
                if (!collectionNames.contains(collectionName)) {
                    CollectionAdminRequest.createCollection(collectionName, solrCloudConfigName, solrCloudNumShards, solrCloudNumReplicas).process(primary);
                }
            }
        }
    }
    
    
    public SolrClient getSiteServer() {
        BroadleafRequestContext ctx = BroadleafRequestContext.getBroadleafRequestContext();
        Site site = ctx.getNonPersistentSite();

        CloudSolrClient client = (CloudSolrClient) primaryServer;
        client.connect();
        
        String aliasName = getSiteAliasName(site);
        if (aliasName != null) {
            String collectionName = getSiteCollectionName(site);

            createCollectionIfNotExist(client, collectionName);
            createAliasIfNotExist(client, collectionName, collectionName);
        }

        return client;
    }
    
    public SolrClient getSiteReindexServer() {
        BroadleafRequestContext ctx = BroadleafRequestContext.getBroadleafRequestContext();
        Site site = ctx.getNonPersistentSite();

        CloudSolrClient client = (CloudSolrClient) primaryServer;
        client.connect();
        
        String aliasName = getSiteReindexAliasName(site);
        if (aliasName != null) {
            String collectionName = getSiteReindexCollectionName(site);

            createCollectionIfNotExist(client, collectionName);
            createAliasIfNotExist(client, collectionName, collectionName);
        }

        return client;
    }
    
    protected void createCollectionIfNotExist(CloudSolrClient client, String collectionName) {
        if (!client.getZkStateReader().getClusterState().hasCollection(collectionName)) {
            try {
                CollectionAdminRequest.createCollection(collectionName, getSolrCloudConfigName(), getSolrCloudNumShards(), getSolrCloudNumReplicas())
                        .setMaxShardsPerNode(getSolrCloudNumShards()).process(client);
            } catch (SolrServerException e) {
                throw ExceptionHelper.refineException(e);
            } catch (IOException e) {
                throw ExceptionHelper.refineException(e);
            }
        }
    }
    
    protected void createAliasIfNotExist(CloudSolrClient client, String collectionName, String aliasName) {
        Aliases aliases = client.getZkStateReader().getAliases();
        Map<String, String> aliasCollectionMap = aliases.getCollectionAliasMap();
        if (!aliasCollectionMap.containsKey(aliasName)) {
            try {
                CollectionAdminRequest.createAlias(aliasName, collectionName).process(client);
            } catch (SolrServerException e) {
                throw ExceptionHelper.refineException(e);
            } catch (IOException e) {
                throw ExceptionHelper.refineException(e);
            }
        }
    }
    
    /**
     * @param site the Site
     * @return the alias name for the given Site
     */
    protected String getSiteAliasName(Site site) {
        if (site == null) {
            return null;
        }

        return getSiteAliasBase() + site.getId();
    }

    /**
     * @param site the Site
     * @return the collection name for the given Site
     */
    protected String getSiteCollectionName(Site site) {
        if (site == null) {
            return null;
        }

        return getSiteCollectionBase() + site.getId();
    }

    /**
     * @param site the Site
     * @return the reindex alias name for the given Site
     */
    protected String getSiteReindexAliasName(Site site) {
        if (site == null) {
            return null;
        }

        return getSiteAliasName(site) + "R";
    }

    /**
     * @param site the Site
     * @return the reindex collection name for the given Site
     */
    protected String getSiteReindexCollectionName(Site site) {
        if (site == null) {
            return null;
        }

        return getSiteCollectionName(site) + "R";
    }

    protected String getSiteAliasBase() {
        return siteAliasBase;
    }

    protected String getSiteCollectionBase() {
        return siteCollectionBase;
    }

    /**
     * @return whether to index a separate collection per site
     */
    public boolean isSiteCollections() {
        return siteCollections;
    }

    public void setSiteAliasBase(String siteAliasBase) {
        this.siteAliasBase = siteAliasBase;
    }

    public void setSiteCollectionBase(String siteCollectionBase) {
        this.siteCollectionBase = siteCollectionBase;
    }

    public void setSiteCollections(boolean siteCollections) {
        this.siteCollections = siteCollections;
    }

    public void setSolrCloudNumShards(int solrCloudNumShards) {
        this.solrCloudNumShards = solrCloudNumShards;
    }

    public String getQueryCollectionName() {
        if (isSiteCollections() && isSolrCloudMode()) {
            Site site = BroadleafRequestContext.getBroadleafRequestContext().getNonPersistentSite();
            return getSiteAliasName(site);
        } else if (isSolrCloudMode()) {
            return primaryName;
        }
        // If it's not SolrCloud mode then we just want to operate on the primary core for that server and we do that by not specifying a collection
        return null;
    }

    public String getReindexCollectionName() {
        if (isSiteCollections() && isSolrCloudMode()) {
            Site site = BroadleafRequestContext.getBroadleafRequestContext().getNonPersistentSite();
            return getSiteReindexAliasName(site);
        } else if (isSolrCloudMode()) {
            return reindexName;
        }
        // If it's not SolrCloud mode then we just want to operate on the primary core for that server and we do that by not specifying a collection
        return null;
    }

    protected String determineCoreName(HttpSolrClient httpSolrClient) {
        String url = httpSolrClient.getBaseURL();
        String coreName = url.substring(url.lastIndexOf('/') + 1);

        return coreName;
    }

    public void destroy() throws Exception {
        //Make sure we shut down each of the SolrClient references (these is really the Solr clients despite the name)
        try {
            if (getServer() != null) {
                getServer().close();;
            }
        } catch (Exception e) {
            LOG.error("Error shutting down primary SolrClient (client).", e);
        }

        try {
            if (getReindexServer() != null
                    && getReindexServer() != getServer()) {
                getReindexServer().close();
            }
        } catch (Exception e) {
            LOG.error("Error shutting down reindex SolrClient (client).", e);
        }

        try {
            if (getAdminServer() != null
                    && getAdminServer() != getServer()
                    && getAdminServer() != getReindexServer()) {
                getAdminServer().close();
            }
        } catch (Exception e) {
            LOG.error("Error shutting down admin SolrClient (client).", e);
        }
    }
}
