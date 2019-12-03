package org.broadleafcommerce.core.search.service.solr.indexer;

import org.springframework.util.Assert;

public class SiteReindexCommand extends SolrUpdateCommand {

    private static final long serialVersionUID = 1L;

    private final Long siteId;
    
    public SiteReindexCommand(Long siteId) {
        Assert.notNull(siteId, "siteId cannot be null.");
        this.siteId = siteId;
    }
    
    public Long getSiteId() {
        return siteId;
    }

    @Override
    public int hashCode() {
        return getSiteId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof SiteReindexCommand) {
            if (getSiteId().equals(((SiteReindexCommand) obj).getSiteId())) {
                return true;
            }
        }
        return false;
    }
    
    
}
