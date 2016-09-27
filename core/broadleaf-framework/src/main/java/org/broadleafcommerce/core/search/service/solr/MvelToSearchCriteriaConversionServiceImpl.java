/*
 * #%L
 * BroadleafCommerce Common Libraries
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.search.dao.IndexFieldDao;
import org.broadleafcommerce.core.search.domain.IndexFieldType;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * Convenience methods for converting simple MVEL rules to Solr SearchCriteria
 * 
 * @author Chris Kittrell (ckittrell)
 */
@Service("blMvelToSearchCriteriaConversionService")
public class MvelToSearchCriteriaConversionServiceImpl implements MvelToSearchCriteriaConversionService {

    private static final Log LOG = LogFactory.getLog(MvelToSearchCriteriaConversionServiceImpl.class);

    public static final String CATEGORY_FORMAT_REGEX = "^CollectionUtils\\.intersection\\(product\\.\\?allParentCategoryIds,\\[\\\"([0-9]+)\\\"\\]\\)\\.size\\(\\)>0$";
    public static final String CUSTOM_FIELD_EQUALS_FORMAT_REGEX = "^product\\.\\?getProductAttributes\\(\\)\\[\\\"([a-zA-Z0-9_]+)\\\"\\]\\=\\=\\\"([a-zA-Z0-9\\s]+)\\\"$";
    public static final String CUSTOM_FIELD_CONTAINS_FORMAT_REGEX = "^MvelHelper\\.toUpperCase\\(product\\.\\?getProductAttributes\\(\\)\\[\\\"([a-zA-Z0-9_]+)\\\"\\]\\)\\.contains\\(MvelHelper\\.toUpperCase\\(\\\"([a-zA-Z0-9\\s]+)\\\"\\)\\)$";

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blIndexFieldDao")
    protected IndexFieldDao indexFieldDao;

    @Override
    public SearchCriteria convert(String mvelRule) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setPageSize(Integer.MAX_VALUE);

        if (isCustomFieldTargetingRule(mvelRule)) {
            String customFieldQuery = buildCustomFieldQuery(mvelRule);
            criteria.setQuery(customFieldQuery);
        } else if (isCategoryTargetingRule(mvelRule)) {
            Long categoryId = getCategoryId(mvelRule);
            Category category = catalogService.findCategoryById(categoryId);
            criteria.setCategory(category);
        } else {
            throw new UnsupportedOperationException("The provided MVEL rule format is not currently supported " +
                    "for MVEL to Solr Search Criteria conversion.");
        }

        return criteria;
    }

    protected String buildCustomFieldQuery(String mvelRule) {
        String customFieldPropertyName = getCustomFieldPropertyName(mvelRule);
        String customFieldValue = getCustomFieldValue(mvelRule);

        return buildCustomFieldQuery(customFieldPropertyName, customFieldValue);
    }

    // Expected Custom Field Rule Formats
    // Equals - product.?getProductAttributes()["custom_field_name"]=="custom field value"
    // Contains - MvelHelper.toUpperCase(product.?getProductAttributes()["custom_field_name"]).contains(MvelHelper.toUpperCase("custom field value"))
    protected String getCustomFieldPropertyName(String mvelRule) {
        int startIndex = mvelRule.indexOf("[\"") + 2;
        int endIndex = mvelRule.lastIndexOf("\"]");

        return mvelRule.substring(startIndex, endIndex);
    }

    // Expected Custom Field Rule Formats
    // Equals - product.?getProductAttributes()["custom_field_name"]=="custom field value"
    // Contains - MvelHelper.toUpperCase(product.?getProductAttributes()["custom_field_name"]).contains(MvelHelper.toUpperCase("custom field value"))
    protected String getCustomFieldValue(String mvelRule) {
        String customFieldValue = new String();
        if (isCustomFieldEqualsCheck(mvelRule)) {
            int startIndex = mvelRule.indexOf("==\"") + 3;
            int endIndex = mvelRule.lastIndexOf("\"");

            customFieldValue = mvelRule.substring(startIndex, endIndex);

        } else if (isCustomFieldContainsCheck(mvelRule)) {
            int startIndex = mvelRule.lastIndexOf("(\"") + 2;
            int endIndex = mvelRule.lastIndexOf("\"))");

            customFieldValue = mvelRule.substring(startIndex, endIndex);
        }

        return customFieldValue;
    }

    protected String buildCustomFieldQuery(String customFieldPropertyName, String customFieldValue) {
        String customFieldQuery;

        List<IndexFieldType> indexFieldTypes = indexFieldDao.getIndexFieldTypesByAbbreviationOrPropertyName(customFieldPropertyName);
        if (isCustomFieldIndexed(indexFieldTypes)) {
            customFieldQuery = "*:*&fq=(";
            for (IndexFieldType indexFieldType : indexFieldTypes) {
                String type = indexFieldType.getFieldType().getType();
                String indexFieldName = customFieldPropertyName + "_" + type;
                String indexFieldValue = "\"" + customFieldValue + "\"";

                if (!isFirstItem(customFieldQuery)) {
                    customFieldQuery += " OR ";
                }

                customFieldQuery += indexFieldName + ":" + indexFieldValue;
            }
            customFieldQuery += ")";
        } else {
            customFieldQuery = "";
            LOG.warn("The " + customFieldPropertyName + " Custom Field must be indexed with Solr in order " +
                    "to gather results based on an MVEL rule.");
        }

        return customFieldQuery;
    }

    protected boolean isFirstItem(String customFieldQuery) {
        return customFieldQuery.endsWith("&fq=(");
    }

    protected boolean isCustomFieldIndexed(List<IndexFieldType> indexFieldTypes) {
        return CollectionUtils.isNotEmpty(indexFieldTypes);
    }

    // Expected Custom Field Rule Formats
    // Equals - product.?getProductAttributes()["custom_field_name"]=="custom field value"
    // Contains - MvelHelper.toUpperCase(product.?getProductAttributes()["custom_field_name"]).contains(MvelHelper.toUpperCase("custom field value"))
    protected boolean isCustomFieldTargetingRule(String mvelRule) {
        boolean isEqualsCheck = isCustomFieldEqualsCheck(mvelRule);
        boolean isContainsCheck = isCustomFieldContainsCheck(mvelRule);

        return isEqualsCheck || isContainsCheck;
    }

    protected boolean isCustomFieldEqualsCheck(String mvelRule) {
        return mvelRule.matches(CUSTOM_FIELD_EQUALS_FORMAT_REGEX);
    }

    protected boolean isCustomFieldContainsCheck(String mvelRule) {
        return mvelRule.matches(CUSTOM_FIELD_CONTAINS_FORMAT_REGEX);
    }

    // Expected Category Rule Format - CollectionUtils.intersection(product.?allParentCategoryIds,["2002"]).size()>0
    protected boolean isCategoryTargetingRule(String mvelRule) {
        return mvelRule.matches(CATEGORY_FORMAT_REGEX);
    }

    // Expected Category Rule Format - CollectionUtils.intersection(product.?allParentCategoryIds,["2002"]).size()>0
    protected Long getCategoryId(String mvelRule) {
        int startIndex = mvelRule.indexOf("[\"") + 2;
        int endIndex = mvelRule.indexOf("\"]");
        String categoryId = mvelRule.substring(startIndex, endIndex);
        return categoryId == null ? null : Long.parseLong(categoryId);
    }

}
