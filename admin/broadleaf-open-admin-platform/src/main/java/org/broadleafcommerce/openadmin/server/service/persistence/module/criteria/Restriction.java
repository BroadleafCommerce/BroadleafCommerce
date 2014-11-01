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
