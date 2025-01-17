/*-
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Holds the values for custom fields that are part of a <code>StructuredContent</code> item.
 * <br>
 * Each item maintains a list of its custom fields.    The fields associated with an item are
 * determined by the {@link org.broadleafcommerce.cms.field.domain.FieldDefinition}s  associated
 * with the {@link StructuredContentType}.
 *
 * @author bpolster
 * @see StructuredContentType
 * @see org.broadleafcommerce.cms.field.domain.FieldDefinition
 */
public interface StructuredContentField extends Serializable, Cloneable, MultiTenantCloneable<StructuredContentField> {

    /**
     * Gets the primary key.
     *
     * @return the primary key
     */
    @Nullable
    Long getId();

    /**
     * Sets the primary key.
     *
     * @param id the new primary key
     */
    void setId(@Nullable Long id);

    /**
     * Returns the fieldKey associated with this field.   The key used for a
     * <code>StructuredContentField</code> is determined by the associated
     * {@link org.broadleafcommerce.cms.field.domain.FieldDefinition} that was used by the
     * Content Management System to create this instance.
     * <p>
     * As an example, a <code>StructuredContentType</code> might be configured to contain a
     * field definition with a key of "targetUrl".
     *
     * @return the key associated with this item
     * @see org.broadleafcommerce.cms.field.domain.FieldDefinition
     */
    @Nonnull
    String getFieldKey();

    /**
     * Sets the fieldKey.
     *
     * @param fieldKey
     * @see org.broadleafcommerce.cms.field.domain.FieldDefinition
     */
    void setFieldKey(@Nonnull String fieldKey);

    /**
     * Sets the value of this custom field.
     *
     * @return
     */
    @Nonnull
    String getValue();

    /**
     * Returns the value for this custom field.
     *
     * @param value
     */
    void setValue(@Nonnull String value);

    /**
     * @return a deep copy of this object. By default, clones the fieldKey and value fields and ignores the auditable
     * and id fields.
     */
    StructuredContentField clone();

}
