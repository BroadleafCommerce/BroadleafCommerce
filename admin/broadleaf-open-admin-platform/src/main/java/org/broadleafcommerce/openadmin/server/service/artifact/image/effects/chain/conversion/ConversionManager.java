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
