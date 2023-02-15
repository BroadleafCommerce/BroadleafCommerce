/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.dao.provider.metadata.request;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.hibernate.mapping.Property;
import org.hibernate.type.Type;

import java.util.List;

/**
 * Contains the requested Hibernate type, metadata and support classes.
 *
 * @author Jeff Fischer
 */
public class AddMetadataFromMappingDataRequest {

    private final List<Property> componentProperties;
    private final SupportedFieldType type;
    private final SupportedFieldType secondaryType;
    private final Type requestedEntityType;
    private final String propertyName;
    private final MergedPropertyType mergedPropertyType;
    private final DynamicEntityDao dynamicEntityDao;

    public AddMetadataFromMappingDataRequest(List<Property> componentProperties, SupportedFieldType type, SupportedFieldType secondaryType, Type requestedEntityType, String propertyName, MergedPropertyType mergedPropertyType, DynamicEntityDao dynamicEntityDao) {
        this.componentProperties = componentProperties;
        this.type = type;
        this.secondaryType = secondaryType;
        this.requestedEntityType = requestedEntityType;
        this.propertyName = propertyName;
        this.mergedPropertyType = mergedPropertyType;
        this.dynamicEntityDao = dynamicEntityDao;
    }

    public List<Property> getComponentProperties() {
        return componentProperties;
    }

    public SupportedFieldType getType() {
        return type;
    }

    public SupportedFieldType getSecondaryType() {
        return secondaryType;
    }

    public Type getRequestedEntityType() {
        return requestedEntityType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public MergedPropertyType getMergedPropertyType() {
        return mergedPropertyType;
    }

    public DynamicEntityDao getDynamicEntityDao() {
        return dynamicEntityDao;
    }
}
