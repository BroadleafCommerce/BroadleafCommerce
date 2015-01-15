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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.core.CoreContainer;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.util.BLCMapUtils;
import org.broadleafcommerce.common.util.TypedClosure;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.dao.ProductDao;
import org.broadleafcommerce.core.catalog.dao.SkuDao;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.search.dao.FieldDao;
import org.broadleafcommerce.core.search.dao.SearchFacetDao;
import org.broadleafcommerce.core.search.domain.CategorySearchFacet;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.domain.RequiredFacet;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchFacetRange;
import org.broadleafcommerce.core.search.domain.SearchFacetResultDTO;
import org.broadleafcommerce.core.search.domain.SearchResult;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.broadleafcommerce.core.search.service.SearchService;
import org.springframework.beans.factory.DisposableBean;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
public class SolrSearchServiceImpl implements SearchService, DisposableBean {
    private static final Log LOG = LogFactory.getLog(SolrSearchServiceImpl.class);

    @Value("${solr.index.use.sku}")
    protected boolean useSku;

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

    @Resource(name = "blSolrSearchServiceExtensionManager")
    protected SolrSearchServiceExtensionManager extensionManager;

    public SolrSearchServiceImpl(String solrServer) throws IOException, ParserConfigurationException, SAXException {
        if ("solrhome".equals(solrServer)) {

            final String baseTempPath = System.getProperty("java.io.tmpdir");

            File tempDir = new File(baseTempPath + File.separator + System.getProperty("user.name") + File.separator + "solrhome");
            if (System.getProperty("tmpdir.solrhome") != null) {
                //allow for an override of tmpdir
                tempDir = new File(System.getProperty("tmpdir.solrhome"));
            }
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
            
            solrServer = tempDir.getAbsolutePath();
            
            // create the 'lib' directory with a placeholder file that has to exist in Solr's home directory to avoid a
            // warning log message
            String libDir = FilenameUtils.concat(solrServer, "lib");
            LOG.debug("Creating Solr home lib directory: " + libDir);
            new File(libDir).mkdir();
            
            String placeholder = FilenameUtils.concat(libDir, "solrlib_placeholder.deleteme");
            LOG.debug("Creating Solr lib placeholder file: " + placeholder);
            new File(placeholder).createNewFile();
        }
        
        File solrXml = new File(new File(solrServer), "solr.xml");
        if (!solrXml.exists()) {
            copyConfigToSolrHome(this.getClass().getResourceAsStream("/solr-default.xml"), solrXml);
        }

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
    public void destroy() throws Exception {
        if (SolrContext.getServer() instanceof EmbeddedSolrServer) {
            ((EmbeddedSolrServer) SolrContext.getServer()).shutdown();
        }
    }

    @Override
    public SearchResult findExplicitSearchResultsByCategory(Category category, SearchCriteria searchCriteria) throws ServiceException {
        List<SearchFacetDTO> facets = getCategoryFacets(category);
        String query = shs.getExplicitCategoryFieldName() + ":" + shs.getCategoryId(category.getId());
        return findSearchResults("*:*", facets, searchCriteria, shs.getCategorySortFieldName(category) + " asc", query);
    }

    @Override
    public SearchResult findSearchResultsByCategory(Category category, SearchCriteria searchCriteria) throws ServiceException {
        List<SearchFacetDTO> facets = getCategoryFacets(category);
        String query = shs.getCategoryFieldName() + ":" + shs.getCategoryId(category.getId());
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

        String catFq = shs.getCategoryFieldName() + ":" + shs.getCategoryId(category.getId());
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
            fields = fieldDao.readAllSkuFields();
        } else {
            fields = fieldDao.readAllProductFields();
        }

        for (Field currentField : fields) {
            if (currentField.getSearchable()) {
                appendFieldToQuery(queryBuilder, currentField);
            }
        }
        return queryBuilder.toString();
    }

    protected void appendFieldToQuery(StringBuilder queryBuilder, Field currentField) {
        List<FieldType> searchableFieldTypes = shs.getSearchableFieldTypes(currentField);
        for (FieldType currentType : searchableFieldTypes) {
            queryBuilder.append(shs.getPropertyNameForFieldSearchable(currentField, currentType)).append(" ");
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
        if (useSku) {
            solrQuery.setFields(shs.getSkuIdFieldName());
        } else {
            solrQuery.setFields(shs.getProductIdFieldName());
        }
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
    }
    
    protected List<SolrDocument> getResponseDocuments(QueryResponse response) {
        List<SolrDocument> docs;
        
        if (response.getGroupResponse() == null) {
            docs = response.getResults();
        } else {
            docs = new ArrayList<SolrDocument>();
            GroupResponse gr = response.getGroupResponse();
            for (GroupCommand gc : gr.getValues()) {
                for (Group g : gc.getValues()) {
                    for (SolrDocument d : g.getResult()) {
                        docs.add(d);
                    }
                }
            }
        }
        
        return docs;
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
        Map<String, String> solrFieldKeyMap = getSolrFieldKeyMap(searchCriteria);

        String sortQuery = searchCriteria.getSortQuery();
        if (StringUtils.isBlank(sortQuery)) {
            sortQuery = defaultSort;
        }

        if (StringUtils.isNotBlank(sortQuery)) {
            String[] sortFields = sortQuery.split(",");
            for (String sortField : sortFields) {
                String field = sortField.split(" ")[0];
                if (solrFieldKeyMap.containsKey(field)) {
                    field = solrFieldKeyMap.get(field);
                }
                ORDER order = "desc".equals(sortField.split(" ")[1]) ? ORDER.desc : ORDER.asc;

                if (field != null) {
                    query.addSort(new SortClause(field, order));
                }
            }
        }
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
        if (searchCriteria.getFilterCriteria() != null) {
            for (Entry<String, String[]> entry : searchCriteria.getFilterCriteria().entrySet()) {
                String solrKey = null;
                for (Entry<String, SearchFacetDTO> dtoEntry : namedFacetMap.entrySet()) {
                    if (dtoEntry.getValue().getFacet().getField().getAbbreviation().equals(entry.getKey())) {
                        solrKey = dtoEntry.getKey();
                        dtoEntry.getValue().setActive(true);
                    }
                }

                if (solrKey != null) {
                    String[] selectedValues = entry.getValue().clone();
                    for (int i = 0; i < selectedValues.length; i++) {
                        if (selectedValues[i].contains("range[")) {
                            String rangeValue = selectedValues[i].substring(selectedValues[i].indexOf('[') + 1,
                                    selectedValues[i].indexOf(']'));
                            String[] rangeValues = StringUtils.split(rangeValue, ':');
                            BigDecimal minValue = new BigDecimal(rangeValues[0]);
                            BigDecimal maxValue = null;
                            if (!rangeValues[1].equals("null")) {
                                maxValue = new BigDecimal(rangeValues[1]);
                            }
                            selectedValues[i] = "{!" + getSolrRangeFunctionString(minValue, maxValue) + "}field(" + solrKey + ")";
                        } else {
                            selectedValues[i] = solrKey + ":\"" + selectedValues[i] + "\"";
                        }
                    }
                    String valueString = StringUtils.join(selectedValues, " OR ");

                    StringBuilder sb = new StringBuilder();
                    sb.append("(").append(valueString).append(")");

                    query.addFilterQuery(sb.toString());
                }
            }
        }
    }

    /**
     * Notifies solr about which facets you want it to determine results and counts for
     * 
     * @param query
     * @param namedFacetMap
     */
    protected void attachFacets(SolrQuery query, Map<String, SearchFacetDTO> namedFacetMap) {
        query.setFacet(true);
        for (Entry<String, SearchFacetDTO> entry : namedFacetMap.entrySet()) {
            SearchFacetDTO dto = entry.getValue();

            // Clone the list - we don't want to remove these facets from the DB
            List<SearchFacetRange> facetRanges = new ArrayList<SearchFacetRange>(dto.getFacet().getSearchFacetRanges());

            if (extensionManager != null) {
                extensionManager.getProxy().filterSearchFacetRanges(dto, facetRanges);
            }

            if (facetRanges != null && facetRanges.size() > 0) {
                for (SearchFacetRange range : facetRanges) {
                    query.addFacetQuery(getSolrTaggedFieldString(entry.getKey(), "key", range));
                }
            } else {
                query.addFacetField(getSolrTaggedFieldString(entry.getKey(), "key", null));
            }
        }
    }

    /**
     * Builds out the DTOs for facet results from the search. This will then be used by the view layer to
     * display which values are available given the current constraints as well as the count of the values.
     * 
     * @param namedFacetMap
     * @param response
     */
    protected void setFacetResults(Map<String, SearchFacetDTO> namedFacetMap, QueryResponse response) {
        if (response.getFacetFields() != null) {
            for (FacetField facet : response.getFacetFields()) {
                String facetFieldName = facet.getName();
                SearchFacetDTO facetDTO = namedFacetMap.get(facetFieldName);

                for (Count value : facet.getValues()) {
                    SearchFacetResultDTO resultDTO = new SearchFacetResultDTO();
                    resultDTO.setFacet(facetDTO.getFacet());
                    resultDTO.setQuantity(new Long(value.getCount()).intValue());
                    resultDTO.setValue(value.getName());
                    facetDTO.getFacetValues().add(resultDTO);
                }
            }
        }

        if (response.getFacetQuery() != null) {
            for (Entry<String, Integer> entry : response.getFacetQuery().entrySet()) {
                String key = entry.getKey();
                String facetFieldName = key.substring(0, key.indexOf("["));
                SearchFacetDTO facetDTO = namedFacetMap.get(facetFieldName);

                String minValue = key.substring(key.indexOf("[") + 1, key.indexOf(":"));
                String maxValue = key.substring(key.indexOf(":") + 1, key.indexOf("]"));
                if (maxValue.equals("*")) {
                    maxValue = null;
                }

                SearchFacetResultDTO resultDTO = new SearchFacetResultDTO();
                resultDTO.setFacet(facetDTO.getFacet());
                resultDTO.setQuantity(entry.getValue());
                resultDTO.setMinValue(new BigDecimal(minValue));
                resultDTO.setMaxValue(maxValue == null ? null : new BigDecimal(maxValue));

                facetDTO.getFacetValues().add(resultDTO);
            }
        }
    }

    /**
     * Invoked to sort the facet results. This method will use the natural sorting of the value attribute of the
     * facet (or, if value is null, the minValue of the facet result). Override this method to customize facet
     * sorting for your given needs.
     * 
     * @param namedFacetMap
     */
    protected void sortFacetResults(Map<String, SearchFacetDTO> namedFacetMap) {
        for (Entry<String, SearchFacetDTO> entry : namedFacetMap.entrySet()) {
            Collections.sort(entry.getValue().getFacetValues(), new Comparator<SearchFacetResultDTO>() {
                @Override
                public int compare(SearchFacetResultDTO o1, SearchFacetResultDTO o2) {
                    if (o1.getValue() != null && o2.getValue() != null) {
                        return o1.getValue().compareTo(o2.getValue());
                    } else if (o1.getMinValue() != null && o2.getMinValue() != null) {
                        return o1.getMinValue().compareTo(o2.getMinValue());
                    }
                    return 0; // Don't know how to compare
                }
            });
        }
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
            productIds.add((Long) doc.getFieldValue(shs.getProductIdFieldName()));
        }

        List<Product> products = productDao.readProductsByIds(productIds);

        // We have to sort the products list by the order of the productIds list to maintain sortability in the UI
        if (products != null) {
            Collections.sort(products, new Comparator<Product>() {
                @Override
                public int compare(Product o1, Product o2) {
                    Long o1id = shs.getProductId(o1.getId());
                    Long o2id = shs.getProductId(o2.getId());
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
            skuIds.add((Long) doc.getFieldValue(shs.getSkuIdFieldName()));
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
        List<SearchFacetDTO> facets = new ArrayList<SearchFacetDTO>();
        Map<String, String[]> requestParameters = BroadleafRequestContext.getRequestParameterMap();

        for (SearchFacet facet : searchFacets) {
            if (facetIsAvailable(facet, requestParameters)) {
                SearchFacetDTO dto = new SearchFacetDTO();
                dto.setFacet(facet);
                dto.setShowQuantity(true);
                facets.add(dto);
            }
        }

        return facets;
    }

    /**
     * Checks to see if the requiredFacets condition for a given facet is met.
     * 
     * @param facet
     * @param request
     * @return whether or not the facet parameter is available 
     */
    protected boolean facetIsAvailable(SearchFacet facet, Map<String, String[]> params) {
        // Facets are available by default if they have no requiredFacets
        if (CollectionUtils.isEmpty(facet.getRequiredFacets())) {
            return true;
        }

        // If we have at least one required facet but no active facets, it's impossible for this facet to be available
        if (MapUtils.isEmpty(params)) {
            return false;
        }

        // We must either match all or just one of the required facets depending on the requiresAllDependentFacets flag
        int requiredMatches = facet.getRequiresAllDependentFacets() ? facet.getRequiredFacets().size() : 1;
        int matchesSoFar = 0;

        for (RequiredFacet requiredFacet : facet.getRequiredFacets()) {
            if (requiredMatches == matchesSoFar) {
                return true;
            }

            // Check to see if the required facet has a value in the current request parameters
            for (Entry<String, String[]> entry : params.entrySet()) {
                String key = entry.getKey();
                if (key.equals(requiredFacet.getRequiredFacet().getField().getAbbreviation())) {
                    matchesSoFar++;
                    break;
                }
            }
        }

        return requiredMatches == matchesSoFar;
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
        return query.replace("(", "").replace("%28", "")
                .replace(")", "").replace("%29", "")
                .replace(":", "").replace("%3A", "").replace("%3a", "")
                .replace("&quot;", "\""); // Allow quotes in the query for more finely tuned matches
    }

    /**
     * Returns a fully composed solr field string. Given indexField = a, tag = ex, and a non-null range,
     * would produce the following String: {!tag=a frange incl=false l=minVal u=maxVal}a
     */
    protected String getSolrTaggedFieldString(String indexField, String tag, SearchFacetRange range) {
        return getSolrFieldTag(indexField, tag, range) + (range == null ? indexField : ("field(" + indexField + ")"));
    }

    /**
     * Returns a solr field tag. Given indexField = a, tag = tag, would produce the following String:
     * {!tag=a}. if range is not null it will produce {!tag=a frange incl=false l=minVal u=maxVal} 
     */
    protected String getSolrFieldTag(String tagField, String tag, SearchFacetRange range) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(tag)) {
            sb.append("{!").append(tag).append("=").append(tagField);
        	
            if (range != null) {
                sb.append("[").append(range.getMinValue().toPlainString()).append(":");
                if (range.getMaxValue() != null)
                    sb.append(range.getMaxValue().toPlainString());
                else
                    sb.append("*");
                sb.append("]");
    			
                sb.append(" "+getSolrRangeFunctionString(range.getMinValue(), range.getMaxValue()));
            }
        	
            sb.append("}");
        }
        return sb.toString();
    }
	
    /**
     * @param minValue
     * @param maxValue
     * @return a string representing a call to the frange solr function. it is not inclusive of lower limit, inclusive of upper limit
     */
	protected String getSolrRangeFunctionString(BigDecimal minValue, BigDecimal maxValue) {
		StringBuilder sb = new StringBuilder();

        sb.append("frange incl=false l=").append(minValue.toPlainString());
        if (maxValue != null) {
        	sb.append(" u=").append(maxValue.toPlainString());
        }
		
		return sb.toString();
	}

    /**
     * @param facets
     * @param searchCriteria
     * @return a map of fully qualified solr index field key to the searchFacetDTO object
     */
    protected Map<String, SearchFacetDTO> getNamedFacetMap(List<SearchFacetDTO> facets,
            final SearchCriteria searchCriteria) {
        return BLCMapUtils.keyedMap(facets, new TypedClosure<String, SearchFacetDTO>() {

            @Override
            public String getKey(SearchFacetDTO facet) {
                return getSolrFieldKey(facet.getFacet().getField(), searchCriteria);
            }
        });
    }

    /**
     * This method will be used to map a field abbreviation to the appropriate solr index field to use. Typically,
     * this default implementation that maps to the facet field type will be sufficient. However, there may be 
     * cases where you would want to use a different solr index depending on other currently active facets. In that
     * case, you would associate that mapping here. For example, for the "price" abbreviation, we would generally
     * want to use "defaultSku.retailPrice_td". However, if a secondary facet on item condition is selected (such
     * as "refurbished", we may want to index "price" to "refurbishedSku.retailPrice_td". That mapping occurs here.
     * 
     * @param fields
     * @param searchCriteria the searchCriteria in case it is needed to determine the field key
     * @return the solr field index key to use
     */
    protected String getSolrFieldKey(Field field, SearchCriteria searchCriteria) {
        return shs.getPropertyNameForFieldFacet(field);
    }

    /**
     * @param searchCriteria
     * @return a map of abbreviated key to fully qualified solr index field key for all product fields
     */
    protected Map<String, String> getSolrFieldKeyMap(SearchCriteria searchCriteria) {
        List<Field> fields = null;
        if (useSku) {
            fields = fieldDao.readAllSkuFields();
        } else {
            fields = fieldDao.readAllProductFields();
        }
        Map<String, String> solrFieldKeyMap = new HashMap<String, String>();
        for (Field field : fields) {
            solrFieldKeyMap.put(field.getAbbreviation(), getSolrFieldKey(field, searchCriteria));
        }
        return solrFieldKeyMap;
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
