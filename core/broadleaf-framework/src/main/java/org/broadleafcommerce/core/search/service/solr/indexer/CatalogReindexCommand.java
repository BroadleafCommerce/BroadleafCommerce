package org.broadleafcommerce.core.search.service.solr.indexer;

import org.springframework.util.Assert;

public class CatalogReindexCommand extends SolrUpdateCommand {

    private static final long serialVersionUID = 1L;

    private final Long catalogId;
    
    public CatalogReindexCommand(Long catalogId) {
        Assert.notNull(catalogId, "Catalog ID cannot be null.");
        
        this.catalogId = catalogId;
    }
    
    public Long getCatalogId() {
        return catalogId;
    }

    @Override
    public int hashCode() {
        return getCatalogId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof CatalogReindexCommand) {
            if (getCatalogId().equals(((CatalogReindexCommand) obj).getCatalogId())) {
                return true;
            }
        }
        
        return false;
        
    }
    
    
    
}
