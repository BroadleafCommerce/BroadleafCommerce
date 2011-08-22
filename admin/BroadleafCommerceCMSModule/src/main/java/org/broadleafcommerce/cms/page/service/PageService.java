package org.broadleafcommerce.cms.page.service;

import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageFolder;
import org.broadleafcommerce.cms.site.domain.Site;
import org.broadleafcommerce.openadmin.server.domain.SandBox;

import java.util.List;
import java.util.Locale;
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
    public Page findPageById(Long pageId);


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
     * Returns the Page associated with the given path, site, and locale.
     * Null values can be passed in for all parameters except the path
     * indicating that the default site, locale, and sandbox should be
     * used.
     *
     * @param path - The full path for which to return the content page.
     * @param pageFolder - The page's parent folder.
     * @param sandboxName - The sandboxName to add this page.
     * @return The associated page or null if no matching page is found.
     */
    public Page findPageByPath(String path, PageFolder pageFolder, Site site, String sandboxName);



    /**
     * Merges sandbox and site production content
     * @param
     * @param parentFolder if null then root folder for the site.
     * @return
     */
    public List<PageFolder> findPageFolderChildren(SandBox sandbox, Locale locale, PageFolder parentFolder);


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
     *
     * @param pageFolder
     * @return
     */
    public PageFolder addPageFolder(PageFolder pageFolder, PageFolder parentFolder);

}
