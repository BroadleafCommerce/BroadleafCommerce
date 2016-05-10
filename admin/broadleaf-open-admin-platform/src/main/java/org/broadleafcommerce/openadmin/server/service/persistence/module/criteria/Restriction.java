/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria;

import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.converter.FilterValueConverter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.PredicateProvider;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

/**
 * Responsible for providing a JPA {@link javax.persistence.criteria.Predicate} instance for query construction based
 * on several objects that define query construction behavior for the target field. The objects can be replaced with custom
 * implementations for more specific behavior.
 *
 * @see org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.PredicateProvider
 * @see org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.converter.FilterValueConverter
 * @see org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPathBuilder
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
    
    /**
     * This method  will return a FieldPathBuilder that could be used by the caller to establish any additional Roots that 
     * might be necessary due to the path living inside of a polymorphic version of the ceiling entity. The Predicate 
     * object that {@link #buildRestriction(...)} returns is also available inside of the FieldPathBuilder object for 
     * the caller's use.
     * 
     * @param builder
     * @param root
     * @param ceilingEntity
     * @param targetPropertyName
     * @param explicitPath
     * @param directValues
     * @param shouldConvert
     * @return
     */
    public Predicate buildRestriction(CriteriaBuilder builder, From root, String ceilingEntity, String targetPropertyName, 
            Path explicitPath, List directValues, boolean shouldConvert, CriteriaQuery criteria, List<Predicate> restrictions) {
        fieldPathBuilder.setCriteria(criteria);
        fieldPathBuilder.setRestrictions(restrictions);
        List<Object> convertedValues = new ArrayList<Object>();
        if (shouldConvert && filterValueConverter != null) {
            for (Object item : directValues) {
                String stringItem = (String) item;
                convertedValues.add(filterValueConverter.convert(stringItem));
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
