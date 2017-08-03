/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.url.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.url.dao.URLHandlerDao;
import org.broadleafcommerce.cms.url.domain.NullURLHandler;
import org.broadleafcommerce.cms.url.domain.URLHandler;
import org.broadleafcommerce.cms.url.domain.URLHandlerDTO;
import org.broadleafcommerce.common.cache.StatisticsService;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.util.EfficientLRUMap;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by bpolster.
 */
@Service("blURLHandlerService")
public class URLHandlerServiceImpl implements URLHandlerService {

    protected static final String REGEX_SPECIAL_CHARS_PATTERN = "([\\[\\]\\.\\|\\?\\*\\+\\(\\)\\\\~`\\!@#%&\\-_+={}'\"\"<>:;, \\/])"; //other than ^ and $
    //This is just a placeholder object to allow us to cache a URI that does not have a URL handler.
    protected static final NullURLHandler NULL_URL_HANDLER = new NullURLHandler();
    private static final Log LOG = LogFactory.getLog(URLHandlerServiceImpl.class);
    protected Cache urlHandlerCache;

    @Resource(name = "blURLHandlerDao")
    protected URLHandlerDao urlHandlerDao;

    @Resource(name = "blStatisticsService")
    protected StatisticsService statisticsService;

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

        Site site = null;
        if (BroadleafRequestContext.getBroadleafRequestContext() != null) {
            site = BroadleafRequestContext.getBroadleafRequestContext().getNonPersistentSite();
        }

        String key = buildURLHandlerCacheKey(site, uri);

        //See if this is in cache first, but only if we are in production
        if (BroadleafRequestContext.getBroadleafRequestContext().isProductionSandBox()) {
            handler = getUrlHandlerFromCache(key);
        }

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
                handler = new URLHandlerDTO(handler.getId(),
                        handler.getIncomingURL(),
                        handler.getNewURL(),
                        handler.getUrlRedirectType());
            }

            if (BroadleafRequestContext.getBroadleafRequestContext().isProductionSandBox()) {
                getUrlHandlerCache().put(new Element(key, handler));
            }
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
    public List<URLHandler> findURLHandlersByDestination(String uri) {
        return urlHandlerDao.findURLHandlersByDestination(uri);
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

    @Override
    @Transactional("blTransactionManager")
    public void savePartialURLHandler(URLHandler handler) {
        urlHandlerDao.savePartialURLHandler(handler);
    }

    @Override
    @Transactional("blTransactionManager")
    public void deleteURLHandler(URLHandler urlHandler) {
        urlHandlerDao.deleteURLHandler(urlHandler);
    }

    protected URLHandler checkForMatches(String requestURI) {
        URLHandler currentHandler = null;
        try {
            List<URLHandler> urlHandlers = findAllURLHandlers();
            for (URLHandler urlHandler : urlHandlers) {
                currentHandler = urlHandler;
                String incomingUrl = wrapStringsWithAnchors(currentHandler.getIncomingURL());

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
        } catch (RuntimeException re) {
            if (currentHandler != null) {
                // We don't want an invalid regex to cause tons of logging                
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Error parsing URL Handler incoming = (" + currentHandler.getIncomingURL() + "), outgoing = ("
                            + currentHandler.getNewURL() + "), " + requestURI);
                }
            }
        }

        return null;
    }

    @Override
    public Boolean removeURLHandlerFromCache(String mapKey) {
        Boolean success = Boolean.FALSE;
        if (mapKey != null) {
            Element e = getUrlHandlerCache().get(mapKey);

            if (e != null && e.getObjectValue() != null) {
                success = Boolean.valueOf(getUrlHandlerCache().remove(mapKey));
            }
        }

        return success;
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

    @Override
    public String buildURLHandlerCacheKey(Site site, String requestUri) {
        StringBuilder key = new StringBuilder();
        if (site != null) {
            key.append("site:").append(site.getId()).append('_');
        }

        //make sure the uri part of the key is always lower case for consistency when dealing with the cache
        key.append(StringUtils.lowerCase(requestUri));

        return key.toString();
    }

    protected String wrapStringsWithAnchors(String incomingUrl) {
        if (!incomingUrl.startsWith("^")) {
            if (incomingUrl.substring(0, 1).matches(REGEX_SPECIAL_CHARS_PATTERN)) {
                incomingUrl = "^" + incomingUrl;
            } else {
                incomingUrl = "^/" + incomingUrl;
            }
        }

        if (!incomingUrl.endsWith("$")) {
            incomingUrl += "$";
        }

        return incomingUrl;
    }
}
