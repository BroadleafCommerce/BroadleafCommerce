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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
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
import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.broadleafcommerce.common.dao.GenericEntityDao;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.util.BLCMapUtils;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.common.util.TypedClosure;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.search.dao.IndexFieldDao;
import org.broadleafcommerce.core.search.dao.SearchFacetDao;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.domain.IndexField;
import org.broadleafcommerce.core.search.domain.IndexFieldType;
import org.broadleafcommerce.core.search.domain.RequiredFacet;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchFacetRange;
import org.broadleafcommerce.core.search.domain.SearchFacetResultDTO;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexServiceExtensionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
    protected static final String[] specialCharacters = new String[] { "\\\\", "\\+", "-", "&&", "\\|\\|", "\\!", "\\(", "\\)", "\\{", "\\}", "\\[", "\\]", "\\^", "\"", "~", "\\*", "\\?", ":" };
    private static final String SOLR_SORTABLE_FIELD_TYPES = "solr.sortable.field.types";
    private static final String DEFAULT_SORTABLE_FIELD_TYPES = "sort,s,p,i,l";

    protected static final String PREFIX_SEPARATOR = "_";

    protected static Locale defaultLocale;

    @Resource(name = "blSystemPropertiesService")
    protected SystemPropertiesService systemPropertiesService;

    @Resource(name = "blLocaleService")
    protected LocaleService localeService;

    @Resource(name = "blSolrSearchServiceExtensionManager")
    protected SolrSearchServiceExtensionManager searchExtensionManager;

    @Resource(name = "blSolrIndexServiceExtensionManager")
    protected SolrIndexServiceExtensionManager indexExtensionManager;

    @Resource(name = "blSearchFacetDao")
    protected SearchFacetDao searchFacetDao;

    @Resource(name = "blIndexFieldDao")
    protected IndexFieldDao indexFieldDao;

    @Value("${solr.index.use.sku}")
    protected boolean useSku;

    @Value(value = "${using.solr.server:true}")
    protected boolean isSolrConfigured;

    @Resource(name = "blGenericEntityDao")
    protected GenericEntityDao genericEntityDao;

    /**
     * This should only ever be called when using the Solr reindex service to do a full reindex. 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    @Override
    public synchronized void swapActiveCores(SolrConfiguration solrConfiguration) throws ServiceException {
        if (!isSolrConfigured) {
            return;
        }
        if (CloudSolrClient.class.isAssignableFrom(solrConfiguration.getServer().getClass()) && CloudSolrClient.class.isAssignableFrom(solrConfiguration.getReindexServer().getClass())) {
            CloudSolrClient primaryCloudClient = (CloudSolrClient) solrConfiguration.getServer();
            CloudSolrClient reindexCloudClient = (CloudSolrClient) solrConfiguration.getReindexServer();
            try {
                primaryCloudClient.connect();
                Aliases aliases = primaryCloudClient.getZkStateReader().getAliases();
                Map<String, String> aliasCollectionMap = aliases.getCollectionAliasMap();
                if (aliasCollectionMap == null || !aliasCollectionMap.containsKey(primaryCloudClient.getDefaultCollection())
                        || !aliasCollectionMap.containsKey(reindexCloudClient.getDefaultCollection())) {
                    throw new IllegalStateException("Could not determine the PRIMARY or REINDEX "
                            + "collection or collections from the Solr aliases.");
                }

                String primaryCollectionName = aliasCollectionMap.get(primaryCloudClient.getDefaultCollection());
                //Do this just in case primary is aliased to more than one collection
                primaryCollectionName = primaryCollectionName.split(",")[0];

                String reindexCollectionName = aliasCollectionMap.get(reindexCloudClient.getDefaultCollection());
                //Do this just in case primary is aliased to more than one collection
                reindexCollectionName = reindexCollectionName.split(",")[0];

                //Essentially "swap cores" here by reassigning the aliases
                new CollectionAdminRequest.CreateAlias().setAliasName(primaryCloudClient.getDefaultCollection())
                        .setAliasedCollections(reindexCollectionName).process(primaryCloudClient);
                new CollectionAdminRequest.CreateAlias().setAliasName(reindexCloudClient.getDefaultCollection())
                        .setAliasedCollections(primaryCollectionName).process(reindexCloudClient);
            } catch (Exception e) {
                LOG.error("An exception occured swapping cores.", e);
                throw new ServiceException("Unable to swap SolrCloud collections after a full reindex.", e);
            }
        } else {
            if (solrConfiguration.getReindexServer() == null || solrConfiguration.getServer() == solrConfiguration.getReindexServer()) {
                LOG.debug("In single core mode. There are no cores to swap.");
            } else {
                LOG.debug("Swapping active cores");

                String primaryCoreName = solrConfiguration.getPrimaryName();
                String reindexCoreName = solrConfiguration.getReindexName();

                if (!StringUtils.isEmpty(primaryCoreName) && !StringUtils.isEmpty(reindexCoreName)) {
                    CoreAdminRequest car = new CoreAdminRequest();
                    car.setCoreName(primaryCoreName);
                    car.setOtherCoreName(reindexCoreName);
                    car.setAction(CoreAdminAction.SWAP);

                    try {
                        solrConfiguration.getAdminServer().request(car);
                    } catch (Exception e) {
                        LOG.error(e);
                        throw new ServiceException("Unable to swap cores", e);
                    }
                } else {
                    LOG.error("Could not determine core names for the Solr Clients provided");
                    throw new ServiceException("Unable to swap cores");
                }
            }
        }
    }

    @Override
    public String getGlobalFacetTagField() {
        return GLOBAL_FACET_TAG_FIELD;
    }

    @Override
    public String getPropertyNameForIndexField(IndexField field, FieldType fieldType, String prefix) {
        ExtensionResultHolder<String> erh = new ExtensionResultHolder<>();
        ExtensionResultStatusType result = searchExtensionManager.getProxy().getPropertyNameForIndexField(field, fieldType, prefix, erh);

        if (!ExtensionResultStatusType.NOT_HANDLED.equals(result) && erh.getResult() != null) {
            return erh.getResult();
        }

        String fieldName = field.getField().getAbbreviation();
        if (StringUtils.isEmpty(fieldName)) {
            fieldName = field.getField().getPropertyName();
        }

        return new StringBuilder()
                .append(prefix)
                .append(fieldName).append("_").append(fieldType.getType())
                .toString();
    }

    @Override
    public String getPropertyNameForIndexField(IndexField field, FieldType searchableFieldType) {
        List<String> prefixList = new ArrayList<String>();
        searchExtensionManager.getProxy().buildPrefixListForIndexField(field, searchableFieldType, prefixList);
        String prefix = convertPrefixListToString(prefixList);
        return getPropertyNameForIndexField(field, searchableFieldType, prefix);
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
        //TODO (qa 607) Need to review the performance of retrieval of the Category instance here every time (using the id version breaks for indexing derived catalogs)
        ExtensionResultStatusType result = searchExtensionManager.getProxy().getCategoryId(genericEntityDao.readGenericEntity(CategoryImpl.class, category), returnId);
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
    public String getPrimaryDocumentType() {
        return (useSku) ? FieldEntity.SKU.getType() : FieldEntity.PRODUCT.getType();
    }

    @Override
    public Long getCurrentProductId(Indexable indexable) {
        if (Sku.class.isAssignableFrom(indexable.getClass())) {
            return ((Sku) indexable).getProduct().getId();
        }

        return indexable.getId();
    }

    @Override
    public Product getProductForIndexable(Indexable indexable) {
        if (Sku.class.isAssignableFrom(indexable.getClass())) {
            return ((Sku) indexable).getProduct();
        }

        return (Product) indexable;
    }

    @Override
    public String getTypeFieldName() {
        return org.apache.commons.lang3.StringUtils.join("type_", FieldType.STRING.getType());
    }

    @Override
    public String getDocumentType(Indexable indexable) {
        return indexable.getFieldEntityType().getType();
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
        if (useSku) {
            return "skuId";
        } else {
            return "productId";
        }
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
                .append("_").append(categoryId).append("_").append(FieldType.SORT.getType())
                .toString();
    }

    @Override
    public String getCategorySortFieldName(Long categoryId) {
        categoryId = getCategoryId(categoryId);
        return new StringBuilder()
                .append(getCategoryFieldName())
                .append("_").append(categoryId).append("_").append(FieldType.SORT.getType())
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
    public void optimizeIndex(SolrClient server) throws ServiceException, IOException {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Optimizing the index...");
            }
            if (isSolrConfigured) {
                server.optimize();
            }
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

        searchExtensionManager.getProxy().setFacetResults(namedFacetMap, response);
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
    @Deprecated
    public void attachFacets(SolrQuery query, Map<String, SearchFacetDTO> namedFacetMap) {
        attachFacets(query, namedFacetMap, null);
    }

    @Override
    public void attachFacets(SolrQuery query, Map<String, SearchFacetDTO> namedFacetMap, SearchCriteria searchCriteria) {
        query.setFacet(true);
        for (Entry<String, SearchFacetDTO> entry : namedFacetMap.entrySet()) {
            SearchFacetDTO dto = entry.getValue();

            ExtensionResultStatusType status = searchExtensionManager.getProxy().attachFacet(query, entry.getKey(), dto, searchCriteria);

            if (ExtensionResultStatusType.NOT_HANDLED.equals(status)) {
                List<SearchFacetRange> facetRanges = searchFacetDao.readSearchFacetRangesForSearchFacet(dto.getFacet());

                if (searchExtensionManager != null) {
                    searchExtensionManager.getProxy().filterSearchFacetRanges(dto, facetRanges);
                }

                if (CollectionUtils.isNotEmpty(facetRanges)) {
                    for (SearchFacetRange range : facetRanges) {
                        query.addFacetQuery(getSolrTaggedFieldString(entry.getKey(), "key", range));
                    }
                } else {
                    query.addFacetField(getSolrTaggedFieldString(entry.getKey(), "ex", null));
                }
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

    protected List<String> getSortableFieldTypes() {
        List<String> strings;
        try {
            strings = Arrays.asList(systemPropertiesService.resolveSystemProperty(SOLR_SORTABLE_FIELD_TYPES).
                    split(","));
        } catch (Exception ex) {
            LOG.error(String.format("Error reading property %s. Using default values: %s",
                    SOLR_SORTABLE_FIELD_TYPES, DEFAULT_SORTABLE_FIELD_TYPES), ex);
            strings = Arrays.asList(DEFAULT_SORTABLE_FIELD_TYPES.split(","));
        }
        return strings;
    }

    @Override
    public void attachSortClause(SolrQuery query, SearchCriteria searchCriteria, String defaultSort) {
        String sortQuery = searchCriteria.getSortQuery();
        if (StringUtils.isBlank(sortQuery)) {
            sortQuery = defaultSort;
        }

        if (StringUtils.isNotBlank(sortQuery)) {
            String[] sortFields = sortQuery.split(",");
            List<String> sortableFieldTypes = getSortableFieldTypes();

            for (String sortField : sortFields) {
                String[] sortFieldsSegments = sortField.split(" ");
                String requestedSortFieldName = sortFieldsSegments[0];
                ORDER order = getSortOrder(sortFieldsSegments, sortQuery);

                ExtensionResultStatusType result = searchExtensionManager.getProxy().attachSortField(query, requestedSortFieldName, order);

                if (!ExtensionResultStatusType.NOT_HANDLED.equals(result)) {
                    // if an index field was not found, or the extension handler handled attaching the sort field, move to the next field
                    continue;
                }
                
                List<IndexFieldType> fieldTypes = indexFieldDao.getIndexFieldTypesByAbbreviation(requestedSortFieldName);
                
                // Used to determine if, by looping through the index field types managed in the database, we actually
                // attach the sort field that is being requested. If we do, then we shouldn't manually add the requested
                // sort field ourselves but if not, we should
                boolean requestedSortFieldAdded = false;

                // and some not  give a preference to NOT tokenized fields, and remove tokenized.
                // In case you have tokenized only just add them all
                List<SortClause> sortClauses = new ArrayList<>();
                boolean foundNotTokenizedField = false;

                // Loop through all of the field types for the given sort field and add each generated field name
                // as a sort. Generated field names are comprised of both the field abbreviation and their type, and each
                // field could have indexed multiple field types. Rather than try to guess which field type to sort by
                // this sorts by them all
                for (IndexFieldType fieldType : fieldTypes) {
                    String field = getPropertyNameForIndexField(fieldType.getIndexField(), fieldType.getFieldType());
                    
                    // Verify that the field that is being added as a sort is a match for the field that is requesting
                    // to be sorted by. Since field abbreviations are what are added to the index, this is what should
                    // be checked
                    if (fieldType.getIndexField().getField().getAbbreviation().equals(requestedSortFieldName)) {
                        requestedSortFieldAdded = true;
                    }
                    
                    SortClause sortClause = new SortClause(field, order);

                    if (sortableFieldTypes.contains(fieldType.getFieldType().getType())) {
                        if (!sortClauses.isEmpty() && !foundNotTokenizedField) {
                            sortClauses.clear();
                        }
                        sortClauses.add(sortClause);
                        foundNotTokenizedField = true;
                    } else if (!foundNotTokenizedField) {
                        sortClauses.add(sortClause);
                    }

                }
                if (!foundNotTokenizedField) {
                    LOG.warn(String.format("Sorting on a tokenized field, this could have adverse effects on the ordering of results. " +
                                    "Add a field type for this field from the following list to ensure proper result ordering: [%s]",
                            StringUtils.join(sortableFieldTypes, ", ")));
                }
                if (!sortClauses.isEmpty()) {
                    for (SortClause sortClause : sortClauses) {
                        query.addSort(sortClause);
                    }
                }
                
                // At the end here, it's possible that the field that was passed in to sort by was not managed in the
                // database in the list of index fields and their types. If that's the case, go ahead and add it as a sort
                // field anyway since we're trusting that the field was actually added to the index by some programmatic means
                if (!requestedSortFieldAdded) {
                    query.addSort(new SortClause(requestedSortFieldName, order));
                }
            }
        }
    }
    
    protected ORDER getSortOrder(String[] sortFieldsSegments, String sortQuery) {
        ORDER order = ORDER.asc;
        if (sortFieldsSegments.length < 2) {
            StringBuilder msg = new StringBuilder().append("Solr sortquery received was " + StringUtil.sanitize(sortQuery) 
                    + ", but no sorting tokens could be extracted.");
            msg.append("\nDefaulting to ASCending");
            LOG.warn(msg.toString());
        } else if ("desc".equals(sortFieldsSegments[1])) {
            order = ORDER.desc;
        }
        return order;
    }

    @Override
    public Map<String, String> getSolrFieldKeyMap(SearchCriteria searchCriteria, List<IndexField> fields) {
        Map<String, String> solrFieldKeyMap = new HashMap<String, String>();
        for (IndexField field : fields) {
            for (IndexFieldType type : field.getFieldTypes()) {
                solrFieldKeyMap.put(field.getField().getAbbreviation(), getPropertyNameForIndexField(field, type.getFieldType()));
            }
        }
        return solrFieldKeyMap;
    }

    @Override
    public Map<String, SearchFacetDTO> getNamedFacetMap(List<SearchFacetDTO> facets,
            final SearchCriteria searchCriteria) {
        return BLCMapUtils.keyedMap(facets, new TypedClosure<String, SearchFacetDTO>() {

            @Override
            public String getKey(SearchFacetDTO facet) {
                return getPropertyNameForIndexField(facet.getFacet().getFieldType().getIndexField(),
                        FieldType.getInstance(facet.getFacet().getFacetFieldType()));
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

                    List<String> valueStrings = new ArrayList<>();
                    ExtensionResultStatusType status = searchExtensionManager.getProxy().buildActiveFacetFilter(namedFacetMap.get(solrKey).getFacet(), selectedValues, valueStrings);

                    if (ExtensionResultStatusType.NOT_HANDLED.equals(status)) {
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
                        valueStrings.add(valueString.toString());
                    }

                    query.addFilterQuery(valueStrings.toArray(new String[valueStrings.size()]));
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

        boolean isPropertyReadable = PropertyUtils.isReadable(object, components[currentPosition]);
        if (!isPropertyReadable) {
            LOG.debug(String.format("Could not find %s on %s, assuming this exists elsewhere in the class hierarchy",
                    StringUtil.sanitize(components[currentPosition]), object.getClass().getName()));
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

    @Override
    public List<IndexField> getSearchableIndexFields() {
        List<IndexField> fields = new ArrayList<>();

        ExtensionResultStatusType status = searchExtensionManager.getProxy().getSearchableIndexFields(fields);

        if (ExtensionResultStatusType.NOT_HANDLED.equals(status)) {
            if (useSku) {
                fields = indexFieldDao.readSearchableFieldsByEntityType(FieldEntity.SKU);
            } else {
                fields = indexFieldDao.readSearchableFieldsByEntityType(FieldEntity.PRODUCT);
            }
        }

        return fields;
    }

    @Override
    public List<Long> getCategoryFilterIds(Category category, SearchCriteria searchCriteria) {
        List<Long> categoryIds = new ArrayList<>();

        categoryIds.add(getCategoryId(category));

        searchExtensionManager.getProxy().addAdditionalCategoryIds(category, searchCriteria, categoryIds);

        return categoryIds;
    }

}
