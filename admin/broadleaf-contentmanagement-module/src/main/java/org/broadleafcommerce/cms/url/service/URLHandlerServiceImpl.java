/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.url.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.url.dao.URLHandlerDao;
import org.broadleafcommerce.cms.url.domain.NullURLHandler;
import org.broadleafcommerce.cms.url.domain.URLHandler;
import org.broadleafcommerce.common.cache.CacheStatType;
import org.broadleafcommerce.common.cache.StatisticsService;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by bpolster.
 */
@Service("blURLHandlerService")
public class URLHandlerServiceImpl implements URLHandlerService {

    private static final Log LOG = LogFactory.getLog(URLHandlerServiceImpl.class);
    
    private final NullURLHandler NULL_URL_HANDLER = new NullURLHandler();

    @Resource(name="blURLHandlerDao")
    protected URLHandlerDao urlHandlerDao;

    @Resource(name="blStatisticsService")
    protected StatisticsService statisticsService;
    
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

    @Override
    public URLHandler findURLHandlerById(Long id) {
        return urlHandlerDao.findURLHandlerById(id);
    }

    @Override
    public void removeURLHandlerFromCache(SandBox sandBox, URLHandler urlhandler) {
        getUrlHandlerCache().remove(buildKey(sandBox, urlhandler));
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

    @Override
    public Cache getUrlHandlerCache() {
        if (urlHandlerCache == null) {
            urlHandlerCache = CacheManager.getInstance().getCache("cmsUrlHandlerCache");
        }
        return urlHandlerCache;
    }

    protected String buildKey(SandBox sandBox, String requestUri) {
        String key = requestUri;
        if (sandBox != null) {
            key = sandBox.getId() + "_" + key;
        }       
        return key;
    }
    
    protected String buildKey(SandBox sandBox, URLHandler urlHandler) {
        String key = urlHandler.getIncomingURL();
        if (sandBox != null) {
            key = sandBox.getId() + "_" + key;
        }       
        return key;
    }
    
    protected URLHandler lookupHandlerFromCache(String requestURI)  {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        String[] keys = CacheManager.getInstance().getCacheNames();
        URLHandler handler = null;
        String key = buildKey(context.getSandBox(), requestURI);
        if (context.isProductionSandBox()) {
            handler = getUrlHandlerFromCache(key);
        }
        if (handler == null) {
            handler = findURLHandlerByURIInternal(requestURI);
            //only handle null, non-hits. Otherwise, let level 2 cache handle it
            if (context.isProductionSandBox() && handler instanceof NullURLHandler) {
                getUrlHandlerCache().put(new Element(key, handler));
            }
        }

        if (handler == null || handler instanceof NullURLHandler) {
            return null;
        } else {
            return handler;
        }
    }
    
    protected URLHandler getUrlHandlerFromCache(String key) {
        Element cacheElement = getUrlHandlerCache().get(key);
        if (cacheElement != null) {
            statisticsService.addCacheStat(CacheStatType.URL_HANDLER_CACHE_HIT_RATE.toString(), true);
            return (URLHandler) cacheElement.getValue();
        }
        statisticsService.addCacheStat(CacheStatType.URL_HANDLER_CACHE_HIT_RATE.toString(), false);
        return null;
    }

    protected URLHandler findURLHandlerByURIInternal(String uri) {
        URLHandler urlHandler = urlHandlerDao.findURLHandlerByURI(uri);
        if (urlHandler != null) {
            return urlHandler;
        } else {
            return NULL_URL_HANDLER;
        }
    }

}
