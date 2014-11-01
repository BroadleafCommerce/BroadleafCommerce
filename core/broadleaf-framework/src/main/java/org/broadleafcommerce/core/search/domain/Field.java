/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.search.domain;

import org.broadleafcommerce.core.search.domain.solr.FieldType;

import java.util.List;

/**
 * Represents a String-based mapping of entities and properties. This is used in various places,
 * including search facets and report fields. 
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface Field {
    
    /**
     * Gets the id
     * @return the id
     */
    public Long getId();

    /**
     * Sets the id
     * @param id 
     */
    public void setId(Long id);

    /**
     * Gets the entityType of this Field
     * @return the entityType
     */
    public FieldEntity getEntityType();

    /**
     * Sets the entityType 
     * @param entityType 
     */
    public void setEntityType(FieldEntity entityType);

    /**
     * Gets the propertyName of this Field. This would be something like "manufacturer" or "defaultSku.price"
     * if the EntityType was "product"
     * @return the propertyName
     */
    public String getPropertyName();

    /**
     * Sets the propertyName
     * @param propertyName
     */
    public void setPropertyName(String propertyName);

    /**
     * Gets the abbreviation of this Field. This will be used in URL query string parameters for sorting and
     * filtering
     * @return the abbreviation
     */
    public String getAbbreviation();

    /**
     * Sets the abbreviation
     * @param abbreviation
     */
    public void setAbbreviation(String abbreviation);

    /**
     * Gets the searchable flag
     * @return whether or not this Field is searchable
     */
    public Boolean getSearchable();

    /** 
     * Sets the searchable flag
     * @param searchable
     */
    public void setSearchable(Boolean searchable);

    /**
     * Sets the facet field type
     * @param facetFieldType
     */
    public void setFacetFieldType(FieldType facetFieldType);

    /**
     * Gets the facet field type. Note that the facet field type is also the type used to perform sorting.
     * Any field where there is a desire to facet or sort on should have this FieldType specified.
     * 
     * @see #getSearchableFieldTypes()
     * @return the facet field type
     */
    public FieldType getFacetFieldType();

    /**
     * Sets the searchableFieldTypes
     * 
     * @see #getSearchableFieldTypes()
     * @param searchableFieldTypes
     */
    public void setSearchableFieldTypes(List<FieldType> searchableFieldTypes);

    /**
     * Gets the dynamic searchable field types. For example, in solr, if you wanted to index a field as both
     * text and string, you would have two searchable field types, String and Text
     * 
     * @return the searchable types for this field
     */
    public List<FieldType> getSearchableFieldTypes();
    
    /**
     * Gets the searchConfigs. Note that a concrete implementation or usage of this class is not available 
     * in the community version of Broadleaf Commerce.
     * @return the searchConfigs
     */
    public List<SearchConfig> getSearchConfigs();
    
    /**
     * Sets the searchConfigs. 
     * @param searchConfigs
     */
    public void setSearchConfigs (List<SearchConfig> searchConfigs);

    /**
     * Returns the qualified name of this Field. The default implementation returns the entityType joined
     * with the properName by a "."
     * @return the qualifiedFieldName
     */
    public String getQualifiedFieldName();

    /**
     * Returns whether or not this field should be considered translatable
     * @return translatable
     */
    public Boolean getTranslatable();
    
    /**
     * Sets whether or not this field should be considered translatable
     * @param translatable
     */
    public void setTranslatable(Boolean translatable);
}
