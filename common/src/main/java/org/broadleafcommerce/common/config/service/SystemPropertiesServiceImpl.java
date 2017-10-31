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
package org.broadleafcommerce.common.config.service;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
import org.broadleafcommerce.common.config.dao.SystemPropertiesDao;
import org.broadleafcommerce.common.config.domain.SystemProperty;
import org.broadleafcommerce.common.config.service.type.SystemPropertyFieldType;
import org.broadleafcommerce.common.extensibility.jpa.SiteDiscriminator;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Service that retrieves property settings from the database.   If not set in 
 * the DB then returns the value from property files.
 *  
 * @author bpolster
 */
@Service("blSystemPropertiesService")
public class SystemPropertiesServiceImpl implements SystemPropertiesService{

    public static final String PROPERTY_SOURCE_NAME = "systemPropertySource";
    protected static final String ENV_CACHE_PREFIX = "ORIGIN_FROM_ENV";

    /**
     * If the property resoltion comes from the Spring Environment I don't want to try to re-resolve a property from the Environment. This
     * ensures that we don't get a StackOverflowException
     */
    protected static final ThreadLocal<Boolean> originatedFromEnvironment = ThreadLocalManager.createThreadLocal(Boolean.class, false);

    private static final String NULL_RESPONSE = "*NULL_RESPONSE*";

    protected Cache systemPropertyCache;

    @Resource(name="blSystemPropertiesDao")
    protected SystemPropertiesDao systemPropertiesDao;

    @Resource(name = "blSystemPropertyServiceExtensionManager")
    protected SystemPropertyServiceExtensionManager extensionManager;

    @Value("${system.property.cache.timeout}")
    protected int systemPropertyCacheTimeout;

    @Autowired
    protected Environment env;

    @Override
    public String resolveSystemProperty(String name, String defaultValue) {
        String result = resolveSystemProperty(name);
        if (StringUtils.isBlank(result)) {
            return defaultValue;
        }
        return result;
    }
    
    @Override
    public String resolveSystemProperty(String name) {
        if (extensionManager != null) {
            ExtensionResultHolder holder = new ExtensionResultHolder();
            extensionManager.getProxy().resolveProperty(name, holder);
            if (holder.getResult() != null) {
                return holder.getResult().toString();
            }
        }

        String result;
        // We don't want to utilize this cache for sandboxes
        if (BroadleafRequestContext.getBroadleafRequestContext() == null
            || BroadleafRequestContext.getBroadleafRequestContext().getSandBox() == null) {
            result = getPropertyFromCache(name);
        } else {
            result = null;
        }

        if (result != null) {
            return result.equals(NULL_RESPONSE)?null:result;
        }

        SystemProperty property = systemPropertiesDao.readSystemPropertyByName(name);
        boolean envOrigination = BooleanUtils.isTrue(originatedFromEnvironment.get());
        if (property == null || StringUtils.isEmpty(property.getValue())) {
            if (envOrigination) {
                result = null;
            } else {
                result = getPropertyFromCache(name);
                if (result == null) {
                    result = env.getProperty(name);
                }
            }
        } else {
            if ("_blank_".equals(property.getValue())) {
                result = "";
            } else {
                result = property.getValue();
            }
        }

        if (result == null) {
            result = NULL_RESPONSE;
        }
        addPropertyToCache(name, result);
        return result.equals(NULL_RESPONSE)?null:result;
    }

    protected void addPropertyToCache(String propertyName, String propertyValue) {
        String key = buildKey(propertyName);
        if (systemPropertyCacheTimeout < 0) {
            getSystemPropertyCache().put(new Element(key, propertyValue));
        } else {
            getSystemPropertyCache().put(new Element(key, propertyValue, systemPropertyCacheTimeout, 
                    systemPropertyCacheTimeout));
        }
    }

    protected String getPropertyFromCache(String propertyName) {
        String key = buildKey(propertyName);
        Element cacheElement = getSystemPropertyCache().get(key);
        if (cacheElement != null && cacheElement.getObjectValue() != null) {
            return (String) cacheElement.getObjectValue();
        }
        return null;
    }

    /**
     * Properties can vary by site.   If a site is found on the request, use the site id as part of the
     * cache-key.
     *
     * @param propertyName
     * @return
     */
    protected String buildKey(String propertyName) {
        Long siteId = null;
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null) {
            if (brc.getSite() != null) {
                siteId = brc.getSite().getId();
            }
        }
        return buildKey(propertyName, siteId);
    }

    /**
     * Properties can vary by site.   If a site is found on the request, use the site id as part of the
     * cache-key.
     * 
     * @param systemProperty
     * @return
     */
    protected String buildKey(SystemProperty systemProperty) {
        String propertyName = systemProperty.getName();
        Long siteId = null;
        if (systemProperty instanceof SiteDiscriminator && ((SiteDiscriminator) systemProperty).getSiteDiscriminator() != null) {
            siteId = ((SiteDiscriminator) systemProperty).getSiteDiscriminator();
        }
        return buildKey(propertyName, siteId);
    }

    protected String buildKey(String propertyName, Long siteId) {
        String key = propertyName;
        if (siteId != null) {
            key = siteId + "-" + key;
        }

        if (BooleanUtils.isTrue(originatedFromEnvironment.get())) {
            key = ENV_CACHE_PREFIX + "-" + key;
        }

        return key;
    }

    protected Cache getSystemPropertyCache() {
        if (systemPropertyCache == null) {
            systemPropertyCache = CacheManager.getInstance().getCache("blSystemPropertyElements");
        }
        return systemPropertyCache;
    }

    @Override
    public SystemProperty findById(Long id) {
        return systemPropertiesDao.readById(id);
    }
    
    @Override
    public void removeFromCache(SystemProperty systemProperty) {
        //Could have come from a cache invalidation service that does not
        //include the site on the thread, so we should build the key
        //including the site (if applicable) from the systemProperty itself
        String key = buildKey(systemProperty);
        getSystemPropertyCache().remove(key);
        systemPropertiesDao.removeFromCache(systemProperty);
    }

    @Override
    public int resolveIntSystemProperty(String name) {
        String systemProperty = resolveSystemProperty(name, "0");
        return Integer.valueOf(systemProperty).intValue();
    }
    
    @Override
    public int resolveIntSystemProperty(String name, int defaultValue) {
        String systemProperty = resolveSystemProperty(name, Integer.toString(defaultValue));
        return Integer.valueOf(systemProperty).intValue();
    }

    @Override
    public boolean resolveBooleanSystemProperty(String name) {
        String systemProperty = resolveSystemProperty(name, "false");
        return Boolean.valueOf(systemProperty).booleanValue();
    }
    
    @Override
    public boolean resolveBooleanSystemProperty(String name, boolean defaultValue) {
        String systemProperty = resolveSystemProperty(name, Boolean.toString(defaultValue));
        return Boolean.valueOf(systemProperty).booleanValue();
    }

    @Override
    public long resolveLongSystemProperty(String name) {
        String systemProperty = resolveSystemProperty(name, "0");
        return Long.valueOf(systemProperty).longValue();
    }
    
    @Override
    public long resolveLongSystemProperty(String name, long defaultValue) {
        String systemProperty = resolveSystemProperty(name, Long.toString(defaultValue));
        return Long.valueOf(systemProperty).longValue();
    }
    
    @Override
    public boolean isValueValidForType(String value, SystemPropertyFieldType type) {
        if (value == null) {
            return true;
        }

        if (type.equals(SystemPropertyFieldType.BOOLEAN_TYPE)) {
            value = value.toUpperCase();
            if (value != null && (value.equals("TRUE") || value.equals("FALSE"))) {
                return true;
            }
        } else if (type.equals(SystemPropertyFieldType.INT_TYPE)) {
            try {
                Integer.parseInt(value);
                return true;
            } catch (Exception e) {
                // Do nothing - we will fail on validation
            }
        } else if (type.equals(SystemPropertyFieldType.LONG_TYPE)) {
            try {
                Long.parseLong(value);
                return true;
            } catch (Exception e) {
                // Do nothing - we will fail on validation
            }
        } else if (type.equals(SystemPropertyFieldType.DOUBLE_TYPE)) {
            try {
                Double.parseDouble(value);
                return true;
            } catch (Exception e) {
                // Do nothing - we will fail on validation
            }
        } else if (type.equals(SystemPropertyFieldType.STRING_TYPE)) {
            return true;
        }

        return false;
    }
}
