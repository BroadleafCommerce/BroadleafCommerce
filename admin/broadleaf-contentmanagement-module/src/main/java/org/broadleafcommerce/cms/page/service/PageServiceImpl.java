/*-
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.cms.page.service;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.page.dao.PageDao;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.common.cache.CacheStatType;
import org.broadleafcommerce.common.cache.StatisticsService;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ResultType;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.locale.util.LocaleUtil;
import org.broadleafcommerce.common.page.dto.NullPageDTO;
import org.broadleafcommerce.common.page.dto.PageDTO;
import org.broadleafcommerce.common.rule.RuleProcessor;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.template.TemplateOverrideExtensionManager;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.cache.Cache;
import javax.cache.CacheManager;


/**
 * @author Brian Polster (bpolster)
 * @author Nathan Moore (nathandmoore)
 */
@Service("blPageService")
public class PageServiceImpl implements PageService {

    protected static final Log LOG = LogFactory.getLog(PageServiceImpl.class);
    protected static String AND = " && ";

    @Resource(name="blPageDao")
    protected PageDao pageDao;

    @Resource(name="blPageRuleProcessors")
    protected List<RuleProcessor<PageDTO>> pageRuleProcessors;

    @Resource(name="blLocaleService")
    protected LocaleService localeService;

    @Resource(name="blStaticAssetService")
    protected StaticAssetService staticAssetService;

    @Resource(name="blStatisticsService")
    protected StatisticsService statisticsService;

    @Resource(name = "blTemplateOverrideExtensionManager")
    protected TemplateOverrideExtensionManager templateOverrideManager;

    @Resource(name = "blPageServiceUtility")
    protected PageServiceUtility pageServiceUtility;

    @Resource(name = "blPageServiceExtensionManager")
    protected PageServiceExtensionManager extensionManager;

    @Resource(name = "blPageQueryExtensionManager")
    protected PageQueryExtensionManager queryExtensionManager;
    
    @Resource(name = "blCacheManager")
    protected CacheManager cacheManager;

    protected Cache pageCache;
    protected Cache pageMapCache;
    protected Cache uriCachedDateCache;
    
    protected final PageDTO NULL_PAGE = new NullPageDTO();

    /*
     * Returns the page with the passed in id.
     */
    @Override
    public Page findPageById(Long pageId) {
        return pageDao.readPageById(pageId);
    }

    /*
     * Returns the page with the passed in id.
     */
    @Override
    public Map<String, PageField> findPageFieldMapByPageId(Long pageId) {
        Map<String, PageField> returnMap = new HashMap<>();
        List<PageField> pageFields = pageDao.readPageFieldsByPageId(pageId);

        for (PageField pf : pageFields) {
            returnMap.put(pf.getFieldKey(), pf);
        }

        return returnMap;
    }

    @Override
    public PageTemplate findPageTemplateById(Long id) {
        return pageDao.readPageTemplateById(id);
    }

    @Override
    @Transactional("blTransactionManager")
    public PageTemplate savePageTemplate(PageTemplate template) {
        return pageDao.savePageTemplate(template);
    }

    /*
     * Retrieve the page if one is available for the passed in uri.
     */
    @Override
    public PageDTO findPageByURI(Locale locale, String uri, Map<String,Object> ruleDTOs, boolean secure) {
        PageDTO dto;

        if (!isNullPageCached(locale, uri, secure)) {
            final List<PageDTO> returnList = getPageDTOListForURI(locale, uri, secure);
            dto = evaluatePageRules(returnList, locale, ruleDTOs);

            if (dto.getId() != null) {
                final Page page = findPageById(dto.getId());
                final ExtensionResultHolder<PageDTO> newDTO = new ExtensionResultHolder<>();

                // Allow an extension point to override the page to render.
                extensionManager.getProxy().overridePageDto(newDTO, dto, page);
                if (newDTO.getResult() != null) {
                    dto = newDTO.getResult();
                }
            }

            if (dto != null) {
                dto = pageServiceUtility.hydrateForeignLookups(dto);
            }

        } else {
            dto = NULL_PAGE;
        }

        return dto;
    }

    protected boolean isNullPageCached(Locale locale, String uri, boolean secure) {
        boolean result = false;
        final String cacheKey = buildKey(uri, locale, secure);
        if (getPageCache().get(cacheKey) != null) {
            Object pageDto = ((List) this.getPageCache().get(cacheKey)).get(0);
            if (pageDto instanceof NullPageDTO) {
                result = true;
            }
        }
        return result;
    }

    protected List<PageDTO> getPageDTOListForURI(final Locale locale, final String uri, final boolean secure) {
        final List<PageDTO> dtoList;

        if (uri != null) {
            final String key = buildKey(uri, locale, secure);
            addCachedDate(key);

            final List<Page> pageList = pageDao.findPageByURIAndActiveDate(uri, getCachedDate(key));

            // if page doesn't exist - cached NullPageDTO to reduce queries to DB
            if (pageList.isEmpty()) {
                getPageCache().put(key, Collections.singletonList(NULL_PAGE));
                addPageMapCacheEntry(uri, key);
            }

            dtoList = buildPageDTOList(pageList, secure, uri, locale);
        } else {
            dtoList = null;
        }

        return dtoList;
    }

    protected void addCachedDate(final String key) {
        if (getPageCache().get(key) == null) {
            getUriCachedDateCache().put(key, new Date());
        }
    }

    protected Date getCachedDate(final String key) {
        final Object element = getUriCachedDateCache().get(key);
        final Date cachedDate;

        if (element != null ) {
            cachedDate = (Date) element;
        } else {
            cachedDate = new Date();
        }

        return cachedDate;
    }

    /*
     * Converts a list of pages to a list of pageDTOs, and caches the list.
     */
    @Override
    public List<PageDTO> buildPageDTOList(List<Page> pageList, boolean secure, String identifier, Locale locale) {
        List<PageDTO> dtoList = new ArrayList<>();
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();

        if (context.isProductionSandBox()) {
            dtoList = buildPageDTOListUsingCache(pageList, identifier, locale, secure);
        } else {
            // no caching actions needed if not production sandbox
            addPageListToPageDTOList(pageList, secure, dtoList);
        }

        return copyDTOList(dtoList);
    }

    @SuppressWarnings("unchecked")
    protected List<PageDTO> buildPageDTOListUsingCache(List<Page> pageList, String identifier, Locale locale, boolean secure) {
        List<PageDTO> dtoList = getCachedPageDTOList(pageList, identifier, locale, secure);

        if (dtoList == null || dtoList.isEmpty()) {
            addPageListToPageDTOList(pageList, secure, dtoList);

            if (dtoList != null && !dtoList.isEmpty()) {
                Collections.sort(dtoList, new BeanComparator("priority"));
                addPageListToCache(dtoList, identifier, locale, secure);
            }
        }

        return dtoList;
    }

    protected List<PageDTO> getCachedPageDTOList(List<Page> pageList, String identifier, Locale locale, boolean secure) {
        List<PageDTO> dtoList = new ArrayList<>();
        String key = buildKey(identifier, locale, secure);
        List<PageDTO> cachedList = getPageListFromCache(key);

        if (cachedList != null && cachedList.size() == pageList.size()) {
            dtoList = cachedList;
        }

        return dtoList;
    }

    protected void addPageListToPageDTOList(List<Page> pageList, boolean secure, List<PageDTO> dtoList) {
        if (pageList != null) {
            for(Page page : pageList) {
                PageDTO pageDTO = pageServiceUtility.buildPageDTO(page, secure);

                if (!dtoList.contains(pageDTO)) {
                    dtoList.add(pageDTO);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected List<PageDTO> getPageListFromCache(String key) {
        if (key != null) {
            Object cacheElement = getPageCache().get(key);

            if (cacheElement != null ) {
                statisticsService.addCacheStat(CacheStatType.PAGE_CACHE_HIT_RATE.toString(), true);
                return (List<PageDTO>) cacheElement;
            }

            statisticsService.addCacheStat(CacheStatType.PAGE_CACHE_HIT_RATE.toString(), false);
        }

        return null;
    }

    protected void addPageListToCache(List<PageDTO> pageList, String identifier, Locale locale, boolean secure) {
        String key = buildKey(identifier, locale, secure);
        getPageCache().put(key, pageList);
        addPageMapCacheEntry(identifier, key);
    }

    @SuppressWarnings("unchecked")
    protected void addPageMapCacheEntry(String identifier, String key) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        Site site = context.getNonPersistentSite();
        Long siteId = (site != null) ? site.getId() : null;

        String mapKey = getPageMapCacheKey(identifier, siteId);

        if (mapKey != null) {
            Object e = getPageMapCache().get(mapKey);

            if (e == null) {
                List<String> keys = new ArrayList<>();
                keys.add(key);
                getPageMapCache().put(mapKey, keys);
            } else {
                ((List<String>) e).add(mapKey);
            }
        }
    }

    @Override
    public String getPageMapCacheKey(String uri, Long site) {
        String siteString = (site == null) ? "ALL" : String.valueOf(site);
        return uri + "-" + siteString;
    }

    protected String buildKey(String identifier, Locale locale, Boolean secure) {
        String localeCode = locale != null ? locale.getLocaleCode() : "";
        return buildKey(identifier, localeCode, secure, null);
    }

    
    protected String buildKey(String identifier, String localeCode, Boolean secure, ResultType resultType) {
        if (resultType == null) {
            resultType = ResultType.STANDARD;
        }
        String cacheKey = buildBaseKey(identifier, localeCode, secure);
        //if we have a queryExtensionManager then the cacheKey will be modified (usually for multitenant sites)
        if (queryExtensionManager != null) {
            ExtensionResultHolder<String> result = new ExtensionResultHolder<String>();
            queryExtensionManager.getProxy().getCacheKey(cacheKey, resultType, result);
            if (result.getResult() != null) {
                cacheKey = result.getResult();
            }
        }
        return cacheKey;
    }

    protected String buildBaseKey(String identifier, String localeCode, Boolean secure) {
        StringBuilder key = new StringBuilder(identifier);
        if (localeCode != null) {
            key.append("-").append(localeCode);
        }
        if (secure != null) {
            key.append("-").append(secure);
        }
        return key.toString();
    }
        
    protected Locale findLanguageOnlyLocale(Locale locale) {
        if (locale != null ) {
            Locale languageOnlyLocale = localeService.findLocaleByCode(LocaleUtil.findLanguageCode(locale));
            if (languageOnlyLocale != null) {
                return languageOnlyLocale;
            }
        }
        return locale;
    }

    @Override
    public Cache getPageCache() {
        if (pageCache == null) {
            pageCache = cacheManager.getCache("cmsPageCache");
        }
        return pageCache;
    }

    @Override
    public Cache getPageMapCache() {
        if (pageMapCache == null) {
            pageMapCache = cacheManager.getCache("cmsPageMapCache");
        }
        return pageMapCache;
    }

    @Override
    public Cache getUriCachedDateCache() {
        if (uriCachedDateCache == null) {
            uriCachedDateCache = cacheManager.getCache("uriCachedDateCache");
        }

        return uriCachedDateCache;
    }

    /*
     * Because everything is passed by reference in java, and we don't want the cached list to be modified when the
     * returned list is.
     */
    protected List<PageDTO> copyDTOList(List<PageDTO> dtoList) {
        List<PageDTO> dtoListCopy = new ArrayList<>();

        for (PageDTO dto : dtoList) {
            PageDTO dtoCopy = new PageDTO();
            dtoCopy.copy(dto);
            dtoListCopy.add(dtoCopy);
        }

        return dtoListCopy;
    }

    protected PageDTO evaluatePageRules(List<PageDTO> pageDTOList, Locale locale, Map<String, Object> ruleDTOs) {
        if (pageDTOList == null) {
            return NULL_PAGE;
        }

        // First check to see if we have a page that matches on the full locale.
        for (PageDTO page : pageDTOList) {
            if (locale != null && locale.getLocaleCode() != null) {
                if (locale.getLocaleCode().equals(page.getLocaleCode())) {
                    if (passesPageRules(page, ruleDTOs)) {
                        return page;
                    }
                }
            }
        }

        // Otherwise, we look for a match using just the language.
        for (PageDTO page : pageDTOList) {
            if (passesPageRules(page, ruleDTOs)) {
                return page;
            }
        }

        return NULL_PAGE;
    }

    protected boolean passesPageRules(PageDTO page, Map<String, Object> ruleDTOs) {
        if (pageRuleProcessors != null) {
            for (RuleProcessor<PageDTO> processor : pageRuleProcessors) {
                boolean matchFound = processor.checkForMatch(page, ruleDTOs);
                if (! matchFound) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<Page> readAllPages() {
        return pageDao.readAllPages();
    }

    @Override
    public List<PageTemplate> readAllPageTemplates() {
        return pageDao.readAllPageTemplates();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Boolean removePageFromCache(String mapKey) {
        Boolean success = null;
        if (mapKey != null) {
            Object e = getPageMapCache().get(mapKey);

            if (e != null) {
                List<String> keys = (List<String>) e;

                for (String k : keys) {
                    if (success == null) {
                        success = getPageCache().remove(k);
                    } else {
                        success = success && getPageCache().remove(k);
                    }
                }
            }
        }

        return success == null ? Boolean.FALSE : success;
    }

    @Override
    public Boolean removeTranslationPageFromCache(final String uri, String localeCode, boolean isSecure) {
        String cacheKey = buildBaseKey(uri, localeCode, isSecure);
        List<String> cacheKeys = new ArrayList<>();
        cacheKeys.add(cacheKey);
        if (queryExtensionManager != null) {
            ExtensionResultHolder<List<String>> response = new ExtensionResultHolder<List<String>>();
            queryExtensionManager.getProxy().getCacheKeyListForTemplateSite(cacheKey, response);
            cacheKeys = response.getResult();
        }
        for (String cKey : cacheKeys) {
            // cacheKeys from the templateSites (extensionManager) are returned with a "templateSiteId:" prefix.  Parsing those out to get just the child site keys
            if (cKey.contains(":")) {
                cKey = cKey.substring(cKey.indexOf(":")+1);
            }
            getPageCache().remove(cKey);
        }
        return true;
    }
}
