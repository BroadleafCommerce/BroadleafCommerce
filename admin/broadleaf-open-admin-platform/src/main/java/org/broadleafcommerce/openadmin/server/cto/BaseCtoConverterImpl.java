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

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.cto.server.FilterAndSortMapping;
import com.anasoft.os.daofusion.cto.server.FilterValueConverter;
import com.anasoft.os.daofusion.cto.server.NestedPropertyCriteriaBasedConverter;
import com.anasoft.os.daofusion.util.FilterValueConverters;

/**
 * 
 * @author jfischer
 *
 */
public class BaseCtoConverterImpl extends NestedPropertyCriteriaBasedConverter implements BaseCtoConverter {
    
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

    public static class NullAwareDateConverter implements FilterValueConverter<Date> {

        private final String dateFormatPattern;

        /**
         * Creates a new converter.
         *
         * @param dateFormatPattern Pattern describing the date format.
         */
        public NullAwareDateConverter(String dateFormatPattern) {
            this.dateFormatPattern = dateFormatPattern;
        }

        /**
         * @return Pattern describing the date format.
         */
        public String getDateFormatPattern() {
            return dateFormatPattern;
        }

        /**
         * @see com.anasoft.os.daofusion.cto.server.FilterValueConverter#convert(java.lang.String)
         */
        public Date convert(String stringValue) {
            return parseDate(stringValue, dateFormatPattern);
        }

        public Date parseDate(String value, String dateFormatPattern) {
            if (value == null) {
                return null;
            }
            try {
                return new SimpleDateFormat(dateFormatPattern).parse(value);
            } catch (ParseException e) {
                throw new RuntimeException("Error while converting '" + value + "' into Date using pattern " + dateFormatPattern, e);
            }
        }

    }
    
    @Override
    public void addStringLikeMapping(String mappingGroupName, String propertyId,
                                     AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<String>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.LIKE, FilterValueConverters.STRING));
    }
    
    @Override
    public void addDecimalMapping(String mappingGroupName, String propertyId,
                                  AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<BigDecimal>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.BETWEEN, DECIMAL));
    }
    
    @Override
    public void addLongMapping(String mappingGroupName, String propertyId,
                               AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Long>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.BETWEEN, FilterValueConverters.LONG));
    }
    
    @Override
    public void addLongEQMapping(String mappingGroupName, String propertyId,
                                 AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Long>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.EQ, FilterValueConverters.LONG));
    }
    
    @Override
    public void addStringEQMapping(String mappingGroupName, String propertyId,
                                   AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<String>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.EQ, FilterValueConverters.STRING));
    }
    
    @Override
    public void addNullMapping(String mappingGroupName, String propertyId,
                               AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Long>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.ISNULL, NULL_AWARE_LONG));
    }

    @Override
    public void addEmptyMapping(String mappingGroupName, String propertyId) {
        addMapping(mappingGroupName, new EmptyNestedPropertyMapping(propertyId));
    }
    
    @Override
    public void addBooleanMapping(String mappingGroupName, String propertyId,
                                  AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Boolean>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.EQ, FilterValueConverters.BOOLEAN));
    }

    @Override
    public void addCharacterMapping(String mappingGroupName, String propertyId,
                                    AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Character>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.EQ, CHARACTER));
    }
    
    @Override
    public void addDateMapping(String mappingGroupName, String propertyId,
                               AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Date>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.BETWEEN_DATE, new NullAwareDateConverter("yyyy-MM-dd'T'HH:mm:ss Z")));
    }
    
    @Override
    public void addCollectionSizeEqMapping(String mappingGroupName, String propertyId,
                                           AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Integer>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.COLLECTION_SIZE_EQ, FilterValueConverters.INTEGER));
    }
    
}
