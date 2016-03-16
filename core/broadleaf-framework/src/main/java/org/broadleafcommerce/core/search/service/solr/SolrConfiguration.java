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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import javax.jms.IllegalStateException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.LBHttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.common.cloud.Aliases;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.CoreContainer;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexServiceImpl;
import org.springframework.beans.factory.InitializingBean;
import org.xml.sax.SAXException;

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

    protected String solrHomePath = null;


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
                    if (cs.getDefaultCollection().equals(((CloudSolrClient) reindexServer).getDefaultCollection())) {
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
                    if (cs.getDefaultCollection().equals(((CloudSolrClient) primaryServer).getDefaultCollection())) {
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
        return primaryServer;
    }

    /**
     * @return the primary server if {@link #isSingleCoreMode()}, else the reindex server
     */
    public SolrClient getReindexServer() {
        return isSingleCoreMode() ? primaryServer : reindexServer;
    }

    /**
     * @return if this Solr context has a reindex server set not
     */
    public boolean isSingleCoreMode() {
        return reindexServer == null;
    }

    /**
     * This constructor should be used to set up embedded solr given a String to a SolrHome directory to use, or if 
     * 'solrhome' is passed in as a parameter we will use the java temp directory to setup solr
     *
     * @param solrServer
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IllegalStateException
     */
    public SolrConfiguration(String solrServer) throws IOException, ParserConfigurationException, SAXException, IllegalStateException {
        // using embedded solr so we will default the core names
        this.setPrimaryName("primary");
        this.setReindexName("reindex");

        if (Objects.equals("solrhome", solrServer)) {

            final String baseTempPath = System.getProperty("java.io.tmpdir");

            File tempDir = new File(baseTempPath + File.separator + System.getProperty("user.name") + File.separator + "solrhome-5.3.1");
            if (System.getProperty("tmpdir.solrhome") != null) {
                //allow for an override of tmpdir
                tempDir = new File(System.getProperty("tmpdir.solrhome"));
            }
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            solrServer = tempDir.getAbsolutePath();
        }
        setSolrHomePath(solrServer);

        File solrXml = new File(new File(solrServer), "solr.xml");
        if (!solrXml.exists()) {
            copyConfigToSolrHome(this.getClass().getResourceAsStream("/solr-default.xml"), solrXml);
        }

        buildSolrCoreDirectories(solrServer);

        LOG.debug(String.format("Using [%s] as solrhome", solrServer));
        LOG.debug(String.format("Using [%s] as solr.xml", solrXml.getAbsoluteFile()));
        
        if (LOG.isTraceEnabled()) {
            LOG.trace("Contents of solr.xml:");
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(solrXml));
                String line;
                while ((line = br.readLine()) != null) {
                    LOG.trace(line);
                }
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (Throwable e) {
                        //do nothing
                    }
                }
            }
            LOG.trace("Done printing solr.xml");
        }

        CoreContainer coreContainer = CoreContainer.createAndLoad(solrServer, solrXml);
        EmbeddedSolrServer primaryServer = new EmbeddedSolrServer(coreContainer, getPrimaryName());
        EmbeddedSolrServer reindexServer = new EmbeddedSolrServer(coreContainer, getReindexName());

        this.setServer(primaryServer);
        this.setReindexServer(reindexServer);
        //NOTE: There is no reason to set the admin server here as the we will return the primary server
        //if the admin server is not set...
    }

    public void copyConfigToSolrHome(InputStream configIs, File destFile) throws IOException {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(configIs);
            bos = new BufferedOutputStream(new FileOutputStream(destFile, false));
            boolean eof = false;
            while (!eof) {
                int temp = bis.read();
                if (temp == -1) {
                    eof = true;
                } else {
                    bos.write(temp);
                }
            }
            bos.flush();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (Throwable e) {
                    //do nothing
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (Throwable e) {
                    //do nothing
                }
            }
        }
    }

    /**
     * This creates the proper directories and writes the correct properties files for Solr to run in embedded mode.
     * @param solrServer
     * @throws IOException
     */
    protected void buildSolrCoreDirectories(String solrServer) throws IOException {
        //Create a "cores" directory if it does not exist
        File cores = new File(new File(solrServer), "cores");
        if (!cores.exists() || !cores.isDirectory()) {
            cores.mkdirs();
        }

        //Create a "cores/primary" if it does not exist
        File primaryCoreDir = new File(cores, "primary");
        if (!primaryCoreDir.exists() || !primaryCoreDir.isDirectory()) {
            primaryCoreDir.mkdirs();
        }

        //Create a cores/primary/core.properties file, populated with "name=primary"
        File primaryCoreFile = new File(primaryCoreDir, "core.properties");
        if (!primaryCoreFile.exists()) {
            FileOutputStream os = new FileOutputStream(primaryCoreFile);
            Properties prop = new Properties();
            prop.put("name", getPrimaryName());
            prop.store(os, "Generated Solr core properties file");
            IOUtils.closeQuietly(os);
        }

        //Create a "cores/primary/conf" directory if it does not exist
        File primaryConfDir = new File(primaryCoreDir, "conf");
        if (!primaryConfDir.exists() || !primaryConfDir.isDirectory()) {
            primaryConfDir.mkdirs();
        }

        //Create a "cores/reindex" if it does not exist
        File reindexCoreDir = new File(cores, "reindex");
        if (!reindexCoreDir.exists() || !reindexCoreDir.isDirectory()) {
            reindexCoreDir.mkdirs();
        }

        //Create a cores/reindex/core.properties file, populated with "name=reindex"
        File reindexCoreFile = new File(reindexCoreDir, "core.properties");
        if (!reindexCoreFile.exists()) {
            FileOutputStream os = new FileOutputStream(reindexCoreFile);
            Properties prop = new Properties();
            prop.put("name", getReindexName());
            prop.store(os, "Generated Solr core properties file");
            IOUtils.closeQuietly(os);
        }

        //Create a "cores/reindex/conf" directory if it does not exist
        File reindexConfDir = new File(reindexCoreDir, "conf");
        if (!reindexConfDir.exists() || !reindexConfDir.isDirectory()) {
            reindexConfDir.mkdirs();
        }
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
        }
        if (HttpSolrClient.class.isAssignableFrom(reindexServer.getClass())) {
            this.setReindexName(determineCoreName((HttpSolrClient) reindexServer));
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
        }
        if (HttpSolrClient.class.isAssignableFrom(reindexServer.getClass())) {
            this.setReindexName(determineCoreName((HttpSolrClient) reindexServer));
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
        if (CloudSolrClient.class.isAssignableFrom(getServer().getClass())) {
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
            List<String> collectionNames = listResponse.get("collections") == null ? collectionNames = new ArrayList<String>() : (List<String>) listResponse.get("collections");

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

                new CollectionAdminRequest.Create().setCollectionName(collectionName).setNumShards(solrCloudNumShards)
                        .setConfigName(solrCloudConfigName).process(primary);

                new CollectionAdminRequest.CreateAlias().setAliasName(primary.getDefaultCollection())
                        .setAliasedCollections(collectionName).process(primary);
            } else {
                //Aliases can be mapped to collections that don't exist.... Make sure the collection exists
                String collectionName = aliasCollectionMap.get(primary.getDefaultCollection());
                collectionName = collectionName.split(",")[0];
                if (!collectionNames.contains(collectionName)) {
                    new CollectionAdminRequest.Create().setCollectionName(collectionName).setNumShards(solrCloudNumShards)
                            .setConfigName(solrCloudConfigName).process(primary);
                }
            }

            //Reload the collection names
            listResponse = new CollectionAdminRequest.List().process(primary).getResponse();
            collectionNames = listResponse.get("collections") == null ? collectionNames = new ArrayList<String>() : (List<String>) listResponse.get("collections");

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

                new CollectionAdminRequest.Create().setCollectionName(collectionName).setNumShards(solrCloudNumShards)
                        .setConfigName(solrCloudConfigName).process(primary);

                new CollectionAdminRequest.CreateAlias().setAliasName(reindex.getDefaultCollection())
                        .setAliasedCollections(collectionName).process(primary);
            } else {
                //Aliases can be mapped to collections that don't exist.... Make sure the collection exists
                String collectionName = aliasCollectionMap.get(reindex.getDefaultCollection());
                collectionName = collectionName.split(",")[0];
                if (!collectionNames.contains(collectionName)) {
                    new CollectionAdminRequest.Create().setCollectionName(collectionName).setNumShards(solrCloudNumShards)
                            .setConfigName(solrCloudConfigName).process(primary);
                }
            }
        }
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
