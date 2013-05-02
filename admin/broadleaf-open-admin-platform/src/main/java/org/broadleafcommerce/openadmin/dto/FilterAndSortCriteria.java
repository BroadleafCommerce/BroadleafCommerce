/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.dto;

import java.util.ArrayList;
import java.util.List;


public class FilterAndSortCriteria {

    private static final long serialVersionUID = 1L;

    public static final String SORT_PROPERTY_PARAMETER = "sortProperty";
    public static final String SORT_DIRECTION_PARAMETER = "sortDirection";
    public static final String START_INDEX_PARAMETER = "startIndex";
    public static final String MAX_INDEX_PARAMETER = "maxIndex";

    protected String propertyId;
    protected List<String> filterValues = new ArrayList<String>();

    protected SortDirection sortDirection;

    public FilterAndSortCriteria(String propertyId) {
        this.propertyId = propertyId;
    }
    
    public FilterAndSortCriteria(String propertyId, String filterValue) {
        this.propertyId = propertyId;
        setFilterValue(filterValue);
    }
    
    public FilterAndSortCriteria(String propertyId, List<String> filterValues) {
        this.propertyId = propertyId;
        setFilterValues(filterValues);
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public void clearFilterValues() {
        filterValues.clear();
    }

    public void setFilterValue(String value) {
        clearFilterValues();
        filterValues.add(value);
    }

    public List<String> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(List<String> filterValues) {
        this.filterValues = filterValues;
    }

    public Boolean getSortAscending() {
        return (sortDirection == null) ? null : SortDirection.ASCENDING.equals(sortDirection);
    }

    public void setSortAscending(Boolean sortAscending) {
        this.sortDirection = (sortAscending) ? SortDirection.ASCENDING : SortDirection.DESCENDING;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }

}
