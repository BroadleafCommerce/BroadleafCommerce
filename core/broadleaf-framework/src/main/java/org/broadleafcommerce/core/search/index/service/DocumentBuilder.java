package org.broadleafcommerce.core.search.index.service;

import org.broadleafcommerce.core.catalog.domain.Indexable;

/**
 * Interface that defines a method to accept an indexable instance and return a search engine-specific input.
 * 
 * @author Kelly Tisdell
 *
 * @param <I>
 * @param <D>
 */
public interface DocumentBuilder<I extends Indexable, D> {
    
    /**
     * Generic interface to build a document or input for the index.  For example, this could 
     * accept a Product and return a SolrInputDocument.
     * 
     * @param indexable
     * @return
     */
    public D buildDocument(I indexable);

}
