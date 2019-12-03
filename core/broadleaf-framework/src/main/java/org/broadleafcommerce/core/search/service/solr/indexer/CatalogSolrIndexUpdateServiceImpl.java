package org.broadleafcommerce.core.search.service.solr.indexer;

public class CatalogSolrIndexUpdateServiceImpl extends AbstractSolrIndexUpdateServiceImpl implements CatalogSolrIndexUpdateService {

    public CatalogSolrIndexUpdateServiceImpl(SolrIndexQueueProvider queueProvider, SolrIndexUpdateCommandHandler commandHandler) {
        super("catalog", queueProvider, commandHandler);
    }

}
