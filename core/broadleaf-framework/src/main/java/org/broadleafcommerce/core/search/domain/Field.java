/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.search.domain;

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
}