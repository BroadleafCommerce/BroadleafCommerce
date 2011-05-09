package org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.client.presentation.SupportedFieldType;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.RelativeDate;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.util.EnumUtil;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.JSON;
import com.smartgwt.client.widgets.form.fields.RelativeDateItem;

public class AdvancedCriteriaToMVELTranslator {

	private static Map<FilterType, String> MVELKEYWORDMAP = new HashMap<FilterType, String>();
	static {
		MVELKEYWORDMAP.put(FilterType.ORDER, "order");
		MVELKEYWORDMAP.put(FilterType.ORDER_ITEM, "discreteOrderItem");
		MVELKEYWORDMAP.put(FilterType.FULFILLMENT_GROUP, "fulfillmentGroup");
		MVELKEYWORDMAP.put(FilterType.CUSTOMER, "customer");
	}
	
	public String createMVEL(AdvancedCriteria criteria, FilterType filterType, DataSource dataSource) {
		StringBuffer sb = new StringBuffer();
		buildMVEL(criteria, sb, filterType, dataSource, null);
		return sb.toString();
	}
	
	protected void buildMVEL(Criteria criteria, StringBuffer sb, FilterType filterType, DataSource dataSource, OperatorId groupOperator) {
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
			buildExpression(criteria, sb, filterType, operator, dataSource);
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
				buildMVEL(myCriteria, sb, filterType, dataSource, operator);
			}
			if (includeTopLevelParenthesis) sb.append(")");
		}
	}
	
	protected Date parseRelativeDate(Map<String,String> dateItems) {
		return RelativeDateItem.getAbsoluteDate(new RelativeDate(dateItems.get("value")));
	}
	
	@SuppressWarnings("rawtypes")
	protected void buildExpression(Criteria criteria, StringBuffer sb, FilterType filterType, OperatorId operator, DataSource dataSource) {
		String entityKey = MVELKEYWORDMAP.get(filterType);
		Map values = criteria.getValues();
		String field = (String) values.get("fieldName");
		SupportedFieldType type = SupportedFieldType.valueOf(dataSource.getField(field).getAttribute("fieldType"));
		SupportedFieldType secondaryType = null;
		String secondaryTypeVal = dataSource.getField(field).getAttribute("secondaryFieldType");
		Object value;
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
			value = values.get("value");
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
			buildExpression(sb, entityKey, field, "null", type, secondaryType, "==", false, false, false, false, true);
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
			buildExpression(sb, entityKey, field, "null", type, secondaryType, "!=", false, false, false, false, true);
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
				buildExpression(sb, entityKey, field, values.get("start"), type, secondaryType, ">", false, false, false, false, false);
				sb.append("&&");
				buildExpression(sb, entityKey, field, values.get("end"), type, secondaryType, "<", false, false, false, false, false);
				sb.append(")");
			}
			break;
		}
		case BETWEEN_INCLUSIVE: {
			if (
				SupportedFieldType.DATE.toString().equals(type.toString())
			) {
				sb.append("(");
				buildExpression(sb, entityKey, field, extractDate(criteria, OperatorId.GREATER_OR_EQUAL, (Map) values.get("start"), "start"), type, secondaryType, ">=", false, false, false, false, false);
				sb.append("&&");
				buildExpression(sb, entityKey, field, extractDate(criteria, OperatorId.LESS_OR_EQUAL, (Map) values.get("end"), "end"), type, secondaryType, "<=", false, false, false, false, false);
				sb.append(")");
			} else {
				sb.append("(");
				buildExpression(sb, entityKey, field, values.get("start"), type, secondaryType, ">=", false, false, false, false, false);
				sb.append("&&");
				buildExpression(sb, entityKey, field, values.get("end"), type, secondaryType, "<=", false, false, false, false, false);
				sb.append(")");
			}
			break;
		}
		}
	}

	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	protected Object extractDate(Criteria criteria, OperatorId operator, Map values, String key) {
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
		return value;
	}

	protected void buildExpression(StringBuffer sb, String entityKey, String field, Object value, SupportedFieldType type, SupportedFieldType secondaryType, String operator, boolean includeParenthesis, boolean isFieldComparison, boolean ignoreCase, boolean isNegation, boolean ignoreQuotes) {
		sb.append(formatField(entityKey, type, field, ignoreCase, isNegation));
		sb.append(operator);
		if (includeParenthesis) {
			sb.append("(");
		}
		sb.append(formatValue(entityKey, type, secondaryType, value, isFieldComparison, ignoreCase, ignoreQuotes));
		if (includeParenthesis) {
			sb.append(")");
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
			response.append(".toString()");
			break;
		case MONEY:
			response.append(entityKey);
			response.append(".");
			response.append(field);
			response.append(".getAmount()");
			break;
		default:
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
		}
		return response.toString();
	}
	
	protected String formatValue(String entityKey, SupportedFieldType type, SupportedFieldType secondaryType, Object value, boolean isFieldComparison, boolean ignoreCase, boolean ignoreQuotes) {
		StringBuffer response = new StringBuffer();
		if (isFieldComparison) {
			switch(type) {
			case MONEY:
				response.append(entityKey);
				response.append(".");
				response.append(value);
				response.append(".getAmount()");
				break;
			default:
				if (ignoreCase) {
					response.append("MVEL.eval(\"toUpperCase()\",");
				}
				response.append(entityKey);
				response.append(".");
				response.append(value);
				if (ignoreCase) {
					response.append(")");
				}
				break;
			}
		} else {
			switch(type) {
			case BOOLEAN:
				response.append(value);
				break;
			case DECIMAL:
				response.append(value);
				break;
			case ID:
				if (secondaryType != null && secondaryType.toString().equals(SupportedFieldType.STRING.toString())) {
					if (ignoreCase) {
						response.append("MVEL.eval(\"toUpperCase()\",");
					}
					if (!ignoreQuotes) {
						response.append("\"");
					}
					response.append(value);
					if (!ignoreQuotes) {
						response.append("\"");
					}
					if (ignoreCase) {
						response.append(")");
					}
				} else {
					response.append(value);
				}
				break;
			case INTEGER:
				response.append(value);
				break;
			case MONEY:
				response.append(value);
				break;
			case DATE:
				DateTimeFormat formatter = DateTimeFormat.getFormat("MM/dd/yy H:mm a");
				String formattedDate = formatter.format((Date) value);
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
				response.append(value);
				if (!ignoreQuotes) {
					response.append("\"");
				}
				if (ignoreCase) {
					response.append(")");
				}
				break;
			}
		}
		return response.toString();
	}
}
