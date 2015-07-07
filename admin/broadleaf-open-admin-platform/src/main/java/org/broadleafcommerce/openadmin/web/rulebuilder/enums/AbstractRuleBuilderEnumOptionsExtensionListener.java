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
package org.broadleafcommerce.openadmin.web.rulebuilder.enums;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Abstract extension listener for rule builder enum options that handles the boilerplate code required for setting up
 * the response to the client. This class provides two abstract methods that must be implemented, {@link #getVariableName()}
 * and {@link #getEnumClass()}. Generates a String with the following pattern:
 * 
 * var variableName = [
 *     { label : "enumFriendlyType", name : "enumType" },
 *     { label : "enumFriendlyType2", name : "enumType2" },
 *     ...
 *     { label : "enumFriendlyTypeN", name : "enumTypeN" }
 * ];
 * 
 * @author Andre Azzolini (apazzolini)
 */
public abstract class AbstractRuleBuilderEnumOptionsExtensionListener implements RuleBuilderEnumOptionsExtensionListener {
    
    public String getOptionValues() {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Class<? extends BroadleafEnumerationType>> entry : getValuesToGenerate().entrySet()) {
            try {
                sb.append("var ").append(entry.getKey()).append(" = [");
                
                int i = 0;
                Map<String, ? extends BroadleafEnumerationType> types = getTypes(entry.getValue());
                for (Entry<String, ? extends BroadleafEnumerationType> entry2 : types.entrySet()) {
                    sb.append("{ label : \"" + entry2.getValue().getFriendlyType() + "\"");
                    sb.append(", ");
                    sb.append(" name : \"" + entry2.getValue().getType() + "\" }");
                    if (++i < types.size()) {
                        sb.append(", ");
                    }
                }
                sb.append("]; \r\n");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sb.toString();
    }
    
    @SuppressWarnings("unchecked")
    protected Map<String, ? extends BroadleafEnumerationType> getTypes(Class<? extends BroadleafEnumerationType> clazz) {
        try {
            return (Map<String, ? extends BroadleafEnumerationType>) FieldUtils.readStaticField(clazz, "TYPES", true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * @return a map representing the various values that this extension listener should generate
     */
    protected abstract Map<String, Class<? extends BroadleafEnumerationType>> getValuesToGenerate();

}
