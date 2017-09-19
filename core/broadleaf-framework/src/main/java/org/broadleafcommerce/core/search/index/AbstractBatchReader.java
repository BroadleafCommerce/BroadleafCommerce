/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.index;

import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.service.SiteService;
import org.broadleafcommerce.common.util.tenant.IdentityExecutionUtils;
import org.broadleafcommerce.common.util.tenant.IdentityOperation;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract reader that handles the details of keeping track of pagination
 * 
 * @author Kelly Tisdell
 *
 * @param <T>
 */
public abstract class AbstractBatchReader<T> implements BatchReader<T> {
    
    protected static final int DEFAULT_BATCH_SIZE = 100;
    private boolean complete = false;
    private int page = -1; //Start at -1 so first call to getNextPage() will return 0.
    private List<Site> sites;
    private List<Catalog> catalogs;
    private int siteIndex = 0;
    private int catalogIndex = 0;
    
    @Override
    public final synchronized List<T> readBatch() {
        if (isComplete()) {
            return null;
        }
        final int pageSize = getPageSize();
        Assert.isTrue(pageSize > 0, "pageSize must be greater than 0.");
        final int nextPage = getNextPage();
        final List<T> out;
        final Site currentSite;
        final Catalog currentCatalog;
        if(sites != null && !sites.isEmpty()) {
            currentSite = sites.get(siteIndex);
            if (catalogs == null) {
                catalogs = IdentityExecutionUtils
                        .runOperationByIdentifier(new IdentityOperation<List<Catalog>, RuntimeException>() {
                    @Override
                    public List<Catalog> execute() throws RuntimeException {
                        ArrayList<Catalog> out = new ArrayList<>();
                        List<Catalog> cats = getSiteService().findAllCatalogs();
                        if (cats != null) {
                            for (Catalog cat : cats) {
                                if (cat.isActive()) {
                                    out.add(cat);
                                }
                            }
                        }
                        
                        return out;
                    }
                }, currentSite);
            }
            
            if (catalogs != null && ! catalogs.isEmpty()) {
                currentCatalog = catalogs.get(catalogIndex);
            } else {
                currentCatalog = null;
            }   
        } else {
            currentSite = null;
            currentCatalog = null;
        }
        
        out = readBatchInternal(nextPage, pageSize, currentSite, currentCatalog);
        if (out == null || out.size() <  pageSize) {
            //We've reached the end of a batch.  Decide what to do next...
            //Advance the catalog index
            catalogIndex++;
            if (catalogs == null || catalogIndex >= catalogs.size()) {
                //We know we need to move to the next site...
                siteIndex++;
                catalogs = null;
                catalogIndex = 0;
            } 
            
            if (siteIndex >= sites.size()) {
                //We're at the end of sites.
                markComplete();
            }
        }
        
        return out;
    }
    
    private synchronized int getNextPage() {
        return page++;
    }
    
    private final synchronized void markComplete() {
        this.complete = true;
        sites = null;
        catalogs = null;
        siteIndex = 0;
        catalogIndex = 0;
        page = 0;
    }

    @Override
    public final synchronized void reset() {
        complete = false;
        page = 0;
        siteIndex = 0;
        sites = getSiteService().findAllNonPersistentActiveSites();
        catalogs = null;
        resetInternal();
    }

    @Override
    public final synchronized boolean isComplete() {
        return complete;
    }
    
    /**
     * This is the size of the page to return. This must return a value > 0.  This will generally return a constant value. 
     * The default value is 100.
     * @return
     */
    protected int getPageSize() {
        return DEFAULT_BATCH_SIZE;
    }
    
    /**
     * Mechanism to allow for internal resetting of state.
     */
    protected void resetInternal() {
        //Nothing...
    }
    
    /**
     * The page always starts at zero (0) and increments by 1 on each invocation.  It is the implementor's responsibility to 
     * to adjust the page if necessary (e.g. page = page + 1, for example, when the page needs to start at 1 or a different  
     * value). But the first time this method is called, the page value will be zero.  And it will be incremented by one on  
     * each additional invocation until reset is called. The page size depends on the a call to getBatchSize(), 
     * which must return a non-null value.
     * 
     * It is expected that this will consistently return items for the specified page and batch size.  When there are no more 
     * items to return, this method MUST call markComplete(), indicating that there is no more data to return.
     * 
     * @param page
     * @param pageSize
     * @return
     */
    protected abstract List<T> readBatchInternal(int page, int pageSize, Site site, Catalog catalog);
    
    /**
     * Returns the site service that should be used by this component.
     * @return
     */
    protected abstract SiteService getSiteService();
}
