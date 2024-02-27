package org.broadleafcommerce.common.web.dialect;

import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.expression.IExpressionObjectFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

public class BroadleafVariableExpressionObjectFactory implements IExpressionObjectFactory {

    @Resource
    protected List<BroadleafVariableExpression> expressions = new ArrayList<>();

    @Override
    public Set<String> getAllExpressionObjectNames() {
        Set<String> expressionObjectNames = new HashSet<>();
        for (BroadleafVariableExpression expression : expressions) {
            expressionObjectNames.add(expression.getName());
        }
        return expressionObjectNames;
    }

    @Override
    public Object buildObject(IExpressionContext context, String expressionObjectName) {
        if (context instanceof IWebContext || context instanceof IEngineContext) {
            for (BroadleafVariableExpression expression : expressions) {
                if (expressionObjectName.equals(expression.getName())) {
                    return expression;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isCacheable(String expressionObjectName) {
        return true;
    }

}
