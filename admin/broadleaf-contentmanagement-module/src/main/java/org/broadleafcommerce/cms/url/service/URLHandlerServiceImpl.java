/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.url.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.url.dao.URLHandlerDao;
import org.broadleafcommerce.cms.url.domain.NullURLHandler;
import org.broadleafcommerce.cms.url.domain.URLHandler;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.annotation.Resource;


/**
 * Created by bpolster.
 */
@Service("blURLHandlerService")
public class URLHandlerServiceImpl implements URLHandlerService {
    private static final Log LOG = LogFactory.getLog(URLHandlerServiceImpl.class);
    
    private final NullURLHandler NULL_URL_HANDLER = new NullURLHandler();

    @Resource(name="blURLHandlerDao")
    protected URLHandlerDao urlHandlerDao;
    
    protected Cache urlHandlerCache;

    /**
     * Checks the passed in URL to determine if there is a matching URLHandler.
     * Returns null if no handler was found.
     * 
     * @param uri
     * @return
     */
    @Override
    public URLHandler findURLHandlerByURI(String uri) {
        URLHandler urlHandler = lookupHandlerFromCache(uri);
        if (urlHandler instanceof NullURLHandler) {
            return null;
        } else {
            return urlHandler;
        }               
    }
    
    private String buildKey(String requestUri) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        String key = requestUri;
        if (context != null && context.getSandbox() != null) {
            key = context.getSandbox().getId() + "_" + key;
        }       
        return key;
    }
    
    private String buildKey(URLHandler urlHandler) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        String key = urlHandler.getIncomingURL();
        if (context != null & context.getSandbox() != null) {
            key = context.getSandbox().getId() + "_" + key;
        }       
        return key;
    }
    
    private URLHandler lookupHandlerFromCache(String requestURI)  {
        String key =buildKey(requestURI);
        URLHandler handler = getUrlHandlerFromCache(key);
        if (handler == null) {
            handler = findURLHandlerByURIInternal(requestURI);
            getUrlHandlerCache().put(new Element(key, handler));
        }
        
        if (handler == null || handler instanceof NullURLHandler) {
            return null;
        } else {
            return handler;
        }
    }
    
    private Cache getUrlHandlerCache() {
        if (urlHandlerCache == null) {
            urlHandlerCache = CacheManager.getInstance().getCache("cmsUrlHandlerCache");
        }
        return urlHandlerCache;
    }
    
    private URLHandler getUrlHandlerFromCache(String key) {
        Element cacheElement = getUrlHandlerCache().get(key);
        if (cacheElement != null) {
            return (URLHandler) cacheElement.getValue();
        }
        return null;
    }

    /**
     * Call to evict an item from the cache.
     * @param p
     */
    public void removeURLHandlerFromCache(URLHandler urlhandler) {
        getUrlHandlerCache().remove(buildKey(urlhandler));
    }
    
    protected URLHandler findURLHandlerByURIInternal(String uri) {
        URLHandler urlHandler = urlHandlerDao.findURLHandlerByURI(uri);
        if (urlHandler != null) {
            return urlHandler;
        } else {
            return NULL_URL_HANDLER;
        }
    }
    
    @Override
    public List<URLHandler> findAllURLHandlers() {
        return urlHandlerDao.findAllURLHandlers();
    }
    
    @Override
    @Transactional("blTransactionManager")
    public URLHandler saveURLHandler(URLHandler handler) {
        return urlHandlerDao.saveURLHandler(handler);
    }

}
