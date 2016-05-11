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
package org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.conversion;

import org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.ConverterNotFoundException;
import org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.conversion.impl
        .BooleanParameterConverter;
import org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.conversion.impl
        .DoubleParameterConverter;
import org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.conversion.impl
        .FloatParameterConverter;
import org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.conversion.impl
        .IntParameterConverter;
import org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.conversion.impl
        .RectangleParameterConverter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("blImageConversionManager")
public class ConversionManager {
    
    protected Map<String, ParameterConverter> converters = new HashMap<String, ParameterConverter>();

    public ConversionManager() {
        converters.put(ParameterTypeEnum.BOOLEAN.toString(), new BooleanParameterConverter());
        converters.put(ParameterTypeEnum.DOUBLE.toString(), new DoubleParameterConverter());
        converters.put(ParameterTypeEnum.FLOAT.toString(), new FloatParameterConverter());
        converters.put(ParameterTypeEnum.INT.toString(), new IntParameterConverter());
        converters.put(ParameterTypeEnum.RECTANGLE.toString(), new RectangleParameterConverter());
    }

    public Parameter convertParameter(String value, String type, Double factor, boolean applyFactor) throws ConverterNotFoundException, ConversionException {
        ParameterConverter converter = converters.get(type);
        if (converter == null) throw new ConverterNotFoundException("Could not find a parameter converter with the type name: " + type);
        return converter.convert(value, factor, applyFactor);
    }
    
    /**
     * @return the converters
     */
    public Map<String, ParameterConverter> getConverters() {
        return converters;
    }

    /**
     * @param converters the converters to set
     */
    public void setConverters(Map<String, ParameterConverter> converters) {
        this.converters = converters;
    }

}
