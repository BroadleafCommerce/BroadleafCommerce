/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.rulebuilder;

import org.apache.commons.lang.StringUtils;
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

    public static final String CONTAINS_OPERATOR = "org.apache.commons.lang3.StringUtils.contains";
    public static final String STARTS_WITH_OPERATOR = "org.apache.commons.lang3.StringUtils.startsWith";
    public static final String ENDS_WITH_OPERATOR = "org.apache.commons.lang3.StringUtils.endsWith";
    public static final String EQUALS_OPERATOR = "==";
    public static final String NOT_EQUALS_OPERATOR = "!=";
    public static final String GREATER_THAN_EQUALS_OPERATOR = ">=";
    public static final String LESS_THAN_EQUALS_OPERATOR = "<=";
    public static final String GREATER_THAN_OPERATOR = ">";
    public static final String LESS_THAN_OPERATOR = "<";
    public static final String COLLECTION_OPERATOR = "CollectionUtils.intersection";
    public static final String SIZE_GREATER_THAN_OPERATOR = ".size()>";
    public static final String SIZE_GREATER_THAN_EQUALS_OPERATOR = ".size()>=";
    public static final String SIZE_LESS_THAN_OPERATOR = ".size()<";
    public static final String SIZE_LESS_THAN_EQUALS_OPERATOR = ".size()<=";
    public static final String SIZE_EQUALS_OPERATOR = ".size()==";
    public static final String ZERO_OPERATOR = "0";

    public static final String OLD_CONTAINS_OPERATOR = ".contains";
    public static final String OLD_STARTS_WITH_OPERATOR = ".startsWith";
    public static final String OLD_ENDS_WITH_OPERATOR = ".endsWith";


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
            operator = BLCOperator.valueOf(dataDTO.getCondition());
        }
        ArrayList<DataDTO> groups = dataDTO.getRules();
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
                buildMVEL(dto, sb, entityKey, dataDTO.getCondition(), fieldService);
            }
            if (includeTopLevelParenthesis) sb.append(")");
        }
    }

    protected void buildExpression(ExpressionDTO expressionDTO, StringBuffer sb, String entityKey,
            BLCOperator operator, RuleBuilderFieldService fieldService)
            throws MVELTranslationException {
        String field = expressionDTO.getId();
        String overrideEntityKey = fieldService.getOverrideFieldEntityKey(field);
        if (overrideEntityKey != null) {
            entityKey = overrideEntityKey;
        }

        SupportedFieldType type = fieldService.getSupportedFieldType(field);
        SupportedFieldType secondaryType = fieldService.getSecondaryFieldType(field);
        Object[] value;

        if (type == null) {
            throw new MVELTranslationException(MVELTranslationException.SPECIFIED_FIELD_NOT_FOUND, "The DataDTO is not compatible with the RuleBuilderFieldService " +
                    "associated with the current rules builder. Unable to find the field " +
                    "specified: ("+field+")");
        }

        value = extractBasicValues(expressionDTO.getValue());

        switch(operator) {
            case CONTAINS: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, CONTAINS_OPERATOR,
                        true, false, false, false, false);
                break;
            }
            case CONTAINS_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, CONTAINS_OPERATOR,
                        true, true, false, false, false);
                break;
            }
            case ENDS_WITH: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ENDS_WITH_OPERATOR,
                        true, false, false, false, false);
                break;
            }
            case ENDS_WITH_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ENDS_WITH_OPERATOR,
                        true, true, false, false, false);
                break;
            }
            case EQUALS: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, EQUALS_OPERATOR, false, false, false, false, false);
                break;
            }
            case EQUALS_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, EQUALS_OPERATOR, false, true, false, false, false);
                break;
            }
            case GREATER_OR_EQUAL: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, GREATER_THAN_EQUALS_OPERATOR, false, false, false, false, false);
                break;
            }
            case GREATER_OR_EQUAL_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, GREATER_THAN_EQUALS_OPERATOR, false, true, false, false, false);
                break;
            }
            case GREATER_THAN: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, GREATER_THAN_OPERATOR, false, false, false, false, false);
                break;
            }
            case GREATER_THAN_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, GREATER_THAN_OPERATOR, false, true, false, false, false);
                break;
            }
            case ICONTAINS: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, CONTAINS_OPERATOR,
                        true, false, true, false, false);
                break;
            }
            case IENDS_WITH: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ENDS_WITH_OPERATOR,
                        true, false, true, false, false);
                break;
            }
            case IEQUALS: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, EQUALS_OPERATOR, false, false, true, false, false);
                break;
            }
            case INOT_CONTAINS: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, CONTAINS_OPERATOR,
                        true, false, true, true, false);
                break;
            }
            case INOT_ENDS_WITH: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ENDS_WITH_OPERATOR,
                        true, false, true, true, false);
                break;
            }
            case INOT_EQUAL: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, NOT_EQUALS_OPERATOR, false, false, true, false, false);
                break;
            }
            case INOT_STARTS_WITH: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, STARTS_WITH_OPERATOR,
                        true, false, true, true, false);
                break;
            }
            case IS_NULL: {
                buildExpression(sb, entityKey, field, new Object[]{"null"}, type, secondaryType, EQUALS_OPERATOR,
                        false, false, false, false, true);
                break;
            }
            case ISTARTS_WITH: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, STARTS_WITH_OPERATOR,
                        true, false, true, false, false);
                break;
            }
            case LESS_OR_EQUAL: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, LESS_THAN_EQUALS_OPERATOR, false, false, false, false, false);
                break;
            }
            case LESS_OR_EQUAL_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, LESS_THAN_EQUALS_OPERATOR, false, true, false, false, false);
                break;
            }
            case LESS_THAN: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, LESS_THAN_OPERATOR, false, false, false, false, false);
                break;
            }
            case LESS_THAN_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, LESS_THAN_OPERATOR,
                        false, true, false, false, false);
                break;
            }
            case NOT_CONTAINS: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, CONTAINS_OPERATOR,
                        true, false, false, true, false);
                break;
            }
            case NOT_ENDS_WITH: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, ENDS_WITH_OPERATOR,
                        true, false, false, true, false);
                break;
            }
            case NOT_EQUAL: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, NOT_EQUALS_OPERATOR, false, false, false, false, false);
                break;
            }
            case NOT_EQUAL_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, NOT_EQUALS_OPERATOR,
                        false, true, false, false, false);
                break;
            }
            case NOT_NULL: {
                buildExpression(sb, entityKey, field, new Object[]{"null"}, type, secondaryType, NOT_EQUALS_OPERATOR,
                        false, false, false, false, true);
                break;
            }
            case NOT_STARTS_WITH: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, STARTS_WITH_OPERATOR,
                        true, false, false, true, false);
                break;
            }
            case STARTS_WITH: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, STARTS_WITH_OPERATOR,
                        true, false, false, false, false);
                break;
            }
            case STARTS_WITH_FIELD: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, STARTS_WITH_OPERATOR,
                        true, true, false, false, false);
                break;
            }
            case COUNT_GREATER_THAN: {
                buildExpression(sb, entityKey, field, value, type, secondaryType, SIZE_GREATER_THAN_OPERATOR, false, false, false, false, true);
                break;
            }
            case COUNT_GREATER_OR_EQUAL:{
                buildExpression(sb, entityKey, field, value, type, secondaryType, SIZE_GREATER_THAN_EQUALS_OPERATOR, false, false, false, false, true);
                break;
            }
            case COUNT_LESS_THAN:{
                buildExpression(sb, entityKey, field, value, type, secondaryType, SIZE_LESS_THAN_OPERATOR, false, false, false, false, true);
                break;
            }
            case COUNT_LESS_OR_EQUAL:{
                buildExpression(sb, entityKey, field, value, type, secondaryType, SIZE_LESS_THAN_EQUALS_OPERATOR, false, false, false, false, true);
                break;
            }
            case COUNT_EQUALS:{
                buildExpression(sb, entityKey, field, value, type, secondaryType, SIZE_EQUALS_OPERATOR, false, false, false, false, true);
                break;
            }
            case COLLECTION_IN:{
                buildCollectionExpression(sb, entityKey, field, value, type, secondaryType, SIZE_GREATER_THAN_OPERATOR + ZERO_OPERATOR, false, false, false, false, true);
                break;
            }
            case COLLECTION_NOT_IN:{
                buildCollectionExpression(sb, entityKey, field, value, type, secondaryType, SIZE_EQUALS_OPERATOR + ZERO_OPERATOR, false, false, false, false, true);
                break;
            }
            case BETWEEN: {
                if (value != null && value.length==2) {
                    sb.append("(");
                    buildExpression(sb, entityKey, field, new Object[]{value[0]}, type, secondaryType, GREATER_THAN_OPERATOR,
                            false, false, false, false, true);
                    sb.append("&&");
                    buildExpression(sb, entityKey, field, new Object[]{value[1]}, type, secondaryType, LESS_THAN_OPERATOR,
                            false, false, false, false, true);
                    sb.append(")");
                }
                break;
            }
            case BETWEEN_INCLUSIVE: {
                if (value != null && value.length==2) {
                    sb.append("(");
                    buildExpression(sb, entityKey, field, new Object[]{value[0]}, type, secondaryType, GREATER_THAN_EQUALS_OPERATOR,
                            false, false, false, false, true);
                    sb.append("&&");
                    buildExpression(sb, entityKey, field, new Object[]{value[1]}, type, secondaryType, LESS_THAN_EQUALS_OPERATOR,
                            false, false, false, false, true);
                    sb.append(")");
                }
                break;
            }
        }
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
                String processedValue = stringValue.substring(initial, end);
                processedValue = escapeInternalQuotes(processedValue);

                temp.add(processedValue);
                initial = end + 1;
            }
            response = temp.toArray(response);
        } else {
            response = new Object[]{value};
        }
        return response;
    }

    protected String escapeInternalQuotes(String processedValue) {
        String regex = "(?<!^)(?<!^\\s)\\\"(?!\\s$)(?!$)";
        return processedValue.replaceAll(regex, "\\\\\"");
    }

    public boolean isProjection(Object value) {
        String stringValue = value.toString().trim();
        return stringValue.startsWith("[") && stringValue.endsWith("]");
    }

    protected void buildCollectionExpression(StringBuffer sb, String entityKey, String field, Object[] value,
            SupportedFieldType type, SupportedFieldType secondaryType, String operator,
            boolean includeParenthesis, boolean isFieldComparison, boolean ignoreCase,
            boolean isNegation, boolean ignoreQuotes) throws MVELTranslationException {
        sb.append(COLLECTION_OPERATOR);
        sb.append("(");
        sb.append(formatField(entityKey, type, field, ignoreCase));
        sb.append(",");
        sb.append("[");
        sb.append(formatValue(field, entityKey, type, secondaryType, value, isFieldComparison,
                ignoreCase, ignoreQuotes));
        sb.append("])");
        sb.append(operator);
    }

    protected void buildExpression(StringBuffer sb, String entityKey, String field, Object[] value,
                                   SupportedFieldType type, SupportedFieldType secondaryType, String operator,
                                   boolean includeParenthesis, boolean isFieldComparison, boolean ignoreCase,
                                   boolean isNegation, boolean ignoreQuotes)
            throws MVELTranslationException {

        if (operator.equals(EQUALS_OPERATOR) && !isFieldComparison && value.length > 1) {
            sb.append("(");
            sb.append("[");
            sb.append(formatValue(field, entityKey, type, secondaryType, value, isFieldComparison,
                    ignoreCase, ignoreQuotes));
            sb.append("] contains ");
            sb.append(formatField(entityKey, type, field, ignoreCase));
            if ((type.equals(SupportedFieldType.ID) && secondaryType != null &&
                    secondaryType.equals(SupportedFieldType.INTEGER)) || type.equals(SupportedFieldType.INTEGER)) {
                sb.append(".intValue()");
            }
            sb.append(")");
        } else if (CONTAINS_OPERATOR.equals(operator) || STARTS_WITH_OPERATOR.equals(operator) || ENDS_WITH_OPERATOR.equals(operator)) {
            if(isNegation) {
                sb.append("!");
            }
            sb.append(operator);
            sb.append("(");
            sb.append(formatField(entityKey, type, field, ignoreCase));
            sb.append(",");
            sb.append(formatValue(field, entityKey, type, secondaryType, value, isFieldComparison, ignoreCase, ignoreQuotes));
            sb.append(")");
        } else {
            sb.append(formatField(entityKey, type, field, ignoreCase));
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
                                 boolean ignoreCase) {
        StringBuilder response = new StringBuilder();
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
                case DATA_DRIVEN_ENUMERATION:
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
                    response.append(buildFieldName(entityKey, convertedField));
                    response.append(".getType()");
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
                if (StringUtils.isBlank(value[j].toString())) {
                    break;
                }

                String parsableVal = value[j].toString();
                parsableVal = parsableVal.replaceAll("^\"|\"$", "");

                switch(type) {
                    case BOOLEAN:
                        response.append(value[j]);
                        break;
                    case DECIMAL:
                        try {
                            Double.parseDouble(parsableVal);
                        } catch (Exception e) {
                            throw new MVELTranslationException(MVELTranslationException.INCOMPATIBLE_DECIMAL_VALUE, "Cannot format value for the field ("
                                    + fieldName + ") based on field type. The type of field is Decimal, " +
                                    "and you entered: (" + parsableVal +")");
                        }
                        response.append(parsableVal);
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
                                Integer.parseInt(parsableVal);
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
                            Integer.parseInt(parsableVal);
                        } catch (Exception e) {
                            throw new MVELTranslationException(MVELTranslationException.INCOMPATIBLE_INTEGER_VALUE, "Cannot format value for the field (" +
                                    fieldName + ") based on field type. The type of field is Integer, " +
                                    "and you entered: (" + parsableVal +")");
                        }
                        response.append(parsableVal);
                        break;
                    case MONEY:
                        try {
                            Double.parseDouble(parsableVal);
                        } catch (Exception e) {
                            throw new MVELTranslationException(MVELTranslationException.INCOMPATIBLE_DECIMAL_VALUE, "Cannot format value for the field (" +
                                    fieldName + ") based on field type. The type of field is Money, " +
                                    "and you entered: (" + parsableVal +")");
                        }
                        response.append(parsableVal);
                        break;
                    case DATE:
                        //convert the date to our standard date/time format
                        Date temp = null;
                        try {
                            temp = RuleBuilderFormatUtil.parseDate(parsableVal);
                        } catch (ParseException e) {
                            throw new MVELTranslationException(MVELTranslationException.INCOMPATIBLE_DATE_VALUE, "Cannot format value for the field (" +
                                    fieldName + ") based on field type. The type of field is Date, " +
                                    "and you entered: (" + parsableVal +"). Dates must be in the format MM/dd/yyyy HH:mm.");
                        }
                        String convertedDate = FormatUtil.getTimeZoneFormat().format(temp);
                        response.append("MvelHelper.convertField(\"DATE\",\"");
                        response.append(convertedDate);
                        response.append("\")");
                        break;
                    default:
                        String stringVersionState = String.valueOf(value[j]);
                        boolean alreadyHasQuotes = stringVersionState.startsWith("\"") && stringVersionState.endsWith("\"");
                        if (ignoreCase) {
                            response.append("MvelHelper.toUpperCase(");
                        }
                        if (!ignoreQuotes && !alreadyHasQuotes) {
                            response.append("\"");
                        }
                        response.append(stringVersionState);
                        if (!ignoreQuotes && !alreadyHasQuotes) {
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
