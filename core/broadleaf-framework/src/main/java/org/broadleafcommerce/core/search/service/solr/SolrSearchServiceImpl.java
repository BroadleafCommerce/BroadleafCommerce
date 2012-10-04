/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.search.service.solr;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.pricelist.domain.PriceList;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.dao.ProductDao;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.search.dao.FieldDao;
import org.broadleafcommerce.core.search.dao.SearchFacetDao;
import org.broadleafcommerce.core.search.domain.CategorySearchFacet;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.ProductSearchCriteria;
import org.broadleafcommerce.core.search.domain.ProductSearchResult;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchFacetRange;
import org.broadleafcommerce.core.search.domain.SearchFacetResultDTO;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.broadleafcommerce.core.search.service.SearchService;
import org.broadleafcommerce.core.util.StopWatch;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * An implementation of SearchService that uses Solr
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class SolrSearchServiceImpl implements SearchService, DisposableBean {
    private static final Log LOG = LogFactory.getLog(SolrSearchServiceImpl.class);
    protected static final String GLOBAL_FACET_TAG_FIELD = "a";
    protected static final String DEFAULT_NAMESPACE = "d";
    
	@Resource(name = "blProductDao")
	protected ProductDao productDao;
	
	@Resource(name = "blFieldDao")
	protected FieldDao fieldDao;
	
	@Resource(name = "blSearchFacetDao")
	protected SearchFacetDao searchFacetDao;
	
	@Resource(name = "blLocaleService")
	protected LocaleService localeService;
	
	protected SolrServer server;

	public SolrSearchServiceImpl(String solrServer) throws IOException, ParserConfigurationException, SAXException {
		System.setProperty("solr.solr.home", solrServer);
		CoreContainer.Initializer initializer = new CoreContainer.Initializer();
		CoreContainer coreContainer = initializer.initialize();
		EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "");
		this.server = server;
	}
	
	public SolrSearchServiceImpl(SolrServer solrServer) {
		this.server = solrServer;
	}

    @Override
    public void destroy() throws Exception {
        if (server instanceof EmbeddedSolrServer) {
            ((EmbeddedSolrServer) server).shutdown();
        }
    }
    
    @Override
    @Transactional("blTransactionManager")
	public void rebuildIndex() throws ServiceException, IOException {
		LOG.info("Rebuilding the solr index...");
		StopWatch s = new StopWatch();
		
		List<Product> products = productDao.readAllActiveProducts(SystemTime.asDate());
		List<Field> fields = fieldDao.readAllProductFields();
		
		List<Locale> locales = getAllLocales();
		List<PriceList> priceLists = getAllPriceLists();
		
	    Collection<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();
		for (Product product : products) {
	        documents.add(buildDocument(product, fields, locales, priceLists));
		}
		
		if (LOG.isTraceEnabled()) {
		    for (SolrInputDocument document : documents) {
		        LOG.trace(document);
		    }
		}
		
	    try {
	        String deleteQuery = getNamespaceFieldName() + ":" + getCurrentNamespace();
	    	LOG.trace("Deleting by query: " + deleteQuery);
	    	server.deleteByQuery(deleteQuery);
	    	server.commit();
	    	
		    server.add(documents);
		    server.commit();
	    } catch (SolrServerException e) {
	    	throw new ServiceException("Could not rebuild index", e);
	    }
	    
	    LOG.info("Finished rebuilding the solr index in " + s.toLapString());
	}
    
    /**
     * @return a list of all possible locale prefixes to consider
     */
    protected List<Locale> getAllLocales() {
        return localeService.findAllLocales();
    }
    
    /**
     * @return a list of all possible pricelist prefixes to consider
     */
    protected List<PriceList> getAllPriceLists() {
        //FIXME: Return actual pricelists
		List<PriceList> priceLists = new ArrayList<PriceList>();
		return priceLists;
    }
    
    /**
     * Given a product, fields that relate to that product, and a list of locales and pricelists, builds a SolrInputDocument
     * to be added to the Solr index.
     * 
     * @param product
     * @param fields
     * @param locales
     * @param priceLists
     * @return the document
     */
    protected SolrInputDocument buildDocument(Product product, List<Field> fields, 
            List<Locale> locales, List<PriceList> priceLists) {
        SolrInputDocument document = new SolrInputDocument();
        
        attachBasicDocumentFields(product, document);
        
        // Add data-driven user specified searchable fields
        List<String> addedProperties = new ArrayList<String>();
        Map<String, List<String>> copyFieldValues = new HashMap<String, List<String>>();
        
        for (Field field : fields) {
            try {
                // Index the searchable fields
                if (field.getSearchable()) {
                    for (FieldType searchableFieldType : field.getSearchableFieldTypes()) {
                        Map<String, Object> propertyValues = 
                                getPropertyValues(product, field, searchableFieldType, locales, priceLists);
                        
                        // Build out the field for every prefix
                        for (Entry<String, Object> entry : propertyValues.entrySet()) {
                            String prefix = entry.getKey() + "_";
                            Object value = entry.getValue();
                            
                            String solrPropertyName = getPropertyNameForFieldSearchable(field, searchableFieldType, prefix);
                            
                            // Add the field to Solr to search directly against it
                            document.addField(solrPropertyName, value);
                            addedProperties.add(solrPropertyName);
                            
                            // Add this field to the copyField so that we can search against its content generally
                            List<String> copyFieldValue = copyFieldValues.get(prefix);
                            if (copyFieldValue == null) {
                                copyFieldValue = new ArrayList<String>();
                                copyFieldValues.put(prefix, copyFieldValue);
                            }
                            copyFieldValue.add(value.toString());
                        }
                    }
                }
                
                // Index the faceted field type as well
                FieldType facetFieldType = field.getFacetFieldType();
                if (facetFieldType != null) {
                    Map<String, Object> propertyValues = 
                            getPropertyValues(product, field, facetFieldType, locales, priceLists);
                    
                    // Build out the field for every prefix
                    for (Entry<String, Object> entry : propertyValues.entrySet()) {
                        String prefix = entry.getKey() + "_";
                        String solrFacetPropertyName = getPropertyNameForFieldFacet(field, prefix);
                        if (!addedProperties.contains(solrFacetPropertyName)) {
                            Object value = entry.getValue();
                            document.addField(solrFacetPropertyName, value);
                        }
                    }
                }
            } catch (Exception e) {
                LOG.trace("Could not get value for property[" + field.getQualifiedFieldName() + "] for product id["
                        + product.getId() + "]");
            }
        }
        
        for (Entry<String, List<String>> entry : copyFieldValues.entrySet()) {
            document.addField(getSearchableFieldName(entry.getKey()), StringUtils.join(entry.getValue(), " "));
        }
        
        return document;
    }

    /**
     * Adds the ID, category, and explicitCategory fields for the product to the document
     * 
     * @param product
     * @param document
     */
    protected void attachBasicDocumentFields(Product product, SolrInputDocument document) {
        // Add the namespace and ID fields for this product
        document.addField(getNamespaceFieldName(), getCurrentNamespace());
        document.addField(getIdFieldName(), product.getId());
        
        // The explicit categories are the ones defined by the product itself
        for (Category category : product.getAllParentCategories()) {
            document.addField(getExplicitCategoryFieldName(), category.getId());
            
            String categorySortField = getCategorySortField(category);
            int listIndex = category.getAllProducts().indexOf(product);
            document.addField(categorySortField, listIndex);
        }
        
        // This is the entire tree of every category defined on the product
        Set<Category> fullCategoryHierarchy = new HashSet<Category>();
        for (Category category : product.getAllParentCategories()) {
            fullCategoryHierarchy.addAll(category.buildFullCategoryHierarchy(null));
        }
        for (Category category : fullCategoryHierarchy) {
            document.addField(getCategoryFieldName(), category.getId());
        }
    }
    
    /**
     * Returns a map of prefix to value for the requested attributes. For example, if the requested field corresponds to
     * a Sku's description and the locales list has the en_US locale and the es_ES locale, the resulting map could be
     * 
     * { "en_US" : "A description",
     *   "es_ES" : "Una descripcion" }
     * 
     * @param product
     * @param field
     * @param isPriceField
     * @param prefix
     * @return the value of the property
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    protected Map<String, Object> getPropertyValues(Product product, Field field, FieldType fieldType, List<Locale> locales, 
            List<PriceList> priceLists) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        
        String propertyName = field.getPropertyName();
        if (propertyName.contains("productAttributes.")) {
            propertyName = convertToMappedProperty(propertyName, "productAttributes", "mappedProductAttributes");
        }
        
        Map<String, Object> values = new HashMap<String, Object>();
        
        if (fieldType.equals(FieldType.PRICE)) {
            //FIXME: this is wrong
            Object propertyValue = PropertyUtils.getProperty(product, propertyName);
            values.put("default", propertyValue);
            //for (PriceList priceList : priceLists) {
                //
            //}
        } else {
            for (Locale locale : locales) {
                BroadleafRequestContext ctx = BroadleafRequestContext.getBroadleafRequestContext();
                if (ctx == null) { 
                    ctx = new BroadleafRequestContext();
                    BroadleafRequestContext.setBroadleafRequestContext(ctx);
                }
                
                ctx.setLocale(locale);
                Object propertyValue = PropertyUtils.getProperty(product, propertyName);
                
                values.put(locale.getLocaleCode(), propertyValue);
            }
        }
        
        return values;
    }
    
	@Override
	public ProductSearchResult findExplicitProductsByCategory(Category category, ProductSearchCriteria searchCriteria) 
			throws ServiceException {
		List<SearchFacetDTO> facets = getCategoryFacets(category);
		String query = getExplicitCategoryFieldName() + ":" + category.getId();
		return findProducts(query, facets, searchCriteria, getCategorySortField(category) + " asc");
	}
	
	@Override
	public ProductSearchResult findProductsByCategory(Category category, ProductSearchCriteria searchCriteria) 
			throws ServiceException {
		List<SearchFacetDTO> facets = getCategoryFacets(category);
		String query = getCategoryFieldName() + ":" + category.getId();
		return findProducts(query, facets, searchCriteria, getCategorySortField(category) + " asc");
	}
	
	@Override
	public ProductSearchResult findProductsByQuery(String query, ProductSearchCriteria searchCriteria) 
			throws ServiceException {
		List<SearchFacetDTO> facets = getSearchFacets();
		
		query = sanitizeQuery(query);
		
		query = getSearchableFieldName() + ":(" + query + ")"; 
		return findProducts(query, facets, searchCriteria, null);
	}
	
	@Override
	public ProductSearchResult findProductsByCategoryAndQuery(Category category, String query, 
	        ProductSearchCriteria searchCriteria) throws ServiceException {
		List<SearchFacetDTO> facets = getSearchFacets();
		
		query = sanitizeQuery(query);
		
		StringBuilder sb = new StringBuilder();
		sb.append(getCategoryFieldName()).append(":").append(category.getId())
		    .append(" AND ")
		    .append(getSearchableFieldName()).append(":(").append(query).append(")"); 
		return findProducts(sb.toString(), facets, searchCriteria, null);
	}
	
	@Override
	public List<SearchFacetDTO> getSearchFacets() {
		return buildSearchFacetDTOs(searchFacetDao.readAllSearchFacets());
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
	 * Given a qualified solr query string (such as "category:2002"), actually performs a solr search. It will
	 * take into considering the search criteria to build out facets / pagination / sorting.
	 * 
	 * @param qualifiedSolrQuery
	 * @param facets
	 * @param searchCriteria
	 * @return the ProductSearchResult of the search
	 * @throws ServiceException
	 */
	protected ProductSearchResult findProducts(String qualifiedSolrQuery, List<SearchFacetDTO> facets, 
			ProductSearchCriteria searchCriteria, String defaultSort) throws ServiceException {
		Map<String, SearchFacetDTO> namedFacetMap = getNamedFacetMap(facets, searchCriteria);
		
		// Build the basic query
	    SolrQuery solrQuery = new SolrQuery()
	    	.setQuery(qualifiedSolrQuery)
            .setFields(getIdFieldName())
//.setRows(searchCriteria.getPageSize())
.setRows(100)
            .setFilterQueries(getNamespaceFieldName() + ":" + getCurrentNamespace())
    		.setStart((searchCriteria.getPage() - 1) * searchCriteria.getPageSize());
	    
	    // Attach additional restrictions
	    attachSortClause(solrQuery, searchCriteria, defaultSort);
	    attachActiveFacetFilters(solrQuery, namedFacetMap, searchCriteria);
	    attachFacets(solrQuery, namedFacetMap);
	    
	    if (LOG.isTraceEnabled()) {
	        try {
	            LOG.trace(URLDecoder.decode(solrQuery.toString(), "UTF-8"));
	        } catch (Exception e) {
	            LOG.trace("Couldn't UTF-8 URL Decode: " + solrQuery.toString());
	        }
	    }
	    
	    // Query solr
	    QueryResponse response;
	    try {
	    	response = server.query(solrQuery);
	    	if (LOG.isTraceEnabled()) {
	    	    LOG.trace(response.toString());
	        
    	        for (SolrDocument doc : response.getResults()) {
    	            LOG.trace(doc);
    	        }
	    	}
	    } catch (SolrServerException e) {
	    	throw new ServiceException("Could not perform search", e);
	    }
	    
	    // Get the facets
	    setFacetResults(namedFacetMap, response);
	    sortFacetResults(namedFacetMap);
	    	
	    // Get the products
	    List<Product> products = getProducts(response);
	    
	    ProductSearchResult result = new ProductSearchResult();
	    result.setFacets(facets);
	    result.setProducts(products);
	    setPagingAttributes(result, response, searchCriteria);
	    return result;
	}
	
	/**
	 * Sets up the sorting criteria. This will support sorting by multiple fields at a time
	 * 
	 * @param query
	 * @param searchCriteria
	 */
	protected void attachSortClause(SolrQuery query, ProductSearchCriteria searchCriteria, String defaultSort) {
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
					query.addSortField(field, order);
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
			ProductSearchCriteria searchCriteria) {
		for (Entry<String, String[]> entry : searchCriteria.getFilterCriteria().entrySet()) {
			String solrKey = null;
			for (Entry<String, SearchFacetDTO> dtoEntry : namedFacetMap.entrySet()) {
				if (dtoEntry.getValue().getFacet().getField().getAbbreviation().equals(entry.getKey())) {
					solrKey = dtoEntry.getKey();
					dtoEntry.getValue().setActive(true);
				}
			}
			
			if (solrKey != null) {
				String solrTag = getSolrFieldTag(GLOBAL_FACET_TAG_FIELD, "tag");
				
				String[] selectedValues = entry.getValue().clone();
				for (int i = 0; i < selectedValues.length; i++) {
					if (selectedValues[i].contains("range[")) {
						String rangeValue = selectedValues[i].substring(selectedValues[i].indexOf('[') + 1, 
								selectedValues[i].indexOf(']'));
						String[] rangeValues = StringUtils.split(rangeValue, ':');
						if (rangeValues[1].equals("null")) {
							rangeValues[1] = "*";
						}
						selectedValues[i] = solrKey + ":[" + rangeValues[0] + " TO " + rangeValues[1] + "]";
					} else {
						selectedValues[i] = solrKey + ":\"" + selectedValues[i] + "\"";
					}
				}
				String valueString = StringUtils.join(selectedValues, " OR ");
				
				StringBuilder sb = new StringBuilder();
				sb.append(solrTag).append("(").append(valueString).append(")");
				
				query.addFilterQuery(sb.toString());
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
			String facetTagField = entry.getValue().isActive() ? GLOBAL_FACET_TAG_FIELD : entry.getKey();
			if (dto.getFacet().getSearchFacetRanges().size() > 0) {
				for (SearchFacetRange range : dto.getFacet().getSearchFacetRanges()) {
					query.addFacetQuery(getSolrTaggedFieldString(entry.getKey(), facetTagField, "ex", range));
				}
			} else {
				query.addFacetField(getSolrTaggedFieldString(entry.getKey(), facetTagField, "ex", null));
			}
		}
	}
	
	/**
	 * Builds out the DTOs for facet results from the search. This will then be used by the view layer to
	 * display which values are avaialble given the current constraints as well as the count of the values.
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
	    		String facetFieldName = key.substring(key.indexOf("}") + 1, key.indexOf(':'));
	    		SearchFacetDTO facetDTO = namedFacetMap.get(facetFieldName);
	    		
	    		String minValue = key.substring(key.indexOf("[") + 1, key.indexOf(" TO"));
	    		String maxValue = key.substring(key.indexOf(" TO ") + 4, key.indexOf("]"));
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
	public void setPagingAttributes(ProductSearchResult result, QueryResponse response, 
			ProductSearchCriteria searchCriteria) {
	    result.setTotalResults(new Long(response.getResults().getNumFound()).intValue());
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
	protected List<Product> getProducts(QueryResponse response) {
	    final List<Long> productIds = new ArrayList<Long>();
		SolrDocumentList docs = response.getResults();
    	for (SolrDocument doc : docs) {
    		productIds.add((Long) doc.getFieldValue(getIdFieldName()));
    	}
    	
	    List<Product> products = productDao.readProductsByIds(productIds); 
	    
	    // We have to sort the products list by the order of the productIds list to maintain sortability in the UI
	    if (products != null) {
		    Collections.sort(products, new Comparator<Product>() {
				public int compare(Product o1, Product o2) {
					return new Integer(productIds.indexOf(o1.getId())).compareTo(productIds.indexOf(o2.getId()));
				}
		    });
	    }
	    
		return products;
	}
	
	/**
	 * Returns a fully composed solr field string. Given indexField = a, tag = ex, and a non-null range,
	 * would produce the following String: {!ex=a}a:[minVal TO maxVal]
	 */
	protected String getSolrTaggedFieldString(String indexField, String tagField, String tag, SearchFacetRange range) {
		return getSolrFieldTag(tagField, tag) + getSolrFieldString(indexField, range);
	}
	
	/**
	 * Returns a solr field tag. Given indexField = a, tag = ex, would produce the following String:
	 * {!ex=a}
	 */
	protected String getSolrFieldTag(String tagField, String tag) {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotBlank(tag)) {
			sb.append("{!").append(tag).append("=").append(tagField).append("}");
		}
		return sb.toString();
	}
	
	/**
	 * Returns a field string. Given indexField = a and a non-null range, would produce the following String:
	 * a:[minVal TO maxVal]
	 */
	protected String getSolrFieldString(String indexField, SearchFacetRange range) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(indexField);
		
		if (range != null) {
			String minValue = range.getMinValue().toPlainString();
			String maxValue = range.getMaxValue() == null ? "*" : range.getMaxValue().toPlainString();
			sb.append(":[").append(minValue).append(" TO ").append(maxValue).append("]");
		}
		
		return sb.toString();
	}
	
	/**
	 * Create the wrapper DTO around the SearchFacet
	 * 
	 * @param searchFacets
	 * @return the wrapper DTO
	 */
	protected List<SearchFacetDTO> buildSearchFacetDTOs(List<SearchFacet> searchFacets) {
		List<SearchFacetDTO> facets = new ArrayList<SearchFacetDTO>();
		
		for (SearchFacet facet : searchFacets) {
			SearchFacetDTO dto = new SearchFacetDTO();
			dto.setFacet(facet);
			dto.setShowQuantity(true);
			facets.add(dto);
		}
		
		return facets;
	}
	
	/**
	 * Converts a propertyName to one that is able to reference inside a map. For example, consider the property
	 * in Product that references a List<ProductAttribute>, "productAttributes". Also consider the utility method
	 * in Product called "mappedProductAttributes", which returns a map of the ProductAttributes keyed by the name
	 * property in the ProductAttribute. Given the parameters "productAttributes.heatRange", "productAttributes", 
	 * "mappedProductAttributes" (which would represent a property called "productAttributes.heatRange" that 
	 * references a specific ProductAttribute inside of a product whose "name" property is equal to "heatRange", 
	 * this method will convert this property to mappedProductAttributes(heatRange).value, which is then usable 
	 * by the standard beanutils PropertyUtils class to get the value.
	 * 
	 * @param propertyName
	 * @param listPropertyName
	 * @param mapPropertyName
	 * @return the converted property name
	 */
	protected String convertToMappedProperty(String propertyName, String listPropertyName, String mapPropertyName) {
		String[] splitName = StringUtils.split(propertyName, ".");
		StringBuilder convertedProperty = new StringBuilder();
		for (int i = 0; i < splitName.length; i++) {
			if (convertedProperty.length() > 0) {
				convertedProperty.append(".");
			}
			
			if (splitName[i].equals(listPropertyName)) {
				convertedProperty.append(mapPropertyName).append("(");
				convertedProperty.append(splitName[i+1]).append(").value");
				i++;
			} else {
				convertedProperty.append(splitName[i]);
			}
		}
		return convertedProperty.toString();
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
	protected String getSolrFieldKey(Field field, ProductSearchCriteria searchCriteria) {
	    return getPropertyNameForFieldFacet(field);
	}
	
	/**
	 * @param searchCriteria
	 * @return a map of abbreviated key to fully qualified solr index field key for all product fields
	 */
	protected Map<String, String> getSolrFieldKeyMap(ProductSearchCriteria searchCriteria) {
		List<Field> fields = fieldDao.readAllProductFields();
		Map<String, String> solrFieldKeyMap = new HashMap<String, String>();
		for (Field field : fields) {
			solrFieldKeyMap.put(field.getAbbreviation(), getSolrFieldKey(field, searchCriteria));
		}
		return solrFieldKeyMap;
	}
	
	/**
	 * @param facets
	 * @param searchCriteria
	 * @return a map of fully qualified solr index field key to the searchFacetDTO object
	 */
	protected Map<String, SearchFacetDTO> getNamedFacetMap(List<SearchFacetDTO> facets, 
			ProductSearchCriteria searchCriteria) {
		Map<String, SearchFacetDTO> namedFacetMap = new HashMap<String, SearchFacetDTO>();
		for (SearchFacetDTO facet : facets) {
			Field facetField = facet.getFacet().getField();
			namedFacetMap.put(getSolrFieldKey(facetField, searchCriteria), facet);
		}
		return namedFacetMap;
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
	 * Determines the current namespace we are operating on. For example, if you have multiple sites set up, 
	 * you may want to filter that here. 
	 * 
	 * <ul>
	 *     <li>Note: This method should ALWAYS return a non-empty string.</li>
	 * </ul>
	 * 
	 * @return the global prefix if there is one, "" if there isn't
	 */
	protected String getCurrentNamespace() {
	    return DEFAULT_NAMESPACE;
	}
	
	/**
	 * Determines if there is a locale prefix that needs to be applied to fields for this particular request.
	 * By default, a locale prefix is not applicable for category, explicitCategory, or fields that have type Price
	 * 
	 * <ul>
	 *     <li>Note: This method should NOT return null. If there is no prefix, it should return the empty string.</li>
	 *     <li>Note: If there is a prefix, it MUST end in an underscore</li>
	 * </ul>
	 * 
	 * @return the global prefix if there is one, "" if there isn't
	 */
	protected String getLocalePrefix() {
	    if (BroadleafRequestContext.getBroadleafRequestContext() != null) {
	        Locale locale = BroadleafRequestContext.getBroadleafRequestContext().getLocale();
	        if (locale != null) {
	            return locale.getLocaleCode() + "_";
	        }
	    }
	    return "";
	}
	
	/**
	 * Determines if there is a pricelist prefix that needs to be applied to fields for this particular request.
	 * By default, a pricelist prefix will only apply to fields that have type Price
	 * 
	 * <ul>
	 *     <li>Note: This method should NOT return null. If there is no prefix, it should return the empty string.</li>
	 *     <li>Note: If there is a prefix, it MUST end in an underscore</li>
	 * </ul>
	 * 
	 * @return the global prefix if there is one, "" if there isn't
	 */
	protected String getPricelistPrefix() {
	    return "default_";
	}
	
	/**
	 * Returns the property name for the given field, field type, and prefix
	 * 
	 * @param field
	 * @param searchableFieldType
	 * @param prefix
	 * @return the property name for the field and fieldtype
	 */
	protected String getPropertyNameForFieldSearchable(Field field, FieldType searchableFieldType, String prefix) {
	    return new StringBuilder()
	        .append(prefix)
	        .append(field.getPropertyName()).append("_").append(searchableFieldType.getType())
	        .toString();
	}
	
	/**
	 * Returns the property name for the given field and field type. This will apply the global prefix to the field,
	 * and it will also apply either the locale prefix or the pricelist prefix, depending on whether or not the field
	 * type was set to FieldType.PRICE
	 * 
	 * @param field
	 * @param searchableFieldType
	 * @return the property name for the field and fieldtype
	 */
	protected String getPropertyNameForFieldSearchable(Field field, FieldType searchableFieldType) {
	    String prefix = searchableFieldType.equals(FieldType.PRICE) ? getPricelistPrefix() : getLocalePrefix();
	    return getPropertyNameForFieldSearchable(field, searchableFieldType, prefix);
	}
	
	/**
	 * Returns the property name for the given field, its configured facet field type, and the given prefix
	 * 
	 * @param field
	 * @param prefix
	 * @return the property name for the facet type of this field
	 */
	protected String getPropertyNameForFieldFacet(Field field, String prefix) {
	    if (field.getFacetFieldType() == null) { 
	        return null;
	    }
	    
	    return new StringBuilder()
	        .append(prefix)
	        .append(field.getPropertyName()).append("_").append(field.getFacetFieldType().getType())
	        .toString();
	}
	
	/**
	 * Returns the property name for the given field and its configured facet field type. This will apply the global prefix 
	 * to the field, and it will also apply either the locale prefix or the pricelist prefix, depending on whether or not 
	 * the field type was set to FieldType.PRICE
	 * 
	 * @param field
	 * @return the property name for the facet type of this field
	 */
	protected String getPropertyNameForFieldFacet(Field field) {
	    if (field.getFacetFieldType() == null) {
	        return null;
	    }
	    
	    String prefix = field.getFacetFieldType().equals(FieldType.PRICE) ? getPricelistPrefix() : getLocalePrefix();
	    return getPropertyNameForFieldFacet(field, prefix);
	}
	
	/**
	 * @return the name of the field that keeps track what namespace this document belongs to
	 */
	protected String getNamespaceFieldName() {
	    return "namespace";
	}
	
	/**
	 * @return the id field name, with the global prefix as appropriate
	 */
	protected String getIdFieldName() {
	    return "id";
	}
	
	/**
	 * @return the category field name, with the global prefix as appropriate
	 */
	protected String getCategoryFieldName() {
	    return "category";
	}
	
	/**
	 * @return the explicit category field name, with the global prefix as appropriate
	 */
	protected String getExplicitCategoryFieldName() {
	    return "explicitCategory";
	}
	
	/**
	 * @param prefix
	 * @return the searchable field name, with the global and specific prefix as appropriate
	 */
	protected String getSearchableFieldName(String prefix) {
	    return new StringBuilder()
	        .append(prefix)
	        .append("searchable")
	        .toString();
	}
	
	/**
	 * @return the searchable field name, with the global and locale prefixes as appropriate
	 */
	protected String getSearchableFieldName() {
	    return getSearchableFieldName(getLocalePrefix());
	}
	
	/**
	 * @param category
	 * @return the default sort field name for this category
	 */
	protected String getCategorySortField(Category category) {
	    return new StringBuilder()
	        .append(getCategoryFieldName())
	        .append("_").append(category.getId()).append("_").append("sort_i")
	        .toString();
	}

}
