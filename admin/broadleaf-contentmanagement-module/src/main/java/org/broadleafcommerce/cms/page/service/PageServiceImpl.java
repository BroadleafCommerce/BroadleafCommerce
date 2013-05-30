/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.page.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.common.AbstractContentService;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.page.dao.PageDao;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageImpl;
import org.broadleafcommerce.cms.page.domain.PageItemCriteria;
import org.broadleafcommerce.cms.page.domain.PageRule;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.cms.page.dto.NullPageDTO;
import org.broadleafcommerce.cms.page.dto.PageDTO;
import org.broadleafcommerce.cms.page.message.ArchivedPagePublisher;
import org.broadleafcommerce.cms.structure.dto.ItemCriteriaDTO;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.locale.util.LocaleUtil;
import org.broadleafcommerce.common.sandbox.dao.SandBoxDao;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.broadleafcommerce.openadmin.server.dao.SandBoxItemDao;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItemListener;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItemType;
import org.broadleafcommerce.openadmin.server.domain.SandBoxOperationType;
import org.hibernate.Criteria;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Created by bpolster.
 */
@Service("blPageService")
public class PageServiceImpl extends AbstractContentService implements PageService, SandBoxItemListener {
    protected static final Log LOG = LogFactory.getLog(PageServiceImpl.class);
    
    protected static String AND = " && ";

    @Resource(name="blPageDao")
    protected PageDao pageDao;

    @Resource(name="blSandBoxItemDao")
    protected SandBoxItemDao sandBoxItemDao;

    @Resource(name="blSandBoxDao")
    protected SandBoxDao sandBoxDao;
    
    @Resource(name="blPageRuleProcessors")
    protected List<PageRuleProcessor> pageRuleProcessors;    

    @Resource(name="blLocaleService")
    protected LocaleService localeService;
    
    @Resource(name="blStaticAssetService")
    protected StaticAssetService staticAssetService;

    @Value("${automatically.approve.pages}")
    protected boolean automaticallyApproveAndPromotePages=true;
    
    protected Cache pageCache;

    protected List<ArchivedPagePublisher> archivedPageListeners;
    
    protected final PageDTO NULL_PAGE = new NullPageDTO();    
    
    protected final List<PageDTO> EMPTY_PAGE_DTO = new ArrayList<PageDTO>();

    /**
     * Returns the page with the passed in id.
     *
     * @param pageId - The id of the page.
     * @return The associated page.
     */
    @Override
    public Page findPageById(Long pageId) {
        return pageDao.readPageById(pageId);
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

    /**
     * Returns the page-fields associated with the passed in page-id.
     * This is preferred over the direct access from Page so that the
     * two items can be cached distinctly
     *
     * @param pageId - The id of the page.
     * @return The associated page.
     */
    @Override
    public Map<String, PageField> findPageFieldsByPageId(Long pageId) {
        Page page = findPageById(pageId);
        return pageDao.readPageFieldsByPage(page);
    }

    /**
     * This method is intended to be called from within the CMS
     * admin only.
     * <p/>
     * Adds the passed in page to the DB.
     * <p/>
     */
    @Override
    public Page addPage(Page page, SandBox destinationSandbox) {

        if (automaticallyApproveAndPromotePages) {
            if (destinationSandbox != null && destinationSandbox.getSite() != null) {
                destinationSandbox = destinationSandbox.getSite().getProductionSandbox();
            } else {
                // Null means production for single-site installations.
                destinationSandbox = null;
            }
        }

        page.setSandbox(destinationSandbox);
        page.setArchivedFlag(false);
        page.setDeletedFlag(false);
        Page newPage = pageDao.addPage(page);
        if (! isProductionSandBox(destinationSandbox)) {
            sandBoxItemDao.addSandBoxItem(destinationSandbox.getId(), SandBoxOperationType.ADD, SandBoxItemType.PAGE, newPage.getFullUrl(), newPage.getId(), null);
        }
        return newPage;
    }

    /**
     * This method is intended to be called from within the CMS
     * admin only.
     * <p/>
     * Updates the page according to the following rules:
     * <p/>
     * 1.  If sandbox has changed from null to a value
     * This means that the user is editing an item in production and
     * the edit is taking place in a sandbox.
     * <p/>
     * Clone the page and add it to the new sandbox and set the cloned
     * page's originalPageId to the id of the page being updated.
     * <p/>
     * 2.  If the sandbox has changed from one value to another
     * This means that the user is moving the item from one sandbox
     * to another.
     * <p/>
     * Update the siteId for the page to the one associated with the
     * new sandbox
     * <p/>
     * 3.  If the sandbox has changed from a value to null
     * This means that the item is moving from the sandbox to production.
     * <p/>
     * If the page has an originalPageId, then update that page by
     * setting it's archived flag to true.
     * <p/>
     * Then, update the siteId of the page being updated to be the
     * siteId of the original page.
     * <p/>
     * 4.  If the sandbox is the same then just update the page.
     */
    @Override
    public Page updatePage(Page page, SandBox destSandbox) {
        if (page.getLockedFlag()) {
            throw new IllegalArgumentException("Unable to update a locked record");
        }

        if (automaticallyApproveAndPromotePages) {
            if (destSandbox != null && destSandbox.getSite() != null) {
                destSandbox = destSandbox.getSite().getProductionSandbox();
            } else {
                // Null means production for single-site installations.
                destSandbox = null;
            }
        }

        if (checkForSandboxMatch(page.getSandbox(), destSandbox)) {
            if (page.getDeletedFlag()) {
                SandBoxItem item = page.getSandbox()==null?null:sandBoxItemDao.retrieveBySandboxAndTemporaryItemId(page.getSandbox().getId(), SandBoxItemType.PAGE, page.getId());
                if (page.getOriginalPageId() == null && item != null) {
                    // This page was added in this sandbox and now needs to be deleted.
                    item.setArchivedFlag(true);
                    page.setArchivedFlag(true);
                } else if (item != null) {
                    // This page was being updated but now is being deleted - so change the
                    // sandbox operation type to deleted
                    item.setSandBoxOperationType(SandBoxOperationType.DELETE);
                    sandBoxItemDao.updateSandBoxItem(item);
                } else if (automaticallyApproveAndPromotePages) {
                    page.setArchivedFlag(true);
                }

            }
            return pageDao.updatePage(page);
        } else if (isProductionSandBox(page.getSandbox())) {

            // The passed in page is an existing page with updated values.
            // Instead, we want to create a clone of this page for the destSandbox
            Page clonedPage = page.cloneEntity();
            clonedPage.setOriginalPageId(page.getId());
            clonedPage.setSandbox(destSandbox);

            // Detach the old page from the session so it is not updated.
            pageDao.detachPage(page);

            // Save the cloned page
            Page returnPage = pageDao.addPage(clonedPage);

            // Lookup the original page and mark it as locked
            Page prod = findPageById(page.getId());
            prod.setLockedFlag(true);
            pageDao.updatePage(prod);

            SandBoxOperationType type = SandBoxOperationType.UPDATE;
            if (clonedPage.getDeletedFlag()) {
                type = SandBoxOperationType.DELETE;
            }

            // Add this item to the sandbox.
            sandBoxItemDao.addSandBoxItem(destSandbox.getId(), type, SandBoxItemType.PAGE, clonedPage.getFullUrl(), returnPage.getId(), returnPage.getOriginalPageId());
            return returnPage;
        } else {
            // This should happen via a promote, revert, or reject in the sandbox service
            throw new IllegalArgumentException("Update called when promote or reject was expected.");
        }
    }

    // Returns true if the src and dest sandbox are the same.
    protected boolean checkForSandboxMatch(SandBox src, SandBox dest) {
        if (src != null) {
            if (dest != null) {
                return src.getId().equals(dest.getId());
            }
        }
        return (src == null && dest == null);
    }

    // Returns true if the dest sandbox is production.
    protected boolean isProductionSandBox(SandBox dest) {
        if (dest == null) {
            return true;
        } else {
            return SandBoxType.PRODUCTION.equals(dest.getSandBoxType());
        }
    }


    /**
     * If deleting and item where page.originalPageId != null
     * then the item is deleted from the database.
     * <p/>
     * If the originalPageId is null, then this method marks
     * the items as deleted within the passed in sandbox.
     *
     * @param page
     * @param destinationSandbox
     * @return
     */
    @Override
    public void deletePage(Page page, SandBox destinationSandbox) {
        page.setDeletedFlag(true);
        updatePage(page, destinationSandbox);
    }
    
    /**
     * Converts a list of pages to a list of pageDTOs.<br>
     * Internally calls buildPageDTO(...).
     *
     * @param pageList
     * @param secure
     * @return
     */
    protected List<PageDTO> buildPageDTOList(List<Page> pageList, boolean secure) {
        List<PageDTO> dtoList = new ArrayList<PageDTO>();
        if (pageList != null) {
            for(Page page : pageList) {
                dtoList.add(buildPageDTOInternal(page, secure));
            }
        }
        return dtoList;
    }

    protected PageDTO buildPageDTOInternal(Page page, boolean secure) {
        PageDTO pageDTO = new PageDTO();
        pageDTO.setId(page.getId());
        pageDTO.setDescription(page.getDescription());
        pageDTO.setUrl(page.getFullUrl());
        pageDTO.setPriority(page.getPriority());

        if (page.getSandbox() != null) {
            pageDTO.setSandboxId(page.getSandbox().getId());
        }

        if (page.getPageTemplate() != null) {
            pageDTO.setTemplatePath(page.getPageTemplate().getTemplatePath());
            if (page.getPageTemplate().getLocale() != null) {
                pageDTO.setLocaleCode(page.getPageTemplate().getLocale().getLocaleCode());
            }
        }

        String envPrefix = staticAssetService.getStaticAssetEnvironmentUrlPrefix();
        if (envPrefix != null && secure) {
            envPrefix = staticAssetService.getStaticAssetEnvironmentSecureUrlPrefix();
        }
        
        String cmsPrefix = staticAssetService.getStaticAssetUrlPrefix();

        for (String fieldKey : page.getPageFields().keySet()) {
            PageField pf = page.getPageFields().get(fieldKey);
            String originalValue = pf.getValue();
            if (StringUtils.isNotBlank(envPrefix) && StringUtils.isNotBlank(originalValue) && StringUtils.isNotBlank(cmsPrefix) && originalValue.contains(cmsPrefix)) {
                String fldValue = originalValue.replaceAll(cmsPrefix, envPrefix+cmsPrefix);
                pageDTO.getPageFields().put(fieldKey, fldValue);
            } else {
                pageDTO.getPageFields().put(fieldKey, originalValue);
            }
        }
        
        pageDTO.setRuleExpression(buildRuleExpression(page));
                               
        if (page.getQualifyingItemCriteria() != null && page.getQualifyingItemCriteria().size() > 0) {
            pageDTO.setItemCriteriaDTOList(buildItemCriteriaDTOList(page));
        }
         
        return pageDTO;
    }
    
        
    protected String buildRuleExpression(Page page) {
       StringBuffer ruleExpression = null;
       Map<String, PageRule> ruleMap = page.getPageMatchRules();
       if (ruleMap != null) {
           for (String ruleKey : ruleMap.keySet()) {
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
    
    protected List<ItemCriteriaDTO> buildItemCriteriaDTOList(Page page) {
        List<ItemCriteriaDTO> itemCriteriaDTOList = new ArrayList<ItemCriteriaDTO>();
        for(PageItemCriteria criteria : page.getQualifyingItemCriteria()) {
            ItemCriteriaDTO criteriaDTO = new ItemCriteriaDTO();
            criteriaDTO.setMatchRule(criteria.getMatchRule());
            criteriaDTO.setQty(criteria.getQuantity());
            itemCriteriaDTOList.add(criteriaDTO);
        }
        return itemCriteriaDTOList;
    }      
    
    protected List<PageDTO> mergePages(List<PageDTO> productionPageList, List<Page> sandboxPageList, boolean secure) {
        if (sandboxPageList == null || sandboxPageList.size() == 0) {
            return productionPageList;
        }
        
        Map<Long, PageDTO> pageMap = new LinkedHashMap<Long, PageDTO>();
        if (productionPageList != null) {
            for(PageDTO page : productionPageList) {
                pageMap.put(page.getId(), page);
            }
        }
        
        for (Page page : sandboxPageList) {
            if (page.getOriginalPageId() != null) {
                pageMap.remove(page.getOriginalPageId());
            }
            
            if (! page.getDeletedFlag() && page.getOfflineFlag() != null && ! page.getOfflineFlag()) {
                PageDTO convertedPage = buildPageDTOInternal(page, secure);
                pageMap.put(page.getId(), convertedPage);
            }
        }
        
        ArrayList<PageDTO> returnList = new ArrayList<PageDTO>(pageMap.values());
        
        if (returnList.size() > 1) {
            Collections.sort(returnList, new BeanComparator("priority"));
        }
        
        return returnList;
    }
    
    protected PageDTO evaluatePageRules(List<PageDTO> pageDTOList, Locale locale, Map<String, Object> ruleDTOs) {
        if (pageDTOList == null) {
            return NULL_PAGE;
        }

        // First check to see if we have a page that matches on the full locale.
        for(PageDTO page : pageDTOList) {
            if (locale != null && locale.getLocaleCode() != null) {
                if (page.getLocaleCode().equals(locale.getLocaleCode())) {
                    if (passesPageRules(page, ruleDTOs)) {
                        return page;
                    }
                }
            }
        }

        // Otherwise, we look for a match using just the language.
        for(PageDTO page : pageDTOList) {
            if (passesPageRules(page, ruleDTOs)) {
                return page;
            }
        }
        
        return NULL_PAGE;       
    }
    
    protected boolean passesPageRules(PageDTO page, Map<String, Object> ruleDTOs) {
        if (pageRuleProcessors != null) {
            for (PageRuleProcessor processor : pageRuleProcessors) {
                boolean matchFound = processor.checkForMatch(page, ruleDTOs);
                if (! matchFound) {
                    return false;
                }
            }
        }
        return true;
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

    /**
     * Retrieve the page if one is available for the passed in uri.
     */
    @Override
    public PageDTO findPageByURI(SandBox currentSandbox, Locale locale, String uri, Map<String,Object> ruleDTOs, boolean secure) {
        List<PageDTO> returnList = null;
        if (uri != null) {        
            SandBox productionSandbox = null;
            if (currentSandbox != null && currentSandbox.getSite() != null) {
                productionSandbox = currentSandbox.getSite().getProductionSandbox();
            }

            Locale languageOnlyLocale = findLanguageOnlyLocale(locale);
    
            String key = buildKey(productionSandbox, locale, uri);
            key = key + "-" + secure;
            returnList = getPageListFromCache(key);
            if (returnList == null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Page not found in cache, searching DB for key: " + key);
                }
                List<Page> productionPages = pageDao.findPageByURI(productionSandbox, locale, languageOnlyLocale, uri);
                
                if (productionPages != null) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Pages found, adding pages to cache with key: " + key);
                    }
                    returnList = buildPageDTOList(productionPages, secure);
                    Collections.sort(returnList, new BeanComparator("priority"));
                    addPageListToCache(returnList, key);
                } else {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("No match found for passed in URI, locale, and sandbox.  Key = " + key);
                    }
                    addPageListToCache(EMPTY_PAGE_DTO, key);
                }
            }
            
            // If the request is from a non-production SandBox, we need to check to see if the SandBox has an override 
            // for this page before returning.  No caching is used for Sandbox pages.
            if (currentSandbox != null && ! currentSandbox.getSandBoxType().equals(SandBoxType.PRODUCTION)) {
                List<Page> sandboxPages = pageDao.findPageByURI(currentSandbox, locale, languageOnlyLocale, uri);
                if (sandboxPages != null && sandboxPages.size() > 0) {                                  
                    returnList = mergePages(returnList, sandboxPages, secure);                                    
                }
            }
        }
        
        return evaluatePageRules(returnList, locale, ruleDTOs);
    }

    @Override
    public List<Page> findPages(SandBox sandbox, Criteria c) {
        return findItems(sandbox, c, Page.class, PageImpl.class, "originalPageId");
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
    public Long countPages(SandBox sandbox, Criteria c) {
       return countItems(sandbox, c, PageImpl.class, "originalPageId");
    }
    
    protected void productionItemArchived(Page page) {
        // Immediately remove the page from this VM.
        removePageFromCache(page);

        if (archivedPageListeners != null) {
            for (ArchivedPagePublisher listener : archivedPageListeners) {
                listener.processPageArchive(page, buildKey(page));
            }
        }
    }

    @Override
    public void itemPromoted(SandBoxItem sandBoxItem, SandBox destinationSandBox) {
        if (! SandBoxItemType.PAGE.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }

        Page page = pageDao.readPageById(sandBoxItem.getTemporaryItemId());
        if (page == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Page not found " + sandBoxItem.getTemporaryItemId());
            }
        } else {
            boolean productionSandBox = isProductionSandBox(destinationSandBox);
            if (productionSandBox) {
                page.setLockedFlag(false);
            } else {
                page.setLockedFlag(true);
            }
            if (productionSandBox && page.getOriginalPageId() != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Page promoted to production.  " + page.getId() + ".  Archiving original page " + page.getOriginalPageId());
                }
                Page originalPage = pageDao.readPageById(page.getOriginalPageId());
                originalPage.setArchivedFlag(Boolean.TRUE);
                pageDao.updatePage(originalPage);
                productionItemArchived(originalPage);

               // We are archiving the old page and making this the new "production page", so
               // null out the original page id before saving.
               page.setOriginalPageId(null);

               if (page.getDeletedFlag()) {
                   // If this page is being deleted, set it as archived.
                   page.setArchivedFlag(true);
               }
            }
        }
        if (page.getOriginalSandBox() == null) {
            page.setOriginalSandBox(page.getSandbox());
        }
        page.setSandbox(destinationSandBox);
        pageDao.updatePage(page);
    }

    @Override
    public void itemRejected(SandBoxItem sandBoxItem, SandBox destinationSandBox) {
        if (! SandBoxItemType.PAGE.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }

        Page page = pageDao.readPageById(sandBoxItem.getTemporaryItemId());
        if (page != null) {
            page.setSandbox(destinationSandBox);
            page.setOriginalSandBox(null);
            page.setLockedFlag(false);
            pageDao.updatePage(page);
        }
    }

    @Override
    public void itemReverted(SandBoxItem sandBoxItem) {
        if (! SandBoxItemType.PAGE.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }
        Page page = pageDao.readPageById(sandBoxItem.getTemporaryItemId());

        if (page != null) {
            page.setArchivedFlag(Boolean.TRUE);
            page.setLockedFlag(false);
            pageDao.updatePage(page);

            if (page.getOriginalPageId() != null) {
                Page originalPage = pageDao.readPageById(page.getOriginalPageId());
                originalPage.setLockedFlag(false);
                pageDao.updatePage(originalPage);
            }
        }
    }
    
    protected Cache getPageCache() {
        if (pageCache == null) {
            pageCache = CacheManager.getInstance().getCache("cmsPageCache");
        }
        return pageCache;
    }

    protected String buildKey(SandBox currentSandbox, Locale locale, String uri) {
        StringBuffer key = new StringBuffer(uri);
        if (locale != null) {
            key.append("-").append(locale.getLocaleCode());
        }

        if (currentSandbox != null) {
            key.append("-").append(currentSandbox.getId());
        }

        return key.toString();
    }

    protected String buildKey(Page page) {
        return buildKey(page.getSandbox(), page.getPageTemplate().getLocale(), page.getFullUrl());
    }
    
    protected void addPageListToCache(List<PageDTO> pageList, String key) {
        getPageCache().put(new Element(key, pageList));
    }
    
    @SuppressWarnings("unchecked")
    protected List<PageDTO> getPageListFromCache(String key) {
        Element cacheElement = getPageCache().get(key);
        if (cacheElement != null && cacheElement.getValue() != null) {
            return (List<PageDTO>) cacheElement.getValue();
        }
        return null;
    }

    /**
     * Call to evict an item from the cache.
     * @param p
     */
    public void removePageFromCache(Page p) {
        // Remove secure and non-secure instances of the page.
        // Typically the page will be in one or the other if at all.
        removePageFromCache(buildKey(p));
    }

    /**
     * Call to evict both secure and non-secure pages matching
     * the passed in key.
     *
     * @param baseKey
     */
    @Override
    public void removePageFromCache(String baseKey) {
        // Remove secure and non-secure instances of the page.
        // Typically the page will be in one or the other if at all.
        getPageCache().remove(baseKey+"-"+true);
        getPageCache().remove(baseKey+"-"+false);
    }

    @Override
    public List<ArchivedPagePublisher> getArchivedPageListeners() {
        return archivedPageListeners;
    }

    @Override
    public void setArchivedPageListeners(List<ArchivedPagePublisher> archivedPageListeners) {
        this.archivedPageListeners = archivedPageListeners;
    }

    @Override
    public boolean isAutomaticallyApproveAndPromotePages() {
        return automaticallyApproveAndPromotePages;
    }

    @Override
    public void setAutomaticallyApproveAndPromotePages(boolean automaticallyApproveAndPromotePages) {
        this.automaticallyApproveAndPromotePages = automaticallyApproveAndPromotePages;
    }
}
