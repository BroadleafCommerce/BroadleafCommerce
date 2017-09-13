package org.broadleafcommerce.core.search.service.solr.index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.search.index.service.AbstractGenericSearchIndexService;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

import javax.annotation.Resource;

public abstract class AbstractSolrIndexServiceImpl<I extends Indexable> extends AbstractGenericSearchIndexService<I> {
    
    private static final Log LOG = LogFactory.getLog(AbstractSolrIndexServiceImpl.class);
    
    @Resource(name="blSolrUtil")
    protected SolrUtil solrUtil;
    
    @Resource(name = "blTransactionManager")
    protected PlatformTransactionManager transactionManager;

    @Override
    public void reindexItems(List<I> items) throws ServiceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void process(String processId) throws ServiceException {
        // TODO Auto-generated method stub
        
    }
    
}
