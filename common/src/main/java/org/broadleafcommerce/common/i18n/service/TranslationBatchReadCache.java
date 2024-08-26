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
package org.broadleafcommerce.common.i18n.service;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.domain.Translation;
import org.broadleafcommerce.common.util.BLCMapUtils;
import org.broadleafcommerce.common.util.TypedClosure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Thread-local cache structure that contains all of the {@link Translation}s for a batch of processing. This is mainly
 * used when executing a search re-index operation. Rather than go to the database for each item being indexed, it makes
 * more sense to go to the database once, cache all of the results here, and then let the {@link TranslationService}
 * use this instead.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class TranslationBatchReadCache {

    public static final String CACHE_NAME = "blBatchTranslationCache";

    protected static Cache getCache() {
        return CacheManager.getInstance().getCache(CACHE_NAME);
    }

    protected static Map<String, Translation> getThreadlocalCache() {
        long threadId = Thread.currentThread().getId();
        Element cacheElement = getCache().get(threadId);
        return cacheElement == null ? null : (Map<String, Translation>) cacheElement.getObjectValue();
    }

    public static void clearCache() {
        long threadId = Thread.currentThread().getId();
        getCache().remove(threadId);
    }

    public static boolean hasCache() {
        return getThreadlocalCache() != null;
    }

    public static void addToCache(List<Translation> translations) {
        long threadId = Thread.currentThread().getId();
        Map<String, Translation> threadlocalCache = getThreadlocalCache();
        if (threadlocalCache == null) {
            threadlocalCache = new HashMap<String, Translation>();
        }

        Map<String, Translation> additionalTranslations = BLCMapUtils.keyedMap(translations, new TypedClosure<String, Translation>() {

            @Override
            public String getKey(Translation translation) {
                return buildCacheKey(translation);
            }
        });

        threadlocalCache.putAll(additionalTranslations);

        getCache().put(new Element(threadId, threadlocalCache));
    }

    public static Translation getFromCache(TranslatedEntity entityType, String id, String propertyName, String localeCode) {
        Map<String, Translation> threadlocalCache = getThreadlocalCache();
        Translation translation = threadlocalCache.get(buildCacheKey(entityType, id, propertyName, localeCode));

        if (translation == null && StringUtils.contains(localeCode, '_')) {
            String languageWithoutCountryCode = localeCode.substring(localeCode.indexOf('_') + 1);
            translation = threadlocalCache.get(buildCacheKey(entityType, id, propertyName, languageWithoutCountryCode));
        }

        return translation;
    }

    protected static String buildCacheKey(Translation translation) {
        return buildCacheKey(translation.getEntityType(),
                translation.getEntityId(),
                translation.getFieldName(),
                translation.getLocaleCode());
    }

    protected static String buildCacheKey(TranslatedEntity entityType, String id, String propertyName, String localeCode) {
        return StringUtils.join(new String[]{entityType.getType(), id, propertyName, localeCode}, "-");
    }

}
