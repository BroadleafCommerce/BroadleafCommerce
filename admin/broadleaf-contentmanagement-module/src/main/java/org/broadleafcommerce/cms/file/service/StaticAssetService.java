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

package org.broadleafcommerce.cms.file.service;

import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItemListener;
import org.hibernate.Criteria;

import java.util.List;

/**
 * Created by bpolster.
 */
public interface StaticAssetService extends SandBoxItemListener {


    public StaticAsset findStaticAssetById(Long id);

    public StaticAsset findStaticAssetByFullUrl(String fullUrl, SandBox targetSandBox);

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


}
