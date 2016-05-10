/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.cache;

import org.thymeleaf.Template;
import org.thymeleaf.cache.AbstractCacheManager;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.StandardCache;
import org.thymeleaf.cache.StandardCacheManager;
import org.thymeleaf.dom.Node;

import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

/**
 * Implementation of {@link org.thymeleaf.cache.AbstractCacheManager} to use {@link BLCICache} for templates.
 * This class heavily leverages {@link org.thymeleaf.cache.StandardCacheManager} functionality. Only the
 * initializeTemplateCache() method should behave differently by instantiating a BLCICache instead of a StandardCache.
 *
 * @author Chad Harchar (charchar)
 */
public class BLCICacheManager extends AbstractCacheManager {

    @Resource(name = "blICacheExtensionManager")
    protected BLCICacheExtensionManager extensionManager;

    protected StandardCacheManager standardCacheManager = new StandardCacheManager();

    /**
     * This method was changed just to return a BLCICache, instead of a StandardCache
     *
     * @return
     */
    @Override
    protected ICache<String, Template> initializeTemplateCache() {
        final int maxSize = standardCacheManager.getTemplateCacheMaxSize();
        if (maxSize == 0) {
            return null;
        }
        return new BLCICache<String, Template>(
                standardCacheManager.getTemplateCacheName(), standardCacheManager.getTemplateCacheUseSoftReferences(),
                standardCacheManager.getTemplateCacheInitialSize(), maxSize,
                standardCacheManager.getTemplateCacheValidityChecker(), standardCacheManager.getTemplateCacheLogger(),
                extensionManager);
    }

    /**
     * This method was changed just to use StandardCacheManager methods and should function the same
     *
     * @return
     */
    @Override
    protected final ICache<String, List<Node>> initializeFragmentCache() {
        final int maxSize = standardCacheManager.getFragmentCacheMaxSize();
        if (maxSize == 0) {
            return null;
        }
        return new StandardCache<String, List<Node>>(
                standardCacheManager.getFragmentCacheName(), standardCacheManager.getFragmentCacheUseSoftReferences(),
                standardCacheManager.getFragmentCacheInitialSize(), maxSize,
                standardCacheManager.getFragmentCacheValidityChecker(), standardCacheManager.getFragmentCacheLogger());
    }

    /**
     * This method was changed just to use StandardCacheManager methods and should function the same
     *
     * @return
     */
    @Override
    protected final ICache<String, Properties> initializeMessageCache() {
        final int maxSize = standardCacheManager.getMessageCacheMaxSize();
        if (maxSize == 0) {
            return null;
        }
        return new StandardCache<String, Properties>(
                standardCacheManager.getMessageCacheName(), standardCacheManager.getMessageCacheUseSoftReferences(),
                standardCacheManager.getMessageCacheInitialSize(), maxSize,
                standardCacheManager.getMessageCacheValidityChecker(), standardCacheManager.getMessageCacheLogger());
    }

    /**
     * This method was changed just to use StandardCacheManager methods and should function the same
     *
     * @return
     */
    @Override
    protected final ICache<String, Object> initializeExpressionCache() {
        final int maxSize = standardCacheManager.getExpressionCacheMaxSize();
        if (maxSize == 0) {
            return null;
        }
        return new StandardCache<String, Object>(
                standardCacheManager.getExpressionCacheName(), standardCacheManager.getExpressionCacheUseSoftReferences(),
                standardCacheManager.getExpressionCacheInitialSize(), maxSize,
                standardCacheManager.getExpressionCacheValidityChecker(), standardCacheManager.getExpressionCacheLogger());
    }
}
