package org.broadleafcommerce.core.search.service.solr.indexer;

import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.search.domain.IndexField;

import java.util.List;

/**
 * Provides an abstract implementation of {@link CatalogSolrIndexUpdateServiceExtensionHandler} that has no-op methods implemented for convenience.
 * @author kellytisdell
 *
 */
public abstract class AbstractCatalogSolrIndexUpdateServiceExtensionHandler implements CatalogSolrIndexUpdateServiceExtensionHandler {

    @Override
    public void preProcess(ReindexStateHolder holder) {
        //Nothing, by default...
    }

    @Override
    public void postProcess(ReindexStateHolder holder) {
        //Nothing, by default...
    }

    @Override
    public void preCatalog(Catalog catalog, ReindexStateHolder holder) {
        //Nothing, by default...
    }

    @Override
    public void postCatalog(Catalog catalog, ReindexStateHolder holder) {
        // Nothing, by default...
    }

    @Override
    public void prePage(List<Long> ids, List<Locale> locales, List<IndexField> fields, ReindexStateHolder holder) {
        // Nothing, by default...
    }

    @Override
    public void postPage(List<Long> ids, List<Locale> locales, List<IndexField> fields, ReindexStateHolder holder) {
        // Nothing, by default...
    }

    @Override
    public void preDocument(Indexable indexable, List<IndexField> fields, List<Locale> locales, SolrInputDocument doc) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void postDocument(Indexable indexable, List<IndexField> fields, List<Locale> locales, SolrInputDocument doc) {
        // Nothing, by default...
    }

    
}
