/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
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
