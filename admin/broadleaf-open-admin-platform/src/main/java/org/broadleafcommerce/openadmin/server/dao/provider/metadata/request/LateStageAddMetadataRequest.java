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
package org.broadleafcommerce.openadmin.server.dao.provider.metadata.request;

import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;

/**
 * Contains the requested field, metadata and support classes.
 *
 * @author Jeff Fischer
 */
public class LateStageAddMetadataRequest {

    private final String fieldName;
    private final Class<?> parentClass;
    private final Class<?> targetClass;
    private final DynamicEntityDao dynamicEntityDao;
    private final String prefix;

    public LateStageAddMetadataRequest(String fieldName, Class<?> parentClass, Class<?> targetClass, DynamicEntityDao dynamicEntityDao, String prefix) {
        this.fieldName = fieldName;
        this.parentClass = parentClass;
        this.targetClass = targetClass;
        this.dynamicEntityDao = dynamicEntityDao;
        this.prefix = prefix;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getParentClass() {
        return parentClass;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public DynamicEntityDao getDynamicEntityDao() {
        return dynamicEntityDao;
    }

    public String getPrefix() {
        return prefix;
    }
}
