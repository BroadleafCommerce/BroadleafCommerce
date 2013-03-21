/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.server.cto;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.cto.server.FilterAndSortMapping;
import com.anasoft.os.daofusion.cto.server.FilterValueConverter;
import com.anasoft.os.daofusion.cto.server.NestedPropertyCriteriaBasedConverter;
import com.anasoft.os.daofusion.util.FilterValueConverters;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

/**
 * 
 * @author jfischer
 *
 */
@Component("blBaseCtoConverter")
@Scope("prototype")
public class BaseCtoConverterImpl extends NestedPropertyCriteriaBasedConverter implements BaseCtoConverter {
    
    public static final FilterValueConverter<Long> NULL_AWARE_LONG = new FilterValueConverter<Long>() {
        @Override
        public Long convert(String stringValue) {
            if (stringValue == null || stringValue.equals("null")) {
                return null;
            }
            try {
                return Long.valueOf(stringValue);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    };
    
    public static final FilterValueConverter<Integer> NULL_AWARE_INTEGER = new FilterValueConverter<Integer>() {
        @Override
        public Integer convert(String stringValue) {
            if (stringValue == null || stringValue.equals("null")) {
                return null;
            }
            try {
                return Integer.valueOf(stringValue);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    };
    
    public static final FilterValueConverter<BigDecimal> DECIMAL = new FilterValueConverter<BigDecimal>() {
        @Override
        public BigDecimal convert(String stringValue) {
            if (stringValue == null) {
                return null;
            }
            try {
                return new BigDecimal(stringValue);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    };

    public static final FilterValueConverter<Character> CHARACTER = new FilterValueConverter<Character>() {
        @Override
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
        @Override
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
    
    @Resource(name = "blFilterCriterionProviders")
    protected FilterCriterionProviders filterCriterionProviders;

    @Override
    public void addStringLikeMapping(String mappingGroupName, String propertyId,
                                     AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<String>(
                propertyId, associationPath, targetPropertyName,
                filterCriterionProviders.getLikeProvider(associationPath), FilterValueConverters.STRING));
    }
    
    @Override
    public void addDecimalMapping(String mappingGroupName, String propertyId,
                                  AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<BigDecimal>(
                propertyId, associationPath, targetPropertyName,
                filterCriterionProviders.getBetweenProvider(associationPath), DECIMAL));
    }
    
    @Override
    public void addLongMapping(String mappingGroupName, String propertyId,
                               AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Long>(
                propertyId, associationPath, targetPropertyName,
                filterCriterionProviders.getBetweenProvider(associationPath), FilterValueConverters.LONG));
    }
    
    @Override
    public void addLongEQMapping(String mappingGroupName, String propertyId,
                                 AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Long>(
                propertyId, associationPath, targetPropertyName,
                filterCriterionProviders.getEqProvider(associationPath), FilterValueConverters.LONG));
    }
    
    @Override
    public void addStringEQMapping(String mappingGroupName, String propertyId,
                                   AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<String>(
                propertyId, associationPath, targetPropertyName,
                filterCriterionProviders.getEqProvider(associationPath), FilterValueConverters.STRING));
    }
    
    @Override
    public void addNullMapping(String mappingGroupName, String propertyId,
                               AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Long>(
                propertyId, associationPath, targetPropertyName,
                filterCriterionProviders.getIsNullProvider(associationPath), NULL_AWARE_LONG));
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
                filterCriterionProviders.getEqProvider(associationPath), FilterValueConverters.BOOLEAN));
    }

    @Override
    public void addCharacterMapping(String mappingGroupName, String propertyId,
                                    AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Character>(
                propertyId, associationPath, targetPropertyName,
                filterCriterionProviders.getEqProvider(associationPath), CHARACTER));
    }
    
    @Override
    public void addDateMapping(String mappingGroupName, String propertyId,
                               AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Date>(
                propertyId, associationPath, targetPropertyName,
                filterCriterionProviders.getBetweenDateProvider(associationPath), new NullAwareDateConverter("yyyy-MM-dd'T'HH:mm:ss Z")));
    }
    
    @Override
    public void addCollectionSizeEqMapping(String mappingGroupName, String propertyId,
                                           AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Integer>(
                propertyId, associationPath, targetPropertyName,
                filterCriterionProviders.getCollectionSizeEqualsProvider(associationPath), FilterValueConverters.INTEGER));
    }

    public FilterCriterionProviders getFilterCriterionProviders() {
        return filterCriterionProviders;
    }

    public void setFilterCriterionProviders(FilterCriterionProviders filterCriterionProviders) {
        this.filterCriterionProviders = filterCriterionProviders;
    }
    
}
