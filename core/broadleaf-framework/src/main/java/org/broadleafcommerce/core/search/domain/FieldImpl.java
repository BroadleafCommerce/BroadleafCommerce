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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FIELD")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
public class FieldImpl implements Field {
	
    @Id
    @GeneratedValue(generator = "FieldId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "FieldId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "FieldImpl", allocationSize = 50)
    @Column(name = "FIELD_ID")
    protected Long id;
    
    // This is a broadleaf enumeration
    @Column(name = "ENTITY_TYPE", nullable = false)
    protected String entityType;
    
    @Column(name = "PROPERTY_NAME", nullable = false)
    protected String propertyName;
    
    @Column(name = "ABBREVIATION", unique = true)
    protected String abbreviation;
    
    @Column(name =  "SEARCHABLE")
    protected Boolean searchable = false;
    
    // This is a broadleaf enumeration
    @Column(name =  "FACET_FIELD_TYPE")
    protected String facetFieldType;

    // This is a broadleaf enumeration
	@ElementCollection
    @CollectionTable(name="BLC_FIELD_SEARCH_TYPES", joinColumns=@JoinColumn(name="FIELD_ID"))
    @Column(name="SEARCHABLE_FIELD_TYPE")
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})    
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
	private List<String> searchableFieldTypes = new ArrayList<String>();
    
    @Override
    public String getQualifiedFieldName() {
    	return getEntityType().getFriendlyType() + "." + propertyName;
    }

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public FieldEntity getEntityType() {
		return FieldEntity.getInstance(entityType);
	}

	@Override
	public void setEntityType(FieldEntity entityType) {
		this.entityType = entityType.getType();
	}

	@Override
	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public String getAbbreviation() {
		return abbreviation;
	}

	@Override
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	@Override
	public Boolean getSearchable() {
		return searchable;
	}

	@Override
	public void setSearchable(Boolean searchable) {
		this.searchable = searchable;
	}

	@Override
	public List<SearchConfig> getSearchConfigs() {
		throw new UnsupportedOperationException("The default Field implementation does not support search configs");
	}

	@Override
	public void setSearchConfigs(List<SearchConfig> searchConfigs) {
		throw new UnsupportedOperationException("The default Field implementation does not support search configs");
	}
}
