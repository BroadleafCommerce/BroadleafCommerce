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
