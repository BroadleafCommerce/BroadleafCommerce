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
package org.broadleafcommerce.common.web.expression;

import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.spring4.expression.SpelVariableExpressionEvaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Provides a skeleton to register multiple {@link BroadleafVariableExpression} implementors.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class BroadleafVariableExpressionEvaluator extends SpelVariableExpressionEvaluator {
    
    @Resource(name = "blVariableExpressions")
    protected List<BroadleafVariableExpression> expressions = new ArrayList<BroadleafVariableExpression>();
    
    @Override
    protected Map<String,Object> computeAdditionalExpressionObjects(final IProcessingContext processingContext) {
        Map<String, Object> map = new HashMap<String, Object>();
        
        for (BroadleafVariableExpression expression : expressions) {
            if (!(expression instanceof NullBroadleafVariableExpression)) {
                map.put(expression.getName(), expression);
            }
        }
        
        return map;
    }

}
