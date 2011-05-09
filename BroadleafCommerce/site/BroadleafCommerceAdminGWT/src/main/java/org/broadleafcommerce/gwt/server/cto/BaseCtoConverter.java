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
    
    protected void addStringLikeMapping(String mappingGroupName, String propertyId,
            AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<String>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.LIKE, FilterValueConverters.STRING));
    }
    
    protected void addLongMapping(String mappingGroupName, String propertyId,
            AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Long>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.EQ, FilterValueConverters.LONG));
    }
    
    protected void addNullMapping(String mappingGroupName, String propertyId,
            AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Long>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.ISNULL, NULL_AWARE_LONG));
    }
    
    protected void addDateMapping(String mappingGroupName, String propertyId,
            AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Date>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.LE, new FilterValueConverters.DateConverter("MM/dd/yyyy")));
    }
    
    protected void addCollectionSizeEqMapping(String mappingGroupName, String propertyId,
            AssociationPath associationPath, String targetPropertyName) {
        addMapping(mappingGroupName, new FilterAndSortMapping<Integer>(
                propertyId, associationPath, targetPropertyName,
                FilterCriterionProviders.COLLECTION_SIZE_EQ, FilterValueConverters.INTEGER));
    }
    
}
