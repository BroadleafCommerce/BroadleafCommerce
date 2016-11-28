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

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.web.deeplink.DeepLink;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;

import java.util.List;
import java.util.Map;

/**
 * Abstract implementation of {@link ContentProcessorExtensionHandler}
 * 
 * @author Andre Azzolini (apazzolini)
 */
public abstract class AbstractContentProcessorExtensionHandler extends AbstractExtensionHandler implements ContentProcessorExtensionHandler {

    @Override
    public ExtensionResultStatusType addAdditionalFieldsToModel(String tagName, Map<String, String> tagAttributes, Map<String, Object> newModelVars, BroadleafTemplateContext context) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType addExtensionFieldDeepLink(List<DeepLink> links, String tagName, Map<String, String> tagAttributes, BroadleafTemplateContext context) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType postProcessDeepLinks(List<DeepLink> links) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

}
