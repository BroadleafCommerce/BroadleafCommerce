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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.url.dao.URLHandlerDao;
import org.broadleafcommerce.cms.url.domain.NullURLHandler;
import org.broadleafcommerce.cms.url.domain.URLHandler;
import org.broadleafcommerce.cms.url.domain.URLHandlerDTO;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.util.EfficientLRUMap;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;


/**
 * Created by bpolster.
 */
@Service("blURLHandlerService")
public class URLHandlerServiceImpl implements URLHandlerService {

    private static final Log LOG = LogFactory.getLog(URLHandlerServiceImpl.class);

    //This is just a placeholder object to allow us to cache a URI that does not have a URL handler.
    protected static final NullURLHandler NULL_URL_HANDLER = new NullURLHandler();

    protected Cache urlHandlerCache;

    @Resource(name = "blURLHandlerDao")
    protected URLHandlerDao urlHandlerDao;

    protected Map<String, Pattern> urlPatternMap = new EfficientLRUMap<String, Pattern>(2000);

    /**
     * Checks the passed in URL to determine if there is a matching URLHandler.
     * Returns null if no handler was found.
     *
     * @param uri
     * @return
     */
    @Override
    public URLHandler findURLHandlerByURI(String uri) {

        //This allows clients or implementors to manipulate the URI, for example making it all lower case.
        //The default implementation simply does not manipulate the URI in any way, but simply returns
        //what is passed in.
        uri = manipulateUri(uri);

        URLHandler handler = null;

        SandBox sandbox = null;
        Site site = null;
        if (BroadleafRequestContext.getBroadleafRequestContext() != null) {
            sandbox = BroadleafRequestContext.getBroadleafRequestContext().getSandBox();
            site = BroadleafRequestContext.getBroadleafRequestContext().getNonPersistentSite();
        }

        String key = buildKey(site, sandbox, uri);

        //See if this is in cache first...
        handler = getUrlHandlerFromCache(key);

        if (handler == null) {
            //Check for an exact match in the DB...
            handler = urlHandlerDao.findURLHandlerByURI(uri);

            if (handler == null) {
                //Check for a regex match
                handler = checkForMatches(uri);
            }

            if (handler == null) {
                //Use the NullURLHandler instance. This will be cached to indicate that 
                //This URL does not have a match.
                handler = NULL_URL_HANDLER;
            } else if (!(URLHandlerDTO.class.isAssignableFrom(handler.getClass()))) {
                //Create a non-entity instance of the DTO to cache.
                handler = new URLHandlerDTO(handler.getNewURL(), handler.getUrlRedirectType());
            }

            getUrlHandlerCache().put(new Element(key, handler));
        }

        if (handler instanceof NullURLHandler) {
            return null;
        }

        return handler;
    }

    @Override
    public URLHandler findURLHandlerById(Long id) {
        return urlHandlerDao.findURLHandlerById(id);
    }

    @Override
    public List<URLHandler> findAllURLHandlers() {
        return urlHandlerDao.findAllURLHandlers();
    }

    @Override
    public List<URLHandler> findAllRegexURLHandlers() {
        return urlHandlerDao.findAllRegexURLHandlers();
    }

    @Override
    @Transactional("blTransactionManager")
    public URLHandler saveURLHandler(URLHandler handler) {
        return urlHandlerDao.saveURLHandler(handler);
    }

    protected URLHandler checkForMatches(String requestURI) {
        URLHandler currentHandler = null;
        try {
            List<URLHandler> urlHandlers = findAllRegexURLHandlers();
            if (urlHandlers != null) {
                for (URLHandler urlHandler : urlHandlers) {
                    currentHandler = urlHandler;
                    String incomingUrl = currentHandler.getIncomingURL();
                    if (!incomingUrl.startsWith("^")) {
                        if (incomingUrl.startsWith("/")) {
                            incomingUrl = "^" + incomingUrl + "$";
                        } else {
                            incomingUrl = "^/" + incomingUrl + "$";
                        }
                    }

                    Pattern p = urlPatternMap.get(incomingUrl);
                    if (p == null) {
                        p = Pattern.compile(incomingUrl);
                        urlPatternMap.put(incomingUrl, p);
                    }
                    Matcher m = p.matcher(requestURI);
                    if (m.find()) {
                        String newUrl = m.replaceFirst(urlHandler.getNewURL());
                        if (newUrl.equals(urlHandler.getNewURL())) {
                            return urlHandler;
                        } else {
                            return new URLHandlerDTO(newUrl, urlHandler.getUrlRedirectType());
                        }
                    }

                }
            }
        } catch (RuntimeException re) {
            if (currentHandler != null) {
                // We don't want an invalid regex to cause tons of logging                
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Error parsing URL Handler (incoming =" + currentHandler.getIncomingURL() + "), outgoing = ( "
                            + currentHandler.getNewURL() + "), " + requestURI);
                }
            }
        }

        return null;
    }

    /*
     * Some clients may wish, for example, to convert the URI into all lower case, or to manipulate it in some way.  This 
     * is just a convenience method to allow the manipulation of the URI coming in that we are trying to 
     * find a match for.
     */
    protected String manipulateUri(String uri) {
        //The default is not to modify the URI at all.
        return uri;
    }

    protected URLHandler getUrlHandlerFromCache(String key) {
        Element cacheElement = getUrlHandlerCache().get(key);
        if (cacheElement != null) {
            return (URLHandler) cacheElement.getObjectValue();
        }
        return null;
    }

    protected Cache getUrlHandlerCache() {
        if (urlHandlerCache == null) {
            urlHandlerCache = CacheManager.getInstance().getCache("cmsUrlHandlerCache");
        }
        return urlHandlerCache;
    }

    protected String buildKey(Site site, SandBox sandBox, String requestUri) {
        StringBuilder key = new StringBuilder();
        if (site != null) {
            key.append("site:").append(site.getId()).append('_');
        }

        if (sandBox != null) {
            key.append("sbx:").append(sandBox.getId()).append('_');
        }

        key.append(requestUri);

        return key.toString();
    }
}
