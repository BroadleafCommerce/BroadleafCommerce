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

package org.broadleafcommerce.openadmin.client.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Basic client-side persistent entity criteria
 * representation for a single property of the
 * target persistent entity.
 * 
 * <p>
 * 
 * This class is essentially an analogy of server-side
 * property criteria regarding the <em>filtering</em>
 * and <em>sorting</em> functionality.
 * 
 * <p>
 * 
 * Back on the server side, persistent entity property
 * mappings are used to resolve:
 * 
 * <ul>
 *  <li>symbolic persistent entity property identifier
 *      (<tt>propertyId</tt>) into the corresponding
 *      <tt>associationPath</tt> / <tt>targetPropertyName</tt>
 *      combination
 *  <li>array of string-based filter values into
 *      appropriate typed object representations
 *      (integers, dates, etc.)
 * </ul>
 * 
 * Note that the <tt>propertyId</tt> of each
 * {@link FilterAndSortCriteria} instance must be
 * unique within the {@link CriteriaTransferObject}.
 * 
 * @see CriteriaTransferObject
 * 
 * @author vojtech.szocs
 */
public class FilterAndSortCriteria implements IsSerializable, Serializable {

    private static final long serialVersionUID = 1L;

    protected enum SortType {
        ASCENDING, DESCENDING
    }

    protected String propertyId;
    protected List<String> filterValues = new ArrayList<String>();
    
    protected Boolean ignoreCase;
    protected SortType sortType;

    /**
     * @deprecated use sortType instead
     */
    @Deprecated
    protected Boolean sortAscending;

    /**
     * Creates a new persistent entity criteria
     * (for deserialization purposes only).
     */
    public FilterAndSortCriteria() {
        // nothing to do here
    }
    
    /**
     * Creates a new persistent entity criteria.
     * 
     * @param propertyId Symbolic persistent entity property
     * identifier.
     */
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
    
    /**
     * @return Symbolic persistent entity property identifier.
     */
    public String getPropertyId() {
        return propertyId;
    }
    
    /**
     * @param propertyId the propertyId to set
     */
    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    /**
     * @return Array of string-based filter values.
     */
    public String[] getFilterValues() {
        return filterValues.toArray(new String[0]);
    }
    
    /**
     * Clears any filter value(s) set previously
     * to this criteria.
     */
    public void clearFilterValues() {
        filterValues.clear();
    }
    
    /**
     * Sets a single filter value to this criteria,
     * replacing any filter value(s) set previously.
     * 
     * @param value String-based filter value.
     */
    public void setFilterValue(String value) {
        clearFilterValues();
        filterValues.add(value);
    }
    
    /**
     * Sets multiple filter values to this criteria,
     * replacing any filter value(s) set previously.
     * 
     * @param values String-based filter values.
     */
    public void setFilterValues(String... values) {
        clearFilterValues();
        filterValues.addAll(Arrays.asList(values));
    }
    
    /**
     * @param filterValues the filterValues to set
     */
    public void setFilterValues(List<String> filterValues) {
        this.filterValues = filterValues;
    }

    /**
     * @return <tt>true</tt> for ascending, <tt>false</tt>
     * for descending sort order or <tt>null</tt> to disable
     * the sorting functionality.
     */
    public Boolean getSortAscending() {
        return (sortType == null) ? null : SortType.ASCENDING.equals(sortType);
    }

    @Deprecated
    public void setSortAscending(Boolean sortAscending) {
        this.sortAscending = sortAscending;
    }
    
    /**
     * @return the sortType
     */
    public SortType getSortType() {
        return sortType;
    }

    /**
     * @param sortType the sortType to set
     */
    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    /**
     * @return <tt>true</tt> for case-insensitive sorting,
     * <tt>false</tt> for case-sensitive sorting (applicable
     * only when <tt>sortAscending</tt> is not <tt>null</tt>).
     */
    public Boolean getIgnoreCase() {
        return ignoreCase;
    }
    
    /**
     * @param ignoreCase <tt>true</tt> for case-insensitive
     * sorting, <tt>false</tt> for case-sensitive sorting
     * (applicable only when <tt>sortAscending</tt> is not
     * <tt>null</tt>).
     */
    public void setIgnoreCase(Boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

}
