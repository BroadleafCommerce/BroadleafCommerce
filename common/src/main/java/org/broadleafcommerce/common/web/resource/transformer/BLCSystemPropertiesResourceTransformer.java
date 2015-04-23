/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.common.web.resource.transformer;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.springframework.stereotype.Component;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles Resource "BLC-system-property.js".
 * @see org.broadleafcommerce.common.web.resource.transformer.BLCAbstractResourceTransformer
 * @since 4.0
 */
@Component("blBLCSystemPropertiesTransformer")
public class BLCSystemPropertiesResourceTransformer extends BLCAbstractResourceTransformer {


    protected static final String BLC_SYSTEM_PROPERTY_FILE="BLC-system-property.js";

    @Override
    public String getResourceFileName() {
        return BLC_SYSTEM_PROPERTY_FILE;
    }

    @Override
    protected String generateNewContent(String content) {
        String newContents = content;
        if (StringUtils.isNotBlank(content)) {
            String regexKey = "\\\"BLC_PROP:(.*)\\\"";

            Pattern p = Pattern.compile(regexKey);
            Matcher m = p.matcher(content);
            while (m.find()) {
                String matchedPlaceholder = m.group(0);
                String propertyName = m.group(1);

                String propVal = BLCSystemProperty.resolveSystemProperty(propertyName);
                if (StringUtils.isBlank(propVal)) {
                    propVal = "";
                }

                newContents = newContents.replaceAll(matchedPlaceholder, '"' + propVal + '"');
            }
        }

        return  newContents;
    }
}
