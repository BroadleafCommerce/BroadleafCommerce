package org.broadleafcommerce.core.search.service.solr.indexer;

public class FullReindexCommand extends SolrUpdateCommand {

    private static final long serialVersionUID = 1L;
    
    public static final FullReindexCommand DEFAULT_INSTANCE = new FullReindexCommand();
    
    public FullReindexCommand() {}

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FullReindexCommand) {
            return true;
        }
        
        return false;
    }
    
    

}
