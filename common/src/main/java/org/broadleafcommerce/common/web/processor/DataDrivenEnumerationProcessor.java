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
package org.broadleafcommerce.common.web.processor;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumeration;
import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumerationValue;
import org.broadleafcommerce.common.enumeration.service.DataDrivenEnumerationService;
import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;


/**
 * Processor that adds a list of {@link DataDriveEnumerationValue}s onto the model for a particular key.
 *  This will add a new variable on the model called 'enumValues'
 *
 * @param key (required) key for the {@link DataDrivenEnumeration} that the {@link DataDrivenEnumerationValue}s should be
 * apart of. This corresponds to {@link DataDrivenEnumeration#getKey()}.
 * 
 * @param sort (optional) <i>ASCENDING</i> or <i>DESCENDING</i> if the resulting values should be sorted by not. The sort will be on
 *          {@link DataDrivenEnumerationValue#getDisplay()}
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public class DataDrivenEnumerationProcessor extends AbstractModelVariableModifierProcessor {

    @Resource(name = "blDataDrivenEnumerationService")
    protected DataDrivenEnumerationService enumService;
    
    /**
     * @param elementName
     */
    public DataDrivenEnumerationProcessor() {
        super("enumeration");
    }

    @Override
    protected void modifyModelAttributes(Arguments arguments, Element element) {
        String key = element.getAttributeValue("key");
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("No 'key' parameter was passed to find enumeration values");
        }
        
        DataDrivenEnumeration ddEnum = enumService.findEnumByKey(key);
        if (ddEnum == null) {
            throw new IllegalArgumentException("Could not find a data driven enumeration keyed by " + key);
        }
        List<DataDrivenEnumerationValue> enumValues = new ArrayList<DataDrivenEnumerationValue>(ddEnum.getEnumValues());
        
        final String sort = element.getAttributeValue("sort");
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
        
        addToModel(arguments, "enumValues", enumValues);
    }

    @Override
    public int getPrecedence() {
        return 1;
    }

}
