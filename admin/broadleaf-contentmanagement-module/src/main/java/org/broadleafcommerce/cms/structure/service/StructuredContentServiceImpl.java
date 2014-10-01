/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.structure.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.field.domain.FieldDefinition;
import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.structure.dao.StructuredContentDao;
import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.domain.StructuredContentField;
import org.broadleafcommerce.cms.structure.domain.StructuredContentItemCriteria;
import org.broadleafcommerce.cms.structure.domain.StructuredContentRule;
import org.broadleafcommerce.cms.structure.domain.StructuredContentType;
import org.broadleafcommerce.common.cache.CacheStatType;
import org.broadleafcommerce.common.cache.StatisticsService;
import org.broadleafcommerce.common.dao.GenericEntityDao;
import org.broadleafcommerce.common.extensibility.jpa.SiteDiscriminator;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.file.service.StaticAssetPathService;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.locale.util.LocaleUtil;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.rule.RuleProcessor;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.structure.dto.ItemCriteriaDTO;
import org.broadleafcommerce.common.structure.dto.StructuredContentDTO;
import org.broadleafcommerce.common.util.FormatUtil;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

/**
 * @author bpolster
 */
@Service("blStructuredContentService")
public class StructuredContentServiceImpl implements StructuredContentService {

    protected static final Log LOG = LogFactory.getLog(StructuredContentServiceImpl.class);

    protected static String AND = " && ";
    protected static final String FOREIGN_LOOKUP = "BLC_FOREIGN_LOOKUP";

    @Resource(name="blStructuredContentDao")
    protected StructuredContentDao structuredContentDao;
    
    @Resource(name="blStaticAssetService")
    protected StaticAssetService staticAssetService;

    @Resource(name="blStaticAssetPathService")
    protected StaticAssetPathService staticAssetPathService;

    @Resource(name="blLocaleService")
    protected LocaleService localeService;

    @Resource(name="blContentRuleProcessors")
    protected List<RuleProcessor<StructuredContentDTO>> contentRuleProcessors;
    
    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;
    
    @Resource(name = "blStructuredContentServiceExtensionManager")
    protected StructuredContentServiceExtensionManager extensionManager;

    @Resource(name="blStatisticsService")
    protected StatisticsService statisticsService;

    @Resource(name = "blGenericEntityDao")
    protected GenericEntityDao genericDao;

    protected Cache structuredContentCache;

    @Override
    public StructuredContent findStructuredContentById(Long contentId) {
        return structuredContentDao.findStructuredContentById(contentId);
    }

    @Override
    public StructuredContentType findStructuredContentTypeById(Long id) {
        return structuredContentDao.findStructuredContentTypeById(id);
    }

    @Override
    public StructuredContentType findStructuredContentTypeByName(String name) {
        return structuredContentDao.findStructuredContentTypeByName(name);
    }

    @Override
    public List<StructuredContentType> retrieveAllStructuredContentTypes() {
        return structuredContentDao.retrieveAllStructuredContentTypes();
    }

    @Override
    public List<StructuredContent> findContentItems(Criteria c) {
        return c.list();
    }
    
    @Override
    public List<StructuredContent> findAllContentItems() {
        return structuredContentDao.findAllContentItems();
    }

    @Override
    public Long countContentItems(Criteria c) {
        c.setProjection(Projections.rowCount());
        return (Long) c.uniqueResult();
    }
    
    /**
     * Saves the given <b>type</b> and returns the merged instance
     */
    @Override
    public StructuredContentType saveStructuredContentType(StructuredContentType type) {
        return structuredContentDao.saveStructuredContentType(type);
    }

    /**
     * Converts a list of structured content items to a list of structured content DTOs.<br>
     * Internally calls buildStructuredContentDTO(...).
     *
     * @param structuredContentList
     * @param secure
     * @return
     * @see {@link #buildStructuredContentDTO(StructuredContent, boolean)}
     */
    @Override
    public List<StructuredContentDTO> buildStructuredContentDTOList(List<StructuredContent> structuredContentList, boolean secure) {
        List<StructuredContentDTO> dtoList = new ArrayList<StructuredContentDTO>();
        if (structuredContentList != null) {
            for(StructuredContent sc : structuredContentList) {
                dtoList.add(buildStructuredContentDTO(sc, secure));
            }
        }
        return dtoList;
    }

    @Override
    public List<StructuredContentDTO> evaluateAndPriortizeContent(List<StructuredContentDTO> structuredContentList, int count, Map<String, Object> ruleDTOs) {
        // some optimization for single item lists which don't require prioritization
        if (structuredContentList.size() == 1) {
            if (processContentRules(structuredContentList.get(0), ruleDTOs)) {
                return structuredContentList;
            } else {
                return new ArrayList<StructuredContentDTO>();
            }
        }

        ExtensionResultHolder resultHolder = new ExtensionResultHolder();
        extensionManager.getProxy().modifyStructuredContentDtoList(structuredContentList, resultHolder);
        if (resultHolder.getResult() != null) {
            structuredContentList = (List<StructuredContentDTO>) resultHolder.getResult();
        }

        Iterator<StructuredContentDTO> structuredContentIterator = structuredContentList.iterator();
        List<StructuredContentDTO> returnList = new ArrayList<StructuredContentDTO>();
        List<StructuredContentDTO> tmpList = new ArrayList<StructuredContentDTO>();
        Integer lastPriority = Integer.MIN_VALUE;
        while (structuredContentIterator.hasNext()) {
            StructuredContentDTO sc = structuredContentIterator.next();
            if (! lastPriority.equals(sc.getPriority())) {
                // If we've moved to another priority, then shuffle all of the items
                // with the previous priority and add them to the return list.
                if (tmpList.size() > 1) {
                    Collections.shuffle(tmpList);
                }
                returnList.addAll(tmpList);

                tmpList.clear();

                // If we've added enough items to satisfy the count, then return the
                // list.
                if (returnList.size() == count) {
                    return returnList;
                } else if (returnList.size() > count) {
                    return returnList.subList(0, count);
                } else {
                    if (processContentRules(sc, ruleDTOs)) {
                        tmpList.add(sc);
                    }
                }
            } else {
                if (processContentRules(sc, ruleDTOs)) {
                    tmpList.add(sc);
                }
            }
            lastPriority = sc.getPriority();
        }

        if (tmpList.size() > 1) {
            Collections.shuffle(tmpList);
        }

        returnList.addAll(tmpList);


        if (returnList.size() > count) {
            return returnList.subList(0, count);
        }
        return returnList;
    }

    @Override
    public List<StructuredContentDTO> hydrateForeignLookups(List<StructuredContentDTO> dtos) {
        for (StructuredContentDTO dto : dtos) {
            for (Entry<String, Object> entry : dto.getValues().entrySet()) {
                if (entry.getValue() instanceof String && ((String) entry.getValue()).startsWith(FOREIGN_LOOKUP)) {
                    String clazz = ((String) entry.getValue()).split("\\|")[1];
                    String id = ((String) entry.getValue()).split("\\|")[2];
                    Object newValue = genericDao.readGenericEntity(genericDao.getImplClass(clazz), id);
                    entry.setValue(newValue);
                }
            }
        }

        return dtos;
    }

    @Override
    public List<StructuredContentDTO> lookupStructuredContentItemsByType(StructuredContentType contentType, Locale locale,
                                                             Integer count, Map<String, Object> ruleDTOs, boolean secure) {
        List<StructuredContentDTO> contentDTOList = null;
        Locale languageOnlyLocale = findLanguageOnlyLocale(locale);
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        Long site = context.getSite() == null?null:context.getSite().getId();
        String cacheKey = buildTypeKey(context.getSandBox(), site, languageOnlyLocale, contentType.getName());
        cacheKey = cacheKey+"-"+secure;
        if (context.isProductionSandBox()) {
            contentDTOList = getStructuredContentListFromCache(cacheKey);
        }
        if (contentDTOList == null) {
            List<StructuredContent> contentList = structuredContentDao.findActiveStructuredContentByType(contentType,
                    locale, languageOnlyLocale);
            contentDTOList = buildStructuredContentDTOList(contentList, secure);
            if (context.isProductionSandBox()) {
                addStructuredContentListToCache(cacheKey, contentDTOList);
            }
        }

        return hydrateForeignLookups(evaluateAndPriortizeContent(contentDTOList, count, ruleDTOs));
    }

    @Override
    public List<StructuredContentDTO> lookupStructuredContentItemsByName(StructuredContentType contentType,
                                                            String contentName, org.broadleafcommerce.common.locale.domain.Locale locale,
                                                            Integer count, Map<String, Object> ruleDTOs, boolean secure) {
        List<StructuredContentDTO> contentDTOList = null;
        Locale languageOnlyLocale = findLanguageOnlyLocale(locale);
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        Long site = context.getSite() == null?null:context.getSite().getId();
        String cacheKey = buildNameKey(context.getSandBox(), site, languageOnlyLocale, contentType.getName(), contentName);
        cacheKey = cacheKey+"-"+secure;
        if (context.isProductionSandBox()) {
            contentDTOList = getStructuredContentListFromCache(cacheKey);
        }
        if (contentDTOList == null) {
            List<StructuredContent> productionContentList = structuredContentDao.findActiveStructuredContentByNameAndType(
                    contentType, contentName, locale, languageOnlyLocale);
            contentDTOList = buildStructuredContentDTOList(productionContentList, secure);
            if (context.isProductionSandBox()) {
                addStructuredContentListToCache(cacheKey, contentDTOList);
            }
        }

        return hydrateForeignLookups(evaluateAndPriortizeContent(contentDTOList, count, ruleDTOs));
    }
    
    @Override
    public List<StructuredContentDTO> convertToDtos(List<StructuredContent> scs, boolean isSecure) {
        List<StructuredContentDTO> contentDTOList = new ArrayList<StructuredContentDTO>();

        for (StructuredContent sc : scs) {
            String cacheKey = "SC|" + sc.getId();
            StructuredContentDTO dto = null;
            BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();

            if (context.isProductionSandBox()) {
                dto = getSingleStructuredContentFromCache(cacheKey);
            }

            if (dto == null) {
                dto = buildStructuredContentDTO(sc, isSecure);
                if (context.isProductionSandBox()) {
                    addSingleStructuredContentToCache(cacheKey, dto);
                }
            }
            
            contentDTOList.add(dto);
        }

        return hydrateForeignLookups(contentDTOList);
    }

    @Override
    public List<StructuredContentDTO> lookupStructuredContentItemsByName(String contentName,
                                                             org.broadleafcommerce.common.locale.domain.Locale locale,
                                                             Integer count, Map<String, Object> ruleDTOs, boolean secure) {
        List<StructuredContentDTO> contentDTOList = null;
        Locale languageOnlyLocale = findLanguageOnlyLocale(locale);
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        Long site = context.getSite() == null?null:context.getSite().getId();
        String cacheKey = buildNameKey(context.getSandBox(), site, languageOnlyLocale, "any", contentName);
        cacheKey = cacheKey+"-"+secure;
        if (context.isProductionSandBox()) {
            contentDTOList = getStructuredContentListFromCache(cacheKey);
        }
        if (contentDTOList == null) {
            List<StructuredContent> productionContentList = structuredContentDao.findActiveStructuredContentByName(contentName, locale, languageOnlyLocale);
            contentDTOList = buildStructuredContentDTOList(productionContentList, secure);
            if (context.isProductionSandBox()) {
                addStructuredContentListToCache(cacheKey, contentDTOList);
            }
        }

        return hydrateForeignLookups(evaluateAndPriortizeContent(contentDTOList, count, ruleDTOs));
    }

    public List<RuleProcessor<StructuredContentDTO>> getContentRuleProcessors() {
        return contentRuleProcessors;
    }

    public void setContentRuleProcessors(List<RuleProcessor<StructuredContentDTO>> contentRuleProcessors) {
        this.contentRuleProcessors = contentRuleProcessors;
    }

    /**
     * Call to evict an item from the cache.
     * @param sc
     */
    @Override
    public void removeStructuredContentFromCache(SandBox sandBox, StructuredContent sc) {
        // Remove secure and non-secure instances of the page.
        // Typically the page will be in one or the other if at all.
        removeItemFromCache(buildNameKey(sandBox, sc), buildTypeKey(sandBox, sc));
    }

    @Override
    public Locale findLanguageOnlyLocale(Locale locale) {
        if (locale != null ) {
            Locale languageOnlyLocale = localeService.findLocaleByCode(LocaleUtil.findLanguageCode(locale));
            if (languageOnlyLocale != null) {
                return languageOnlyLocale;
            }
        }
        return locale;
    }

    @Override
    public Cache getStructuredContentCache() {
        if (structuredContentCache == null) {
            structuredContentCache = CacheManager.getInstance().getCache("cmsStructuredContentCache");
        }
        return structuredContentCache;
    }

    /**
     * Call to evict both secure and non-secure SC items matching
     * the passed in key.
     *
     * @param nameKey
     */
    @Override
    public void removeItemFromCache(String nameKey, String typeKey) {
        // Remove secure and non-secure instances of the structured content.
        // Typically the structured content will be in one or the other if at all.
        if (!StringUtils.isEmpty(nameKey)) {
            getStructuredContentCache().remove(nameKey+"-"+true);
            getStructuredContentCache().remove(nameKey+"-"+false);
        }

        if (!StringUtils.isEmpty(typeKey)) {
            getStructuredContentCache().remove(typeKey+"-"+true);
            getStructuredContentCache().remove(typeKey+"-"+false);
        }
    }

    protected String buildRuleExpression(StructuredContent sc) {
       StringBuffer ruleExpression = null;
        Map<String, StructuredContentRule> ruleMap = sc.getStructuredContentMatchRules();
       if (ruleMap != null) {
           for (String ruleKey : ruleMap.keySet()) {
                if (ruleMap.get(ruleKey).getMatchRule() == null) {
                    continue;
                }
               if (ruleExpression == null) {
                    ruleExpression = new StringBuffer(ruleMap.get(ruleKey).getMatchRule());
               } else {
                   ruleExpression.append(AND);
                    ruleExpression.append(ruleMap.get(ruleKey).getMatchRule());
               }
           }
       }
       if (ruleExpression != null) {
           return ruleExpression.toString();
       } else {
           return null;
       }
    }

    protected List<ItemCriteriaDTO> buildItemCriteriaDTOList(StructuredContent sc) {
        List<ItemCriteriaDTO> itemCriteriaDTOList = new ArrayList<ItemCriteriaDTO>();
        for(StructuredContentItemCriteria criteria : sc.getQualifyingItemCriteria()) {
            ItemCriteriaDTO criteriaDTO = entityConfiguration.createEntityInstance(ItemCriteriaDTO.class.getName(), ItemCriteriaDTO.class);
            criteriaDTO.setMatchRule(criteria.getMatchRule());
            criteriaDTO.setQty(criteria.getQuantity());
            itemCriteriaDTOList.add(criteriaDTO);
        }
        return itemCriteriaDTOList;
    }

    /**
     * Parses the given {@link StructuredContent} into its {@link StructuredContentDTO} representation. This will also
     * format the values from {@link StructuredContentDTO#getValues()} into their actual data types. For instance, if the
     * given {@link StructuredContent} has a DATE field, then this method will ensure that the resulting object in the values
     * map of the DTO is a {@link Date} rather than just a String representing a date.
     *
     * Current support of parsing field types is:
     *    DATE - {@link Date}
     *    BOOLEAN - {@link Boolean}
     *    DECIMAL - {@link BigDecimal}
     *    INTEGER - {@link Integer}
     *    MONEY - {@link Money}
     *
     * All other fields are treated as strings. This will also fix URL strings that have the CMS prefix (like images) by
     * prepending the standard CMS prefix with the particular environment prefix
     *
     * @param sc
     * @param scDTO
     * @param secure
     * @see {@link StaticAssetService#getStaticAssetEnvironmentUrlPrefix()}
     */
    protected void buildFieldValues(StructuredContent sc, StructuredContentDTO scDTO, boolean secure) {

        String cmsPrefix = staticAssetPathService.getStaticAssetUrlPrefix();

        scDTO.getValues().put("id", sc.getId());

        for (String fieldKey : sc.getStructuredContentFieldXrefs().keySet()) {
            StructuredContentField scf = sc.getStructuredContentFieldXrefs().get(fieldKey).getStructuredContentField();
            String originalValue = scf.getValue();
            if (StringUtils.isNotBlank(originalValue) && StringUtils.isNotBlank(cmsPrefix) && originalValue.contains(cmsPrefix)) {
                //This may either be an ASSET_LOOKUP image path or an HTML block (with multiple <img>) or a plain STRING that contains the cmsPrefix.
                //If there is an environment prefix configured (e.g. a CDN), then we must replace the cmsPrefix with this one.
                String fldValue = staticAssetPathService.convertAllAssetPathsInContent(originalValue, secure);
                scDTO.getValues().put(fieldKey, fldValue);
            } else {
                FieldDefinition definition = null;
                Iterator<FieldGroup> groupIterator = sc.getStructuredContentType().getStructuredContentFieldTemplate().getFieldGroups().iterator();
                while (groupIterator.hasNext() && definition == null) {
                    FieldGroup group = groupIterator.next();
                    for (FieldDefinition def : group.getFieldDefinitions()) {
                        if (def.getName().equals(fieldKey)) {
                            definition = def;
                            break;
                        }
                    }
                }

                if (definition != null) {
                    Object value = null;
                    if (originalValue != null) {
                        switch (definition.getFieldType()) {
                            case DATE:
                                try {
                                    value = FormatUtil.getDateFormat().parse(originalValue);
                                } catch (Exception e) {
                                    value = originalValue;
                                }
                                break;
                            case BOOLEAN:
                                value = new Boolean(originalValue);
                                break;
                            case DECIMAL:
                                value = new BigDecimal(originalValue);
                                break;
                            case INTEGER:
                                value = Integer.parseInt(originalValue);
                                break;
                            case MONEY:
                                value = new Money(originalValue);
                                break;
                            case ADDITIONAL_FOREIGN_KEY:
                                value = FOREIGN_LOOKUP + '|' + definition.getAdditionalForeignKeyClass() + '|' + originalValue;
                                break;
                            default:
                                value = originalValue;
                                break;
                        }
                    }
                    scDTO.getValues().put(fieldKey, value);
                } else {
                    scDTO.getValues().put(fieldKey, sc.getStructuredContentFieldXrefs().get(fieldKey).getStructuredContentField().getValue());
                }
            }
        }

        // allow modules to contribute to the fields of the DTO
        extensionManager.getProxy().populateAdditionalStructuredContentFields(sc, scDTO, secure);
    }

    /**
     * Converts a StructuredContent into a StructuredContentDTO.   If the item contains fields with
     * broadleaf cms urls, the urls are converted to utilize the domain.
     * 
     * The StructuredContentDTO is built via the {@link EntityConfiguration}. To override the actual type that is returned,
     * include an override in an applicationContext like any other entity override.
     * 
     * @param sc
     * @param secure
     * @return
     */
    protected StructuredContentDTO buildStructuredContentDTO(StructuredContent sc, boolean secure) {
        StructuredContentDTO scDTO = entityConfiguration.createEntityInstance(StructuredContentDTO.class.getName(), StructuredContentDTO.class);
        scDTO.setContentName(sc.getContentName());
        scDTO.setContentType(sc.getStructuredContentType().getName());
        scDTO.setId(sc.getId());
        scDTO.setPriority(sc.getPriority());
        
        if (sc.getLocale() != null) {
            scDTO.setLocaleCode(sc.getLocale().getLocaleCode());
        }

        scDTO.setRuleExpression(buildRuleExpression(sc));
        buildFieldValues(sc, scDTO, secure);
        
        if (sc.getQualifyingItemCriteria() != null && sc.getQualifyingItemCriteria().size() > 0) {
            scDTO.setItemCriteriaDTOList(buildItemCriteriaDTOList(sc));
        }
        return scDTO;
        
    }

    protected boolean processContentRules(StructuredContentDTO sc, Map<String, Object> ruleDTOs) {
        if (contentRuleProcessors != null) {
            for (RuleProcessor<StructuredContentDTO> processor : contentRuleProcessors) {
                boolean matchFound = processor.checkForMatch(sc, ruleDTOs);
                if (! matchFound) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String buildTypeKey(SandBox currentSandbox, Long site, Locale locale, String contentType) {
        StringBuilder key = new StringBuilder(contentType);
        if (locale != null) {
            key.append("-").append(locale.getLocaleCode());
        }

        if (currentSandbox != null) {
            key.append("-").append(currentSandbox.getId());
        }

        if (site != null) {
            key.append("-").append(site);
        }

        return key.toString();
    }

    protected String buildNameKey(SandBox sandBox, StructuredContent sc) {
        Long site = (sc instanceof SiteDiscriminator)?((SiteDiscriminator) sc).getSiteDiscriminator():null;
        return buildNameKey(sandBox, site, findLanguageOnlyLocale(sc.getLocale()), sc.getStructuredContentType().getName(), sc.getContentName());
    }

    protected String buildTypeKey(SandBox sandBox, StructuredContent sc) {
        Long site = (sc instanceof SiteDiscriminator)?((SiteDiscriminator) sc).getSiteDiscriminator():null;
        return buildTypeKey(sandBox, site, findLanguageOnlyLocale(sc.getLocale()), sc.getStructuredContentType().getName());
    }


    protected String buildNameKey(SandBox currentSandbox, Long site, Locale locale, String contentType, String contentName) {
        StringBuffer key = new StringBuffer(contentType).append("-").append(contentName);
        if (locale != null) {
            key.append("-").append(locale.getLocaleCode());
        }

        if (currentSandbox != null) {
            key.append("-").append(currentSandbox.getId());
        }

        if (site != null) {
            key.append("-").append(site);
        }

        return key.toString();
    }

    @Override
    public void addStructuredContentListToCache(String key, List<StructuredContentDTO> scDTOList) {
        getStructuredContentCache().put(new Element(key, scDTOList));
    }

    protected void addSingleStructuredContentToCache(String key, StructuredContentDTO scDTO) {
        getStructuredContentCache().put(new Element(key, scDTO));
    }

    protected StructuredContentDTO getSingleStructuredContentFromCache(String key) {
        Element scElement =  getStructuredContentCache().get(key);
        if (scElement != null) {
            statisticsService.addCacheStat(CacheStatType.STRUCTURED_CONTENT_CACHE_HIT_RATE.toString(), true);
            return (StructuredContentDTO) scElement.getValue();
        }
        statisticsService.addCacheStat(CacheStatType.STRUCTURED_CONTENT_CACHE_HIT_RATE.toString(), false);
        return null;
    }

    @Override
    public List<StructuredContentDTO> getStructuredContentListFromCache(String key) {
        Element scElement =  getStructuredContentCache().get(key);
        if (scElement != null) {
            statisticsService.addCacheStat(CacheStatType.STRUCTURED_CONTENT_CACHE_HIT_RATE.toString(), true);
            return (List<StructuredContentDTO>) scElement.getValue();
        }
        statisticsService.addCacheStat(CacheStatType.STRUCTURED_CONTENT_CACHE_HIT_RATE.toString(), false);
        return null;
    }

}
