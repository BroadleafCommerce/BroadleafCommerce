package org.broadleafcommerce.core.search.service.solr.indexer;

public class CatalogSolrIndexUpdateCommandManagerImpl extends AbstractSolrIndexCommandManagerImpl implements CatalogSolrIndexUpdateCommandManager {

    public CatalogSolrIndexUpdateCommandManagerImpl(SolrIndexQueueProvider queueProvider, SolrIndexUpdateCommandHandler commandHandler) {
        super("catalog", queueProvider, commandHandler);
    }

}
