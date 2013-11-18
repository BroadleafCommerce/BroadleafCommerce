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
package org.broadleafcommerce.openadmin.web.rulebuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.ExpressionDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.grouping.Group;
import org.broadleafcommerce.openadmin.web.rulebuilder.grouping.GroupingTranslator;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldService;
import org.broadleafcommerce.openadmin.web.rulebuilder.statement.Expression;
import org.broadleafcommerce.openadmin.web.rulebuilder.statement.PhraseTranslator;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to convert an MVEL string into a DataWrapper object
 * which can then be serialized on your view.
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class MVELToDataWrapperTranslator {

    private static final Log LOG = LogFactory.getLog(MVELToDataWrapperTranslator.class);

    //TODO remove dependency on Supported Field Type which has a dependency on SmartGWT
    protected GroupingTranslator groupingTranslator = new GroupingTranslator();
    protected PhraseTranslator phraseTranslator = new PhraseTranslator();

    public DataWrapper createRuleData(Entity[] entities, String mvelProperty, String quantityProperty, String idProperty,
            RuleBuilderFieldService fieldService) {
        if (entities == null || entities.length == 0 || mvelProperty == null) {
            return null;
        }

        DataWrapper dataWrapper = new DataWrapper();
        String mvel = null;
        try {
            for (Entity e : entities) {
                Integer qty = null;
                Long id = null;
                for (Property p : e.getProperties()) {
                    if (mvelProperty.equals(p.getName())){
                        mvel = p.getValue();
                    }

                    if (quantityProperty !=null && quantityProperty.equals(p.getName())){
                        qty = Integer.parseInt(p.getValue());
                    }

                    if (idProperty != null && idProperty.equals(p.getName())) {
                        id = Long.parseLong(p.getValue());
                    }
                }

                if (mvel != null) {
                    Group group = groupingTranslator.createGroups(mvel);
                    DataDTO dataDTO = createRuleDataDTO(null, group, fieldService);
                    if (dataDTO != null) {
                        dataDTO.setId(id);
                        dataDTO.setQuantity(qty);
                        dataWrapper.getData().add(dataDTO);
                    }
                }
            }
        } catch (MVELTranslationException e) {
            LOG.error("Unable to translate rule MVEL", e);
            dataWrapper.getData().clear();
            dataWrapper.setError(e.getLocalizedMessage());
            dataWrapper.setRawMvel(mvel);
        }

        return dataWrapper;
    }

    protected DataDTO createRuleDataDTO(DataDTO parentDTO, Group group, RuleBuilderFieldService fieldService)
        throws MVELTranslationException {
        DataDTO data = new DataDTO();
        if (group.getOperatorType() == null) {
            group.setOperatorType(BLCOperator.AND);
        }
        BLCOperator operator = group.getOperatorType();
        data.setGroupOperator(operator.name());
        List<ExpressionDTO> myCriteriaList = new ArrayList<ExpressionDTO>();
        if (BLCOperator.NOT.equals(group.getOperatorType()) && group.getIsTopGroup()) {
            group = group.getSubGroups().get(0);
            group.setOperatorType(operator);
        }
        int j = 0;
        for (String phrase : group.getPhrases()) {
            appendExpression(phrase, fieldService, j, parentDTO, myCriteriaList);
            j++;
        }
        if (myCriteriaList.size() > 0) {
            data.getGroups().addAll(myCriteriaList);
        }
        for (Group subgroup : group.getSubGroups()) {
            DataDTO subCriteria = createRuleDataDTO(data, subgroup, fieldService);
            if (subCriteria != null && !subCriteria.getGroups().isEmpty()) {
                data.getGroups().add(subCriteria);
            }
        }
        if (data.getGroups() != null && !data.getGroups().isEmpty()) {
            return data;
        } else {
            return null;
        }
    }

    public void appendExpression(String phrase, RuleBuilderFieldService fieldService, int count, DataDTO parentDTO,
                                 List<ExpressionDTO> myCriteriaList) throws MVELTranslationException {
        Expression expression = phraseTranslator.createExpression(phrase);
        FieldDTO field = fieldService.getField(expression.getField());
        if (field == null) {
            throw new MVELTranslationException(MVELTranslationException.SPECIFIED_FIELD_NOT_FOUND, "MVEL phrase is not compatible with the RuleBuilderFieldService " +
                    "associated with the current rules builder. Unable to find the field " +
                    "specified: ("+expression.getField()+")");
        }
        SupportedFieldType type = fieldService.getSupportedFieldType(expression.getField());
        ExpressionDTO expressionDTO = createExpressionDTO(expression);

        postProcessCriteria(parentDTO, myCriteriaList, count, expressionDTO, type);
    }

    public ExpressionDTO createExpressionDTO(Expression expression) {
        ExpressionDTO expressionDTO = new ExpressionDTO();
        expressionDTO.setName(expression.getField());
        expressionDTO.setOperator(expression.getOperator().name());
        expressionDTO.setValue(expression.getValue());
        return expressionDTO;
    }

    public boolean isProjection(Object value) {
        String stringValue = value.toString().trim();
        return stringValue.startsWith("[") && stringValue.endsWith("]") && stringValue.indexOf(",") > 0;
    }

    protected void postProcessCriteria(DataDTO parentDTO, List<ExpressionDTO> myCriteriaList,
                                       int count, ExpressionDTO temp, SupportedFieldType type) {
        if (
            count > 0 &&
                temp.getName().equals(myCriteriaList.get(count - 1).getName()) &&
                myCriteriaList.get(count - 1).getOperator().equals(BLCOperator.GREATER_THAN.name()) &&
                temp.getOperator().equals(BLCOperator.LESS_THAN.name())
            ) {
            myCriteriaList.get(count-1).setOperator(BLCOperator.BETWEEN.name());
            String start;
            String end;
            if (type.toString().equals(SupportedFieldType.DATE.toString())) {
                start = myCriteriaList.get(count-1).getValue();
                end = temp.getValue();
            } else {
                start = myCriteriaList.get(count-1).getValue();
                end = temp.getValue();
            }
            myCriteriaList.get(count-1).setStart(start);
            myCriteriaList.get(count-1).setEnd(end);
            myCriteriaList.get(count-1).setValue(null);
            if (parentDTO != null) {
                parentDTO.getGroups().add(myCriteriaList.remove(count-1));
            }
        } else if (
            count > 0 &&
                temp.getName().equals(myCriteriaList.get(count-1).getName()) &&
                myCriteriaList.get(count-1).getOperator().equals(BLCOperator.GREATER_OR_EQUAL.name()) &&
                temp.getOperator().equals(BLCOperator.LESS_OR_EQUAL.name())
            ) {
            myCriteriaList.get(count - 1).setOperator(BLCOperator.BETWEEN_INCLUSIVE.name());
            String start;
            String end;
            if (type.toString().equals(SupportedFieldType.DATE.toString())) {
                start = myCriteriaList.get(count-1).getValue();
                end = temp.getValue();
            } else {
                start = myCriteriaList.get(count-1).getValue();
                end = temp.getValue();
            }
            myCriteriaList.get(count-1).setStart(start);
            myCriteriaList.get(count-1).setEnd(end);
            myCriteriaList.get(count-1).setValue(null);
            if (parentDTO != null) {
                parentDTO.getGroups().add(myCriteriaList.remove(count-1));
            }
        } else if (isProjection(temp.getValue())) {
            if (parentDTO != null) {
                parentDTO.getGroups().add(temp);
            } else {
                myCriteriaList.add(temp);
            }
        } else {
            myCriteriaList.add(temp);
        }
    }

}
