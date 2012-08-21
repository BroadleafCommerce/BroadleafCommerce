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
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.time.SystemTime;
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
import org.broadleafcommerce.core.search.service.SearchService;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An implementation of SearchService that uses solr
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class SolrSearchServiceImpl implements SearchService {
    private static final Log LOG = LogFactory.getLog(SolrSearchServiceImpl.class);
    protected static final String GLOBAL_FACET_TAG_FIELD = "a";
    
    protected Map<String, String> propertyFacetFieldTypeMap;
    protected Map<String, String[]> propertySearchFieldTypeMap;
	
	@Resource(name = "blProductDao")
	protected ProductDao productDao;
	
	@Resource(name = "blFieldDao")
	protected FieldDao fieldDao;
	
	@Resource(name = "blSearchFacetDao")
	protected SearchFacetDao searchFacetDao;
	
	protected SolrServer server;
	

	public SolrSearchServiceImpl(String solrHome) throws IOException, ParserConfigurationException, SAXException {
		this(solrHome, new HashMap<String, String>(), new HashMap<String, String[]>());
	}
	
	public SolrSearchServiceImpl(String solrHome, Map<String, String> propertyFacetFieldTypeMap, Map<String, String[]> propertySearchFieldTypeMap) 
			throws IOException, ParserConfigurationException, SAXException {
		System.setProperty("solr.solr.home", solrHome);
		CoreContainer.Initializer initializer = new CoreContainer.Initializer();
		CoreContainer coreContainer = initializer.initialize();
		EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "");
		this.server = server;
		this.propertyFacetFieldTypeMap = propertyFacetFieldTypeMap;
		this.propertySearchFieldTypeMap = propertySearchFieldTypeMap;
	}
	
	public SolrSearchServiceImpl(SolrServer server) {
		this(server, new HashMap<String, String>(), new HashMap<String, String[]>());
	}
	
	public SolrSearchServiceImpl(SolrServer server, Map<String, String> propertyFacetFieldTypeMap, Map<String, String[]> propertySearchFieldTypeMap) {
		this.server = server;
		this.propertyFacetFieldTypeMap = propertyFacetFieldTypeMap;
		this.propertySearchFieldTypeMap = propertySearchFieldTypeMap;
	}
	
	@Override
	public ProductSearchResult findProductsByCategory(Category category, ProductSearchCriteria searchCriteria) throws ServiceException {
		List<SearchFacetDTO> facets = getCategoryFacets(category);
		Map<String, SearchFacetDTO> namedFacetMap = getNamedFacetMap(facets, searchCriteria);
		
		// Set up the solr query
		SolrQuery query = buildSolrQuery(category, namedFacetMap, searchCriteria);

	    // Query solr
	    QueryResponse response;
	    try {
	    	response = server.query(query);
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
	
	@Override
	public ProductSearchResult findProductsByQuery(String query, ProductSearchCriteria searchCriteria) throws ServiceException {
		List<SearchFacetDTO> facets = getSearchFacets();
		Map<String, SearchFacetDTO> namedFacetMap = getNamedFacetMap(facets, searchCriteria);
		
		// Set up the solr query
		SolrQuery solrQuery = buildSolrQuery(query, namedFacetMap, searchCriteria);

	    // Query solr
	    QueryResponse response;
	    try {
	    	response = server.query(solrQuery);
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
	
	protected SolrQuery buildSolrQuery(String query, Map<String, SearchFacetDTO> namedFacetMap, ProductSearchCriteria searchCriteria) {
		query = "*" + query + "*";
	    SolrQuery solrQuery = new SolrQuery()
	    	.setQuery("searchable:" + query)
    		.setRows(searchCriteria.getPageSize())
    		.setStart((searchCriteria.getPage() - 1) * searchCriteria.getPageSize());
	    
	    attachSortClause(solrQuery, searchCriteria, namedFacetMap);
	    attachActiveFacetFilters(solrQuery, namedFacetMap, searchCriteria);
	    attachFacets(solrQuery, namedFacetMap);
	    
	    return solrQuery;
	}
	
	protected SolrQuery buildSolrQuery(Category category, Map<String, SearchFacetDTO> namedFacetMap, ProductSearchCriteria searchCriteria) {
	    SolrQuery solrQuery = new SolrQuery()
	    	.setQuery("category:" + category.getId())
    		.setRows(searchCriteria.getPageSize())
    		.setStart((searchCriteria.getPage() - 1) * searchCriteria.getPageSize());
	    
	    attachSortClause(solrQuery, searchCriteria, namedFacetMap);
	    attachActiveFacetFilters(solrQuery, namedFacetMap, searchCriteria);
	    attachFacets(solrQuery, namedFacetMap);
	    
	    return solrQuery;
	}
	
	protected void attachSortClause(SolrQuery query, ProductSearchCriteria searchCriteria, Map<String, SearchFacetDTO> namedFacetMap) {
		if (StringUtils.isNotBlank(searchCriteria.getSortQuery())) {
			String[] sortFields = searchCriteria.getSortQuery().split(",");
			for (String sortField : sortFields) {
				String field = sortField.split(" ")[0];
				String order = sortField.split(" ")[1];
				
				String solrField = null;
				
				for (Entry<String, SearchFacetDTO> dtoEntry : namedFacetMap.entrySet()) {
					if (dtoEntry.getValue().getFacet().getField().getAbbreviation().equals(field)) {
						solrField = dtoEntry.getKey();
					}
				}
				
				if (solrField != null) {
					query.addSortField(solrField, "asc".equals(order) ? ORDER.asc : ORDER.desc);
				}
				
			}
		}
	}
	
	protected void attachActiveFacetFilters(SolrQuery query, Map<String, SearchFacetDTO> namedFacetMap, ProductSearchCriteria searchCriteria) {
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
						String rangeValue = selectedValues[i].substring(selectedValues[i].indexOf('[') + 1, selectedValues[i].indexOf(']'));
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
	    		String facetFieldName = entry.getKey().substring(entry.getKey().indexOf("}") + 1, entry.getKey().indexOf(':'));
	    		SearchFacetDTO facetDTO = namedFacetMap.get(facetFieldName);
	    		
	    		String minValue = entry.getKey().substring(entry.getKey().indexOf("[") + 1, entry.getKey().indexOf(" TO"));
	    		String maxValue = entry.getKey().substring(entry.getKey().indexOf(" TO ") + 4, entry.getKey().indexOf("]"));
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
	
	public void setPagingAttributes(ProductSearchResult result, QueryResponse response, ProductSearchCriteria searchCriteria) {
	    result.setTotalResults(new Long(response.getResults().getNumFound()).intValue());
	    result.setPage(searchCriteria.getPage());
	    result.setPageSize(searchCriteria.getPageSize());
	}

	protected List<Product> getProducts(QueryResponse response) {
	    final List<Long> productIds = new ArrayList<Long>();
		SolrDocumentList docs = response.getResults();
    	for (SolrDocument doc : docs) {
    		productIds.add((Long) doc.getFieldValue("id"));
    	}
    	
	    List<Product> products = productDao.readProductsByIds(productIds); 
	    
	    Collections.sort(products, new Comparator<Product>() {
			public int compare(Product o1, Product o2) {
				return new Integer(productIds.indexOf(o1.getId())).compareTo(productIds.indexOf(o2.getId()));
			}
	    });
	    
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
	 * This method will be used to map a SearchFacetDTO to a solr index field to use. Typically, this default
	 * implementation will be sufficient. However, there may be cases where you would want to use a different
	 * index depending on other currently active facets.
	 * 
	 * @param facets
	 * @param searchCriteria
	 * @return a map of SearchFacetDTOs keyed by the solr field that matches
	 */
	protected Map<String, SearchFacetDTO> getNamedFacetMap(List<SearchFacetDTO> facets, ProductSearchCriteria searchCriteria) {
		Map<String, SearchFacetDTO> namedFacetMap = new HashMap<String, SearchFacetDTO>();
		for (SearchFacetDTO facet : facets) {
			Field facetField = facet.getFacet().getField();
			namedFacetMap.put(facetField.getPropertyName() + "_" + getFacetFieldType(facetField), facet);
		}
		return namedFacetMap;
	}

	protected String[] getSearchableFieldTypes(Field field) {
		return propertySearchFieldTypeMap.get(field.getPropertyName());
	}
	
	protected String getFacetFieldType(Field field) {
		return propertyFacetFieldTypeMap.get(field.getPropertyName());
	}
	
	@SuppressWarnings("rawtypes")
	protected void addFacetFieldTypeMapping(Product product, Field field) {
		try {
			String propertyName = field.getPropertyName();
			if (propertyName.contains("productAttributes.")) {
				propertyName = convertToMappedProperty(propertyName, "productAttributes", "mappedProductAttributes");
			}
			Class fieldClass = PropertyUtils.getPropertyType(product, propertyName);
			if (fieldClass.equals(Money.class)) {
				propertyFacetFieldTypeMap.put(field.getPropertyName(), "td");
			} else {
				propertyFacetFieldTypeMap.put(field.getPropertyName(), "s");
			}
		} catch (Exception e) {
			LOG.warn("Could not find facet field on PRODUCT." + field.getPropertyName());
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected void addSearchFieldTypeMapping(Product product, Field field) {
		try {
			String propertyName = field.getPropertyName();
			if (propertyName.contains("productAttributes.")) {
				propertyName = convertToMappedProperty(propertyName, "productAttributes", "mappedProductAttributes");
			}
			Class fieldClass = PropertyUtils.getPropertyType(product, propertyName);
			if (fieldClass.equals(Money.class)) {
				propertySearchFieldTypeMap.put(field.getPropertyName(), new String[] {"td"});
			} else {
				propertySearchFieldTypeMap.put(field.getPropertyName(), new String[] {"s", "t"});
			}
		} catch (Exception e) {
			LOG.warn("Could not find facet field on PRODUCT." + field.getPropertyName());
		}
	}

	@Override
	//@Scheduled(fixedRate = 3000000)
	public void rebuildIndex() throws ServiceException, IOException {
		List<Product> products = productDao.readAllActiveProducts(SystemTime.asDate());
		List<Field> fields = fieldDao.readAllProductFields();
		
		propertySearchFieldTypeMap.clear();
		propertyFacetFieldTypeMap.clear();
		
	    Collection<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();
		for (Product product : products) {
			SolrInputDocument document = new SolrInputDocument();
			
			// Add fields that are present on all products
			document.addField("id", product.getId());
			for (Category category : product.getAllParentCategories()) {
				document.addField("category", category.getId());
			}
			
			// Add data-driven user specified searchable fields
			List<String> addedProperties = new ArrayList<String>();
			List<String> copyFieldValue = new ArrayList<String>();
			for (Field field : fields) {
				try {
					String propertyName = field.getPropertyName();
					if (propertyName.contains("productAttributes.")) {
						propertyName = convertToMappedProperty(propertyName, "productAttributes", "mappedProductAttributes");
					}
					Object propertyValue = PropertyUtils.getProperty(product, propertyName);
					
					// If there are no user-defined field types (specified in XML config), we will attempt
					// to best-guess the field type that it should be
					String[] searchableFieldTypes = getSearchableFieldTypes(field);
					if (searchableFieldTypes == null || searchableFieldTypes.length == 0) {
						addSearchFieldTypeMapping(product, field);
						searchableFieldTypes = getSearchableFieldTypes(field);
					}
					
					// Index the searchable fields
					for (String searchableFieldType : searchableFieldTypes) {
						String solrPropertyName = field.getPropertyName() + "_" + searchableFieldType;
						document.addField(solrPropertyName, propertyValue);
						addedProperties.add(solrPropertyName);
						copyFieldValue.add(propertyValue.toString());
					}
					
					// If there are no user-defined field types (specified in XML config), we will attempt
					// to best-guess the field type that it should be
					String facetFieldType = getFacetFieldType(field);
					if (facetFieldType == null) {
						addFacetFieldTypeMapping(product, field);
						facetFieldType = getFacetFieldType(field);
					}
					
					// Index the faceted field type as well
					if (facetFieldType != null) {
						String solrFacetPropertyName = field.getPropertyName() + "_" + getFacetFieldType(field);
						if (!addedProperties.contains(solrFacetPropertyName)) {
							document.addField(solrFacetPropertyName, propertyValue);
						}
					}
				} catch (Exception e) {
					LOG.warn("Could not get value for property[" + field.getQualifiedFieldName() + "] for product id[" + product.getId() + "]");
				}
			}
			document.addField("searchable", StringUtils.join(copyFieldValue, " "));
			documents.add(document);
		}
		
	    try {
	    	server.deleteByQuery("*:*");
	    	server.commit();
	    	
		    server.add(documents);
		    server.commit();
	    } catch (SolrServerException e) {
	    	throw new ServiceException("Could not rebuild index", e);
	    }
	}
}
