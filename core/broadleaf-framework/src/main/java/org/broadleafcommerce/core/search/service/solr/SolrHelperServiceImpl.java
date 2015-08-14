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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.cloud.Aliases;
import org.apache.solr.common.params.CoreAdminParams.CoreAdminAction;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.util.BLCMapUtils;
import org.broadleafcommerce.common.util.TypedClosure;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.RequiredFacet;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchFacetRange;
import org.broadleafcommerce.core.search.domain.SearchFacetResultDTO;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexServiceExtensionManager;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Resource;
import javax.jms.IllegalStateException;

/**
 * Provides utility methods that are used by other Solr service classes
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Service("blSolrHelperService")
public class SolrHelperServiceImpl implements SolrHelperService {

    private static final Log LOG = LogFactory.getLog(SolrHelperServiceImpl.class);

    // The value of these two fields has no special significance, but they must be non-blank
    protected static final String GLOBAL_FACET_TAG_FIELD = "a";
    protected static final String DEFAULT_NAMESPACE = "d";
    protected static final String[] specialCharacters = new String[] { "\\\\", "\\+", "-", "&&", "\\|\\|", "\\!", "\\(", "\\)", "\\{", "\\}", "\\[", "\\]", "\\^", "\"", "~", "\\*", "\\?", ":" };

    protected static final String PREFIX_SEPARATOR = "_";

    protected static Locale defaultLocale;

    @Resource(name = "blLocaleService")
    protected LocaleService localeService;

    @Resource(name = "blSolrSearchServiceExtensionManager")
    protected SolrSearchServiceExtensionManager searchExtensionManager;

    @Resource(name = "blSolrIndexServiceExtensionManager")
    protected SolrIndexServiceExtensionManager indexExtensionManager;
    
    /**
     * This should only ever be called when using the Solr reindex service to do a full reindex. 
     */
    @Override
    public synchronized void swapActiveCores() throws ServiceException {
        if (SolrContext.isSolrCloudMode()) {
            CloudSolrServer primary = (CloudSolrServer) SolrContext.getServer();
            CloudSolrServer reindex = (CloudSolrServer) SolrContext.getReindexServer();
            try {
                primary.connect();
                Aliases aliases = primary.getZkStateReader().getAliases();
                Map<String, String> aliasCollectionMap = aliases.getCollectionAliasMap();
                if (aliasCollectionMap == null || !aliasCollectionMap.containsKey(primary.getDefaultCollection())
                        || !aliasCollectionMap.containsKey(reindex.getDefaultCollection())) {
                    throw new IllegalStateException("Could not determine the PRIMARY or REINDEX "
                            + "collection or collections from the Solr aliases.");
                }

                String primaryCollectionName = aliasCollectionMap.get(primary.getDefaultCollection());
                //Do this just in case primary is aliased to more than one collection
                primaryCollectionName = primaryCollectionName.split(",")[0];

                String reindexCollectionName = aliasCollectionMap.get(reindex.getDefaultCollection());
                //Do this just in case primary is aliased to more than one collection
                reindexCollectionName = reindexCollectionName.split(",")[0];

                //Essentially "swap cores" here by reassigning the aliases
                CollectionAdminRequest.createAlias(primary.getDefaultCollection(), reindexCollectionName, primary);
                CollectionAdminRequest.createAlias(reindex.getDefaultCollection(), primaryCollectionName, primary);
            } catch (Exception e) {
                LOG.error("An exception occured swapping cores.", e);
                throw new ServiceException("Unable to swap SolrCloud collections after a full reindex.", e);
            }
        } else {
            if (SolrContext.isSingleCoreMode()) {
                LOG.debug("In single core mode. There are no cores to swap.");
            } else {
                LOG.debug("Swapping active cores");

                CoreAdminRequest car = new CoreAdminRequest();
                car.setCoreName(SolrContext.PRIMARY);
                car.setOtherCoreName(SolrContext.REINDEX);
                car.setAction(CoreAdminAction.SWAP);

                try {
                    SolrContext.getAdminServer().request(car);
                } catch (Exception e) {
                    LOG.error(e);
                    throw new ServiceException("Unable to swap cores", e);
                }
            }
        }
    }

    @Override
    public String getCurrentNamespace() {
        return DEFAULT_NAMESPACE;
    }

    @Override
    public String getGlobalFacetTagField() {
        return GLOBAL_FACET_TAG_FIELD;
    }

    @Override
    public String getPropertyNameForFieldSearchable(Field field, FieldType searchableFieldType, String prefix) {
        return new StringBuilder()
                .append(prefix)
                .append(field.getAbbreviation()).append("_").append(searchableFieldType.getType())
                .toString();
    }

    @Override
    public String getPropertyNameForFieldFacet(Field field, String prefix) {
        if (field.getFacetFieldType() == null) {
            return null;
        }

        return new StringBuilder()
                .append(prefix)
                .append(field.getAbbreviation()).append("_").append(field.getFacetFieldType().getType())
                .toString();
    }

    @Override
    public List<FieldType> getSearchableFieldTypes(Field field) {
        // We will index all configured searchable field types
        List<FieldType> typesToConsider = new ArrayList<FieldType>();
        if (CollectionUtils.isNotEmpty(field.getSearchableFieldTypes())) {
            typesToConsider.addAll(field.getSearchableFieldTypes());
        }

        // If there were no searchable field types configured, we will use TEXT as a default one
        if (CollectionUtils.isEmpty(typesToConsider)) {
            typesToConsider.add(FieldType.TEXT);
        }

        return typesToConsider;
    }

    @Override
    public String getPropertyNameForFieldSearchable(Field field, FieldType searchableFieldType) {
        List<String> prefixList = new ArrayList<String>();
        searchExtensionManager.getProxy().buildPrefixListForSearchableField(field, searchableFieldType, prefixList);
        String prefix = convertPrefixListToString(prefixList);
        return getPropertyNameForFieldSearchable(field, searchableFieldType, prefix);
    }

    @Override
    public String getPropertyNameForFieldFacet(Field field) {
        FieldType fieldType = field.getFacetFieldType();
        if (fieldType == null) {
            return null;
        }

        List<String> prefixList = new ArrayList<String>();

        searchExtensionManager.getProxy().buildPrefixListForSearchableFacet(field, prefixList);
        String prefix = convertPrefixListToString(prefixList);

        return getPropertyNameForFieldFacet(field, prefix);
    }

    protected String convertPrefixListToString(List<String> prefixList) {
        StringBuilder prefixString = new StringBuilder();
        for (String prefix : prefixList) {
            if (prefix != null && !prefix.isEmpty()) {
                prefixString = prefixString.append(prefix).append(PREFIX_SEPARATOR);
            }
        }
        return prefixString.toString();
    }

    @Override
    public Long getCategoryId(Category category) {
        Long[] returnId = new Long[1];
        ExtensionResultStatusType result = searchExtensionManager.getProxy().getCategoryId(category, returnId);
        if (result.equals(ExtensionResultStatusType.HANDLED)) {
            return returnId[0];
        }
        return category.getId();
    }

    @Override
    public Long getCategoryId(Long category) {
        Long[] returnId = new Long[1];
        ExtensionResultStatusType result = indexExtensionManager.getProxy().getCategoryId(category, returnId);
        if (result.equals(ExtensionResultStatusType.HANDLED)) {
            return returnId[0];
        }
        return category;
    }

    @Override
    public Long getIndexableId(Indexable indexable) {
        Long[] returnId = new Long[1];
        ExtensionResultStatusType result = indexExtensionManager.getProxy().getIndexableId(indexable, returnId);
        if (result.equals(ExtensionResultStatusType.HANDLED)) {
            return returnId[0];
        }
        return indexable.getId();
    }

    @Override
    public String getSolrDocumentId(SolrInputDocument document, Indexable indexable) {
        return UUID.randomUUID().toString().toLowerCase(java.util.Locale.ROOT);
    }

    @Override
    public String getNamespaceFieldName() {
        return "namespace";
    }

    @Override
    public String getIdFieldName() {
        return "id";
    }

    @Override
    public String getIndexableIdFieldName() {
        return "indexableId";
    }

    @Override
    public String getCategoryFieldName() {
        return "category";
    }

    @Override
    public String getExplicitCategoryFieldName() {
        return "explicitCategory";
    }

    @Override
    public String getCatalogFieldName() {
        return "catalog_s";
    }

    @Override
    public String getCatalogOverridesFieldName() {
        return "catalog_overrides";
    }

    @Override
    public String getSandBoxFieldName() {
        return "sandboxId";
    }

    @Override
    public String getSandBoxPriorityFieldName() {
        return "sandboxPriority";
    }

    @Override
    public String getSandBoxChangeTypeFieldName() {
        return "sandboxChangeType_s";
    }

    @Override
    public String getCategorySortFieldName(Category category) {
        Long categoryId = getCategoryId(category);
        return new StringBuilder()
                .append(getCategoryFieldName())
                .append("_").append(categoryId).append("_").append("sort_d")
                .toString();
    }

    @Override
    public String getCategorySortFieldName(Long categoryId) {
        categoryId = getCategoryId(categoryId);
        return new StringBuilder()
                .append(getCategoryFieldName())
                .append("_").append(categoryId).append("_").append("sort_d")
                .toString();
    }

    @Override
    public String getLocalePrefix() {
        if (BroadleafRequestContext.getBroadleafRequestContext() != null) {
            Locale locale = BroadleafRequestContext.getBroadleafRequestContext().getLocale();
            if (locale != null) {
                return locale.getLocaleCode() + "_";
            }
        }
        return getDefaultLocalePrefix();
    }

    @Override
    public String getDefaultLocalePrefix() {
        return getDefaultLocale().getLocaleCode() + "_";
    }

    @Override
    public Locale getDefaultLocale() {
        if (defaultLocale == null) {
            defaultLocale = localeService.findDefaultLocale();
        }

        return defaultLocale;
    }

    @Override
    public Object getPropertyValue(Object object, Field field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return getPropertyValue(object, field.getPropertyName());
    }

    @Override
    public Object getPropertyValue(Object object, String propertyName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String[] components = propertyName.split("\\.");
        return getPropertyValueInternal(object, components, 0);
    }

    @Override
    public void optimizeIndex(SolrServer server) throws ServiceException, IOException {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Optimizing the index...");
            }
            server.optimize();
        } catch (SolrServerException e) {
            throw new ServiceException("Could not optimize index", e);
        }
    }

    @Override
    public String scrubFacetValue(String facetValue) {
        String scrubbedFacetValue = facetValue;

        for (String character : specialCharacters) {
            scrubbedFacetValue = scrubbedFacetValue.replaceAll(character, "\\\\" + character);
        }

        return scrubbedFacetValue;
    }

    @Override
    public String sanitizeQuery(String query) {
        return query.replace("(", "").replace("%28", "")
                .replace(")", "").replace("%29", "")
                .replace(":", "").replace("%3A", "").replace("%3a", "")
                .replace("&quot;", "\""); // Allow quotes in the query for more finely tuned matches
    }

    @Override
    public List<SearchFacetDTO> buildSearchFacetDTOs(List<SearchFacet> searchFacets) {
        List<SearchFacetDTO> facets = new ArrayList<SearchFacetDTO>();
        Map<String, String[]> requestParameters = BroadleafRequestContext.getRequestParameterMap();

        for (SearchFacet facet : searchFacets) {
            if (isFacetAvailable(facet, requestParameters)) {
                SearchFacetDTO dto = new SearchFacetDTO();
                dto.setFacet(facet);
                dto.setShowQuantity(true);
                facets.add(dto);
            }
        }

        return facets;
    }

    @Override
    public boolean isFacetAvailable(SearchFacet facet, Map<String, String[]> params) {
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

    @Override
    public String getSolrRangeString(String fieldName, BigDecimal minValue, BigDecimal maxValue) {
        StringBuilder sb = new StringBuilder();

        sb.append(fieldName).append(":[");
        if (minValue == null) {
            sb.append("*");
        } else {
            sb.append(minValue.toPlainString());
        }

        sb.append(" TO ");

        if (maxValue == null) {
            sb.append("*");
        } else {
            sb.append(maxValue.toPlainString());
        }

        sb.append(']');

        return sb.toString();
    }

    @Override
    public String getSolrRangeFunctionString(BigDecimal minValue, BigDecimal maxValue) {
        StringBuilder sb = new StringBuilder();

        sb.append("frange incl=false l=").append(minValue.toPlainString());
        if (maxValue != null) {
            sb.append(" u=").append(maxValue.toPlainString());
        }

        return sb.toString();
    }

    @Override
    public String getSolrFieldTag(String tagField, String tag, SearchFacetRange range) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(tag)) {
            sb.append("{!").append(tag).append("=").append(tagField);

            if (range != null) {
                sb.append("[").append(range.getMinValue().toPlainString()).append(":");
                if (range.getMaxValue() != null) {
                    sb.append(range.getMaxValue().toPlainString());
                } else {
                    sb.append("*");
                }
                sb.append("]");

                sb.append(" " + getSolrRangeFunctionString(range.getMinValue(), range.getMaxValue()));
            }

            sb.append("}");
        }
        return sb.toString();
    }

    @Override
    public void setFacetResults(Map<String, SearchFacetDTO> namedFacetMap, QueryResponse response) {
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

    @Override
    public void sortFacetResults(Map<String, SearchFacetDTO> namedFacetMap) {
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

    @Override
    public void attachFacets(SolrQuery query, Map<String, SearchFacetDTO> namedFacetMap) {
        query.setFacet(true);
        for (Entry<String, SearchFacetDTO> entry : namedFacetMap.entrySet()) {
            SearchFacetDTO dto = entry.getValue();

            // Clone the list - we don't want to remove these facets from the DB
            List<SearchFacetRange> facetRanges = new ArrayList<SearchFacetRange>(dto.getFacet().getSearchFacetRanges());

            if (searchExtensionManager != null) {
                searchExtensionManager.getProxy().filterSearchFacetRanges(dto, facetRanges);
            }

            if (facetRanges != null && facetRanges.size() > 0) {
                for (SearchFacetRange range : facetRanges) {
                    query.addFacetQuery(getSolrTaggedFieldString(entry.getKey(), "key", range));
                }
            } else {
                query.addFacetField(getSolrTaggedFieldString(entry.getKey(), "ex", null));
            }
        }
    }

    @Override
    public String getSolrTaggedFieldString(String indexField, String tag, SearchFacetRange range) {
        return getSolrFieldTag(indexField, tag, range) + (range == null ? indexField : ("field(" + indexField + ")"));
    }

    @Override
    public List<SolrDocument> getResponseDocuments(QueryResponse response) {
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
    public void attachSortClause(SolrQuery query, SearchCriteria searchCriteria, String defaultSort, List<Field> fields) {
        Map<String, String> solrFieldKeyMap = getSolrFieldKeyMap(searchCriteria, fields);

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
                ORDER order = ORDER.asc;
                String[] sortFieldsSegments = sortField.split(" ");
                if (sortFieldsSegments.length < 2) {
                    StringBuilder msg = new StringBuilder().append("Solr sortquery received was " + sortQuery + ", but no sorting tokens could be extracted.");
                    msg.append("\nDefaulting to ASCending");
                    LOG.warn(msg.toString());
                } else if ("desc".equals(sortFieldsSegments[1])) {
                    order = ORDER.desc;
                }
                if (field != null) {
                    query.addSort(new SortClause(field, order));
                }
            }
        }
    }

    @Override
    public Map<String, String> getSolrFieldKeyMap(SearchCriteria searchCriteria, List<Field> fields) {
        Map<String, String> solrFieldKeyMap = new HashMap<String, String>();
        for (Field field : fields) {
            solrFieldKeyMap.put(field.getAbbreviation(), getPropertyNameForFieldFacet(field));
        }
        return solrFieldKeyMap;
    }

    @Override
    public Map<String, SearchFacetDTO> getNamedFacetMap(List<SearchFacetDTO> facets,
            final SearchCriteria searchCriteria) {
        return BLCMapUtils.keyedMap(facets, new TypedClosure<String, SearchFacetDTO>() {

            @Override
            public String getKey(SearchFacetDTO facet) {
                return getPropertyNameForFieldFacet(facet.getFacet().getField());
            }
        });
    }

    @Override
    public void attachActiveFacetFilters(SolrQuery query, Map<String, SearchFacetDTO> namedFacetMap,
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
                    boolean rangeQuery = false;
                    for (int i = 0; i < selectedValues.length; i++) {
                        if (selectedValues[i].contains("range[")) {
                            rangeQuery = true;
                            String rangeValue = selectedValues[i].substring(selectedValues[i].indexOf('[') + 1,
                                    selectedValues[i].indexOf(']'));
                            String[] rangeValues = StringUtils.split(rangeValue, ':');
                            BigDecimal minValue = new BigDecimal(rangeValues[0]);
                            BigDecimal maxValue = null;
                            if (!rangeValues[1].equals("null")) {
                                maxValue = new BigDecimal(rangeValues[1]);
                            }
                            selectedValues[i] = getSolrRangeString(solrKey, minValue, maxValue);
                        } else {
                            selectedValues[i] = "\"" + scrubFacetValue(selectedValues[i]) + "\"";
                        }
                    }
                    StringBuilder valueString = new StringBuilder();
                    if (rangeQuery) {
                        valueString.append(solrKey).append(":(");
                        valueString.append(StringUtils.join(selectedValues, " OR "));
                        valueString.append(")");
                    } else {
                        valueString.append("{!tag=").append(solrKey).append("}");
                        valueString.append(solrKey).append(":(");
                        valueString.append(StringUtils.join(selectedValues, " OR "));
                        valueString.append(")");
                    }

                    query.addFilterQuery(valueString.toString());
                }
            }
        }
    }

    /*
     * This method iteratively and recursively attempts to return the value or values of the property specified by the currentPosition in the 
     * array of components.  The components argument is an array of strings representing the object graph.
     */
    protected Object getPropertyValueInternal(Object object, String[] components, int currentPosition) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        if (object == null) {
            return null;
        }

        Object propertyObject = PropertyUtils.getProperty(object, components[currentPosition]);

        if (propertyObject != null) {
            if (currentPosition < components.length - 1) {
                if (Collection.class.isAssignableFrom(propertyObject.getClass())) {
                    Collection<?> collection = (Collection<?>) propertyObject;
                    HashSet<Object> newCollection = new HashSet<Object>();
                    for (Object item : collection) {
                        Object result = getPropertyValueInternal(item, components, currentPosition + 1);
                        if (result != null) {
                            copyPropertyToCollection(newCollection, result);
                        }
                    }
                    propertyObject = newCollection;
                } else if (Map.class.isAssignableFrom(propertyObject.getClass())) {
                    Map<?, ?> map = (Map<?, ?>) propertyObject;
                    HashSet<Object> newCollection = new HashSet<Object>();
                    for (Object item : map.values()) {
                        Object result = getPropertyValueInternal(item, components, currentPosition + 1);
                        if (result != null) {
                            copyPropertyToCollection(newCollection, result);
                        }
                    }
                    propertyObject = newCollection;
                } else if (propertyObject.getClass().isArray()) {
                    Object[] array = (Object[]) propertyObject;
                    HashSet<Object> newCollection = new HashSet<Object>();
                    for (Object item : array) {
                        Object result = getPropertyValueInternal(item, components, currentPosition + 1);
                        if (result != null) {
                            copyPropertyToCollection(newCollection, result);
                        }
                    }
                    propertyObject = newCollection;
                } else {
                    propertyObject = getPropertyValueInternal(propertyObject, components, currentPosition + 1);
                }
            }
        }

        return propertyObject;
    }

    /*
     * This adds the value of the object to the collection.  If the object is a Map, this adds the values of the 
     * map to the collection.  If the object is a Collection or an Array, it adds each of the values to the collection. 
     */
    protected void copyPropertyToCollection(Collection<Object> collection, Object o) {
        if (o == null) {
            return;
        }

        if (Collection.class.isAssignableFrom(o.getClass())) {
            collection.addAll((Collection<?>) o);
        } else if (Map.class.isAssignableFrom(o.getClass())) {
            collection.addAll(((Map<?, ?>) o).values());
        } else if (o.getClass().isArray()) {
            Object[] array = (Object[]) o;
            if (array.length > 0) {
                for (Object obj : array) {
                    collection.add(obj);
                }
            }
        } else {
            collection.add(o);
        }
    }

}
