/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.service.solr.indexer;

import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.search.domain.IndexField;

import java.util.List;

/**
 * Provides additional hooks to assist in allowing extensions and overrides of basic functionality.
 * 
 * @author Kelly Tisdell
 *
 */
public interface CatalogSolrIndexUpdateServiceExtensionHandler extends ExtensionHandler {

    /**
     * Provides a hook to set up state or execute logic prior to the full process starting.
     * 
     * @param holder
     */
    public void preProcess(ReindexStateHolder holder);
    
    /**
     * Provides a hook to set up state or execute logic after the full process completes successfully. This will not be called in the event of a failure.
     * @param holder
     */
    public void postProcess(ReindexStateHolder holder);
    
    /**
     * Provides a hook to set up state or execute logic prior to the full process starting.
     * @param catalog
     * @param holder
     */
    public void preCatalog(Catalog catalog, ReindexStateHolder holder);
    
    /**
     * 
     * @param catalog
     * @param holder
     */
    public void postCatalog(Catalog catalog, ReindexStateHolder holder);
    
    /**
     * 
     * @param ids
     * @param locales
     * @param fields
     * @param holder
     */
    public void prePage(List<Long> ids, List<Locale> locales, List<IndexField> fields, ReindexStateHolder holder);
    
    /**
     * 
     * @param ids
     * @param locales
     * @param fields
     * @param holder
     */
    public void postPage(List<Long> ids, List<Locale> locales, List<IndexField> fields, ReindexStateHolder holder);
    
    /**
     * 
     * @param indexable
     * @param fields
     * @param locales
     * @param doc
     */
    public void preDocument(Indexable indexable, List<IndexField> fields, List<Locale> locales, SolrInputDocument doc);
    
    /**
     * 
     * @param indexable
     * @param fields
     * @param locales
     * @param doc
     */
    public void postDocument(Indexable indexable, List<IndexField> fields, List<Locale> locales, SolrInputDocument doc);
}
