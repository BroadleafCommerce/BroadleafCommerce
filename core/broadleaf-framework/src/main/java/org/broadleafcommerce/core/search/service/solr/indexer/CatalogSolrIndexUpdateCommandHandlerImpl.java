package org.broadleafcommerce.core.search.service.solr.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.search.service.solr.SolrConfiguration;

public class CatalogSolrIndexUpdateCommandHandlerImpl extends AbstractSolrIndexUpdateCommandHandlerImpl {
    
    private static final Log LOG = LogFactory.getLog(CatalogSolrIndexUpdateCommandHandlerImpl.class);
    
    public CatalogSolrIndexUpdateCommandHandlerImpl(SolrConfiguration solrConfiguration) {
        super("catalog", solrConfiguration);
    }

    @Override
    public <C extends SolrUpdateCommand> void executeCommand(C command) throws ServiceException {
        if (command instanceof IncrementalUpdateCommand) {
            super.executeCommandInternal((IncrementalUpdateCommand)command);
        }
    }

    @Override
    public SolrInputDocument buildDocument(Indexable indexable) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
