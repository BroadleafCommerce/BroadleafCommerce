/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.common.config.domain;

import org.broadleafcommerce.common.cache.AbstractCacheMissAware;
import org.broadleafcommerce.common.config.dao.SystemPropertiesDaoImpl;
import org.broadleafcommerce.common.config.service.type.SystemPropertyFieldType;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;

/**
 * Class created for caching misses as some cache implementations, such as Redis, are unable to serialize a proxy (see {@link AbstractCacheMissAware} and {@link SystemPropertiesDaoImpl})
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public class NullSystemProperty implements SystemProperty {

    private static final long serialVersionUID = 1L;

    @Override
    public <G extends SystemProperty> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        return null;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long id) {
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {
    }

    @Override
    public Boolean getOverrideGeneratedPropertyName() {
        return null;
    }

    @Override
    public void setOverrideGeneratedPropertyName(Boolean overrideGeneratedPropertyName) {
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public void setValue(String value) {

    }

    @Override
    public SystemPropertyFieldType getPropertyType() {
        return null;
    }

    @Override
    public void setPropertyType(SystemPropertyFieldType type) {
    }

    @Override
    public String getFriendlyName() {
        return null;
    }

    @Override
    public void setFriendlyName(String friendlyName) {
    }

    @Override
    public String getFriendlyGroup() {
        return null;
    }

    @Override
    public void setFriendlyGroup(String friendlyGroup) {
    }

    @Override
    public String getFriendlyTab() {
        return null;
    }

    @Override
    public void setFriendlyTab(String friendlyTab) {
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NullSystemProperty;
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
