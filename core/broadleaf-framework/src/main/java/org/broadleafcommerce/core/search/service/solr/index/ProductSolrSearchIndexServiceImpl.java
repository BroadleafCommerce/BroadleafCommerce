package org.broadleafcommerce.core.search.service.solr.index;

import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.index.service.DocumentBuilder;

import javax.annotation.Resource;

public class ProductSolrSearchIndexServiceImpl extends AbstractSolrIndexServiceImpl<Product> {
    
    @Resource(name="blProductSolrDocumentBuilder")
    protected DocumentBuilder<Indexable, SolrInputDocument> documentBuilder;

    @Override
    protected FieldEntity determineFieldEntity() {
        return FieldEntity.PRODUCT;
    }

    @Override
    protected void preProcess(String processId) throws ServiceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void postProcess(String processId) throws ServiceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected Long countIndexables() {
        // TODO Auto-generated method stub
        return null;
    }

    

}
