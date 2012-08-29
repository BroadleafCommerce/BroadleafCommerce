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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CAT_SEARCH_FACET_XREF")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
public class CategorySearchFacetImpl implements CategorySearchFacet,Serializable {
	
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "CategorySearchFacetId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "CategorySearchFacetId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "CategorySearchFacetImpl", allocationSize = 50)
    @Column(name = "CATEGORY_SEARCH_FACET_ID")
    protected Long id;
    
	@ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "CATEGORY_ID")
    protected Category category;
    
	@ManyToOne(targetEntity = SearchFacetImpl.class)
    @JoinColumn(name = "SEARCH_FACET_ID")
    protected SearchFacet searchFacet;
    
    @Column(name =  "POSITION")
    protected Integer position;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Category getCategory() {
		return category;
	}

	@Override
	public void setCategory(Category category) {
		this.category = category;
	}

	@Override
	public SearchFacet getSearchFacet() {
		return searchFacet;
	}

	@Override
	public void setSearchFacet(SearchFacet searchFacet) {
		this.searchFacet = searchFacet;
	}

	@Override
	public Integer getPosition() {
		return position;
	}

	@Override
	public void setPosition(Integer position) {
		this.position = position;
	}
    
}
