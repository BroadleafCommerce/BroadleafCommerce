/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */
package org.broadleafcommerce.common.extension;

/**
 * Extension manager for any Service or Dao that wishes to allow contribution of restrictions
 * to fetch queries. This practice is generally employed by modules that need to embellish fetch
 * queries from core, or even other modules.
 *
 * @see QueryExtensionHandler
 * @author Jeff Fischer
 */
public abstract class AbstractQueryExtensionManager extends ExtensionManager<QueryExtensionHandler> {

    public AbstractQueryExtensionManager() {
        super(QueryExtensionHandler.class);
    }

}
