/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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

package org.broadleafcommerce.common.rest.api.wrapper;

import org.apache.commons.collections4.CollectionUtils;
import org.broadleafcommerce.common.domain.AdditionalFields;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Base class for APIWrapper implementations to inject the EntityConfiguration reference.
 */
public abstract class BaseWrapper implements ApplicationContextAware {

    @XmlTransient
    protected ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * Utility method.
     * Traverses the domain object's additional fields, and generates a list of MapElementWrappers with them
     * @param model  the domain object
     */
    protected List<MapElementWrapper> createElementWrappers(AdditionalFields model) {

        if (model.getAdditionalFields() != null && !model.getAdditionalFields().isEmpty()) {
            List<MapElementWrapper> mapElementWrappers = new ArrayList<MapElementWrapper>();
            for (String key : model.getAdditionalFields().keySet()) {
                MapElementWrapper mapElementWrapper = new MapElementWrapper();
                mapElementWrapper.setKey(key);
                mapElementWrapper.setValue(model.getAdditionalFields().get(key));
                mapElementWrappers.add(mapElementWrapper);
            }
            return mapElementWrappers;
        }
        return null;

    }

    /**
     * Used method, to be used by Wrappers that implement the WrapperAdditionalFields interface.
     * Transfers the additional fields from the wrapper into the domain object
     * @param model
     * @param me
     */
    public void transferAdditionalFieldsFromWrapper(WrapperAdditionalFields from, AdditionalFields to) {
        Map<String, String> destination = new HashMap<String, String>();
        if (CollectionUtils.isNotEmpty(from.getAdditionalFields())) {
            for (MapElementWrapper elem : from.getAdditionalFields()) {
                destination.put(elem.key, elem.value);
            }
        }
        to.setAdditionalFields(destination);
    }

}
