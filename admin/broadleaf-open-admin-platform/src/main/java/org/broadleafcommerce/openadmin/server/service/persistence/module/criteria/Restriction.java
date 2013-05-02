package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria;

import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.converter.FilterValueConverter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.PredicateProvider;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jeff Fischer
 */
public class Restriction {

    protected PredicateProvider predicateProvider;
    protected FilterValueConverter filterValueConverter;
    protected FieldPathBuilder fieldPathBuilder = new FieldPathBuilder();

    public Restriction withPredicateProvider(PredicateProvider predicateProvider) {
        setPredicateProvider(predicateProvider);
        return this;
    }

    public Restriction withFilterValueConverter(FilterValueConverter filterValueConverter) {
        setFilterValueConverter(filterValueConverter);
        return this;
    }

    public Restriction withFieldPathBuilder(FieldPathBuilder fieldPathBuilder) {
        setFieldPathBuilder(fieldPathBuilder);
        return this;
    }

    Predicate buildRestriction(CriteriaBuilder builder, From root, String ceilingEntity, String targetPropertyName, Path explicitPath, List<String> directValues) {
        List<Object> convertedValues = new ArrayList<Object>();
        if (filterValueConverter != null) {
            for (String item : directValues) {
                convertedValues.add(filterValueConverter.convert(item));
            }
        } else {
            convertedValues.addAll(directValues);
        }
        return predicateProvider.buildPredicate(builder, fieldPathBuilder, root, ceilingEntity, targetPropertyName,
                explicitPath, convertedValues);
    }

    public FilterValueConverter getFilterValueConverter() {
        return filterValueConverter;
    }

    public void setFilterValueConverter(FilterValueConverter filterValueConverter) {
        this.filterValueConverter = filterValueConverter;
    }

    public PredicateProvider getPredicateProvider() {
        return predicateProvider;
    }

    public void setPredicateProvider(PredicateProvider predicateProvider) {
        this.predicateProvider = predicateProvider;
    }

    public FieldPathBuilder getFieldPathBuilder() {
        return fieldPathBuilder;
    }

    public void setFieldPathBuilder(FieldPathBuilder fieldPathBuilder) {
        this.fieldPathBuilder = fieldPathBuilder;
    }

    public Restriction clone() {
        Restriction temp = new Restriction().withFilterValueConverter(getFilterValueConverter())
                .withPredicateProvider(getPredicateProvider())
                .withFieldPathBuilder(getFieldPathBuilder());
        return temp;
    }
}
