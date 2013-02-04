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

package org.broadleafcommerce.openadmin.client.translation;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.BLCMain;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RelativeDate;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.util.EnumUtil;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.JSON;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.fields.RelativeDateItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class AdvancedCriteriaToMVELTranslator {
    
    public String createMVEL(String entityKey, AdvancedCriteria criteria, DataSource dataSource) throws IncompatibleMVELTranslationException {
        StringBuffer sb = new StringBuffer();
        buildMVEL(criteria, sb, entityKey, dataSource, null);
        String response = sb.toString().trim();
        if (response.length() == 0) {
            response = null;
        }
        return response;
    }
    
    protected void buildMVEL(Criteria criteria, StringBuffer sb, String entityKey, DataSource dataSource, OperatorId groupOperator) throws IncompatibleMVELTranslationException {
        OperatorId operator = EnumUtil.getEnum(OperatorId.values(), criteria.getAttribute("operator"));
        JavaScriptObject listJS = criteria.getAttributeAsJavaScriptObject("criteria");
        if (sb.length() != 0 && sb.charAt(sb.length() - 1) != '(' && groupOperator != null) {
            switch(groupOperator) {
            default:
                sb.append("&&");
                break;
            case OR:
                sb.append("||");
            }
        }
        if (!JSOHelper.isArray(listJS)) {
            buildExpression(criteria, sb, entityKey, operator, dataSource);
        } else {
            boolean includeTopLevelParenthesis = false;
            if (sb.length() != 0 || operator.getValue().equals(OperatorId.NOT.getValue())) {
                includeTopLevelParenthesis = true;
            }   
            if (operator.getValue().equals(OperatorId.NOT.getValue())) {
                sb.append("!");
            }
            if (includeTopLevelParenthesis) sb.append("(");
            Criteria[] myCriterias = AdvancedCriteria.convertToCriteriaArray(listJS);
            for (Criteria myCriteria : myCriterias) {
                buildMVEL(myCriteria, sb, entityKey, dataSource, operator);
            }
            if (includeTopLevelParenthesis) sb.append(")");
        }
    }
    
    protected Date parseRelativeDate(Map<String,String> dateItems) {
        return RelativeDateItem.getAbsoluteDate(new RelativeDate(dateItems.get("value")));
    }
    
    @SuppressWarnings("rawtypes")
    protected void buildExpression(Criteria criteria, StringBuffer sb, String entityKey, OperatorId operator, DataSource dataSource) throws IncompatibleMVELTranslationException {
        Map values = criteria.getValues();
        String field = (String) values.get("fieldName");
        SupportedFieldType type = SupportedFieldType.valueOf(dataSource.getField(field).getAttribute("fieldType"));
        SupportedFieldType secondaryType = null;
        String secondaryTypeVal = dataSource.getField(field).getAttribute("secondaryFieldType");
        Object[] value;
        if (secondaryTypeVal != null) {
            secondaryType = SupportedFieldType.valueOf(secondaryTypeVal);
        }
        if (
            SupportedFieldType.DATE.toString().equals(type.toString()) && 
            !OperatorId.CONTAINS_FIELD.getValue().equals(operator.getValue()) &&
            !OperatorId.ENDS_WITH_FIELD.getValue().equals(operator.getValue()) &&
            !OperatorId.EQUALS_FIELD.getValue().equals(operator.getValue()) &&
            !OperatorId.GREATER_OR_EQUAL_FIELD.getValue().equals(operator.getValue()) &&
            !OperatorId.GREATER_THAN_FIELD.getValue().equals(operator.getValue()) &&
            !OperatorId.LESS_OR_EQUAL_FIELD.getValue().equals(operator.getValue()) &&
            !OperatorId.LESS_THAN_FIELD.getValue().equals(operator.getValue()) &&
            !OperatorId.NOT_EQUAL_FIELD.getValue().equals(operator.getValue()) &&
            !OperatorId.STARTS_WITH_FIELD.getValue().equals(operator.getValue()) &&
            !OperatorId.BETWEEN.getValue().equals(operator.getValue()) &&
            !OperatorId.BETWEEN_INCLUSIVE.getValue().equals(operator.getValue())
        ) {
            value = extractDate(criteria, operator, values, "value");
        } else {
            value = extractBasicValues(values.get("value"));
        }
        switch(operator) {
        case CONTAINS: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, ".contains", true, false, false, false, false);
            break;
        }
        case CONTAINS_FIELD: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, ".contains", true, true, false, false, false);
            break;
        }
        case ENDS_WITH: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, ".endsWith", true, false, false, false, false);
            break;
        }
        case ENDS_WITH_FIELD: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, ".endsWith", true, true, false, false, false);
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
            buildExpression(sb, entityKey, field, value, type, secondaryType, ".contains", true, false, true, false, false);
            break;
        }
        case IENDS_WITH: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, ".endsWith", true, false, true, false, false);
            break;
        }
        case IEQUALS: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, "==", false, false, true, false, false);
            break;
        }
        case INOT_CONTAINS: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, ".contains", true, false, true, true, false);
            break;
        }
        case INOT_ENDS_WITH: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, ".endsWith", true, false, true, true, false);
            break;
        }
        case INOT_EQUAL: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, "!=", false, false, true, false, false);
            break;
        }
        case INOT_STARTS_WITH: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, ".startsWith", true, false, true, true, false);
            break;
        }
        case IS_NULL: {
            buildExpression(sb, entityKey, field, new Object[]{"null"}, type, secondaryType, "==", false, false, false, false, true);
            break;
        }
        case ISTARTS_WITH: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, ".startsWith", true, false, true, false, false);
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
            buildExpression(sb, entityKey, field, value, type, secondaryType, "<", false, true, false, false, false);
            break;
        }
        case NOT_CONTAINS: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, ".contains", true, false, false, true, false);
            break;
        }
        case NOT_ENDS_WITH: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, ".endsWith", true, false, false, true, false);
            break;
        }
        case NOT_EQUAL: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, "!=", false, false, false, false, false);
            break;
        }
        case NOT_EQUAL_FIELD: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, "!=", false, true, false, false, false);
            break;
        }
        case NOT_NULL: {
            buildExpression(sb, entityKey, field, new Object[]{"null"}, type, secondaryType, "!=", false, false, false, false, true);
            break;
        }
        case NOT_STARTS_WITH: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, ".startsWith", true, false, false, true, false);
            break;
        }
        case STARTS_WITH: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, ".startsWith", true, false, false, false, false);
            break;
        }
        case STARTS_WITH_FIELD: {
            buildExpression(sb, entityKey, field, value, type, secondaryType, ".startsWith", true, true, false, false, false);
            break;
        }
        case BETWEEN: {
            if (
                SupportedFieldType.DATE.toString().equals(type.toString())
            ) {
                sb.append("(");
                buildExpression(sb, entityKey, field, extractDate(criteria, OperatorId.GREATER_THAN, values, "start"), type, secondaryType, ">", false, false, false, false, false);
                sb.append("&&");
                buildExpression(sb, entityKey, field, extractDate(criteria, OperatorId.LESS_THAN, values, "end"), type, secondaryType, "<", false, false, false, false, false);
                sb.append(")");
            } else {
                sb.append("(");
                buildExpression(sb, entityKey, field, new Object[]{values.get("start")}, type, secondaryType, ">", false, false, false, false, false);
                sb.append("&&");
                buildExpression(sb, entityKey, field, new Object[]{values.get("end")}, type, secondaryType, "<", false, false, false, false, false);
                sb.append(")");
            }
            break;
        }
        case BETWEEN_INCLUSIVE: {
            if (
                SupportedFieldType.DATE.toString().equals(type.toString())
            ) {
                sb.append("(");
                buildExpression(sb, entityKey, field, extractDate(criteria, OperatorId.GREATER_OR_EQUAL, values, "start"), type, secondaryType, ">=", false, false, false, false, false);
                sb.append("&&");
                buildExpression(sb, entityKey, field, extractDate(criteria, OperatorId.LESS_OR_EQUAL, values, "end"), type, secondaryType, "<=", false, false, false, false, false);
                sb.append(")");
            } else {
                sb.append("(");
                buildExpression(sb, entityKey, field, new Object[]{values.get("start")}, type, secondaryType, ">=", false, false, false, false, false);
                sb.append("&&");
                buildExpression(sb, entityKey, field, new Object[]{values.get("end")}, type, secondaryType, "<=", false, false, false, false, false);
                sb.append(")");
            }
            break;
        }
        }
    }

    @SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
    protected Object[] extractDate(Criteria criteria, OperatorId operator, Map values, String key) {
        Object value;
        String jsObj = JSON.encode(criteria.getJsObj());
        JSONObject criteriaObj = JSONParser.parse(jsObj).isObject();
        JSONObject valueObj = criteriaObj.get(key).isObject();
        if (valueObj != null) {
            value = parseRelativeDate((Map<String,String>) values.get(key));
        } else {
            value = values.get(key);
        }
        if (
            OperatorId.GREATER_THAN.getValue().equals(operator.getValue()) ||
            OperatorId.LESS_OR_EQUAL.getValue().equals(operator.getValue())
        ) {
            ((Date) value).setHours(23);
            ((Date) value).setMinutes(59);
        } else {
            ((Date) value).setHours(0);
            ((Date) value).setMinutes(0);
        }
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

    protected void buildExpression(StringBuffer sb, String entityKey, String field, Object[] value, SupportedFieldType type, SupportedFieldType secondaryType, String operator, boolean includeParenthesis, boolean isFieldComparison, boolean ignoreCase, boolean isNegation, boolean ignoreQuotes) throws IncompatibleMVELTranslationException {
        if (operator.equals("==") && !isFieldComparison && value.length > 1) {
            sb.append("(");
            sb.append("[");
            sb.append(formatValue(field, entityKey, type, secondaryType, value, isFieldComparison, ignoreCase, ignoreQuotes));
            sb.append("] contains ");
            sb.append(formatField(entityKey, type, field, ignoreCase, isNegation));
            if ((type.equals(SupportedFieldType.ID) && secondaryType != null && secondaryType.equals(SupportedFieldType.INTEGER)) || type.equals(SupportedFieldType.INTEGER)) {
                sb.append(".intValue()");
            }
            sb.append(")");
        } else {
            sb.append(formatField(entityKey, type, field, ignoreCase, isNegation));
            sb.append(operator);
            if (includeParenthesis) {
                sb.append("(");
            }
            sb.append(formatValue(field, entityKey, type, secondaryType, value, isFieldComparison, ignoreCase, ignoreQuotes));
            if (includeParenthesis) {
                sb.append(")");
            }
        }
    }
    
    protected String formatField(String entityKey, SupportedFieldType type, String field, boolean ignoreCase, boolean isNegation) {
        StringBuffer response = new StringBuffer();
        if (isNegation) {
            response.append("!");
        }
        switch(type) {
        case BROADLEAF_ENUMERATION:
            response.append(entityKey);
            response.append(".");
            response.append(field);
            response.append(".getType()");
            break;
        case MONEY:
            response.append(entityKey);
            response.append(".");
            response.append(field);
            response.append(".getAmount()");
            break;
        case STRING:
            if (ignoreCase) {
                response.append("MVEL.eval(\"toUpperCase()\",");
            }
            response.append(entityKey);
            response.append(".");
            response.append(field);
            if (ignoreCase) {
                response.append(")");
            }
            break;
        default:
            response.append(entityKey);
            response.append(".");
            response.append(field);
            break;
        }
        return response.toString();
    }
    
    protected String formatValue(String fieldName, String entityKey, SupportedFieldType type, SupportedFieldType secondaryType, Object[] value, boolean isFieldComparison, boolean ignoreCase, boolean ignoreQuotes) throws IncompatibleMVELTranslationException {
        StringBuffer response = new StringBuffer();
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
                    response.append("MVEL.eval(\"toUpperCase()\",");
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
                        throw new IncompatibleMVELTranslationException("Cannot format value for the field (" + fieldName + ") based on field type. The type of field is Decimal, and you entered: (" + value[j] +")");
                    }
                    response.append(value[j]);
                    break;
                case ID:
                    if (secondaryType != null && secondaryType.toString().equals(SupportedFieldType.STRING.toString())) {
                        if (ignoreCase) {
                            response.append("MVEL.eval(\"toUpperCase()\",");
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
                            throw new IncompatibleMVELTranslationException("Cannot format value for the field (" + fieldName + ") based on field type. The type of field is Integer, and you entered: (" + value[j] +")");
                        }
                        response.append(value[j]);
                    }
                    break;
                case INTEGER:
                    try {
                        Integer.parseInt(value[j].toString());
                    } catch (Exception e) {
                        throw new IncompatibleMVELTranslationException("Cannot format value for the field (" + fieldName + ") based on field type. The type of field is Integer, and you entered: (" + value[j] +")");
                    }
                    response.append(value[j]);
                    break;
                case MONEY:
                    try {
                        Double.parseDouble(value[j].toString());
                    } catch (Exception e) {
                        throw new IncompatibleMVELTranslationException("Cannot format value for the field (" + fieldName + ") based on field type. The type of field is Money, and you entered: (" + value[j] +")");
                    }
                    response.append(value[j]);
                    break;
                case DATE:
                    DateTimeFormat formatter = DateTimeFormat.getFormat("MM/dd/yy H:mm a Z");
                    String formattedDate = formatter.format((Date) value[0]);
                    response.append("java.text.DateFormat.getDateTimeInstance(3,3).parse(\"");
                    response.append(formattedDate);
                    response.append("\")");
                    break;
                default:
                    if (ignoreCase) {
                        response.append("MVEL.eval(\"toUpperCase()\",");
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

    public void translateCriteriaToMVEL(Record record, String fieldName, FilterBuilder filterBuilder) {		
        String attr = record.getAttribute(fieldName);  	
        Object value = null;
        try {
            value = createMVEL(fieldName, filterBuilder.getCriteria(), filterBuilder.getDataSource());
        } catch (IncompatibleMVELTranslationException e) {
            throw new RuntimeException(BLCMain.getMessageManager().getString("mvelTranslationProblem"), e);
        }
	    
        String val = value==null?null:String.valueOf(value);
        if (attr != val && (attr == null || val == null || !attr.equals(val))) {
            record.setAttribute(fieldName, value);
        }
    }
}
