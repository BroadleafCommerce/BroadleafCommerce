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
import org.hibernate.annotations.Index;

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

import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SEARCH_FACET_RANGE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
public class SearchFacetRangeImpl implements SearchFacetRange {
	
    @Id
    @GeneratedValue(generator = "SearchFacetRangeId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SearchFacetRangeId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SearchFacetRangeImpl", allocationSize = 50)
    @Column(name = "SEARCH_FACET_RANGE_ID")
    protected Long id;
    
	@ManyToOne(targetEntity = SearchFacetImpl.class, optional = false)
    @JoinColumn(name = "SEARCH_FACET_ID")
    @Index(name="SEARCH_FACET_INDEX", columnNames={"SEARCH_FACET_ID"})
    protected SearchFacet searchFacet = new SearchFacetImpl();
    
    @Column(name = "MIN_VALUE", precision=19, scale=5, nullable = false)
    protected BigDecimal minValue;
    
    @Column(name = "MAX_VALUE", precision=19, scale=5)
    protected BigDecimal maxValue;
    
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
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
	public BigDecimal getMinValue() {
		return minValue;
	}

	@Override
	public void setMinValue(BigDecimal minValue) {
		this.minValue = minValue;
	}

	@Override
	public BigDecimal getMaxValue() {
		return maxValue;
	}

	@Override
	public void setMaxValue(BigDecimal maxValue) {
		this.maxValue = maxValue;
	}
	
}
