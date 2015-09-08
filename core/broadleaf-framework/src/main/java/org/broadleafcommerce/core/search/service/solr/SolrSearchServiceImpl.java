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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.cloud.Aliases;
import org.apache.solr.core.CoreContainer;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.dao.ProductDao;
import org.broadleafcommerce.core.catalog.dao.SkuDao;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.search.dao.FieldDao;
import org.broadleafcommerce.core.search.dao.SearchFacetDao;
import org.broadleafcommerce.core.search.dao.SearchFieldDao;
import org.broadleafcommerce.core.search.domain.CategorySearchFacet;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchFacetRange;
import org.broadleafcommerce.core.search.domain.SearchField;
import org.broadleafcommerce.core.search.domain.SearchFieldType;
import org.broadleafcommerce.core.search.domain.SearchResult;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.broadleafcommerce.core.search.service.SearchService;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.xml.sax.SAXException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;

/**
 * An implementation of SearchService that uses Solr.
 * 
 * Note that prior to 2.2.0, this class used to contain all of the logic for interaction with Solr. Since 2.2.0, this class
 * has been refactored and parts of it have been split into the other classes you can find in this package.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class SolrSearchServiceImpl implements SearchService, InitializingBean, DisposableBean {
    private static final Log LOG = LogFactory.getLog(SolrSearchServiceImpl.class);

    @Value("${solr.index.use.sku}")
    protected boolean useSku;

    //This is the name of the config that Zookeeper has associated with Solr configs
    @Value("${solr.cloud.configName}")
    protected String solrCloudConfigName = "blc";

    //This is the default number of shards that should be created if a SolrCloud collection is created via API
    @Value("${solr.cloud.defaultNumShards}")
    protected int solrCloudNumShards = 2;

    @Resource(name = "blProductDao")
    protected ProductDao productDao;

    @Resource(name = "blSkuDao")
    protected SkuDao skuDao;

    @Resource(name = "blFieldDao")
    protected FieldDao fieldDao;

    @Resource(name = "blSearchFacetDao")
    protected SearchFacetDao searchFacetDao;

    @Resource(name = "blSolrHelperService")
    protected SolrHelperService shs;

    @Resource(name = "blSolrIndexService")
    protected SolrIndexService solrIndexService;

    @Resource(name = "blSearchFieldDao")
    protected SearchFieldDao searchFieldDao;

    @Resource(name = "blSolrSearchServiceExtensionManager")
    protected SolrSearchServiceExtensionManager extensionManager;

    protected String solrHomePath;

    public SolrSearchServiceImpl(String solrServer) throws IOException, ParserConfigurationException, SAXException {
        if ("solrhome".equals(solrServer)) {

            final String baseTempPath = System.getProperty("java.io.tmpdir");

            File tempDir = new File(baseTempPath + File.separator + System.getProperty("user.name") + File.separator + "solrhome-4.10.3");
            if (System.getProperty("tmpdir.solrhome") != null) {
                //allow for an override of tmpdir
                tempDir = new File(System.getProperty("tmpdir.solrhome"));
            }
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            solrServer = tempDir.getAbsolutePath();
        }
        solrHomePath = solrServer;

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
        EmbeddedSolrServer primaryServer = new EmbeddedSolrServer(coreContainer, SolrContext.PRIMARY);
        EmbeddedSolrServer reindexServer = new EmbeddedSolrServer(coreContainer, SolrContext.REINDEX);

        SolrContext.setPrimaryServer(primaryServer);
        SolrContext.setReindexServer(reindexServer);
        //NOTE: There is no reason to set the admin server here as the SolrContext will return the primary server
        //if the admin server is not set...
    }

    public String getSolrHomePath() {
        return solrHomePath;
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
            prop.put("name", SolrContext.PRIMARY);
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
            prop.put("name", SolrContext.REINDEX);
            prop.store(os, "Generated Solr core properties file");
            IOUtils.closeQuietly(os);
        }

        //Create a "cores/reindex/conf" directory if it does not exist
        File reindexConfDir = new File(reindexCoreDir, "conf");
        if (!reindexConfDir.exists() || !reindexConfDir.isDirectory()) {
            reindexConfDir.mkdirs();
        }
    }

    public SolrSearchServiceImpl(SolrServer solrServer) {
        SolrContext.setPrimaryServer(solrServer);
    }

    /**
     * This constructor serves to mimic the one which takes in one {@link SolrServer} argument.
     * By having this and then simply disregarding the second parameter, we can more easily support 2-core
     * Solr configurations that use embedded/standalone per environment.
     * 
     * @param solrServer
     * @param reindexServer
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * @throws IOException 
     */
    public SolrSearchServiceImpl(String solrServer, String reindexServer)
            throws IOException, ParserConfigurationException, SAXException {
        this(solrServer);
    }

    /**
     * This constructor serves to mimic the one which takes in one {@link SolrServer} argument.
     * By having this and then simply disregarding the second and third parameters, we can more easily support 2-core
     * Solr configurations that use embedded/standalone per environment, along with an admin server.
     * 
     * @param solrServer
     * @param reindexServer
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * @throws IOException 
     */
    public SolrSearchServiceImpl(String solrServer, String reindexServer, String adminServer)
            throws IOException, ParserConfigurationException, SAXException {
        this(solrServer);
    }

    public SolrSearchServiceImpl(SolrServer solrServer, SolrServer reindexServer) {
        SolrContext.setPrimaryServer(solrServer);
        SolrContext.setReindexServer(reindexServer);
    }

    public SolrSearchServiceImpl(SolrServer solrServer, SolrServer reindexServer, SolrServer adminServer) {
        SolrContext.setPrimaryServer(solrServer);
        SolrContext.setReindexServer(reindexServer);
        SolrContext.setAdminServer(adminServer);
    }

    @Override
    public void rebuildIndex() throws ServiceException, IOException {
        solrIndexService.rebuildIndex();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (SolrContext.isSolrCloudMode()) {
            //We want to use the Solr APIs to make sure the correct collections are set up.

            CloudSolrServer primary = (CloudSolrServer) SolrContext.getServer();
            CloudSolrServer reindex = (CloudSolrServer) SolrContext.getReindexServer();
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
            
            //Set the default collection if it's null
            if (StringUtils.isEmpty(primary.getDefaultCollection())) {
                primary.setDefaultCollection(SolrContext.PRIMARY);
            }

            //Set the default collection if it's null
            if (StringUtils.isEmpty(reindex.getDefaultCollection())) {
                reindex.setDefaultCollection(SolrContext.REINDEX);
            }

            if (primary.getDefaultCollection().equals(reindex.getDefaultCollection())) {
                throw new IllegalStateException("The primary and reindex CloudSolrServers must have a null (empty) or "
                        + "unique defaultCollection property.  All other things like the "
                        + "Zookeeper addresses should be the same.");
            }

            primary.connect(); //This is required to ensure no NPE!

            //Get a list of existing collections so we don't overwrite one
            Set<String> collectionNames = primary.getZkStateReader().getClusterState().getCollections();
            if (collectionNames == null) {
                collectionNames = new HashSet<String>();
            }

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

                CollectionAdminRequest.createCollection(collectionName, solrCloudNumShards, solrCloudConfigName, primary);
                CollectionAdminRequest.createAlias(primary.getDefaultCollection(), collectionName, primary);
            } else {
                //Aliases can be mapped to collections that don't exist.... Make sure the collection exists
                String collectionName = aliasCollectionMap.get(primary.getDefaultCollection());
                collectionName = collectionName.split(",")[0];
                if (!collectionNames.contains(collectionName)) {
                    CollectionAdminRequest.createCollection(collectionName, solrCloudNumShards, solrCloudConfigName, primary);
                }
            }

            //Reload the collection names
            collectionNames = primary.getZkStateReader().getClusterState().getCollections();
            if (collectionNames == null) {
                collectionNames = new HashSet<String>();
            }

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

                CollectionAdminRequest.createCollection(collectionName, solrCloudNumShards, solrCloudConfigName, primary);
                CollectionAdminRequest.createAlias(reindex.getDefaultCollection(), collectionName, primary);
            } else {
                //Aliases can be mapped to collections that don't exist.... Make sure the collection exists
                String collectionName = aliasCollectionMap.get(reindex.getDefaultCollection());
                collectionName = collectionName.split(",")[0];
                if (!collectionNames.contains(collectionName)) {
                    CollectionAdminRequest.createCollection(collectionName, solrCloudNumShards, solrCloudConfigName, primary);
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        //Make sure we shut down each of the SolrServer references (these is really the Solr clients despite the name)
        try {
            if (SolrContext.getServer() != null) {
                SolrContext.getServer().shutdown();
            }
        } catch (Exception e) {
            LOG.error("Error shutting down primary SolrServer (client).", e);
        }

        try {
            if (SolrContext.getReindexServer() != null
                    && SolrContext.getReindexServer() != SolrContext.getServer()) {
                SolrContext.getReindexServer().shutdown();
            }
        } catch (Exception e) {
            LOG.error("Error shutting down reindex SolrServer (client).", e);
        }

        try {
            if (SolrContext.getAdminServer() != null
                    && SolrContext.getAdminServer() != SolrContext.getServer()
                    && SolrContext.getAdminServer() != SolrContext.getReindexServer()) {
                SolrContext.getAdminServer().shutdown();
            }
        } catch (Exception e) {
            LOG.error("Error shutting down admin SolrServer (client).", e);
        }
    }

    @Override
    public SearchResult findExplicitSearchResultsByCategory(Category category, SearchCriteria searchCriteria) throws ServiceException {
        List<SearchFacetDTO> facets = getCategoryFacets(category);
        String query = shs.getExplicitCategoryFieldName() + ":\"" + shs.getCategoryId(category) + "\"";
        return findSearchResults("*:*", facets, searchCriteria, shs.getCategorySortFieldName(category) + " asc", query);
    }

    @Override
    public SearchResult findSearchResultsByCategory(Category category, SearchCriteria searchCriteria) throws ServiceException {
        List<SearchFacetDTO> facets = getCategoryFacets(category);
        String query = shs.getCategoryFieldName() + ":\"" + shs.getCategoryId(category) + "\"";
        return findSearchResults("*:*", facets, searchCriteria, shs.getCategorySortFieldName(category) + " asc", query);
    }

    @Override
    public SearchResult findSearchResultsByQuery(String query, SearchCriteria searchCriteria) throws ServiceException {
        List<SearchFacetDTO> facets = getSearchFacets();
        query = "(" + sanitizeQuery(query) + ")";
        return findSearchResults(query, facets, searchCriteria, null);
    }

    @Override
    public SearchResult findSearchResultsByCategoryAndQuery(Category category, String query, SearchCriteria searchCriteria) throws ServiceException {
        List<SearchFacetDTO> facets = getSearchFacets();

        String catFq = shs.getCategoryFieldName() + ":\"" + shs.getCategoryId(category) + "\"";
        query = "(" + sanitizeQuery(query) + ")";
        
        return findSearchResults(query, facets, searchCriteria, null, catFq);
    }

    public String getLocalePrefix() {
        if (BroadleafRequestContext.getBroadleafRequestContext() != null) {
            Locale locale = BroadleafRequestContext.getBroadleafRequestContext().getLocale();
            if (locale != null) {
                return locale.getLocaleCode() + "_";
            }
        }
        return "";
    }

    protected String buildQueryFieldsString() {
        StringBuilder queryBuilder = new StringBuilder();
        List<Field> fields = null;
        if (useSku) {
            fields = fieldDao.readFieldsByEntityType(FieldEntity.SKU);
        } else {
            fields = fieldDao.readFieldsByEntityType(FieldEntity.PRODUCT);
        }

        for (Field currentField : fields) {
            appendFieldToQuery(queryBuilder, currentField);
        }
        return queryBuilder.toString();
    }

    protected void appendFieldToQuery(StringBuilder queryBuilder, Field currentField) {
        SearchField searchField = searchFieldDao.readSearchFieldForField(currentField);

        if (searchField != null) {
            List<SearchFieldType> searchableFieldTypes = searchField.getSearchableFieldTypes();
            for (SearchFieldType currentType : searchableFieldTypes) {
                String solrFieldName = shs.getPropertyNameForFieldSearchable(currentField, FieldType.getInstance(currentType.getSearchableFieldType()));
                queryBuilder.append(solrFieldName);

                extensionManager.getProxy().modifySolrQueryField(searchField, queryBuilder, solrFieldName);

                queryBuilder.append(" ");
            }
        }

    }

    /**
     * @deprecated in favor of the other findSearchResults() method
     */
    @Deprecated
    protected SearchResult findSearchResults(String qualifiedSolrQuery, List<SearchFacetDTO> facets,
            SearchCriteria searchCriteria, String defaultSort) throws ServiceException {
        return findSearchResults(qualifiedSolrQuery, facets, searchCriteria, defaultSort, (String[]) null);
    }

    /**
     * Given a qualified solr query string (such as "category:2002"), actually performs a solr search. It will
     * take into considering the search criteria to build out facets / pagination / sorting.
     *
     * @param qualifiedSolrQuery
     * @param facets
     * @param searchCriteria
     * @return the ProductSearchResult of the search
     * @throws ServiceException
     */
    protected SearchResult findSearchResults(String qualifiedSolrQuery, List<SearchFacetDTO> facets, SearchCriteria searchCriteria, String defaultSort, String... filterQueries) throws ServiceException {
        Map<String, SearchFacetDTO> namedFacetMap = getNamedFacetMap(facets, searchCriteria);

        // Build the basic query
        // Solr queries with a 'start' parameter cannot be a negative number
        int start = (searchCriteria.getPage() <= 0) ? 0 : (searchCriteria.getPage() - 1);
        SolrQuery solrQuery = new SolrQuery()
                .setQuery(qualifiedSolrQuery)
                .setRows(searchCriteria.getPageSize())
                .setStart((start) * searchCriteria.getPageSize());

        //This is for SolrCloud.  We assume that we are always searching against a collection aliased as "PRIMARY"
        solrQuery.setParam("collection", SolrContext.PRIMARY); //This should be ignored if not using SolrCloud

        solrQuery.setFields(shs.getIndexableIdFieldName());
        if (filterQueries != null) {
            solrQuery.setFilterQueries(filterQueries);
        }
        solrQuery.addFilterQuery(shs.getNamespaceFieldName() + ":(\"" + shs.getCurrentNamespace() + "\")");
        solrQuery.set("defType", "edismax");
        solrQuery.set("qf", buildQueryFieldsString());

        // Attach additional restrictions
        attachSortClause(solrQuery, searchCriteria, defaultSort);
        attachActiveFacetFilters(solrQuery, namedFacetMap, searchCriteria);
        attachFacets(solrQuery, namedFacetMap);
        
        modifySolrQuery(solrQuery, qualifiedSolrQuery, facets, searchCriteria, defaultSort);
        
        extensionManager.getProxy().modifySolrQuery(solrQuery, qualifiedSolrQuery, facets,
                searchCriteria, defaultSort);

        if (LOG.isTraceEnabled()) {
            try {
                LOG.trace(URLDecoder.decode(solrQuery.toString(), "UTF-8"));
            } catch (Exception e) {
                LOG.trace("Couldn't UTF-8 URL Decode: " + solrQuery.toString());
            }
        }

        // Query solr
        QueryResponse response;
        List<SolrDocument> responseDocuments;
        int numResults = 0;
        try {
            response = SolrContext.getServer().query(solrQuery, getSolrQueryMethod());
            responseDocuments = getResponseDocuments(response);
            numResults = (int) response.getResults().getNumFound();

            if (LOG.isTraceEnabled()) {
                LOG.trace(response.toString());

                for (SolrDocument doc : responseDocuments) {
                    LOG.trace(doc);
                }
            }
        } catch (SolrServerException e) {
            throw new ServiceException("Could not perform search", e);
        }

        // Get the facets
        setFacetResults(namedFacetMap, response);
        sortFacetResults(namedFacetMap);

        SearchResult result = new SearchResult();
        result.setFacets(facets);
        setPagingAttributes(result, numResults, searchCriteria);

        if (useSku) {
            List<Sku> skus = getSkus(responseDocuments);
            result.setSkus(skus);
        } else {
            // Get the products
            List<Product> products = getProducts(responseDocuments);
            result.setProducts(products);
        }

        return result;
    }

    /**
     * Provides a hook point for implementations to modify all SolrQueries before they're executed.
     * Modules should leverage the extension manager method of the same name,
     * {@link SolrSearchServiceExtensionHandler#modifySolrQuery(SolrQuery, String, List, SearchCriteria, String)}
     * 
     * @param query
     * @param qualifiedSolrQuery
     * @param facets
     * @param searchCriteria
     * @param defaultSort
     */
    protected void modifySolrQuery(SolrQuery query, String qualifiedSolrQuery,
            List<SearchFacetDTO> facets, SearchCriteria searchCriteria, String defaultSort) {

        extensionManager.getProxy().modifySolrQuery(query, qualifiedSolrQuery, facets, searchCriteria, defaultSort);
    }
    
    protected List<SolrDocument> getResponseDocuments(QueryResponse response) {
        return shs.getResponseDocuments(response);
    }

    @Override
    public List<SearchFacetDTO> getSearchFacets() {
        if (useSku) {
            return buildSearchFacetDTOs(searchFacetDao.readAllSearchFacets(FieldEntity.SKU));
        }
        return buildSearchFacetDTOs(searchFacetDao.readAllSearchFacets(FieldEntity.PRODUCT));
    }

    @Override
    public List<SearchFacetDTO> getCategoryFacets(Category category) {
        List<CategorySearchFacet> categorySearchFacets = category.getCumulativeSearchFacets();

        List<SearchFacet> searchFacets = new ArrayList<SearchFacet>();
        for (CategorySearchFacet categorySearchFacet : categorySearchFacets) {
            searchFacets.add(categorySearchFacet.getSearchFacet());
        }

        return buildSearchFacetDTOs(searchFacets);
    }

    /**
     * Sets up the sorting criteria. This will support sorting by multiple fields at a time
     * 
     * @param query
     * @param searchCriteria
     */
    protected void attachSortClause(SolrQuery query, SearchCriteria searchCriteria, String defaultSort) {
        List<Field> fields = null;
        if (useSku) {
            fields = fieldDao.readFieldsByEntityType(FieldEntity.SKU);
        } else {
            fields = fieldDao.readFieldsByEntityType(FieldEntity.PRODUCT);
        }
        shs.attachSortClause(query, searchCriteria, defaultSort, fields);
    }

    /**
     * Restricts the query by adding active facet filters.
     * 
     * @param query
     * @param namedFacetMap
     * @param searchCriteria
     */
    protected void attachActiveFacetFilters(SolrQuery query, Map<String, SearchFacetDTO> namedFacetMap,
            SearchCriteria searchCriteria) {
        shs.attachActiveFacetFilters(query, namedFacetMap, searchCriteria);
    }
    
    /**
     * Scrubs a facet value string for all Solr special characters, automatically adding escape characters
     * 
     * @param facetValue The raw facet value
     * @return The facet value with all special characters properly escaped, safe to be used in construction of a Solr query
     */
    protected String scrubFacetValue(String facetValue) {
        return shs.scrubFacetValue(facetValue);
    }

    /**
     * Notifies solr about which facets you want it to determine results and counts for
     * 
     * @param query
     * @param namedFacetMap
     */
    protected void attachFacets(SolrQuery query, Map<String, SearchFacetDTO> namedFacetMap) {
        shs.attachFacets(query, namedFacetMap);
    }

    /**
     * Builds out the DTOs for facet results from the search. This will then be used by the view layer to
     * display which values are available given the current constraints as well as the count of the values.
     * 
     * @param namedFacetMap
     * @param response
     */
    protected void setFacetResults(Map<String, SearchFacetDTO> namedFacetMap, QueryResponse response) {
        shs.setFacetResults(namedFacetMap, response);
    }

    /**
     * Invoked to sort the facet results. This method will use the natural sorting of the value attribute of the
     * facet (or, if value is null, the minValue of the facet result). Override this method to customize facet
     * sorting for your given needs.
     * 
     * @param namedFacetMap
     */
    protected void sortFacetResults(Map<String, SearchFacetDTO> namedFacetMap) {
        shs.sortFacetResults(namedFacetMap);
    }

    /**
     * Sets the total results, the current page, and the page size on the ProductSearchResult. Total results comes
     * from solr, while page and page size are duplicates of the searchCriteria conditions for ease of use.
     * 
     * @param result
     * @param response
     * @param searchCriteria
     */
    public void setPagingAttributes(SearchResult result, int numResults, SearchCriteria searchCriteria) {
        result.setTotalResults(numResults);
        result.setPage(searchCriteria.getPage());
        result.setPageSize(searchCriteria.getPageSize());
    }

    /**
     * Given a list of product IDs from solr, this method will look up the IDs via the productDao and build out
     * actual Product instances. It will return a Products that is sorted by the order of the IDs in the passed
     * in list.
     * 
     * @param response
     * @return the actual Product instances as a result of the search
     */
    protected List<Product> getProducts(List<SolrDocument> responseDocuments) {
        final List<Long> productIds = new ArrayList<Long>();
        for (SolrDocument doc : responseDocuments) {
            productIds.add((Long) doc.getFieldValue(shs.getIndexableIdFieldName()));
        }

        List<Product> products = productDao.readProductsByIds(productIds);

        // We have to sort the products list by the order of the productIds list to maintain sortability in the UI
        if (products != null) {
            Collections.sort(products, new Comparator<Product>() {
                @Override
                public int compare(Product o1, Product o2) {
                    Long o1id = shs.getIndexableId(o1);
                    Long o2id = shs.getIndexableId(o2);
                    return new Integer(productIds.indexOf(o1id)).compareTo(productIds.indexOf(o2id));
                }
            });
        }

        return products;
    }

    /**
     * Given a list of Sku IDs from solr, this method will look up the IDs via the skuDao and build out
     * actual Sku instances. It will return a Sku list that is sorted by the order of the IDs in the passed
     * in list.
     * 
     * @param response
     * @return the actual Sku instances as a result of the search
     */
    protected List<Sku> getSkus(List<SolrDocument> responseDocuments) {
        final List<Long> skuIds = new ArrayList<Long>();
        for (SolrDocument doc : responseDocuments) {
            skuIds.add((Long) doc.getFieldValue(shs.getIndexableIdFieldName()));
        }

        List<Sku> skus = skuDao.readSkusByIds(skuIds);

        // We have to sort the skus list by the order of the skuIds list to maintain sortability in the UI
        if (skus != null) {
            Collections.sort(skus, new Comparator<Sku>() {
                @Override
                public int compare(Sku o1, Sku o2) {
                    return new Integer(skuIds.indexOf(o1.getId())).compareTo(skuIds.indexOf(o2.getId()));
                }
            });
        }

        return skus;
    }

    /**
     * Create the wrapper DTO around the SearchFacet
     * 
     * @param searchFacets
     * @return the wrapper DTO
     */
    protected List<SearchFacetDTO> buildSearchFacetDTOs(List<SearchFacet> searchFacets) {
        return shs.buildSearchFacetDTOs(searchFacets);
    }

    /**
     * Checks to see if the requiredFacets condition for a given facet is met.
     * 
     * @param facet
     * @param request
     * @return whether or not the facet parameter is available 
     */
    protected boolean facetIsAvailable(SearchFacet facet, Map<String, String[]> params) {
        return shs.isFacetAvailable(facet, params);
    }

    /**
     * Perform any necessary query sanitation here. For example, we disallow open and close parentheses, colons, and we also
     * ensure that quotes are actual quotes (") and not the URL encoding (&quot;) so that Solr is able to properly handle
     * the user's intent.
     * 
     * @param query
     * @return the sanitized query
     */
    protected String sanitizeQuery(String query) {
        return shs.sanitizeQuery(query);
    }

    /**
     * Returns a fully composed solr field string. Given indexField = a, tag = ex, and a non-null range,
     * would produce the following String: {!tag=a frange incl=false l=minVal u=maxVal}a
     */
    protected String getSolrTaggedFieldString(String indexField, String tag, SearchFacetRange range) {
        return shs.getSolrTaggedFieldString(indexField, tag, range);
    }

    /**
     * Returns a solr field tag. Given indexField = a, tag = tag, would produce the following String:
     * {!tag=a}. if range is not null it will produce {!tag=a frange incl=false l=minVal u=maxVal} 
     */
    protected String getSolrFieldTag(String tagField, String tag, SearchFacetRange range) {
        return shs.getSolrFieldTag(tagField, tag, range);
    }

    protected String getSolrRangeString(String fieldName, BigDecimal minValue, BigDecimal maxValue) {
        return shs.getSolrRangeString(fieldName, minValue, maxValue);
    }

    /**
     * @param minValue
     * @param maxValue
     * @return a string representing a call to the frange solr function. it is not inclusive of lower limit, inclusive of upper limit
     */
    protected String getSolrRangeFunctionString(BigDecimal minValue, BigDecimal maxValue) {
        return shs.getSolrRangeFunctionString(minValue, maxValue);
    }

    /**
     * @param facets
     * @param searchCriteria
     * @return a map of fully qualified solr index field key to the searchFacetDTO object
     */
    protected Map<String, SearchFacetDTO> getNamedFacetMap(List<SearchFacetDTO> facets,
            final SearchCriteria searchCriteria) {
        return shs.getNamedFacetMap(facets, searchCriteria);
    }

    /**
     * @param searchCriteria
     * @return a map of abbreviated key to fully qualified solr index field key for all product fields
     */
    protected Map<String, String> getSolrFieldKeyMap(SearchCriteria searchCriteria) {
        List<Field> fields = null;
        if (useSku) {
            fields = fieldDao.readFieldsByEntityType(FieldEntity.SKU);
        } else {
            fields = fieldDao.readFieldsByEntityType(FieldEntity.PRODUCT);
        }
        return shs.getSolrFieldKeyMap(searchCriteria, fields);
    }

    /**
     * Allows the user to choose the query method to use.  POST allows for longer, more complex queries with 
     * a higher number of facets.
     * 
     * Default value is POST.  Implementors can override this to use GET if they wish.
     * 
     * @return
     */
    protected METHOD getSolrQueryMethod() {
        return METHOD.POST;
    }
}
