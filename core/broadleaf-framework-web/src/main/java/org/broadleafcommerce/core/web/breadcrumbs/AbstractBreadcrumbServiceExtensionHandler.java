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
package org.broadleafcommerce.core.web.breadcrumbs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTO;
import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbServiceExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public abstract class AbstractBreadcrumbServiceExtensionHandler implements ExtensionHandler, BreadcrumbServiceExtensionHandler {

    private static final Log LOG = LogFactory.getLog(AbstractBreadcrumbServiceExtensionHandler.class);

    protected Integer priority;
    protected boolean enabled = true;

    @Override
    public ExtensionResultStatusType modifyBreadcrumbList(String url, Map<String, String[]> params,
            ExtensionResultHolder<List<BreadcrumbDTO>> holder) {
        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }

    @Override
    public int getPriority() {
        if (priority == null) {
            return getDefaultPriority();
        }
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String buildLink(String url, Map<String, String[]> params) {
        URIBuilder builder;
        try {
            builder = new URIBuilder(url);
            if (params != null) {
                for (String key : params.keySet()) {
                    String[] paramValues = params.get(key);
                    for (String value : paramValues) {
                        builder.addParameter(key, value);
                    }
                }
            }
            return builder.build().toString();
        } catch (URISyntaxException e) {
            LOG.error("Error creating link for breadcrumb ", e);
            return url;
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Implementations must provide a priority for this extension handler as the
     * order determines the breadcrumb order.
     * @return
     */
    public abstract int getDefaultPriority();

}
