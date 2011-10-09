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

import org.broadleafcommerce.cms.locale.domain.Locale;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageFolder;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.openadmin.server.domain.SandBox;

import java.util.List;
import java.util.Map;

/**
 * Created by bpolster.
 */
public interface PageService {


    /**
     * Returns the page with the passed in id.
     *
     * @param pageId - The id of the page.
     * @return The associated page.
     */
    public PageFolder findPageById(Long pageId);

    /**
     * Returns the page template with the passed in id.
     *
     * @param id - the id of the page template
     * @return The associated page template.
     */
    public PageTemplate findPageTemplateById(Long id);

    /**
     * Returns the page-fields associated with the passed in page-id.
     * This is preferred over the direct access from Page so that the
     * two items can be cached distinctly
     *
     * @param pageId - The id of the page.
     * @return The associated page.
     */
    public Map<String,PageField> findPageFieldsByPageId(Long pageId);


    /**
     * Merges sandbox and site production content
     * @param sandbox - the sandbox to find pages (null indicates pages that are in production for
     *                  sites that are single tennant.
     * @param parentFolder if null then root folder for the site.
     * @param localeCode - the locale to include (null is typical for non-internationalized sites)
     * @return
     */
    public List<PageFolder> findPageFolderChildren(SandBox sandbox, PageFolder parentFolder, String localeCode);


    /**
     * This method is intended to be called from within the CMS
     * admin only.
     *
     * Adds the passed in page to the DB.
     *
     * Creates a sandbox/site if one doesn't already exist.
     */
    public Page addPage(Page page, PageFolder parentFolder, SandBox destinationSandbox);

    /**
     * This method is intended to be called from within the CMS
     * admin only.
     *
     * Updates the page according to the following rules:
     *
     * 1.  If sandbox has changed from null to a value
     * This means that the user is editing an item in production and
     * the edit is taking place in a sandbox.
     *
     * Clone the page and add it to the new sandbox and set the cloned
     * page's originalPageId to the id of the page being updated.
     *
     * 2.  If the sandbox has changed from one value to another
     * This means that the user is moving the item from one sandbox
     * to another.
     *
     * Update the siteId for the page to the one associated with the
     * new sandbox
     *
     * 3.  If the sandbox has changed from a value to null
     * This means that the item is moving from the sandbox to production.
     *
     * If the page has an originalPageId, then update that page by
     * setting it's archived flag to true.
     *
     * Then, update the siteId of the page being updated to be the
     * siteId of the original page.
     *
     * 4.  If the sandbox is the same then just update the page.
     */
    public Page updatePage(Page page, SandBox sandbox);


    /**
     * If deleting and item where page.originalPageId != null
     * then the item is deleted from the database.
     *
     * If the originalPageId is null, then this method marks
     * the items as deleted within the passed in sandbox.
     *
     * @param page
     * @param destinationSandbox
     * @return
     */
    public void deletePage(Page page, SandBox destinationSandbox);


    /**
     * Sets the delete flag on the folder.   Throws an exception if the
     * folder contains non-archived items.
     *
     * @param pageFolder
     */
    public void deletePageFolder(PageFolder pageFolder);


    /**
     * Adds a sub-folder to the passed in parentFolder
     * @param pageFolder
     * @return
     */
    public PageFolder addPageFolder(PageFolder pageFolder, PageFolder parentFolder);


    /**
     * Retrieve the page if one is available for the passed in uri.
     */
    public Page findPageByURI(SandBox currentSandbox, Locale locale, String uri);

}
