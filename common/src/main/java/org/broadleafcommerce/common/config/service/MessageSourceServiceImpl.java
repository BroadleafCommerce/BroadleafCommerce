/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.common.config.service;

import org.broadleafcommerce.common.config.dao.MessageSourceDao;
import org.broadleafcommerce.common.config.domain.MessageSource;
import org.broadleafcommerce.common.extensibility.jpa.SiteDiscriminator;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Service that retrieves Message Sources from the database.
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blMessageSourceService")
public class MessageSourceServiceImpl implements MessageSourceService {

    protected Cache messageSourceCache;

    @Resource(name="blMessageSourceDao")
    protected MessageSourceDao messageSourceDao;

    @Value("${message.source.cache.timeout}")
    protected int messageSourceCacheTimeout;

    @Override
    public String resolveMessageSource(String name, Locale locale) {
        String result;
        // We don't want to utilize this cache for sandboxes
        if (BroadleafRequestContext.getBroadleafRequestContext().getSandBox() == null) {
            result = getMessageFromCache(name, locale);
        } else {
            result = null;
        }

        if (result != null) {
            return result;
        }

        MessageSource messageSource = messageSourceDao.readMessageSourceByNameAndLocale(name, locale);
        if (messageSource != null) {
           result = messageSource.getValue();
           addMessageToCache(name, result, messageSource.getLocale());
        }

        return result;
    }

    protected void addMessageToCache(String messageName, String messageValue, Locale locale) {
        String key = buildKey(messageName, locale);
        if (messageSourceCacheTimeout < 0) {
            getMessageSourceCache().put(new Element(key, messageValue));
        } else {
            getMessageSourceCache().put(new Element(key, messageValue, messageSourceCacheTimeout,
                    messageSourceCacheTimeout));
        }
    }

    protected String getMessageFromCache(String messageName, Locale locale) {
        String key = buildKey(messageName, locale);
        Element cacheElement = getMessageSourceCache().get(key);
        if (cacheElement != null && cacheElement.getObjectValue() != null) {
            return (String) cacheElement.getObjectValue();
        }
        return null;
    }

    /**
     * Messages can vary by site.
     * If a site is found on the request, use the site id as part of the cache-key.
     *
     * @param propertyName
     * @return
     */
    protected String buildKey(String propertyName, Locale locale) {
        String key = propertyName;
        if (locale != null) {
            key = key + locale.getLocaleCode();
        }
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null) {
            if (brc.getSite() != null) {
                key = brc.getSite().getId() + "-" + key;
            }
        }
        return key;
    }

    /**
     * Messages can vary by site.
     * If a site is found on the request, use the site id as part of the cache-key.
     *
     * @param systemProperty
     * @return
     */
    protected String buildKey(MessageSource messageSource) {
        String key = messageSource.getName();
        if (messageSource.getLocale() != null) {
            key = key + messageSource.getLocale().getLocaleCode();
        }
        if (messageSource instanceof SiteDiscriminator && ((SiteDiscriminator) messageSource).getSiteDiscriminator() != null) {
            key = ((SiteDiscriminator) messageSource).getSiteDiscriminator() + "-" + key;
        }
        return key;
    }

    protected Cache getMessageSourceCache() {
        if (messageSourceCache == null) {
            messageSourceCache = CacheManager.getInstance().getCache("blMessageSourceElements");
        }
        return messageSourceCache;
    }

    @Override
    public MessageSource findById(Long id) {
        return messageSourceDao.readById(id);
    }

    @Override
    public void removeFromCache(MessageSource messageSource) {
        //Could have come from a cache invalidation service that does not
        //include the site on the thread, so we should build the key
        //including the site (if applicable) from the messageSource itself
        String key = buildKey(messageSource);
        getMessageSourceCache().remove(key);
        messageSourceDao.removeFromCache(messageSource);
    }


}
