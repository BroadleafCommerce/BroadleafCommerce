/*
 * Copyright 2008-20011 the original author or authors.
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
import org.broadleafcommerce.cms.page.dao.PageDao;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageFolder;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by bpolster.
 */
@Service("blPageService")
public class PageServiceImpl implements PageService {
    private static final Log LOG = LogFactory.getLog(PageServiceImpl.class);

    @Resource(name="blPageDao")
    protected PageDao pageDao;

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
    public List<PageFolder> findPageFolderChildren(SandBox sandbox,PageFolder parentFolder, String localeName) {
        SandBox productionSandbox = null;
        SandBox userSandbox = sandbox;

        if (localeName == null) {
            localeName = "default";
        }

        if (sandbox != null && sandbox.getSite() != null && sandbox.getSite().getProductionSandbox() != null) {
            productionSandbox = sandbox.getSite().getProductionSandbox();
            if (userSandbox.getId().equals(productionSandbox.getId())) {
                userSandbox = null;
            }
        }

        List<PageFolder> pageFolders =  pageDao.readPageFolderChildren(parentFolder, localeName, userSandbox, productionSandbox);
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
        return pageDao.updatePage(page);
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
        if (checkForSandboxMatch(page.getSandbox(), destSandbox)) {
            return pageDao.updatePage(page);
        } else if (checkForProductionSandbox(page.getSandbox())) {
            // Moving from production to destSandbox
            Page clonedPage = page.cloneEntity();
            clonedPage.setOriginalPageId(page.getId());
            clonedPage.setSandbox(destSandbox);
            return pageDao.addPage(clonedPage);
        } else if (checkForProductionSandbox(destSandbox)) {
            // Moving to production
            Page existingPage = (Page) findPageById(page.getOriginalPageId());
            existingPage.setArchivedFlag(true);
            pageDao.updatePage(existingPage);

            if (page.getDeletedFlag() == true) {
                pageDao.delete(page);
            }
            return null;
        } else {
            page.setSandbox(destSandbox);
            return pageDao.updatePage(page);
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
    private boolean checkForProductionSandbox(SandBox dest) {
        boolean productionSandbox = false;

        if (dest == null) {
            productionSandbox = true;
        } else {
            if (dest.getSite() != null && dest.getSite().getProductionSandbox() != null && dest.getSite().getProductionSandbox().getId() != null) {
                productionSandbox = dest.getSite().getProductionSandbox().getId().equals(dest.getId());
            }
        }

        return productionSandbox;
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
        if (page.getOriginalPageId() != null) {
            pageDao.delete(page);
        } else {
            page.setDeletedFlag(true);
            updatePage(page, destinationSandbox);
        }
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

    @Override
    public List<PageTemplate> retrieveAllPageTemplates(String localeName) {
        if (localeName == null) {
            localeName = "default";
        }
        return pageDao.retrieveAllPageTemplates(localeName);
    }
}
