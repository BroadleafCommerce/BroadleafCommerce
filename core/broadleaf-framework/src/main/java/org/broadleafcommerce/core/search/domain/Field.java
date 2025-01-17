/*-
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.search.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a String-based mapping of entities and properties. This is used in various places,
 * including search facets and report fields.
 *
 * @author Andre Azzolini (apazzolini)
 */
public interface Field extends Serializable, MultiTenantCloneable<Field> {

    /**
     * Gets the id
     *
     * @return the id
     */
    Long getId();

    /**
     * Sets the id
     *
     * @param id
     */
    void setId(Long id);

    /**
     * Returns the friendly name of the field, for use by admin or other UI.
     *
     * @return
     */
    String getFriendlyName();

    /**
     * The friendly name of the field, for use by admin or other UI.
     *
     * @param friendlyName
     */
    void setFriendlyName(String friendlyName);

    /**
     * Gets the entityType of this Field
     *
     * @return the entityType
     */
    FieldEntity getEntityType();

    /**
     * Sets the entityType
     *
     * @param entityType
     */
    void setEntityType(FieldEntity entityType);

    /**
     * Gets the propertyName of this Field. This would be something like "manufacturer" or "defaultSku.price"
     * if the EntityType was "product"
     *
     * @return the propertyName
     */
    String getPropertyName();

    /**
     * Sets the propertyName
     *
     * @param propertyName
     */
    void setPropertyName(String propertyName);

    Boolean getOverrideGeneratedPropertyName();

    void setOverrideGeneratedPropertyName(Boolean overrideGeneratedPropertyName);

    /**
     * Gets the abbreviation of this Field. This will be used in URL query string parameters for sorting and
     * filtering
     *
     * @return the abbreviation
     */
    String getAbbreviation();

    /**
     * Sets the abbreviation
     *
     * @param abbreviation
     */
    void setAbbreviation(String abbreviation);

    /**
     * Gets the searchConfigs. Note that a concrete implementation or usage of this class is not available
     * in the community version of Broadleaf Commerce.
     *
     * @return the searchConfigs
     * @deprecated
     */
    @Deprecated
    List<SearchConfig> getSearchConfigs();

    /**
     * Sets the searchConfigs.
     *
     * @param searchConfigs
     * @deprecated
     */
    @Deprecated
    void setSearchConfigs(List<SearchConfig> searchConfigs);

    /**
     * Returns the qualified name of this Field. The default implementation returns the entityType joined
     * with the properName by a "."
     *
     * @return the qualifiedFieldName
     */
    String getQualifiedFieldName();

    /**
     * Returns whether or not this field should be considered translatable
     *
     * @return translatable
     */
    Boolean getTranslatable();

    /**
     * Sets whether or not this field should be considered translatable
     *
     * @param translatable
     */
    void setTranslatable(Boolean translatable);

}
