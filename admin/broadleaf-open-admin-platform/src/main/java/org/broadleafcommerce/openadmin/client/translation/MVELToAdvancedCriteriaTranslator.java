/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.translation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.translation.grouping.Group;
import org.broadleafcommerce.openadmin.client.translation.grouping.GroupingTranslator;
import org.broadleafcommerce.openadmin.client.translation.statement.Expression;
import org.broadleafcommerce.openadmin.client.translation.statement.PhraseTranslator;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criterion;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.util.JSOHelper;

/**
 * 
 * @author jfischer
 *
 */
public class MVELToAdvancedCriteriaTranslator {
    
    protected GroupingTranslator groupingTranslator = new GroupingTranslator();
    protected PhraseTranslator phraseTranslator = new PhraseTranslator();

    public AdvancedCriteria createAdvancedCriteria(String mvel, DataSource dataSource) throws IncompatibleMVELTranslationException {
        if (mvel == null || mvel.length() == 0) {
            return null;
        }
        Group group = groupingTranslator.createGroups(mvel);
        return createAdvancedCriteria(null, group, dataSource);
    }
    
    protected AdvancedCriteria createAdvancedCriteria(AdvancedCriteria parentCriteria, Group group, DataSource dataSource) throws IncompatibleMVELTranslationException {
        AdvancedCriteria myCriteria = new AdvancedCriteria();
        if (group.getOperatorType() == null) {
            group.setOperatorType(OperatorId.AND);
        }
        OperatorId operator = group.getOperatorType();
        myCriteria.setAttribute("operator", operator);
        List<Criterion> myCriteriaList = new ArrayList<Criterion>();
        if (group.getOperatorType().getValue().equals(OperatorId.NOT.getValue()) && group.getIsTopGroup()) {
            group = group.getSubGroups().get(0);
            group.setOperatorType(operator);
        }
        int j = 0;
        for (String phrase : group.getPhrases()) {
            appendCriteria(phrase, dataSource, j, parentCriteria, group.getOperatorType(), myCriteriaList);
            j++;
        }
        if (myCriteriaList.size() > 0) {
            Criterion[] convertedList = myCriteriaList.toArray(new Criterion[]{});
            myCriteria.buildCriterionFromList(group.getOperatorType(), convertedList);
        }
        if (group.getSubGroups().size() > 0) {
            for (Group subgroup : group.getSubGroups()) {
                AdvancedCriteria subCriteria = createAdvancedCriteria(myCriteria, subgroup, dataSource);
                JavaScriptObject listJS = subCriteria.getAttributeAsJavaScriptObject("criteria");
                if (JSOHelper.isArray(listJS)) {
                    myCriteria.appendToCriterionList(subCriteria);
                }
            }
        }
        
        return myCriteria;
    }
    
    public void appendCriteria(String phrase, DataSource dataSource, int count, AdvancedCriteria parentCriteria, OperatorId groupOperator, List<Criterion> myCriteriaList) throws IncompatibleMVELTranslationException {
        Expression expression = phraseTranslator.createExpression(phrase);
        DataSourceField field = dataSource.getField(expression.getField());
        if (field == null) {
            throw new IncompatibleMVELTranslationException("MVEL phrase is not compatible with the datasource associated with the current rules builder. Unable to find the field specified: ("+expression.getField()+")");
        }
        SupportedFieldType type = SupportedFieldType.valueOf(field.getAttribute("fieldType"));
        SupportedFieldType secondaryType = null;
        String secondaryTypeVal = dataSource.getField(expression.getField()).getAttribute("secondaryFieldType");
        if (secondaryTypeVal != null) {
            secondaryType = SupportedFieldType.valueOf(secondaryTypeVal);
        }
        AdvancedCriteria criteria = createCriteria(expression, type, secondaryType);
        
        postProcessCriteria(parentCriteria, groupOperator, myCriteriaList, count, criteria, type);
    }

    protected AdvancedCriteria createCriteria(Expression expression, SupportedFieldType type, SupportedFieldType secondaryType) throws NumberFormatException, IllegalArgumentException {
        AdvancedCriteria criteria;
        switch(type) {
        case DATE:
            DateTimeFormat formatter = DateTimeFormat.getFormat("MM/dd/yy H:mm a Z");
            Date parsedDate = formatter.parse(expression.getValue());
            criteria = new AdvancedCriteria(expression.getField(), expression.getOperator(), parsedDate);
            break;
        default:
            criteria = new AdvancedCriteria(expression.getField(), expression.getOperator(), expression.getValue());
            break;
        }
        return criteria;
    }
    
    public boolean isProjection(Object value) {
        String stringValue = value.toString().trim();
        return stringValue.startsWith("[") && stringValue.endsWith("]") && stringValue.indexOf(",") > 0;
    }
    
    protected void postProcessCriteria(AdvancedCriteria parentCriteria, OperatorId groupOperator, List<Criterion> myCriteriaList, int count, Criterion temp, SupportedFieldType type) {
        if (
            count > 0 && 
            temp.getFieldName().equals(myCriteriaList.get(count-1).getFieldName()) &&
            myCriteriaList.get(count-1).getOperator().getValue().equals(OperatorId.GREATER_THAN.getValue()) &&
            temp.getOperator().getValue().equals(OperatorId.LESS_THAN.getValue())
        ) {
            myCriteriaList.get(count-1).setAttribute("operator", OperatorId.BETWEEN.getValue());
            Object start;
            Object end;
            if (type.toString().equals(SupportedFieldType.DATE.toString())) {
                start = myCriteriaList.get(count-1).getAttributeAsObject("value");
                end = temp.getAttributeAsObject("value");
            } else {
                start = myCriteriaList.get(count-1).getAttribute("value");
                end = temp.getAttribute("value");
            }
            myCriteriaList.get(count-1).setAttribute("start", start);
            myCriteriaList.get(count-1).setAttribute("end", end);
            if (parentCriteria != null) {
                JavaScriptObject listJS = parentCriteria.getAttributeAsJavaScriptObject("criteria");
                if (!JSOHelper.isArray(listJS)) {
                    parentCriteria.setAttribute("criteria", JSOHelper.createJavaScriptArray());
                }
                parentCriteria.appendToCriterionList(myCriteriaList.remove(count-1));
            }
        } else if (
            count > 0 && 
            temp.getFieldName().equals(myCriteriaList.get(count-1).getFieldName()) &&
            myCriteriaList.get(count-1).getOperator().getValue().equals(OperatorId.GREATER_OR_EQUAL.getValue()) &&
            temp.getOperator().getValue().equals(OperatorId.LESS_OR_EQUAL.getValue())
        ) {
            myCriteriaList.get(count-1).setAttribute("operator", OperatorId.BETWEEN_INCLUSIVE.getValue());
            Object start;
            Object end;
            if (type.toString().equals(SupportedFieldType.DATE.toString())) {
                start = myCriteriaList.get(count-1).getAttributeAsObject("value");
                end = temp.getAttributeAsObject("value");
            } else {
                start = myCriteriaList.get(count-1).getAttribute("value");
                end = temp.getAttribute("value");
            }
            myCriteriaList.get(count-1).setAttribute("start", start);
            myCriteriaList.get(count-1).setAttribute("end", end);
            if (parentCriteria != null) {
                JavaScriptObject listJS = parentCriteria.getAttributeAsJavaScriptObject("criteria");
                if (!JSOHelper.isArray(listJS)) {
                    parentCriteria.setAttribute("criteria", JSOHelper.createJavaScriptArray());
                }
                parentCriteria.appendToCriterionList(myCriteriaList.remove(count-1));
            }
        } else if (
            isProjection(temp.getAttribute("value"))
        ) {
            if (parentCriteria != null) {
                JavaScriptObject listJS = parentCriteria.getAttributeAsJavaScriptObject("criteria");
                if (!JSOHelper.isArray(listJS)) {
                    parentCriteria.setAttribute("criteria", JSOHelper.createJavaScriptArray());
                }
                parentCriteria.appendToCriterionList(temp);
            } else {
                myCriteriaList.add(temp);
            }
        } else {
            myCriteriaList.add(temp);
        }
    }
}
