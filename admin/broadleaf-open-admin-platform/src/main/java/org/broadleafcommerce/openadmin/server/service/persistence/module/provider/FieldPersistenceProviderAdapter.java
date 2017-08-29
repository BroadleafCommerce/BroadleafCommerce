/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddFilterPropertiesRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddSearchMappingRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class FieldPersistenceProviderAdapter extends AbstractFieldPersistenceProvider {

    @Override
    public MetadataProviderResponse addSearchMapping(AddSearchMappingRequest addSearchMappingRequest, List<FilterMapping> filterMappings) {
        return MetadataProviderResponse.NOT_HANDLED;
    }

    @Override
    public MetadataProviderResponse populateValue(PopulateValueRequest populateValueRequest, Serializable instance) {
        return MetadataProviderResponse.NOT_HANDLED;
    }

    @Override
    public MetadataProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property) {
        return MetadataProviderResponse.NOT_HANDLED;
    }

    @Override
    public MetadataProviderResponse filterProperties(AddFilterPropertiesRequest addFilterPropertiesRequest, Map<String, FieldMetadata> properties) {
        return MetadataProviderResponse.NOT_HANDLED;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    protected boolean checkDirtyState(PopulateValueRequest request, Object instance, Object checkValue) throws Exception {
        boolean dirty = isFieldDirty(request, instance, checkValue);
        boolean hasDefaultValue = !StringUtils.isEmpty(request.getMetadata().getDefaultValue());

        return (!request.getPreAdd() || hasDefaultValue) && dirty;
    }

    protected boolean isFieldDirty(PopulateValueRequest request, Object instance, Object checkValue) throws IllegalAccessException, FieldNotAvailableException {
        boolean dirty = !(instance == null && checkValue == null) && (instance == null || checkValue == null);
        if (!dirty) {
            Object value = request.getFieldManager().getFieldValue(instance, request.getProperty().getName());
            if (checkValue instanceof String) {
                checkValue = ((String) checkValue).trim();
            }
            if (value instanceof String) {
                value = ((String) value).trim();
            }
            if (value instanceof BigDecimal) {
                BigDecimal origValue = (BigDecimal) value;
                BigDecimal newValue = (BigDecimal) checkValue;
                //set the scale of one of the BigDecimal values to the larger of the two scales
                if (newValue.scale() < origValue.scale()) {
                    checkValue = newValue.setScale(origValue.scale(), RoundingMode.UNNECESSARY);
                } else if (origValue.scale() < newValue.scale()) {
                    value = origValue.setScale(newValue.scale(), RoundingMode.UNNECESSARY);
                }
            }
            dirty = value == null || !value.equals(checkValue);
        }
        return dirty;
    }

    protected void setNonDisplayableValues(PopulateValueRequest request) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        MessageSource messages = context.getMessageSource();
        String label = "(" + messages.getMessage("Workflow_not_displayable", null, "Not Displayable", context.getJavaLocale()) + ")";
        request.getProperty().setDisplayValue(label);
        request.getProperty().setOriginalDisplayValue(label);
    }
}
