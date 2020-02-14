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
package org.broadleafcommerce.common.web;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
import org.broadleafcommerce.common.site.domain.Theme;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

/**
 * @author Stanislav Fedorov
 * @see {@link BroadleafThemeResolverFilter}
 */
@Component("blThemeProcessor")
public class BroadleafThemeProcessor extends AbstractBroadleafWebRequestProcessor {

    protected final Log LOG = LogFactory.getLog(getClass());

    @Resource(name = "blThemeResolver")
    protected BroadleafThemeResolver themeResolver;

    @Override
    public void process(WebRequest request) {

        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        Theme originalTheme = brc.getTheme();

        // Note that this must happen after the request context is set up as resolving a theme is dependent on site
        Theme newTheme = themeResolver.resolveTheme(request);

        //Track if the theme changed
        if (originalTheme != null && newTheme != null && ObjectUtils.compare(originalTheme.getId(), newTheme.getId()) != 0) {
            Map<String, Object> properties = brc.getAdditionalProperties();
            properties.put(BroadleafThemeResolver.BRC_THEME_CHANGE_STATUS, true);
        }

        brc.setTheme(newTheme);

    }

    @Override
    public void postProcess(WebRequest request) {
        ThreadLocalManager.remove();
    }

}
