/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.openadmin.server.cto;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.cto.server.FilterAndSortMapping;
import com.anasoft.os.daofusion.cto.server.FilterValueConverter;
import com.anasoft.os.daofusion.cto.server.NestedPropertyCriteriaBasedConverter;
import com.anasoft.os.daofusion.util.FilterValueConverters;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author jfischer
 *
 */
public class BaseCtoConverter extends NestedPropertyCriteriaBasedConverter {
    
    public static final FilterValueConverter<Long> NULL_AWARE_LONG = new FilterValueConverter<Long>() {
        public Long convert(String stringValue) {
            if (stringValue == null || stringValue.equals("null")) {
                return null;
            }
            return Long.valueOf(stringValue);
        }
    };
    
    public static final FilterValueConverter<Integer> NULL_AWARE_INTEGER = new FilterValueConverter<Integer>() {
        public Integer convert(String stringValue) {
            if (stringValue == null || stringValue.equals("null")) {
                return null;
            }
            return Integer.valueOf(stringValue);
        }
    };
    
    public static final FilterValueConverter<BigDecimal> DECIMAL = new FilterValueConverter<BigDecimal>() {
        public BigDecimal convert(String stringValue) {
            if (stringValue == null) {
                return null;
            }
            return new BigDecimal(stringValue);
        }
    };

    public static final FilterValueConverter<Character> CHARACTER = new FilterValueConverter<Character>() {
        public Character convert(String stringValue) {
            if ("true".equals(stringValue)) {
                return 'Y';
            } else if ("false".equals(stringValue)) {
                return 'N';
            }
            return stringValue.charAt(0);
        }
    };
    
    public void addStringLikeMapping(String mappingGroupName, String propertyId,
            AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<String>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.LIKE, FilterValueConverters.STRING));
    }
    
    public void addDecimalMapping(String mappingGroupName, String propertyId,
            AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<BigDecimal>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.BETWEEN, DECIMAL));
    }
    
    public void addLongMapping(String mappingGroupName, String propertyId,
            AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Long>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.BETWEEN, FilterValueConverters.LONG));
    }
    
    public void addLongEQMapping(String mappingGroupName, String propertyId,
            AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Long>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.EQ, FilterValueConverters.LONG));
    }
    
    public void addStringEQMapping(String mappingGroupName, String propertyId,
            AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<String>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.EQ, FilterValueConverters.STRING));
    }
    
    public void addNullMapping(String mappingGroupName, String propertyId,
            AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Long>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.ISNULL, NULL_AWARE_LONG));
    }
    
    public void addBooleanMapping(String mappingGroupName, String propertyId,
            AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Boolean>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.EQ, FilterValueConverters.BOOLEAN));
    }

    public void addCharacterMapping(String mappingGroupName, String propertyId,
            AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Character>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.EQ, CHARACTER));
    }
    
    public void addDateMapping(String mappingGroupName, String propertyId,
            AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Date>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.BETWEEN, new FilterValueConverters.DateConverter("yyyy-MM-dd'T'HH:mm:ss")));
    }
    
    public void addCollectionSizeEqMapping(String mappingGroupName, String propertyId,
            AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Integer>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.COLLECTION_SIZE_EQ, FilterValueConverters.INTEGER));
    }
    
}
