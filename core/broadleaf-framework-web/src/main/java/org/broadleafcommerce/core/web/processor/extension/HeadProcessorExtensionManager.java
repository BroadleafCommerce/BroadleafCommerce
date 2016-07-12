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

package org.broadleafcommerce.core.web.processor.extension;

import org.broadleafcommerce.common.web.domain.BroadleafThymeleafContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jerry Ocanas (jocanas)
 */
@Component("blHeadProcessorExtensionManager")
public class HeadProcessorExtensionManager implements HeadProcessorExtensionListener {

    protected List<HeadProcessorExtensionListener> listeners;

    @Override
    public void processAttributeValues(String tagName, Map<String, String> tagAttributes, Map<String, Object> newModelVars, BroadleafThymeleafContext context) {
        if (listeners == null) {
            listeners = new ArrayList<HeadProcessorExtensionListener>();
        }
        for (HeadProcessorExtensionListener listener : listeners) {
            listener.processAttributeValues(tagName, tagAttributes, newModelVars, context);
        }
    }

    public List<HeadProcessorExtensionListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<HeadProcessorExtensionListener> listeners) {
        this.listeners = listeners;
    }
}
