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
package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A structured content field template holds the structure for a
 * structured content.
 *
 * For example, an 'Ad' template might describe the fields 'Ad URL' and
 * 'Target URL'.   The 'Ad' template might be used in multiple StructuredContentType
 * instances such as 'Home Page Banner Ad' or 'Cart Bottom Ad', etc.
 *
 * @author bpolster
 */
public interface StructuredContentFieldTemplate extends Serializable, MultiTenantCloneable<StructuredContentFieldTemplate> {

    /**
     * Gets the primary key.
     *
     * @return the primary key
     */
    @Nullable
    public Long getId();


    /**
     * Sets the primary key.
     *
     * @param id the new primary key
     */
    public void setId(@Nullable Long id);

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Nonnull
    String getName();

    /**
     * Sets the name.
     */
    void setName(@Nonnull String name);

    /**
     * Returns the list of the field groups for this template.
     * @return a list of FieldGroups associated with this template
     */
    @Nullable
    List<FieldGroup> getFieldGroups();
    
    List<StructuredContentFieldGroupXref> getFieldGroupXrefs();
    
    void setFieldGroupXrefs(List<StructuredContentFieldGroupXref> fieldGroupXrefs);

    /**
     * Sets the list of field groups for this template.
     * @param fieldGroups
     */
    void setFieldGroups(@Nullable List<FieldGroup> fieldGroups);
}
