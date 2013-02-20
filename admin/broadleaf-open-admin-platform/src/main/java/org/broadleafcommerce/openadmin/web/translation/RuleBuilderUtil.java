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

package org.broadleafcommerce.openadmin.web.translation;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassTree;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.util.PolymorphicEntityMapUtil;
import org.broadleafcommerce.openadmin.web.translation.dto.ConditionsDTO;
import org.broadleafcommerce.openadmin.web.translation.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.translation.dto.ExpressionDTO;
import org.broadleafcommerce.openadmin.web.translation.dto.FieldDTO;
import org.broadleafcommerce.openadmin.web.translation.dto.OperatorDTO;
import org.broadleafcommerce.openadmin.web.translation.dto.OptionsDTO;
import org.broadleafcommerce.openadmin.web.translation.grouping.Group;
import org.broadleafcommerce.openadmin.web.translation.grouping.GroupingTranslator;
import org.broadleafcommerce.openadmin.web.translation.statement.Expression;
import org.broadleafcommerce.openadmin.web.translation.statement.PhraseTranslator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class RuleBuilderUtil {

    //TODO remove dependency on Supported Field Type which has a dependency on SmartGWT
    protected GroupingTranslator groupingTranslator = new GroupingTranslator();
    protected PhraseTranslator phraseTranslator = new PhraseTranslator();

    public ConditionsDTO createConditionsDTO(String mvel, Property[] properties, ClassTree polymorphicEntities) throws MVELTranslationException {
        if (mvel == null || mvel.length() == 0) {
            return null;
        }

        Group group = groupingTranslator.createGroups(mvel);
        return createConditionsDTO(null, group, properties, polymorphicEntities);
    }

    public ConditionsDTO createConditionsDTO(ConditionsDTO parentDTO, Group group, Property[] properties, ClassTree polymorphicEntities) throws MVELTranslationException {
        ConditionsDTO conditions = new ConditionsDTO();
        for (Property p : properties) {
            appendField(conditions, p, polymorphicEntities);
        }

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

        conditions.setData(data);
        return conditions;
    }

    public void appendField(ConditionsDTO dto, Property property, ClassTree polymorphicEntities) {

        if (property.getMetadata() instanceof BasicFieldMetadata) {
            BasicFieldMetadata metadata = (BasicFieldMetadata) property.getMetadata();
            FieldDTO fieldDTO = new FieldDTO();
            //make field names in the rule builder more specific to their owning entity
            String friendlyName = metadata.getOwningClassFriendlyName();
            if (friendlyName == null || friendlyName.equals("")) {
                String fqcn = metadata.getInheritedFromType();
                PolymorphicEntityMapUtil mapUtil = new PolymorphicEntityMapUtil();

                if (fqcn != null) {
                    friendlyName = mapUtil.convertClassTreeToMap(polymorphicEntities).get(fqcn);
                }
                if (friendlyName == null) {
                    //TODO: ?? fix this
                    //fqcn = ((DynamicEntityDataSource) delegate).getDefaultNewEntityFullyQualifiedClassname();
                    friendlyName = mapUtil.convertClassTreeToMap(polymorphicEntities).get(fqcn);
                }
            }
            if (friendlyName!=null && !property.getName().startsWith(friendlyName) && !friendlyName.contains("DTO")) {
                friendlyName = friendlyName + " - " + property.getName();
            }

            fieldDTO.setLabel(friendlyName);
            fieldDTO.setName(property.getName());
            fieldDTO.setOptions(fetchSupportedOptions(metadata));
            fieldDTO.setOperators(fetchSupportedFieldType(metadata.getFieldType()));
            dto.getFields().add(fieldDTO);
        }
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

    public ArrayList<OptionsDTO> fetchSupportedOptions(BasicFieldMetadata bfm ) {
        //TODO finish this
        ArrayList<OptionsDTO> options = new ArrayList<OptionsDTO>();
        switch (bfm.getFieldType()) {
            case BROADLEAF_ENUMERATION:
                String[][] enumValues = bfm.getEnumerationValues();
                OptionsDTO dto = new OptionsDTO();
                for (int i=0; i<enumValues.length; i++){
                    for (int j=0; j<enumValues[i].length; j++) {
                        dto.setLabel(enumValues[i][j]);
                        dto.setName(enumValues[i][j]);
                        options.add(dto);
                    }
                }

                break;
            default:

        }

        return options;
    }

    public ArrayList<OperatorDTO> fetchSupportedFieldType(SupportedFieldType fieldType) {
        ArrayList<OperatorDTO> operators = new ArrayList<OperatorDTO>();
        BLCOperator[] blcOperators;
        BLCFieldType blcFieldType;

        switch(fieldType) {
            case BOOLEAN:
                blcOperators = getBasicBooleanOperators();
                blcFieldType = BLCFieldType.NONE;
                break;
            case DATE:
                blcOperators = getBasicDateOperators();
                blcFieldType = BLCFieldType.TEXT;
                break;
            case ID:
                blcOperators = getBasicNumericOperators();
                blcFieldType = BLCFieldType.TEXT;
                break;
            case INTEGER:
                blcOperators = getBasicNumericOperators();
                blcFieldType = BLCFieldType.TEXT;
                break;
            case DECIMAL:
                blcOperators = getBasicNumericOperators();
                blcFieldType = BLCFieldType.TEXT;
                break;
            case MONEY:
                blcOperators = getBasicNumericOperators();
                blcFieldType = BLCFieldType.TEXT;
                break;
            case BROADLEAF_ENUMERATION:
                blcOperators = getBasicEnumerationOperators();
                blcFieldType = BLCFieldType.SELECT;
                break;
            case EXPLICIT_ENUMERATION:
                blcOperators = getBasicEnumerationOperators();
                blcFieldType = BLCFieldType.SELECT;
                break;
            case EMPTY_ENUMERATION:
                blcOperators = getBasicEnumerationOperators();
                blcFieldType = BLCFieldType.SELECT;
                break;
            case DATA_DRIVEN_ENUMERATION:
                blcOperators = getBasicEnumerationOperators();
                blcFieldType = BLCFieldType.SELECT;
                break;
            default:
                blcOperators = getBasicTextOperators();
                blcFieldType = BLCFieldType.TEXT;
        }

        for (BLCOperator blcOperator : blcOperators) {
            OperatorDTO operatorDTO = new OperatorDTO();
            operatorDTO.setName(blcOperator.name());
            operatorDTO.setLabel(blcOperator.name());
            operatorDTO.setFieldType(blcFieldType.name());
            operators.add(operatorDTO);
        }

        return operators;
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
