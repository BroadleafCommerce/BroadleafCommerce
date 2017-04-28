/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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

package org.broadleafcommerce.openadmin.dto;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.common.util.BLCCollectionUtils;
import org.broadleafcommerce.common.util.TypedPredicate;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.RestrictionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterAndSortCriteria {

    private static final long serialVersionUID = 1L;

    public static final String SORT_PROPERTY_PARAMETER = "sortProperty";
    public static final String SORT_DIRECTION_PARAMETER = "sortDirection";
    public static final String START_INDEX_PARAMETER = "startIndex";
    public static final String MAX_INDEX_PARAMETER = "maxIndex";
    public static final String LAST_ID_PARAMETER = "lastId";
    public static final String FIRST_ID_PARAMETER = "firstId";
    public static final String UPPER_COUNT_PARAMETER = "upperCount";
    public static final String LOWER_COUNT_PARAMETER = "lowerCount";
    public static final String PAGE_SIZE_PARAMETER = "pageSize";

    public static final String IS_NULL_FILTER_VALUE = new String("BLC_SPECIAL_FILTER_VALUE:NULL").intern();
    public static final String IS_NOT_NULL_FILTER_VALUE = new String("BLC_SPECIAL_FILTER_VALUE:NOT_NULL").intern();

    protected String propertyId;
    protected List<String> filterValues = new ArrayList<String>();
    protected RestrictionType restrictionType;
    /**
     * for "order", a null value is relevant, meaning that this field moves to the end of any sort order consideration
     * (this is the opposite of what we would achieve, by having an uninitialized int variable set to 0)
     */
    protected Integer order;

    protected SortDirection sortDirection;
    protected boolean nullsLast = true;

    public FilterAndSortCriteria(String propertyId, int order) {
        this.propertyId = propertyId;
        this.order = order;
    }

    public FilterAndSortCriteria(String propertyId) {
        this.propertyId = propertyId;
    }

    public FilterAndSortCriteria(String propertyId, String filterValue) {
        this.propertyId = propertyId;
        setFilterValue(filterValue);
    }

    public FilterAndSortCriteria(String propertyId, String filterValue, int order) {
        this.propertyId = propertyId;
        this.order = order;
        setFilterValue(filterValue);
    }

    public FilterAndSortCriteria(String propertyId, List<String> filterValues, int order) {
        setPropertyId(propertyId);
        this.order = order;
        setFilterValues(filterValues);
    }

    public FilterAndSortCriteria(String propertyId, List<String> filterValues) {
        setPropertyId(propertyId);
        setFilterValues(filterValues);
    }

    public FilterAndSortCriteria(String propertyId, List<String> filterValues, SortDirection sortDirection, int order) {
        this.order = order;
        setPropertyId(propertyId);
        setFilterValues(filterValues);
        setSortDirection(sortDirection);
    }

    public FilterAndSortCriteria(String propertyId, List<String> filterValues, SortDirection sortDirection) {
        setPropertyId(propertyId);
        setFilterValues(filterValues);
        setSortDirection(sortDirection);
    }

    public FilterAndSortCriteria(String propertyId, String[] filterValues, int order) {
        this.propertyId = propertyId;
        this.order = order;
        setFilterValues(Arrays.asList(filterValues));
    }

    public FilterAndSortCriteria(String propertyId, String[] filterValues) {
        this.propertyId = propertyId;
        setFilterValues(Arrays.asList(filterValues));
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
        addFilterValue(value);
    }

    public void addFilterValue(String value) {
        filterValues.add(value);
    }

    public List<String> getFilterValues() {
        // We want values that are NOT special
        return BLCCollectionUtils.selectList(filterValues, getPredicateForSpecialValues(false));
    }

    public List<String> getSpecialFilterValues() {
        // We want values that ARE special
        return BLCCollectionUtils.selectList(filterValues, getPredicateForSpecialValues(true));
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

    public boolean hasSpecialFilterValue() {
        // We want values that ARE special
        return CollectionUtils.exists(filterValues, getPredicateForSpecialValues(true));
    }

    public RestrictionType getRestrictionType() {
        return restrictionType;
    }

    /**
     * Useful when you want to explicitly define the type of pre-built {@link org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.Restriction}
     * instance to be used. The available, pre-built restrictions are defined in the Spring configured map "blRestrictionFactoryMap".
     *
     * @param restrictionType
     */
    public void setRestrictionType(RestrictionType restrictionType) {
        this.restrictionType = restrictionType;
    }

    protected TypedPredicate<String> getPredicateForSpecialValues(final boolean inclusive) {
        return new TypedPredicate<String>() {

            @Override
            public boolean eval(String value) {
                // Note that this static String is the result of a call to String.intern(). This means that we are
                // safe to compare with == while still allowing the user to specify a filter for the actual value of this
                // string.
                if (inclusive) {
                    return IS_NULL_FILTER_VALUE == value || IS_NOT_NULL_FILTER_VALUE == value;
                } else {
                    return IS_NULL_FILTER_VALUE != value && IS_NOT_NULL_FILTER_VALUE != value;
                }
            }
        };
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public boolean isNullsLast() {
        return nullsLast;
    }

    public void setNullsLast(boolean nullsLast) {
        this.nullsLast = nullsLast;
    }
}
