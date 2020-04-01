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
package org.broadleafcommerce.openadmin.web.rulebuilder.statement;

import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.web.rulebuilder.BLCOperator;
import org.broadleafcommerce.openadmin.web.rulebuilder.DataDTOToMVELTranslator;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELTranslationException;
import org.broadleafcommerce.openadmin.web.rulebuilder.RuleBuilderFormatUtil;

import java.text.ParseException;

/**
 * @author jfischer
 * @author Elbert Bautista (elbertbautista)
 */
public class PhraseTranslator {

    private static final String[] OLD_SPECIAL_CASES = {
            DataDTOToMVELTranslator.OLD_STARTS_WITH_OPERATOR,
            DataDTOToMVELTranslator.OLD_ENDS_WITH_OPERATOR,
            DataDTOToMVELTranslator.OLD_CONTAINS_OPERATOR
    };

    private static final String[] SPECIAL_CASES = {
            DataDTOToMVELTranslator.STARTS_WITH_OPERATOR,
            DataDTOToMVELTranslator.ENDS_WITH_OPERATOR,
            DataDTOToMVELTranslator.CONTAINS_OPERATOR
    };

    private static final String[] STANDARD_OPERATORS = {
            DataDTOToMVELTranslator.SIZE_GREATER_THAN_OPERATOR,
            DataDTOToMVELTranslator.SIZE_GREATER_THAN_EQUALS_OPERATOR,
            DataDTOToMVELTranslator.SIZE_LESS_THAN_OPERATOR,
            DataDTOToMVELTranslator.SIZE_LESS_THAN_EQUALS_OPERATOR,
            DataDTOToMVELTranslator.SIZE_EQUALS_OPERATOR,
            DataDTOToMVELTranslator.EQUALS_OPERATOR,
            DataDTOToMVELTranslator.NOT_EQUALS_OPERATOR,
            DataDTOToMVELTranslator.GREATER_THAN_EQUALS_OPERATOR,
            DataDTOToMVELTranslator.LESS_THAN_EQUALS_OPERATOR,
            DataDTOToMVELTranslator.GREATER_THAN_OPERATOR,
            DataDTOToMVELTranslator.LESS_THAN_OPERATOR
    };

    public Expression createExpression(String phrase) throws MVELTranslationException {
        String[] components = extractComponents(phrase);
        String field = components[0];
        String operator = components[1];
        String value = components[2];

        boolean isNegation = false;
        if (field.startsWith("!") || phrase.startsWith("!")) {
            isNegation = true;
        }

        boolean isIgnoreCase = false;
        boolean isCollectionCase = false;
        if (phrase.contains(DataDTOToMVELTranslator.COLLECTION_OPERATOR)) {
            isCollectionCase = true;
        }

        //remove null check syntax
        field = field.replaceAll("\\.\\?", ".");

        //keep for backwards compatibility with legacy generated MVEL
        String legacyCaseInsensitivityKey = "MVEL.eval(\"toUpperCase()\",";

        String newCaseInsensitivityKey = "MvelHelper.toUpperCase(";
        String caseInsensitivityKey;
        if (field.contains(legacyCaseInsensitivityKey) || value.contains(legacyCaseInsensitivityKey)) {
            caseInsensitivityKey = legacyCaseInsensitivityKey;
        } else {
            caseInsensitivityKey = newCaseInsensitivityKey;
        }
        if (field.contains(caseInsensitivityKey)) {
            isIgnoreCase = true;
            field = field.substring(field.indexOf(caseInsensitivityKey) + caseInsensitivityKey.length(), field.length()-1);
        }
        while(value.contains(caseInsensitivityKey)) {
            int caseIndex = value.indexOf(caseInsensitivityKey);
            value = value.substring(0, caseIndex) +
                    value.substring(caseIndex + caseInsensitivityKey.length());
            if (value.contains("\")")) {
                value = value.substring(0, value.indexOf("\")") + 1) + value.substring(value.indexOf("\")") + 2);
            } else {
                value = value.substring(0, value.indexOf(")")) + value.substring(value.indexOf(")") + 1);
            }
        }
        if (value.startsWith("[") && value.endsWith("]") && !isCollectionCase) {
            value = value.substring(1, value.length() - 1);
            String[] temps = value.split(",");
            for (int j = 0;j<temps.length;j++) {
                if (temps[j].startsWith("\"") && temps[j].endsWith("\"")) {
                    temps[j] = temps[j].substring(1, temps[j].length()-1);
                }
            }
            StringBuffer sb = new StringBuffer();
            sb.append("[");
            for (int j = 0;j<temps.length;j++) {
                sb.append(temps[j]);
                if (j < temps.length - 1) {
                    sb.append(",");
                }
            }
            sb.append("]");
            value = sb.toString();
        }
        //keep for backwards compatibility with legacy generated MVEL
        String legacyDateFormatKey = "java.text.DateFormat.getDateTimeInstance(3,3).parse(";

        String newDateFormatKey = "MvelHelper.convertField(\"DATE\",\"";
        String dateFormatKey;
        if (value.contains(legacyDateFormatKey)) {
            dateFormatKey = legacyDateFormatKey;
        } else {
            dateFormatKey = newDateFormatKey;
        }
        if (value.startsWith(dateFormatKey)) {
            value = value.substring(dateFormatKey.length(), value.length()-1);
            //convert the date into admin display format
            try {
                if (value.startsWith("\"")) {
                    value = value.substring(1, value.length());
                }
                if (value.endsWith("\"")) {
                    value = value.substring(0, value.length()-1);
                }
                value = RuleBuilderFormatUtil.formatDate(RuleBuilderFormatUtil.parseDate(value));
            } catch (ParseException e) {
                throw new MVELTranslationException(MVELTranslationException.INCOMPATIBLE_DATE_VALUE, "Unable to convert " +
                        "the persisted date value(" + value + ") to the admin display format.");
            }
        }
        int entityKeyIndex = field.indexOf(".");
        if (entityKeyIndex < 0) {
            throw new MVELTranslationException(MVELTranslationException.NO_FIELD_FOUND_IN_RULE, "Could not identify a " +
                    "valid property field value in the expression: ("+phrase+")");
        }
        if (value.startsWith(caseInsensitivityKey)) {
            value = value.substring(caseInsensitivityKey.length(), value.length()-1);
        }
        String entityKey = field.substring(0, entityKeyIndex);
        boolean isFieldComparison = false;
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length()-1);
        } else if (value.startsWith(entityKey + ".")){
            isFieldComparison = true;
            value = value.substring(entityKey.length() + 1, value.length());
        }
        field = field.substring(entityKeyIndex + 1, field.length());
        // If this is a Money field, then DataDTOToMVELTranslator.formatValue() will append .getAmount() onto the end
        // of the field name. We need to remove that as it should not be considered part of the field name, but we still
        // want to support other method invocations in MVEL expressions (like for getProductAttributes())
        String moneyAmountMethod = ".getAmount()";
        int amountMethodPos = field.lastIndexOf(moneyAmountMethod);
        if (amountMethodPos >= 0) {
            field = field.substring(0, amountMethodPos);
        }

        // Same as above, but for Enumeration types
        String typeMethod = ".getType()";
        int typeMethodPos = field.lastIndexOf(typeMethod);
        if (typeMethodPos >= 0) {
           field = field.substring(0, typeMethodPos);
        }

        Expression expression = new Expression();
        expression.setField(field);
        BLCOperator operatorId = getOperator(field, operator, value, isNegation, isFieldComparison, isIgnoreCase);
        expression.setOperator(operatorId);
        expression.setValue(value);
        expression.setEntityKey(entityKey);

        return expression;
    }

    protected String[] extractComponents(String phrase) throws MVELTranslationException {
        String[] components = new String[3];

        boolean componentsExtracted = false;

        //If the phrase is a CollectionUtils case - this will need to be evaluated first
        //e.g. CollectionUtils.intersection(groupIds,["100","300"]).size()>0
        if (phrase.startsWith(DataDTOToMVELTranslator.COLLECTION_OPERATOR)) {
            components = extractCollectionCase(phrase);
            componentsExtracted = true;
        }

        if (!componentsExtracted) {
            for (String operator : STANDARD_OPERATORS) {
                if (phrase.contains(operator)) {
                    components = extractStandardComponents(phrase, operator);
                    componentsExtracted = true;
                    break;
                }
            }
        }

        if (!componentsExtracted) {
            for (String operator: SPECIAL_CASES) {
                if (phrase.contains(operator)) {
                    components = extractSpecialComponents(phrase, operator);
                    componentsExtracted = true;
                    break;
                }
            }
        }

        if (!componentsExtracted) {
            //may be an old special expression
            try {
                for (String operator : OLD_SPECIAL_CASES) {
                    if (phrase.contains(operator)) {
                        components = extractOldSpecialComponents(phrase, operator);
                        componentsExtracted = true;
                        break;
                    }
                }
            } catch (Exception e) {
                //do nothing
            }
        }

        if (!componentsExtracted) {
            //may be a projection
            try {
                components = extractProjection(components);
                componentsExtracted = true;
            } catch (Exception e1) {
                //do nothing
            }

            if (!componentsExtracted) {
                throw new MVELTranslationException(MVELTranslationException.UNRECOGNIZABLE_RULE, "Could not parse the MVEL expression to a " +
                        "compatible form for the rules builder (" + phrase + ")");
            }
        }

        components[0] = convertMapAccessSyntax(components[0]);

        return components;
    }

    protected String convertMapAccessSyntax(String field) {
        if (field.matches(".*\\[\".*?\"\\].*")) {
            //this is using map access syntax - must be a map field
            field = field.substring(0, field.lastIndexOf("[")) + FieldManager.MAPFIELDSEPARATOR +
                    field.substring(field.lastIndexOf("[") + 2, field.lastIndexOf("]") - 1) +
                    field.substring(field.lastIndexOf("]") + 1, field.length());
            //strip any convertField usage
            if (field.startsWith("MvelHelper.convertField(")) {
                field = field.substring(field.indexOf("(") + 1, field.length() - 1);
            }
        } else if (field.matches(".*\\?get\\(\".*?\"\\)\\.\\?getValue\\(\\).*")) {
            //this is using null-safe map access syntax - must be a map field
            field = field.substring(0, field.lastIndexOf(".?get(")) + FieldManager.MAPFIELDSEPARATOR +
                    field.substring(field.lastIndexOf(".?get(") + 7, field.lastIndexOf(").?getValue()") - 1) +
                    field.substring(field.lastIndexOf(").?getValue()") + 13, field.length());
        } else if (field.matches(".*\\?get\\(\".*?\"\\)\\.\\?value.*")) {
            //this is using MVELHelper null-safe map access syntax - must be a map field
            field = field.substring(0, field.lastIndexOf(".?get(")) + FieldManager.MAPFIELDSEPARATOR +
                    field.substring(field.lastIndexOf(".?get(") + 7, field.lastIndexOf(").?value") - 1) +
                    field.substring(field.lastIndexOf(").?value") + 8, field.length());
        }

        return field;
    }

    protected String[] extractProjection(String[] components) {
        String[] temp = new String[3];
        int startsWithIndex = components[0].indexOf("contains");
        temp[0] = components[0].substring(startsWithIndex+"contains".length()+1, components[0].length()).trim();
        if (temp[0].endsWith(".intValue()")) {
            temp[0] = temp[0].substring(0, temp[0].indexOf(".intValue()"));
        }
        temp[1] = "==";
        temp[2] = components[0].substring(components[0].indexOf("["), components[0].indexOf("]") + 1);
        return temp;
    }

    protected String[] extractCollectionCase(String phrase) {
        String[] temp = new String[3];
        String collectionBegin = DataDTOToMVELTranslator.COLLECTION_OPERATOR + "(";
        //field
        temp[0] = phrase.substring(collectionBegin.length(), phrase.indexOf(","));
        //value
        temp[2] = phrase.substring(phrase.indexOf(",") + 1, phrase.lastIndexOf("]") + 1);
        //operator
        temp[1] = phrase.substring(phrase.indexOf(".size"));

        return temp;
    }

    protected String[] extractOldSpecialComponents(String phrase, String operator) {
        String[] temp = new String[3];
        int specialIndex = phrase.indexOf(operator);
        //field
        temp[0] = phrase.substring(0, specialIndex);
        //operator
        temp[1] = operator;
        //value
        temp[2] = phrase.substring(specialIndex + operator.length() + 1, phrase.lastIndexOf(")"));

        return temp;
    }

    protected String[] extractStandardComponents(String phrase, String operator) {
        String[] temp = new String[3];
        //field
        temp[0] = phrase.substring(0, phrase.indexOf(operator));
        //operator
        temp[1] = operator;
        //value
        temp[2] = phrase.substring(phrase.indexOf(operator) + operator.length(), phrase.length());

        return temp;
    }

    protected String[] extractSpecialComponents(String phrase, String operator) {
        String[] temp = new String[3];
        //field
        String operatorBegin = operator + "(";
        temp[0] = phrase.substring(operatorBegin.length(), phrase.indexOf(","));
        //operator
        temp[1] = operator;
        //value
        temp[2] = phrase.substring(phrase.indexOf(",") + 1, phrase.lastIndexOf(")"));

        return temp;
    }

    protected BLCOperator getOperator(String field, String operator, String value, boolean isNegation,
                                     boolean isFieldComparison, boolean isIgnoreCase) throws MVELTranslationException {
        if (operator.equals(DataDTOToMVELTranslator.EQUALS_OPERATOR)) {
            if (value.equals("null")) {
                return BLCOperator.IS_NULL;
            } else if (isFieldComparison) {
                return BLCOperator.EQUALS_FIELD;
            } else if (isIgnoreCase) {
                return BLCOperator.IEQUALS;
            } else {
                return BLCOperator.EQUALS;
            }
        } else if (operator.equals(DataDTOToMVELTranslator.NOT_EQUALS_OPERATOR)) {
            if (value.equals("null")) {
                return BLCOperator.NOT_NULL;
            } else if (isFieldComparison) {
                return BLCOperator.NOT_EQUAL_FIELD;
            } else if (isIgnoreCase) {
                return BLCOperator.INOT_EQUAL;
            } else {
                return BLCOperator.NOT_EQUAL;
            }
        } else if (operator.equals(DataDTOToMVELTranslator.GREATER_THAN_OPERATOR)) {
            if (isFieldComparison) {
                return BLCOperator.GREATER_THAN_FIELD;
            } else {
                return BLCOperator.GREATER_THAN;
            }
        } else if (operator.equals(DataDTOToMVELTranslator.LESS_THAN_OPERATOR)) {
            if (isFieldComparison) {
                return BLCOperator.LESS_THAN_FIELD;
            } else {
                return BLCOperator.LESS_THAN;
            }
        } else if (operator.equals(DataDTOToMVELTranslator.GREATER_THAN_EQUALS_OPERATOR)) {
            if (isFieldComparison) {
                return BLCOperator.GREATER_OR_EQUAL_FIELD;
            } else {
                return BLCOperator.GREATER_OR_EQUAL;
            }
        } else if (operator.equals(DataDTOToMVELTranslator.LESS_THAN_EQUALS_OPERATOR)) {
            if (isFieldComparison) {
                return BLCOperator.LESS_OR_EQUAL_FIELD;
            } else {
                return BLCOperator.LESS_OR_EQUAL;
            }
        } else if (operator.equals(DataDTOToMVELTranslator.CONTAINS_OPERATOR) || operator.equals(DataDTOToMVELTranslator.OLD_CONTAINS_OPERATOR)) {
            if (isNegation) {
                if (isIgnoreCase) {
                    return BLCOperator.INOT_CONTAINS;
                } else {
                    return BLCOperator.NOT_CONTAINS;
                }
            } else {
                if (isIgnoreCase) {
                    return BLCOperator.ICONTAINS;
                }
                if (isFieldComparison) {
                    return BLCOperator.CONTAINS_FIELD;
                } else {
                    return BLCOperator.CONTAINS;
                }
            }
        } else if (operator.equals(DataDTOToMVELTranslator.STARTS_WITH_OPERATOR) || operator.equals(DataDTOToMVELTranslator.OLD_STARTS_WITH_OPERATOR)) {
            if (isNegation) {
                if (isIgnoreCase) {
                    return BLCOperator.INOT_STARTS_WITH;
                } else {
                    return BLCOperator.NOT_STARTS_WITH;
                }
            } else {
                if (isIgnoreCase) {
                    return BLCOperator.ISTARTS_WITH;
                } else if (isFieldComparison){
                    return BLCOperator.STARTS_WITH_FIELD;
                } else {
                    return BLCOperator.STARTS_WITH;
                }
            }
        } else if (operator.equals(DataDTOToMVELTranslator.ENDS_WITH_OPERATOR) || operator.equals(DataDTOToMVELTranslator.OLD_ENDS_WITH_OPERATOR)) {
            if (isNegation) {
                if (isIgnoreCase) {
                    return BLCOperator.INOT_ENDS_WITH;
                } else {
                    return BLCOperator.NOT_ENDS_WITH;
                }
            } else {
                if (isIgnoreCase) {
                    return BLCOperator.IENDS_WITH;
                } else if (isFieldComparison) {
                    return BLCOperator.ENDS_WITH_FIELD;
                } else {
                    return BLCOperator.ENDS_WITH;
                }
            }
        } else if (operator.equals(DataDTOToMVELTranslator.SIZE_GREATER_THAN_OPERATOR)) {
            return BLCOperator.COUNT_GREATER_THAN;
        } else if (operator.equals(DataDTOToMVELTranslator.SIZE_GREATER_THAN_EQUALS_OPERATOR)) {
            return BLCOperator.COUNT_GREATER_OR_EQUAL;
        } else if (operator.equals(DataDTOToMVELTranslator.SIZE_LESS_THAN_OPERATOR)) {
            return BLCOperator.COUNT_LESS_THAN;
        } else if (operator.equals(DataDTOToMVELTranslator.SIZE_LESS_THAN_EQUALS_OPERATOR)) {
            return BLCOperator.COUNT_LESS_OR_EQUAL;
        } else if (operator.equals(DataDTOToMVELTranslator.SIZE_EQUALS_OPERATOR)) {
            return BLCOperator.COUNT_EQUALS;
        } else if (operator.equals(DataDTOToMVELTranslator.SIZE_GREATER_THAN_OPERATOR + DataDTOToMVELTranslator.ZERO_OPERATOR)){
            return BLCOperator.COLLECTION_IN;
        } else if (operator.equals(DataDTOToMVELTranslator.SIZE_EQUALS_OPERATOR + DataDTOToMVELTranslator.ZERO_OPERATOR)){
            return BLCOperator.COLLECTION_NOT_IN;
        }
        throw new MVELTranslationException(MVELTranslationException.OPERATOR_NOT_FOUND, "Unable to identify an operator compatible with the " +
                "rules builder: ("+(isNegation?"!":""+field+operator+value)+")");
    }

}
