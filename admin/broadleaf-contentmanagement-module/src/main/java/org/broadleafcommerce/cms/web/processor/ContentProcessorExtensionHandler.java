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

package org.broadleafcommerce.cms.web.processor;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.web.deeplink.DeepLink;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;

import java.util.List;
import java.util.Map;

/**
 * Extension handler for the {@link ContentProcessor}
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface ContentProcessorExtensionHandler extends ExtensionHandler {

    /**
     * This method will add any additional attributes to the model that the extension needs
     *
     * @return - ExtensionResultStatusType
     */
    public ExtensionResultStatusType addAdditionalFieldsToModel(String tagName, Map<String, String> tagAttributes, Map<String, Object> newModelVars, BroadleafTemplateContext context);

    /**
     * Provides a hook point for an extension of content processor to optionally add in deep links
     * for a content item based on its extension fields
     * @param links
     * @param arguments
     * @param element
     * @return ExtensionResultStatusType
     */
    public ExtensionResultStatusType addExtensionFieldDeepLink(List<DeepLink> links, String tagName, Map<String, String> tagAttributes, BroadleafTemplateContext context);

    /**
     * Provides a hook point to allow extension handlers to modify the generated deep links.
     * 
     * @param links
     * @return ExtensionResultStatusType
     */
    public ExtensionResultStatusType postProcessDeepLinks(List<DeepLink> links);

}
