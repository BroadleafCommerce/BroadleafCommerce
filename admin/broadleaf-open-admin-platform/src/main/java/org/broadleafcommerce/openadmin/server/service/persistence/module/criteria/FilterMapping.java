package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria;

import org.broadleafcommerce.openadmin.dto.SortDirection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jeff Fischer
 */
public class FilterMapping {

    public static final String RANGE_SPECIFIER_REGEX = "->";

    protected String fullPropertyName;
    protected List<String> filterValues = new ArrayList<String>();
    protected SortDirection sortDirection;
    protected Restriction restriction;
    protected FieldPath fieldPath;

    public FilterMapping withFullPropertyName(String fullPropertyName) {
        setFullPropertyName(fullPropertyName);
        return this;
    }

    public FilterMapping withFilterValues(List<String> filterValues) {
        setFilterValues(filterValues);
        return this;
    }

    public FilterMapping withSortDirection(SortDirection sortDirection) {
        setSortDirection(sortDirection);
        return this;
    }

    public FilterMapping withRestriction(Restriction restriction) {
        setRestriction(restriction);
        return this;
    }

    public FilterMapping withFieldPath(FieldPath fieldPath) {
        setFieldPath(fieldPath);
        return this;
    }

    public String getFullPropertyName() {
        return fullPropertyName;
    }

    public void setFullPropertyName(String fullPropertyName) {
        this.fullPropertyName = fullPropertyName;
    }

    public List<String> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(List<String> filterValues) {
        List<String> parsedValues = new ArrayList<String>();
        for (String unfiltered : filterValues) {
            parsedValues.addAll(Arrays.asList(parseFilterValue(unfiltered)));
        }
        this.filterValues.addAll(parsedValues);
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }

    public FieldPath getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(FieldPath fieldPath) {
        this.fieldPath = fieldPath;
    }

    protected String[] parseFilterValue(String filterValue) {
        //We do it this way because the String.split() method will return only a single array member
        //when there is nothing on one side of the delimiter. We want to have two array members (one empty)
        //in this case.
        String[] vals;
        if (filterValue.contains(RANGE_SPECIFIER_REGEX)) {
            vals = new String[]{filterValue.substring(0, filterValue.indexOf(RANGE_SPECIFIER_REGEX)),
                filterValue.substring(filterValue.indexOf(RANGE_SPECIFIER_REGEX) + RANGE_SPECIFIER_REGEX.length(),
                filterValue.length())};
        } else {
            vals = new String[]{filterValue};
        }
        for (int j=0;j<vals.length;j++) {
            vals[j] = vals[j].trim();
        }
        return vals;
    }
}
