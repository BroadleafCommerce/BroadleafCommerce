package org.broadleafcommerce.core.search.index;

import org.broadleafcommerce.common.exception.ServiceException;

import java.util.List;

public interface DocumentIndexer<D> {

    public void indexDocument(D doc) throws ServiceException;
    
    public void indexDocuments(List<D> docs) throws ServiceException;
}
