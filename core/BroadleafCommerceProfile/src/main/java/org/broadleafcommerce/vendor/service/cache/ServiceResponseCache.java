package org.broadleafcommerce.vendor.service.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.aspectj.lang.ProceedingJoinPoint;

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
