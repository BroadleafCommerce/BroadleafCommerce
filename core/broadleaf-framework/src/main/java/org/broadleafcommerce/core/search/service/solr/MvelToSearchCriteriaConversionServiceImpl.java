/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.search.dao.IndexFieldDao;
import org.broadleafcommerce.core.search.domain.IndexFieldType;
import org.broadleafcommerce.core.search.domain.IndexFieldTypeImpl;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

/**
 * Convenience methods for converting simple MVEL rules to Solr SearchCriteria
 *
 * @author Chris Kittrell (ckittrell)
 */
@Service("blMvelToSearchCriteriaConversionService")
public class MvelToSearchCriteriaConversionServiceImpl implements MvelToSearchCriteriaConversionService {

    private static final Log LOG = LogFactory.getLog(MvelToSearchCriteriaConversionServiceImpl.class);

    public static final String CATEGORY_FORMAT_REGEX = "^CollectionUtils\\.intersection\\(product\\.\\?allParentCategoryIds,\\[\\\"([0-9]+)(,\\\"[0-9]+\\\")*\\\"\\]\\)\\.size\\(\\)>0$";

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blIndexFieldDao")
    protected IndexFieldDao indexFieldDao;

    @Resource(name = "blLocaleService")
    protected LocaleService localeService;

    @Resource(name = "blSolrHelperService")
    protected SolrHelperService solrHelperService;

    @Override
    public SearchCriteria convert(String mvelRule) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setPageSize(Integer.MAX_VALUE);

        if (isProductRule(mvelRule) || isCategoryTargetingRule(mvelRule)) {
            Collection<String> strings = convertRuleToFilters(mvelRule);
            criteria.setFilterQueries(strings);
        } else {
            throw new UnsupportedOperationException("The selected Add-On Product Group is defined using Rules " +
                    "(Conditional Rules) and selected rule can't be translated to solr criteria and so " +
                    "does not support selection of a Default Product. See Tooltip for supported rule details");
        }

        return criteria;
    }

    protected boolean isProductRule(String rule) {
        return rule.contains("product.");
    }

    protected boolean isCustomFieldIndexed(List<IndexFieldType> indexFieldTypes) {
        return CollectionUtils.isNotEmpty(indexFieldTypes);
    }

    // Expected Category Rule Format - CollectionUtils.intersection(product.?allParentCategoryIds,["2002"]).size()>0
    protected boolean isCategoryTargetingRule(String mvelRule) {
        return mvelRule.matches(CATEGORY_FORMAT_REGEX);
    }

    // Expected Category Rule Format - CollectionUtils.intersection(product.?allParentCategoryIds,["2002",...]).size()>0
    protected Long[] getCategoryIds(String mvelRule) {
        int startIndex = mvelRule.indexOf("[\"") + 2;
        int endIndex = mvelRule.indexOf("\"]");
        String categoryId = mvelRule.substring(startIndex, endIndex);
        //how about multivalue ?
        String[] split = categoryId.split(",");
        Long[] result = null;
        int length = split.length;
        if (length > 0) {
            result = new Long[length];
            for (int i = 0; i < length; i++) {
                result[i] = Long.parseLong(split[i].replace("\"", ""));
            }
        }
        return result;
    }

    // Expected Custom Field Rule Formats
    // Equals - product.?getProductAttributes()["custom_field_name"]=="custom field value"
    // Contains - MvelHelper.toUpperCase(product.?getProductAttributes()["custom_field_name"]).contains(MvelHelper.toUpperCase("custom field value"))
    // Also supports string property contains, not contains, starts with, ends with etc.
    protected Collection<String> convertRuleToFilters(String matchRule) {
        List<String> filters = new ArrayList<>();

        boolean allRulesMustBeTrue = !matchRule.contains("||");
        String[] fragments;
        if (allRulesMustBeTrue) {
            fragments = StringUtils.split(matchRule, "&&");
        } else {
            fragments = StringUtils.split(matchRule, "||");
        }

        for (String fragment : fragments) {
            if (isCategoryTargetingRule(fragment)) {
                Long[] categoryIds = getCategoryIds(fragment);
                if (categoryIds != null) {
                    String explicitCategoryFieldName = solrHelperService.getExplicitCategoryFieldName();
                    StringBuilder categoryFilter = new StringBuilder(explicitCategoryFieldName + ":(\"");
                    for (int i = 0; i < categoryIds.length; i++) {
                        Long catId = solrHelperService.getCategoryId(categoryIds[i]);
                        categoryFilter.append(catId);
                        if (i + 1 < categoryIds.length) {
                            categoryFilter.append(",");
                        }
                    }
                    categoryFilter.append("\")");
                    filters.add(categoryFilter.toString());

                }
            } else {
                String fieldName = getCustomFieldPropertyName(fragment);
                String fieldValue = getCustomFieldValue(fragment);
                boolean exclude = fragment.contains("!=") || fragment.startsWith("!");
                boolean isWildCardSearch = isWildCardSearch(fieldValue);
                fieldName = convertFieldName(fieldName);
                List<IndexFieldType> indexFieldTypes = indexFieldDao.getIndexFieldTypesByAbbreviationOrPropertyName(fieldName);
                boolean isCatalog = false;
                if ("embeddableCatalogTenantDiscriminator.catalogDiscriminator".equals(fieldName)) {
                    fieldName = solrHelperService.getCatalogFieldName();
                    isCatalog = true;
                    indexFieldTypes = new ArrayList<>();
                    indexFieldTypes.add(new IndexFieldTypeImpl());
                }
                if (indexFieldTypes.size() > 0) {
                    Boolean translatable = isCatalog ? Boolean.FALSE : indexFieldTypes.get(0).getIndexField().getField().getTranslatable();
                    List<Locale> allLocales;
                    if (translatable == null || !translatable) {
                        allLocales = new ArrayList<>();
                        LocaleImpl e = new LocaleImpl();
                        e.setLocaleCode("");
                        allLocales.add(e);
                    } else {
                        allLocales = localeService.findAllLocales();
                    }
                    List<String> tmpFilters = new ArrayList<>();
                    String abbreviation = isCatalog ? "" : indexFieldTypes.get(0).getIndexField().getField().getAbbreviation();
                    for (Locale locale : allLocales) {
                        for (IndexFieldType indexFieldType : indexFieldTypes) {
                            String type = isCatalog ? "" : indexFieldType.getFieldType().getType();
                            if (!isWildCardSearch || FieldType.STRING.getType().equals(type) || isCatalog) {
                                String prefix;
                                if (StringUtils.isNotEmpty(locale.getLocaleCode())) {
                                    prefix = locale.getLocaleCode() + "_";
                                } else {
                                    prefix = "";
                                }

                                String indexFieldName = isCatalog ? fieldName : (prefix + abbreviation + "_" + type);

                                // if this is a wildcard search then we do not want to surround the value with quotes
                                String indexFieldValue = fieldValue;
                                if (!isWildCardSearch && !fieldValue.equals("null")) {
                                    if (fieldValue.contains("\",\"")) {
                                        indexFieldValue = "(\"" + fieldValue + "\")";
                                    } else {
                                        indexFieldValue = "\"" + fieldValue + "\"";
                                    }
                                }

                                String filter;
                                if (indexFieldValue.equals("null")) {
                                    // this is for checking if null or non-existent fields
                                    filter = "(*:* AND -" + indexFieldName + ":[* TO *])";
                                } else {
                                    filter = indexFieldName + ":" + indexFieldValue;
                                }

                                if (exclude) {
                                    filter = "NOT " + filter;
                                }

                                tmpFilters.add(filter);
                            }
                        }
                    }

                    if (!exclude) {
                        //any of translation and type fields like en_name_tsy, en_name_s etc can match, so concatinate with OR

                        String s = tmpFilters.get(0);
                        s = "(" + s;
                        tmpFilters.set(0, s);
                        int size = tmpFilters.size() - 1;
                        String s1 = tmpFilters.get(size);
                        s1 = s1 + ")";
                        tmpFilters.set(size, s1);
                        filters.add(StringUtils.join(tmpFilters, " OR "));
                    } else {
                        //if we exclude we don't want to see if at all so "AND" is ok
                        filters.addAll(tmpFilters);
                    }
                }
                else {
                    return Collections.emptyList();
                }
            }
        }
        if (allRulesMustBeTrue) {
            return filters;
        } else {
            return Collections.singletonList(StringUtils.join(filters, " OR "));
        }
    }

    protected String convertFieldName(String fieldName) {
        String result = fieldName;
        result = result.replaceAll("\\?", "");
        if (result.startsWith("product.")) {
            result = result.substring("product.".length());
        }
        if (result.endsWith("()")) {
            result = parseMethod(result);
        }
        return result;
    }

    /**
     * Takes in a fieldName that contains a method (indicated with "()") and converts the method to an attribute.
     * Currently supports getX() methods
     * For example, getType() -> type
     *
     * @param fieldName
     * @return
     */
    protected String parseMethod(String fieldName) {
        String[] segments = fieldName.split("\\.");
        //parse the last segment removing the method prefix (e.g. get)
        String methodSegment = segments[segments.length - 1].replaceFirst("(^get)", "");
        //lowercase first char and remove the ()
        segments[segments.length - 1] = Character.toLowerCase(methodSegment.charAt(0)) + methodSegment.substring(1, methodSegment.length() - 2);
        return String.join(".", segments);
    }

    /**
     * Determines if the given field value string represents a wild card search. In Solr a wild card search either
     * starts or ends with an asterisk.
     *
     * @param fieldValue the String field value
     * @return whether the value is for a wildcard search
     */
    protected boolean isWildCardSearch(String fieldValue) {
        return fieldValue.startsWith("*") || fieldValue.endsWith("*");
    }

    protected String getCustomFieldPropertyName(String mvelRule) {
        if (mvelRule.contains("CollectionUtils")) {
            //assuming left param is field, right is value
            int startIndex = mvelRule.indexOf("(");
            int endIndex = mvelRule.indexOf(",");
            mvelRule = mvelRule.substring(startIndex + 1, endIndex);
        }
        if (mvelRule.indexOf("[\"") > 0) {
            int startIndex = mvelRule.indexOf("[\"") + 2;
            int endIndex = mvelRule.lastIndexOf("\"]");

            return mvelRule.substring(startIndex, endIndex);
        } else {
            if (mvelRule.contains("org.apache.commons.lang3.StringUtils.contains") || mvelRule.contains(".startsWith") || mvelRule.contains(".endsWith")) {
                int startIndex = mvelRule.indexOf("(");
                int endIndex = mvelRule.indexOf(",");
                mvelRule = mvelRule.substring(startIndex + 1, endIndex);
            }
            mvelRule = getRuleOrPropertyFromFunction(mvelRule);
            return mvelRule;
        }
    }

    protected String getRuleOrPropertyFromFunction(String mvelRule) {
        int i = mvelRule.indexOf('(');
        int j = mvelRule.indexOf(')', i);

        //no function or maybe just some function? like xxx.xxx.getType()
        if (i == j || j == i + 1) {
            //expect to have either == or !=
            int exclamationMark = mvelRule.indexOf("!");
            if (exclamationMark > 0) {
                return mvelRule.substring(0, exclamationMark);
            } else {
                int equalsSign = mvelRule.indexOf("=");
                if (equalsSign > 0) {
                    return mvelRule.substring(0, equalsSign);
                }
                //unknown format
            }
        } else {
            //looks like some function ?
            Pattern pattern = Pattern.compile("\\([^!=]+\\)");
            Matcher matcher = pattern.matcher(mvelRule);
            if (matcher.find()) {
                String group = matcher.group();
                return group.substring(1, group.length() - 1);
            }

        }
        return mvelRule;
    }

    protected String getCustomFieldValue(String mvelRule) {
        String customFieldValue = "";
        if (mvelRule.contains(".contains")) {
            int startIndex = mvelRule.lastIndexOf("(\"") + 2;
            int endIndex = mvelRule.lastIndexOf("\"))");

            customFieldValue = mvelRule.substring(startIndex, endIndex);
        } else if (mvelRule.contains("==\"") || mvelRule.contains("!=\"")) {
            int startIndex;
            int endIndex;
            if (mvelRule.contains("toUpperCase")) {
                startIndex = mvelRule.lastIndexOf("(\"") + 2;
                endIndex = mvelRule.lastIndexOf("\")");
            } else {
                startIndex = mvelRule.indexOf("==\"") + 3;
                endIndex = mvelRule.lastIndexOf("\"");
            }

            customFieldValue = mvelRule.substring(startIndex, endIndex);
        } else if (mvelRule.contains("==") || mvelRule.contains("!=")) {
            int startIndex;
            int endIndex = mvelRule.length();
            if (mvelRule.contains("toUpperCase")) {
                startIndex = mvelRule.lastIndexOf("(\"") + 2;
                endIndex = mvelRule.indexOf("\")", startIndex);
            } else {
                int i = mvelRule.indexOf("==");
                if (i > 0) {
                    startIndex = i + 2;
                } else {
                    startIndex = mvelRule.indexOf("!=") + 2;
                }
            }

            customFieldValue = mvelRule.substring(startIndex, endIndex);
        } else if (mvelRule.contains(".startsWith")) {
            int startIndex = mvelRule.lastIndexOf("(\"") + 2;
            int endIndex = mvelRule.lastIndexOf("\"))");

            customFieldValue = mvelRule.substring(startIndex, endIndex).replaceAll(" ", "\\ ") + "*";
        } else if (mvelRule.contains(".endsWith")) {
            int startIndex = mvelRule.lastIndexOf("(\"") + 2;
            int endIndex = mvelRule.lastIndexOf("\"))");

            customFieldValue = "*" + mvelRule.substring(startIndex, endIndex).replaceAll(" ", "\\ ");
        } else if (mvelRule.contains("==null")) {
            // this means we have an "is blank" rule
            customFieldValue = "null";
        } else if (mvelRule.contains("CollectionUtils")) {
            int startIndex = mvelRule.indexOf(",");
            int endIndex = mvelRule.indexOf(")", startIndex);
            customFieldValue = mvelRule.substring(startIndex + 1, endIndex);
        }
        //maybe it is something like [x,x1,x2]? -> (x,x1,x2), or [x1]->x1
        if (customFieldValue.startsWith("[") && customFieldValue.endsWith("]")) {
            customFieldValue = customFieldValue.substring(1, customFieldValue.length() - 1);
            if (customFieldValue.startsWith("\"") && customFieldValue.endsWith("\"")) {
                customFieldValue = customFieldValue.substring(1, customFieldValue.length() - 1);
            }
        }
        return customFieldValue;
    }


}
