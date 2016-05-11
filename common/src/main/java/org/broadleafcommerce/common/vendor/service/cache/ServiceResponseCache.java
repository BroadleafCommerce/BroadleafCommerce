/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.vendor.service.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.aspectj.lang.ProceedingJoinPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class ServiceResponseCache {

    public Object processRequest(ProceedingJoinPoint call) throws Throwable {
        CacheRequest cacheRequest = (CacheRequest) call.getArgs()[0];
        Cache cache = ((ServiceResponseCacheable) call.getTarget()).getCache();
        List<Serializable> cacheItemResponses = new ArrayList<Serializable>();
        Iterator<CacheItemRequest> itr = cacheRequest.getCacheItemRequests().iterator();
        while(itr.hasNext()) {
            CacheItemRequest itemRequest = itr.next();
            if (cache.isKeyInCache(itemRequest.key())) {
                cacheItemResponses.add(cache.get(itemRequest.key()).getValue()); 
                itr.remove();
            }
        }

        CacheResponse returnValue = (CacheResponse) call.proceed();
        Object[] responses = new Object[cacheItemResponses.size() + returnValue.getCacheItemResponses().length];
        responses = cacheItemResponses.toArray(responses);
        for (int j=0; j<returnValue.getCacheItemResponses().length; j++) {
            Element element = new Element(cacheRequest.getCacheItemRequests().get(j).key(), returnValue.getCacheItemResponses()[j]);
            cache.put(element);
        }
        System.arraycopy(returnValue.getCacheItemResponses(), 0, responses, cacheItemResponses.size(), returnValue.getCacheItemResponses().length);
        returnValue.setCacheItemResponses(responses);
        
        return returnValue;
    }
    
}
