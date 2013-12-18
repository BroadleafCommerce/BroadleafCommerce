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

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.util.FormatUtil;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.ExpressionDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utility class to convert a DataDTO/ExpressionDTO into an MVEL string
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class DataDTOToMVELTranslator {

    public String createMVEL(String entityKey, DataDTO dataDTO, RuleBuilderFieldService fieldService)
            throws MVELTranslationException {
        StringBuffer sb = new StringBuffer();
        buildMVEL(dataDTO, sb, entityKey, null, fieldService);
        String response = sb.toString().trim();
        if (response.length() == 0) {
            response = null;
        }
        return response;
    }

    protected void buildMVEL(DataDTO dataDTO, StringBuffer sb, String entityKey, String groupOperator,
                             RuleBuilderFieldService fieldService) throws MVELTranslationException {
        BLCOperator operator = null;
        if (dataDTO instanceof ExpressionDTO) {
            operator = BLCOperator.valueOf(((ExpressionDTO) dataDTO).getOperator());
        } else {
            operator = BLCOperator.valueOf(dataDTO.getGroupOperator());
        }
        ArrayList<DataDTO> groups = dataDTO.getGroups();
        if (sb.length() != 0 && sb.charAt(sb.length() - 1) != '(' && groupOperator != null) {
            BLCOperator groupOp = BLCOperator.valueOf(groupOperator);
            switch(groupOp) {
                default:
                    sb.append("&&");
                    break;
                case OR:
                    sb.append("||");
            }
        }
        if (dataDTO instanceof ExpressionDTO) {
            buildExpression((ExpressionDTO)dataDTO, sb, entityKey, operator, fieldService);
        } else {
            boolean includeTopLevelParenthesis = false;
            if (sb.length() != 0 || BLCOperator.NOT.equals(operator) || (sb.length() == 0 && groupOperator != null)) {
                includeTopLevelParenthesis = true;
            }
            if (BLCOperator.NOT.equals(operator)) {
                sb.append("!");
            }
            if (includeTopLevelParenthesis) sb.append("(");
            for (DataDTO dto : groups) {
                buildMVEL(dto, sb, entityKey, dataDTO.getGroupOperator(), fieldService);
            }
            if (includeTopLevelParenthesis) sb.append(")");
        }
    }

    protected void buildExpression(ExpressionDTO expressionDTO, StringBuffer sb, String entityKey,
            BLCOperator operator, RuleBuilderFieldService fieldService)
            throws MVELTranslationException {
        String field = expressionDTO.getName();
        SupportedFieldType type = fieldService.getSupportedFieldType(field);
        SupportedFieldType secondaryType = fieldService.getSecondaryFieldType(field);
        Object[] value;

        if (type == null) {
            throw new MVELTranslationException(MVELTranslationException.SPECIFIED_FIELD_NOT_FOUND, "The DataDTO is not compatible with the RuleBuilderFieldService " +
                    "associated with the current rules builder. Unable to find the field " +
                    "specified: ("+field+")");
        }

        if (
            SupportedFieldType.DATE.toString().equals(type.toString()) &&
                !BLCOperator.CONTAINS_FIELD.equals(operator) &&
                !BLCOperator.ENDS_WITH_FIELD.equals(operator) &&
                !BLCOperator.EQUALS_FIELD.equals(operator) &&
                !BLCOperator.GREATER_OR_EQUAL_FIELD.equals(operator) &&
                !BLCOperator.GREATER_THAN_FIELD.equals(operator) &&
                !BLCOperator.LESS_OR_EQUAL_FIELD.equals(operator) &&
                !BLCOperator.LESS_THAN_FIELD.equals(operator) &&
                !BLCOperator.NOT_EQUAL_FIELD.equals(operator) &&
                !BLCOperator.STARTS_WITH_FIELD.equals(operator) &&
                !BLCOperator.BETWEEN.equals(operator) &&
                !BLCOperator.BETWEEN_INCLUSIVE.equals(operator)
            ) {
            value = extractDate(expressionDTO, operator, "value");
        } else {
            value = extractBasicValues(expressionDTO.getValue());
        }

        switch(operator) {
            case CONTAINS: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".contains",
                        true, false, false, false, false);
                break;
            }
            case CONTAINS_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".contains",
                        true, true, false, false, false);
                break;
            }
            case ENDS_WITH: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".endsWith",
                        true, false, false, false, false);
                break;
            }
            case ENDS_WITH_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".endsWith",
                        true, true, false, false, false);
                break;
            }
            case EQUALS: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, "==", false, false, false, false, false);
                break;
            }
            case EQUALS_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, "==", false, true, false, false, false);
                break;
            }
            case GREATER_OR_EQUAL: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ">=", false, false, false, false, false);
                break;
            }
            case GREATER_OR_EQUAL_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ">=", false, true, false, false, false);
                break;
            }
            case GREATER_THAN: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ">", false, false, false, false, false);
                break;
            }
            case GREATER_THAN_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ">", false, true, false, false, false);
                break;
            }
            case ICONTAINS: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".contains",
                        true, false, true, false, false);
                break;
            }
            case IENDS_WITH: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".endsWith",
                        true, false, true, false, false);
                break;
            }
            case IEQUALS: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, "==", false, false, true, false, false);
                break;
            }
            case INOT_CONTAINS: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".contains",
                        true, false, true, true, false);
                break;
            }
            case INOT_ENDS_WITH: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".endsWith",
                        true, false, true, true, false);
                break;
            }
            case INOT_EQUAL: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, "!=", false, false, true, false, false);
                break;
            }
            case INOT_STARTS_WITH: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".startsWith",
                        true, false, true, true, false);
                break;
            }
            case IS_NULL: {
                buildExpression(sb, entityKey, field, new Object[]{"null"}, type, secondaryType, "==",
                        false, false, false, false, true);
                break;
            }
            case ISTARTS_WITH: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".startsWith",
                        true, false, true, false, false);
                break;
            }
            case LESS_OR_EQUAL: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, "<=", false, false, false, false, false);
                break;
            }
            case LESS_OR_EQUAL_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, "<=", false, true, false, false, false);
                break;
            }
            case LESS_THAN: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, "<", false, false, false, false, false);
                break;
            }
            case LESS_THAN_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, "<",
                        false, true, false, false, false);
                break;
            }
            case NOT_CONTAINS: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".contains",
                        true, false, false, true, false);
                break;
            }
            case NOT_ENDS_WITH: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".endsWith",
                        true, false, false, true, false);
                break;
            }
            case NOT_EQUAL: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, "!=", false, false, false, false, false);
                break;
            }
            case NOT_EQUAL_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, "!=",
                        false, true, false, false, false);
                break;
            }
            case NOT_NULL: {
                buildExpression(sb, entityKey, field, new Object[]{"null"}, type, secondaryType, "!=",
                        false, false, false, false, true);
                break;
            }
            case NOT_STARTS_WITH: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".startsWith",
                        true, false, false, true, false);
                break;
            }
            case STARTS_WITH: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".startsWith",
                        true, false, false, false, false);
                break;
            }
            case STARTS_WITH_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".startsWith",
                        true, true, false, false, false);
                break;
            }
            case COUNT_GREATER_THAN: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".size()>", false, false, false, false, true);
                break;
            }
            case COUNT_GREATER_OR_EQUAL:{
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".size()>=", false, false, false, false, true);
                break;
            }
            case COUNT_LESS_THAN:{
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".size()<", false, false, false, false, true);
                break;
            }
            case COUNT_LESS_OR_EQUAL:{
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".size()<=", false, false, false, false, true);
                break;
            }
            case COUNT_EQUALS:{
                buildExpression(sb, entityKey, field, value, type, secondaryType, ".size()==", false, false, false, false, true);
                break;
            }
            case BETWEEN: {
                if (SupportedFieldType.DATE.toString().equals(type.toString())) {
                    sb.append("(");
                    buildExpression(sb, entityKey, field, extractDate(expressionDTO, BLCOperator.GREATER_THAN, "start"),
                            type, secondaryType, ">", false, false, false, false, false);
                    sb.append("&&");
                    buildExpression(sb, entityKey, field, extractDate(expressionDTO, BLCOperator.LESS_THAN, "end"),
                            type, secondaryType, "<", false, false, false, false, false);
                    sb.append(")");
                } else {
                    sb.append("(");
                    buildExpression(sb, entityKey, field, new Object[]{expressionDTO.getStart()}, type, secondaryType, ">",
                            false, false, false, false, false);
                    sb.append("&&");
                    buildExpression(sb, entityKey, field, new Object[]{expressionDTO.getEnd()}, type, secondaryType, "<",
                            false, false, false, false, false);
                    sb.append(")");
                }
                break;
            }
            case BETWEEN_INCLUSIVE: {
                if (
                        SupportedFieldType.DATE.toString().equals(type.toString())
                        ) {
                    sb.append("(");
                    buildExpression(sb, entityKey, field,
                            extractDate(expressionDTO, BLCOperator.GREATER_OR_EQUAL, "start"), type,
                            secondaryType, ">=", false, false, false, false, false);
                    sb.append("&&");
                    buildExpression(sb, entityKey, field, extractDate(expressionDTO, BLCOperator.LESS_OR_EQUAL, "end"),
                            type, secondaryType, "<=", false, false, false, false, false);
                    sb.append(")");
                } else {
                    sb.append("(");
                    buildExpression(sb, entityKey, field, new Object[]{expressionDTO.getStart()}, type, secondaryType, ">=",
                            false, false, false, false, false);
                    sb.append("&&");
                    buildExpression(sb, entityKey, field, new Object[]{expressionDTO.getEnd()}, type, secondaryType, "<=",
                            false, false, false, false, false);
                    sb.append(")");
                }
                break;
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
    protected Object[] extractDate(ExpressionDTO expressionDTO, BLCOperator operator, String key) {
        String value;

        if ("start".equals(key)) {
            value = expressionDTO.getStart();
        } else if ("end".equals(key)) {
            value = expressionDTO.getEnd();
        } else {
            value = expressionDTO.getValue();
        }

        //TODO handle Date Time Format
//        if (BLCOperator.GREATER_THAN.equals(operator) || BLCOperator.LESS_OR_EQUAL.equals(operator)) {
//            ((Date) value).setHours(23);
//            ((Date) value).setMinutes(59);
//        } else {
//            ((Date) value).setHours(0);
//            ((Date) value).setMinutes(0);
//        }
        return new Object[]{value};
    }

    protected Object[] extractBasicValues(Object value) {
        if (value == null) {
            return null;
        }
        String stringValue = value.toString().trim();
        Object[] response = new Object[]{};
        if (isProjection(value)) {
            List<String> temp = new ArrayList<String>();
            int initial = 1;
            //assume this is a multi-value phrase
            boolean eof = false;
            while (!eof) {
                int end = stringValue.indexOf(",", initial);
                if (end == -1) {
                    eof = true;
                    end = stringValue.length() - 1;
                }
                temp.add(stringValue.substring(initial, end));
                initial = end + 1;
            }
            response = temp.toArray(response);
        } else {
            response = new Object[]{value};
        }
        return response;
    }

    public boolean isProjection(Object value) {
        String stringValue = value.toString().trim();
        return stringValue.startsWith("[") && stringValue.endsWith("]") && stringValue.indexOf(",") > 0;
    }

    protected void buildExpression(StringBuffer sb, String entityKey, String field, Object[] value,
                                   SupportedFieldType type, SupportedFieldType secondaryType, String operator,
                                   boolean includeParenthesis, boolean isFieldComparison, boolean ignoreCase,
                                   boolean isNegation, boolean ignoreQuotes)
            throws MVELTranslationException {

        if (operator.equals("==") && !isFieldComparison && value.length > 1) {
            sb.append("(");
            sb.append("[");
            sb.append(formatValue(field, entityKey, type, secondaryType, value, isFieldComparison,
                    ignoreCase, ignoreQuotes));
            sb.append("] contains ");
            sb.append(formatField(entityKey, type, field, ignoreCase, isNegation));
            if ((type.equals(SupportedFieldType.ID) && secondaryType != null &&
                    secondaryType.equals(SupportedFieldType.INTEGER)) || type.equals(SupportedFieldType.INTEGER)) {
                sb.append(".intValue()");
            }
            sb.append(")");
        } else {
            sb.append(formatField(entityKey, type, field, ignoreCase, isNegation));
            sb.append(operator);
            if (includeParenthesis) {
                sb.append("(");
            }
            sb.append(formatValue(field, entityKey, type, secondaryType, value,
                    isFieldComparison, ignoreCase, ignoreQuotes));
            if (includeParenthesis) {
                sb.append(")");
            }
        }
    }

    protected String buildFieldName(String entityKey, String fieldName) {
        String response = entityKey + "." + fieldName;
        response = response.replaceAll("\\.", ".?");
        return response;
    }

    protected String formatField(String entityKey, SupportedFieldType type, String field,
                                 boolean ignoreCase, boolean isNegation) {
        StringBuilder response = new StringBuilder();
        if (isNegation) {
            response.append("!");
        }
        String convertedField = field;
        boolean isMapField = false;
        if (convertedField.contains(FieldManager.MAPFIELDSEPARATOR)) {
            //This must be a map field, convert the field name to syntax MVEL can understand for map access
            convertedField = convertedField.substring(0, convertedField.indexOf(FieldManager.MAPFIELDSEPARATOR))
                    + "[\"" + convertedField.substring(convertedField.indexOf(FieldManager.MAPFIELDSEPARATOR) +
                    FieldManager.MAPFIELDSEPARATOR.length(), convertedField.length()) + "\"]";
            isMapField = true;
        }
        if (isMapField) {
            switch(type) {
                case BOOLEAN:
                    response.append("MvelHelper.convertField(\"BOOLEAN\",");
                    response.append(buildFieldName(entityKey, convertedField));
                    response.append(")");
                    break;
                case INTEGER:
                    response.append("MvelHelper.convertField(\"INTEGER\",");
                    response.append(buildFieldName(entityKey, convertedField));
                    response.append(")");
                    break;
                case DECIMAL:
                case MONEY:
                    response.append("MvelHelper.convertField(\"DECIMAL\",");
                    response.append(buildFieldName(entityKey, convertedField));
                    response.append(")");
                    break;
                case DATE:
                    response.append("MvelHelper.convertField(\"DATE\",");
                    response.append(buildFieldName(entityKey, convertedField));
                    response.append(")");
                    break;
                case STRING:
                    if (ignoreCase) {
                        response.append("MvelHelper.toUpperCase(");
                    }
                    response.append(buildFieldName(entityKey, convertedField));
                    if (ignoreCase) {
                        response.append(")");
                    }
                    break;
                case STRING_LIST:
                    response.append(buildFieldName(entityKey, convertedField));
                    break;
                default:
                    throw new UnsupportedOperationException(type.toString() + " is not supported for map fields in the rule builder.");
            }
        } else {
            switch(type) {
                case BROADLEAF_ENUMERATION:
                    if (isMapField) {
                        throw new UnsupportedOperationException("Enumerations are not supported for map fields in the rule builder.");
                    } else {
                        response.append(buildFieldName(entityKey, convertedField));
                        response.append(".getType()");
                    }
                    break;
                case MONEY:
                    response.append(buildFieldName(entityKey, convertedField));
                    response.append(".getAmount()");
                    break;
                case STRING:
                    if (ignoreCase) {
                        response.append("MvelHelper.toUpperCase(");
                    }
                    response.append(buildFieldName(entityKey, convertedField));
                    if (ignoreCase) {
                        response.append(")");
                    }
                    break;
                default:
                    response.append(buildFieldName(entityKey, convertedField));
                    break;
            }
        }
        return response.toString();
    }

    protected String formatValue(String fieldName, String entityKey, SupportedFieldType type,
                                 SupportedFieldType secondaryType, Object[] value,
                                 boolean isFieldComparison, boolean ignoreCase,
                                 boolean ignoreQuotes) throws MVELTranslationException {
        StringBuilder response = new StringBuilder();
        if (isFieldComparison) {
            switch(type) {
                case MONEY:
                    response.append(entityKey);
                    response.append(".");
                    response.append(value[0]);
                    response.append(".getAmount()");
                    break;
                case STRING:
                    if (ignoreCase) {
                        response.append("MvelHelper.toUpperCase(");
                    }
                    response.append(entityKey);
                    response.append(".");
                    response.append(value[0]);
                    if (ignoreCase) {
                        response.append(")");
                    }
                    break;
                default:
                    response.append(entityKey);
                    response.append(".");
                    response.append(value[0]);
                    break;
            }
        } else {
            for (int j=0;j<value.length;j++){
                switch(type) {
                    case BOOLEAN:
                        response.append(value[j]);
                        break;
                    case DECIMAL:
                        try {
                            Double.parseDouble(value[j].toString());
                        } catch (Exception e) {
                            throw new MVELTranslationException(MVELTranslationException.INCOMPATIBLE_DECIMAL_VALUE, "Cannot format value for the field ("
                                    + fieldName + ") based on field type. The type of field is Decimal, " +
                                    "and you entered: (" + value[j] +")");
                        }
                        response.append(value[j]);
                        break;
                    case ID:
                        if (secondaryType != null && secondaryType.toString().equals(
                                SupportedFieldType.STRING.toString())) {
                            if (ignoreCase) {
                                response.append("MvelHelper.toUpperCase(");
                            }
                            if (!ignoreQuotes) {
                                response.append("\"");
                            }
                            response.append(value[j]);
                            if (!ignoreQuotes) {
                                response.append("\"");
                            }
                            if (ignoreCase) {
                                response.append(")");
                            }
                        } else {
                            try {
                                Integer.parseInt(value[j].toString());
                            } catch (Exception e) {
                                throw new MVELTranslationException(MVELTranslationException.INCOMPATIBLE_INTEGER_VALUE, "Cannot format value for the field (" +
                                        fieldName + ") based on field type. The type of field is Integer, " +
                                        "and you entered: (" + value[j] +")");
                            }
                            response.append(value[j]);
                        }
                        break;
                    case INTEGER:
                        try {
                            Integer.parseInt(value[j].toString());
                        } catch (Exception e) {
                            throw new MVELTranslationException(MVELTranslationException.INCOMPATIBLE_INTEGER_VALUE, "Cannot format value for the field (" +
                                    fieldName + ") based on field type. The type of field is Integer, " +
                                    "and you entered: (" + value[j] +")");
                        }
                        response.append(value[j]);
                        break;
                    case MONEY:
                        try {
                            Double.parseDouble(value[j].toString());
                        } catch (Exception e) {
                            throw new MVELTranslationException(MVELTranslationException.INCOMPATIBLE_DECIMAL_VALUE, "Cannot format value for the field (" +
                                    fieldName + ") based on field type. The type of field is Money, " +
                                    "and you entered: (" + value[j] +")");
                        }
                        response.append(value[j]);
                        break;
                    case DATE:
                        //convert the date to our standard date/time format
                        Date temp = null;
                        try {
                            temp = RuleBuilderFormatUtil.parseDate(String.valueOf(value[j]));
                        } catch (ParseException e) {
                            throw new MVELTranslationException(MVELTranslationException.INCOMPATIBLE_DATE_VALUE, "Cannot format value for the field (" +
                                    fieldName + ") based on field type. The type of field is Date, " +
                                    "and you entered: (" + value[j] +"). Dates must be in the format MM/dd/yyyy HH:mm.");
                        }
                        String convertedDate = FormatUtil.getTimeZoneFormat().format(temp);
                        response.append("MvelHelper.convertField(\"DATE\",\"");
                        response.append(convertedDate);
                        response.append("\")");
                        break;
                    default:
                        if (ignoreCase) {
                            response.append("MvelHelper.toUpperCase(");
                        }
                        if (!ignoreQuotes) {
                            response.append("\"");
                        }
                        response.append(value[j]);
                        if (!ignoreQuotes) {
                            response.append("\"");
                        }
                        if (ignoreCase) {
                            response.append(")");
                        }
                        break;
                }
                if (j < value.length - 1) {
                    response.append(",");
                }
            }
        }
        return response.toString();
    }


}
