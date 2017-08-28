/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.search.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a field that gets stored in the search index
 * 
 * @author Chad Harchar (charchar)
 */
public interface IndexField extends Serializable, MultiTenantCloneable<IndexField>  {

    /**
     * Gets the id for this search field
     *
     * @return
     */
    public Long getId();

    /**
     * Sets the id for this search field
     *
     * @param id
     */
    public void setId(Long id);
    
    /**
     * Whether or not the user should see results for this field when typing in search terms in the omnibox, or if
     * this is just a field stored in the index (like margin or sorts)
     */
    public Boolean getSearchable();

    public void setSearchable(Boolean searchable);
    
    /**
     * Gets the field for this search field
     *
     * @return
     */
    public Field getField();

    /**
     * Sets the field for this search field
     *
     * @param field
     */
    public void setField(Field field);

    /**
     * Gets the searchable field types for this search field
     *
     * @return
     */
    public List<IndexFieldType> getFieldTypes();

    /**
     * Sets the searchable field types for this search field
     *
     * @param fieldTypes
     */
    public void setFieldTypes(List<IndexFieldType> fieldTypes);
}
