/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.web.processor;

import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELTranslationException;
import org.broadleafcommerce.openadmin.web.rulebuilder.RuleBuilderUtil;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.springframework.stereotype.Component;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import java.util.ArrayList;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Component("blAdminRuleBuilderProcessor")
public class AdminRuleBuilderProcessor extends AbstractModelVariableModifierProcessor {

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public AdminRuleBuilderProcessor() {
        super("admin_rule_builder");
    }

    @Override
    public int getPrecedence() {
        return 10001;
    }

    @Override
    protected void modifyModelAttributes(Arguments arguments, Element element) {
        DataWrapper dataWrapper = new DataWrapper();

        String mvelProperty = (String) StandardExpressionProcessor.processExpression(arguments,
                element.getAttributeValue("mvelProperty"));
        String quantityProperty = (String) StandardExpressionProcessor.processExpression(arguments,
                element.getAttributeValue("quantityProperty"));
        Entity[] entities = (Entity[]) StandardExpressionProcessor.processExpression(arguments,
                element.getAttributeValue("entities"));

        if (entities != null && mvelProperty != null) {

            RuleBuilderUtil ruleBuilderUtil = new RuleBuilderUtil();
            try {
                dataWrapper = ruleBuilderUtil.createRuleData(entities, mvelProperty, quantityProperty);
            } catch (MVELTranslationException e) {
                //Do nothing right now
            }
        }

        addToModel(arguments, "dataWrapper", dataWrapper);
    }

}

