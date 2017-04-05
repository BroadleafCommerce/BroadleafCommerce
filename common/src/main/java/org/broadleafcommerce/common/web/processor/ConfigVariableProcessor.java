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

import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.expression.PropertiesVariableExpression;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafVariableModifierProcessor;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * <p>
 * Looks up the value of a configuration variable and adds the value to the model.
 * 
 * <p>
 * While this adds the configuration value onto the model, you might want to use the value of this in larger expression. In
 * that instance you may want to use {@link PropertiesVariableExpression} instead with {@code #props.get('property')}.
 * 
 * @parameter name (required) the name of the system property to look up
 * @parameter resultVar (optional) what model variable the system property value is added to, defaults to <b>value</b>
 * 
 * @author bpolster
 * @see {@link PropertiesVariableExpression}
 * @deprecated use {@link PropertiesVariableExpression} instead
 */
@Deprecated
@Component("blConfigVariableProcessor")
@ConditionalOnTemplating
public class ConfigVariableProcessor extends AbstractBroadleafVariableModifierProcessor {

    @Override
    public String getName() {
        return "config";
    }
    
    @Override
    public int getPrecedence() {
        return 10000;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.presentation.dialect.AbstractModelVariableModifierProcessor#populateModelVariables(java.lang.String, java.util.Map, java.util.Map)
     */
    @Override
    public Map<String, Object> populateModelVariables(String tagName, Map<String, String> tagAttributes, BroadleafTemplateContext context) {
        String resultVar = tagAttributes.get("resultVar");
        if (resultVar == null) {
            resultVar = "value";
        }

        String attributeName = tagAttributes.get("name");
        String attributeValue = BLCSystemProperty.resolveSystemProperty(attributeName);
        
        return ImmutableMap.of(resultVar, (Object) attributeValue);
    }

}
