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

package org.broadleafcommerce.openadmin.web.rulebuilder;

import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.ExpressionDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.grouping.Group;
import org.broadleafcommerce.openadmin.web.rulebuilder.grouping.GroupingTranslator;
import org.broadleafcommerce.openadmin.web.rulebuilder.statement.Expression;
import org.broadleafcommerce.openadmin.web.rulebuilder.statement.PhraseTranslator;
import org.hibernate.envers.query.impl.EntitiesAtRevisionQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class RuleBuilderUtil {

    //TODO remove dependency on Supported Field Type which has a dependency on SmartGWT
    protected GroupingTranslator groupingTranslator = new GroupingTranslator();
    protected PhraseTranslator phraseTranslator = new PhraseTranslator();

    public DataWrapper createRuleData(Entity[] entities, String mvelProperty, String quantityProperty)
            throws MVELTranslationException {
        if (entities == null || entities.length == 0 || mvelProperty == null) {
            return null;
        }

        DataWrapper dataWrapper = new DataWrapper();

        for (Entity e : entities) {
            String mvel = null;
            Integer qty = null;
            for (Property p : e.getProperties()) {
                if (mvelProperty.equals(p.getName())){
                    mvel = p.getValue();
                }

                if (quantityProperty !=null && quantityProperty.equals(p.getName())){
                    qty = Integer.parseInt(p.getValue());
                }
            }

            if (mvel != null) {
                Group group = groupingTranslator.createGroups(mvel);
                DataDTO dataDTO = createRuleDataDTO(group);
                dataDTO.setQuantity(qty);
                dataWrapper.getData().add(dataDTO);
            }
        }

        return dataWrapper;
    }

    public DataDTO createRuleDataDTO(Group group) throws MVELTranslationException {
        if (group.getOperatorType() == null) {
            group.setOperatorType(BLCOperator.AND);
        }

        BLCOperator operator = group.getOperatorType();
        DataDTO data = new DataDTO();
        data.setGroupOperator(operator.name());

        ArrayList<DataDTO> expressionDTOList = new ArrayList<DataDTO>();
        int j=0;
        for (String phrase : group.getPhrases()) {
            appendExpression(phrase, expressionDTOList);
            j++;
        }

        data.setGroups(expressionDTOList);
        return data;
    }

    public void appendExpression(String phrase, List<DataDTO> expressionDTOList) throws MVELTranslationException {
        Expression expression = phraseTranslator.createExpression(phrase);
        ExpressionDTO expressionDTO = createExpressionDTO(expression);
        expressionDTOList.add(expressionDTO);
    }

    public ExpressionDTO createExpressionDTO(Expression expression) {
        ExpressionDTO expressionDTO = new ExpressionDTO();
        expressionDTO.setName(expression.getField());
        expressionDTO.setOperator(expression.getOperator().name());
        expressionDTO.setValue(expression.getValue());
        return expressionDTO;
    }

    protected BLCOperator[] getBasicBooleanOperators() {
        return new BLCOperator[]{BLCOperator.EQUALS, BLCOperator.NOT_EQUAL, BLCOperator.NOT_NULL, BLCOperator.EQUALS_FIELD, BLCOperator.NOT_EQUAL_FIELD};
    }

    protected BLCOperator[] getBasicDateOperators() {
        return new BLCOperator[]{BLCOperator.EQUALS, BLCOperator.GREATER_OR_EQUAL, BLCOperator.GREATER_THAN, BLCOperator.NOT_EQUAL, BLCOperator.LESS_OR_EQUAL, BLCOperator.LESS_THAN, BLCOperator.NOT_NULL, BLCOperator.EQUALS_FIELD, BLCOperator.GREATER_OR_EQUAL_FIELD, BLCOperator.GREATER_THAN_FIELD, BLCOperator.LESS_OR_EQUAL_FIELD, BLCOperator.LESS_THAN_FIELD, BLCOperator.NOT_EQUAL_FIELD, BLCOperator.BETWEEN, BLCOperator.BETWEEN_INCLUSIVE};
    }

    protected BLCOperator[] getBasicNumericOperators() {
        return new BLCOperator[]{BLCOperator.EQUALS, BLCOperator.GREATER_OR_EQUAL, BLCOperator.GREATER_THAN, BLCOperator.NOT_EQUAL, BLCOperator.LESS_OR_EQUAL, BLCOperator.LESS_THAN, BLCOperator.NOT_NULL, BLCOperator.EQUALS_FIELD, BLCOperator.GREATER_OR_EQUAL_FIELD, BLCOperator.GREATER_THAN_FIELD, BLCOperator.LESS_OR_EQUAL_FIELD, BLCOperator.LESS_THAN_FIELD, BLCOperator.NOT_EQUAL_FIELD, BLCOperator.IN_SET, BLCOperator.NOT_IN_SET, BLCOperator.BETWEEN, BLCOperator.BETWEEN_INCLUSIVE};
    }

    protected BLCOperator[] getBasicTextOperators() {
        return new BLCOperator[]{BLCOperator.CONTAINS, BLCOperator.NOT_CONTAINS, BLCOperator.STARTS_WITH, BLCOperator.ENDS_WITH, BLCOperator.NOT_STARTS_WITH, BLCOperator.NOT_ENDS_WITH, BLCOperator.EQUALS, BLCOperator.NOT_EQUAL, BLCOperator.NOT_NULL, BLCOperator.EQUALS_FIELD, BLCOperator.NOT_EQUAL_FIELD};
    }

    protected BLCOperator[] getBasicEnumerationOperators() {
        return new BLCOperator[]{BLCOperator.EQUALS, BLCOperator.NOT_EQUAL, BLCOperator.NOT_NULL, BLCOperator.EQUALS_FIELD, BLCOperator.NOT_EQUAL_FIELD};
    }

}
