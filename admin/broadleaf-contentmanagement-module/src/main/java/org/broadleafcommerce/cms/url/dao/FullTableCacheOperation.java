package org.broadleafcommerce.cms.url.dao;

import javax.cache.Cache;

public interface FullTableCacheOperation {
    Long findFullCacheRowCount();
    void buildNewFullCache(Cache<Object, Object> newCache);
}
