/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.conversion.impl;

import org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.conversion.ConversionException;
import org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.conversion.Parameter;
import org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.conversion.ParameterConverter;

public class BooleanParameterConverter implements ParameterConverter {

    /* (non-Javadoc)
     * @see com.xpressdocs.email.asset.effects.chain.conversion.ParameterConverter#convert(java.lang.String, double)
     */
    public Parameter convert(String value, Double factor, boolean applyFactor) throws ConversionException {
        Parameter param = new Parameter();
        param.setParameterClass(boolean.class);
        param.setParameterInstance(Boolean.parseBoolean(value));
        
        return param;
    }

}
