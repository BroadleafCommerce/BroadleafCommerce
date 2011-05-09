package org.broadleafcommerce.gwt.server.cto;

import java.util.Date;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.cto.server.FilterAndSortMapping;
import com.anasoft.os.daofusion.cto.server.FilterValueConverter;
import com.anasoft.os.daofusion.cto.server.NestedPropertyCriteriaBasedConverter;
import com.anasoft.os.daofusion.util.FilterValueConverters;

public class BaseCtoConverter extends NestedPropertyCriteriaBasedConverter {
	
	public static final FilterValueConverter<Long> NULL_AWARE_LONG = new FilterValueConverter<Long>() {
        public Long convert(String stringValue) {
        	if (stringValue == null) {
        		return null;
        	}
            return Long.valueOf(stringValue);
        }
    };
    
    public static final FilterValueConverter<Double> DECIMAL = new FilterValueConverter<Double>() {
        public Double convert(String stringValue) {
        	if (stringValue == null) {
        		return null;
        	}
            return Double.valueOf(stringValue);
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
        addMapping(mappingGroupName, new FilterAndSortMapping<Double>(
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
