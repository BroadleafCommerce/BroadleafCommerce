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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SEARCH_FACET")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
public class SearchFacetImpl implements SearchFacet {
	
    @Id
    @GeneratedValue(generator = "SearchFacetId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SearchFacetId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SearchFacetImpl", allocationSize = 50)
    @Column(name = "SEARCH_FACET_ID")
    protected Long id;
    
    @Column(name = "FIELD_NAME")
    protected String fieldName;
    
    @Column(name = "LABEL")
    protected String label;
    
    @Column(name =  "SHOW_ON_SEARCH")
    protected Boolean showOnSearch = false;
    
    @Column(name = "SEARCH_DISPLAY_PRIORITY")
    protected Integer searchDisplayPriority = 1;
    
    @OneToMany(mappedBy = "searchFacet", targetEntity = SearchFacetRangeImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected List<SearchFacetRange> searchFacetRanges  = new ArrayList<SearchFacetRange>();

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getFieldName() {
		return fieldName;
	}

	@Override
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public Boolean getShowOnSearch() {
		return showOnSearch;
	}

	@Override
	public void setShowOnSearch(Boolean showOnSearch) {
		this.showOnSearch = showOnSearch;
	}

	@Override
	public Integer getSearchDisplayPriority() {
		return searchDisplayPriority;
	}

	@Override
	public void setSearchDisplayPriority(Integer searchDisplayPriority) {
		this.searchDisplayPriority = searchDisplayPriority;
	}

	@Override
	public List<SearchFacetRange> getSearchFacetRanges() {
		return searchFacetRanges;
	}

	@Override
	public void setSearchFacetRanges(List<SearchFacetRange> searchFacetRanges) {
		this.searchFacetRanges = searchFacetRanges;
	}
	
	
    
}
