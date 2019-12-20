package org.broadleafcommerce.core.search.service.solr.indexer;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Component;

@Component("blCatalogSolrIndexUpdateServiceExtensionManager")
public class CatalogSolrIndexUpdateServiceExtensionManager extends ExtensionManager<CatalogSolrIndexUpdateServiceExtensionHandler> {

    public CatalogSolrIndexUpdateServiceExtensionManager() {
        super(CatalogSolrIndexUpdateServiceExtensionHandler.class);
    }

}
