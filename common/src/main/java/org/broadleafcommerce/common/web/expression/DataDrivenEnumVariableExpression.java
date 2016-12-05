/*
 * #%L
 * BroadleafCommerce Common Libraries
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
/**
 * 
 */
package org.broadleafcommerce.common.web.expression;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumeration;
import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumerationValue;
import org.broadleafcommerce.common.enumeration.service.DataDrivenEnumerationService;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

/**
 * Variable expression that looks up a list of {@link DataDrivenEnumerationValue}s based on its enum key
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blDataDrivenEnumVariableExpression")
@ConditionalOnTemplating
public class DataDrivenEnumVariableExpression implements BroadleafVariableExpression {

    @Resource(name = "blDataDrivenEnumerationService")
    protected DataDrivenEnumerationService enumService;
    
    @Override
    public String getName() {
        return "enumeration";
    }
    
    /**
     * Looks up a list of {@link DataDrivenEnumerationValue} by the {@link DataDrivenEnumeration#getKey()} specified by <b>key</b>
     * @param key lookup for the {@link DataDrivenEnumeration}
     * @return the list of {@link DataDrivenEnumerationValue} for the given <b>key</b>
     */
    public List<DataDrivenEnumerationValue> getEnumValues(String key) {
        return getEnumValues(key, null);
    }
    
    /**
     * Looks up a list of {@link DataDrivenEnumerationValue} by the {@link DataDrivenEnumeration#getKey()} specified by <b>key</b>
     * @param key lookup for the {@link DataDrivenEnumeration}
     * @param sort optional, either 'ASCENDING' or 'DESCENDING' depending on how you want the result list sorted
     * @return the list of {@link DataDrivenEnumerationValue} for the given <b>key</b>
     */
    public List<DataDrivenEnumerationValue> getEnumValues(String key, final String sort) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("No 'key' parameter was passed to find enumeration values");
        }
        
        DataDrivenEnumeration ddEnum = enumService.findEnumByKey(key);
        if (ddEnum == null) {
            throw new IllegalArgumentException("Could not find a data driven enumeration keyed by " + key);
        }
        List<DataDrivenEnumerationValue> enumValues = new ArrayList<>(ddEnum.getEnumValues());
        
        if (StringUtils.isNotEmpty(sort)) {
            Collections.sort(enumValues, new Comparator<DataDrivenEnumerationValue>() {

                @Override
                public int compare(DataDrivenEnumerationValue arg0, DataDrivenEnumerationValue arg1) {
                    if (sort.equals("ASCENDING")) {
                        return arg0.getDisplay().compareTo(arg1.getDisplay());
                    } else {
                        return arg1.getDisplay().compareTo(arg0.getDisplay());
                    }
                }
            });
        }
        return enumValues;
    }

}
