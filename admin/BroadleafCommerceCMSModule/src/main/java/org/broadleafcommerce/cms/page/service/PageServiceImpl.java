/*
 * Copyright 2008-2009 the original author or authors.
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
package org.broadleafcommerce.cms.page.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.locale.domain.Locale;
import org.broadleafcommerce.cms.locale.service.LocaleService;
import org.broadleafcommerce.cms.page.dao.PageDao;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageFolder;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.openadmin.server.dao.SandBoxItemDao;
import org.broadleafcommerce.openadmin.server.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by bpolster.
 */
@Service("blPageService")
public class PageServiceImpl implements PageService, SandBoxItemListener {
    private static final Log LOG = LogFactory.getLog(PageServiceImpl.class);

    @Resource(name="blPageDao")
    protected PageDao pageDao;

    @Resource(name="blSandBoxItemDao")
    protected SandBoxItemDao sandBoxItemDao;

    @Resource(name="blLocaleService")
    protected LocaleService localeService;

    /**
     * Returns the page with the passed in id.
     *
     * @param pageId - The id of the page.
     * @return The associated page.
     */
    @Override
    public PageFolder findPageById(Long pageId) {
        return pageDao.readPageById(pageId);
    }

    @Override
    public PageTemplate findPageTemplateById(Long id) {
        return pageDao.readPageTemplateById(id);
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
        Page page = (Page) findPageById(pageId);
        return pageDao.readPageFieldsByPage(page);
    }

    /**
     * Merges sandbox and site production content
     *
     * @param
     * @param parentFolder if null then root folder for the site.
     * @return
     */
    @Override
    public List<PageFolder> findPageFolderChildren(SandBox sandbox,PageFolder parentFolder, String localeCode) {
        SandBox productionSandbox = null;
        SandBox userSandbox = sandbox;

        if (localeCode == null) {
            localeCode = localeService.findDefaultLocale().getLocaleCode();
        }

        if (sandbox != null && sandbox.getSite() != null && sandbox.getSite().getProductionSandbox() != null) {
            productionSandbox = sandbox.getSite().getProductionSandbox();
            if (userSandbox.getId().equals(productionSandbox.getId())) {
                userSandbox = null;
            }
        }

        List<PageFolder> pageFolders =  pageDao.readPageFolderChildren(parentFolder, localeCode, userSandbox, productionSandbox);
        return pageFolders;
    }

    /**
     * This method is intended to be called from within the CMS
     * admin only.
     * <p/>
     * Adds the passed in page to the DB.
     * <p/>
     */
    @Override
    public Page addPage(Page page, PageFolder parentFolder, SandBox destinationSandbox) {
        page.setSandbox(destinationSandbox);
        page.setParentFolder(parentFolder);
        String parentUrl = (parentFolder == null ? "" : parentFolder.getFullUrl());
        page.setFullUrl(parentUrl + "/" + page.getName());
        Page newPage = pageDao.addPage(page);
        if (! isProductionSandBox(destinationSandbox)) {
            sandBoxItemDao.addSandBoxItem(destinationSandbox, SandBoxOperationType.ADD, SandBoxItemType.PAGE, newPage.getFullUrl(), newPage.getId(), null);
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

        String parentUrl = (page.getParentFolder() == null ? "" : page.getParentFolder().getFullUrl());

        if (checkForSandboxMatch(page.getSandbox(), destSandbox)) {
            page.setFullUrl(parentUrl + "/" + page.getName());
            return pageDao.updatePage(page, true);
        } else if (isProductionSandBox(page.getSandbox())) {
            page.setLockedFlag(true);
            page = pageDao.updatePage(page, false);

            // Move from production to destSandbox
            Page clonedPage = page.cloneEntity();
            clonedPage.setOriginalPageId(page.getId());
            clonedPage.setSandbox(destSandbox);
            clonedPage.setFullUrl(parentUrl + "/" + page.getName());
            Page returnPage = pageDao.addPage(clonedPage);

            sandBoxItemDao.addSandBoxItem(destSandbox, SandBoxOperationType.UPDATE, SandBoxItemType.PAGE, clonedPage.getFullUrl(), returnPage.getId(), returnPage.getOriginalPageId());
            return returnPage;
        } else {
            // This should happen via a promote, revert, or reject in the sandbox service
            throw new IllegalArgumentException("Update called when promote or reject was expected.");
        }
    }

    // Returns true if the src and dest sandbox are the same.
    private boolean checkForSandboxMatch(SandBox src, SandBox dest) {
        if (src != null) {
            if (dest != null) {
                return src.getId().equals(dest.getId());
            }
        }
        return (src == null && dest == null);
    }

    // Returns true if the dest sandbox is production.
    private boolean isProductionSandBox(SandBox dest) {
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
     * Sets the delete flag on the folder.   Throws an exception if the
     * folder contains non-archived items.
     *
     * @param pageFolder
     */
    @Override
    public void deletePageFolder(PageFolder pageFolder) {
        if (! pageFolder.getSubFolders().isEmpty()) {
            throw new UnsupportedOperationException("Cannot delete folder with children.");
        } else {
            pageFolder.setDeletedFlag(true);
            pageDao.updatePageFolder(pageFolder);
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @param pageFolder
     * @return
     */
    @Override
    public PageFolder addPageFolder(PageFolder pageFolder, PageFolder parentFolder) {
        pageFolder.setParentFolder(parentFolder);
        return pageDao.addPageFolder(pageFolder);
    }

    /**
     * Retrieve the page if one is available for the passed in uri.
     */
    @Override
    public Page findPageByURI(SandBox currentSandbox, Locale locale, String uri) {
        if (uri != null) {
            // TODO: Optimize production page lookup
            SandBox productionSandbox = null;
            if (currentSandbox != null && currentSandbox.getSite() != null) {
                productionSandbox = currentSandbox.getSite().getProductionSandbox();
            }
            Page productionPage = pageDao.findPageByURI(productionSandbox, locale, uri);

            if (currentSandbox != null && ! currentSandbox.getSandBoxType().equals(SandBoxType.PRODUCTION)) {
                Page sandboxPage = pageDao.findPageByURI(currentSandbox, locale, uri);
                if (sandboxPage != null) {
                    if (sandboxPage.getDeletedFlag()) {
                        return null;
                    } else {
                        return sandboxPage;
                    }
                }

            }
            return productionPage;
        } else {
            return null;
        }
    }

    @Override
    public void itemPromoted(SandBoxItem sandBoxItem, SandBox destinationSandBox) {
        if (! SandBoxItemType.PAGE.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }

        Page page = (Page) pageDao.readPageById(sandBoxItem.getTemporaryItemId());
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
                Page originalPage = (Page) pageDao.readPageById(page.getOriginalPageId());
                originalPage.setArchivedFlag(Boolean.TRUE);
                pageDao.updatePage(originalPage, false);

               // We are archiving the old page and making this the new "production page", so
               // null out the original page id before saving.
               page.setOriginalPageId(null);
            }
        }
        if (page.getOriginalSandBox() == null) {
            page.setOriginalSandBox(page.getSandbox());
        }
        page.setSandbox(destinationSandBox);
        pageDao.updatePage(page, false);
    }

    @Override
    public void itemRejected(SandBoxItem sandBoxItem, SandBox destinationSandBox) {
        if (! SandBoxItemType.PAGE.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }

        Page page = (Page) pageDao.readPageById(sandBoxItem.getTemporaryItemId());
        if (page != null) {
            page.setSandbox(destinationSandBox);
            page.setOriginalSandBox(null);
            page.setLockedFlag(false);
            pageDao.updatePage(page, false);
        }
    }

    @Override
    public void itemReverted(SandBoxItem sandBoxItem) {
        if (! SandBoxItemType.PAGE.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }
        Page page = (Page) pageDao.readPageById(sandBoxItem.getTemporaryItemId());

        if (page != null) {
            page.setArchivedFlag(Boolean.TRUE);
            page.setLockedFlag(false);
            pageDao.updatePage(page, false);

            Page originalPage = (Page) pageDao.readPageById(page.getOriginalPageId());
            originalPage.setLockedFlag(false);
            pageDao.updatePage(originalPage, false);
        }
    }
}
