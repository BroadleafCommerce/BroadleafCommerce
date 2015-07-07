/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
