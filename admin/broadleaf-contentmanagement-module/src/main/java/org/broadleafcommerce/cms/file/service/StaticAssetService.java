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

package org.broadleafcommerce.cms.file.service;

import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItemListener;
import org.hibernate.Criteria;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by bpolster.
 */
public interface StaticAssetService extends SandBoxItemListener {


    public StaticAsset findStaticAssetById(Long id);
    
    public List<StaticAsset> readAllStaticAssets();

    public StaticAsset findStaticAssetByFullUrl(String fullUrl, SandBox targetSandBox);

    /**
     * Used when uploading a file to Broadleaf.    This method will create the corresponding 
     * asset.   
     * 
     * Depending on the the implementation, the actual asset may be saved to the DB or to 
     * the file system.    The default implementation {@link StaticAssetServiceImpl} has a 
     * environment properties that determine this behavior <code>asset.use.filesystem.storage</code>, and 
     * <code>asset.server.file.system.path</code>.
     * 
     * The properties allows for implementors to update other Asset properties at the
     * same time they are uploading a file.  The default implementation uses this for an optional URL to 
     * be specified.
     * 
     * @see StaticAssetServiceImpl
     * 
     * @param file - the file being uploaded
     * @param properties - additional meta-data properties
     * @return
     * @throws IOException
     */
    public StaticAsset createStaticAssetFromFile(MultipartFile file, Map<String, String> properties);

    /**
     * This method is intended to be called from within the CMS
     * admin only.
     *
     * Adds the passed in page to the DB.
     *
     * Creates a sandbox/site if one doesn't already exist.
     */
    public StaticAsset addStaticAsset(StaticAsset staticAsset, SandBox destinationSandbox);

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
    public StaticAsset updateStaticAsset(StaticAsset staticAsset, SandBox sandbox);


    /**
     * If deleting and item where page.originalPageId != null
     * then the item is deleted from the database.
     *
     * If the originalPageId is null, then this method marks
     * the items as deleted within the passed in sandbox.
     *
     *
     * @param staticAsset
     * @param destinationSandbox
     * @return
     */
    public void deleteStaticAsset(StaticAsset staticAsset, SandBox destinationSandbox);


    public Long countAssets(SandBox sandbox, Criteria criteria);

    public List<StaticAsset> findAssets(SandBox sandbox, Criteria criteria);

    /**
     * Returns the value configured to mark an item as a static URL.
     *
     * OOB BLC maintains this value in common.properties.
     *
     * @return
     */
    public String getStaticAssetUrlPrefix();

    public void setStaticAssetUrlPrefix(String prefix);

    /**
     * Returns the value configured for the current environment
     * for the static asset url prefix.   If this is different than
     * the common value, then the URLs will get rewritten by the
     * FieldMapWrapper when called from the DisplayContentTag or
     * ProcessURLFilter.
     *
     * @return
     */
    public String getStaticAssetEnvironmentUrlPrefix();

    /**
     * Returns the secure value of the environment url prefix (e.g. prefixed with https if needed).
     *
     * @return
     */
    public String getStaticAssetEnvironmentSecureUrlPrefix();

    /**
     * Sets the environment url prefix.
     * @param prefix
     */
    public void setStaticAssetEnvironmentUrlPrefix(String prefix);


    /**
     * If set to true, then this service will not use the SandBox concept
     * and will instead automatically promote images to production
     * as they are entered into the system.
     *
     * This is recommended for the best workflow within the BLC-CMS and has
     * been set as the default behavior.
     *
     */
    public boolean getAutomaticallyApproveAndPromoteStaticAssets();

    /**
     * If set to true, then this service will not use the SandBox concept
     * and will instead automatically promote images to production
     * as they are entered into the system.
     *
     * This is recommended for the best workflow within the BLC-CMS and has
     * been set as the default behavior.
     *
     */
    public void setAutomaticallyApproveAndPromoteStaticAssets(boolean setting);


    /**
     * This method will take in an assetPath (think image url) and convert it if
     * the value contains the asseturlprefix.
     * @see StaticAssetService#getStaticAssetUrlPrefix()
     * @see StaticAssetService#getStaticAssetEnvironmentUrlPrefix()
     * 
     * @param assetPath - The path to rewrite if it is a cms managed asset
     * @param contextPath - The context path of the web application (if applicable)
     * @param secureRequest - True if the request is being served over https
     * @return
     */
    public String convertAssetPath(String assetPath, String contextPath, boolean secureRequest);


}
