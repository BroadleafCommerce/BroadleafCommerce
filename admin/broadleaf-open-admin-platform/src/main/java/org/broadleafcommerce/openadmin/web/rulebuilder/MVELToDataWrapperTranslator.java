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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.util.StringUtil;
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

    public static final String SUB_GROUP_MESSAGE = "MVEL phrase is not compatible with nested expressions. " +
            "Please use the rule builder to re-structure the rule.  Use top-level expressions, " +
            "product groups and/or customer segments.";

    protected GroupingTranslator groupingTranslator = new GroupingTranslator();
    protected PhraseTranslator phraseTranslator = new PhraseTranslator();

    public DataWrapper createRuleData(Entity[] entities, String mvelProperty, String quantityProperty, String idProperty, RuleBuilderFieldService fieldService) {
        return createRuleData(entities, mvelProperty, quantityProperty, idProperty, null, fieldService);
    }

    public DataWrapper createRuleData(Entity[] entities, String mvelProperty, String quantityProperty, String idProperty, String containedProperty, RuleBuilderFieldService fieldService) {
        if (entities == null || entities.length == 0 || mvelProperty == null) {
            return null;
        }

        DataWrapper dataWrapper = new DataWrapper();
        String mvel = null;
        try {
            for (Entity e : entities) {
                Integer qty = null;
                Long id = null;
                Long containedId = null;
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

                    if (containedProperty != null && containedProperty.equals(p.getName())) {
                        containedId = Long.parseLong(p.getValue());
                    }
                }

                if (mvel != null) {
                    Group group = groupingTranslator.createGroups(mvel);
                    DataDTO dataDTO = createRuleDataDTO(null, group, fieldService);
                    if (dataDTO != null) {
                        dataDTO.setPk(id);
                        dataDTO.setContainedPk(containedId);
                        dataDTO.setQuantity(qty);
                        dataWrapper.getData().add(dataDTO);

                        if (group.getSubGroups().size() > 0) {
                            Boolean invalidSubGroupFound = checkForInvalidSubGroup(dataDTO);

                            if (invalidSubGroupFound) {
                                throw new MVELTranslationException(MVELTranslationException.SUB_GROUP_DETECTED, SUB_GROUP_MESSAGE);
                            }
                        }
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

    protected Boolean checkForInvalidSubGroup(DataDTO dataDTO) {
        Boolean invalidSubGroupFound = false;

        for (DataDTO rules : dataDTO.getRules()) {
            ArrayList<DataDTO> subRules = rules.getRules();

            if (subRules != null && subRules.size() == 2) {
                ExpressionDTO expression1 = (ExpressionDTO) subRules.get(0);
                ExpressionDTO expression2 = (ExpressionDTO) subRules.get(1);

                Boolean isBetweenDetected = false;
                Boolean isBetweenInclusiveDetected = false;

                if (expression1.getOperator().equals("GREATER_THAN") && expression2.getOperator().equals("LESS_THAN")) {
                    isBetweenDetected = true;
                }

                if (expression1.getOperator().equals("GREATER_OR_EQUAL") && expression2.getOperator().equals("LESS_OR_EQUAL")) {
                    isBetweenInclusiveDetected = true;
                }

                if (!isBetweenDetected && !isBetweenInclusiveDetected) {
                    invalidSubGroupFound = true;
                }
            } else {
                invalidSubGroupFound = true;
            }
        }
        return invalidSubGroupFound;
    }

    protected DataDTO createRuleDataDTO(DataDTO parentDTO, Group group, RuleBuilderFieldService fieldService)
        throws MVELTranslationException {
        DataDTO data = new DataDTO();
        if (group.getOperatorType() == null) {
            group.setOperatorType(BLCOperator.AND);
        }
        BLCOperator operator = group.getOperatorType();
        data.setCondition(operator.name());
        List<ExpressionDTO> myCriteriaList = new ArrayList<ExpressionDTO>();
        if (BLCOperator.NOT.equals(group.getOperatorType()) && group.getIsTopGroup()) {
            group = group.getSubGroups().get(0);
            group.setOperatorType(operator);
        }
        for (String phrase : group.getPhrases()) {
            appendExpression(phrase, fieldService, parentDTO, myCriteriaList);
        }
        if (!myCriteriaList.isEmpty()) {
            data.getRules().addAll(myCriteriaList);
        }
        for (Group subgroup : group.getSubGroups()) {
            DataDTO subCriteria = createRuleDataDTO(data, subgroup, fieldService);
            if (subCriteria != null && !subCriteria.getRules().isEmpty()) {
                data.getRules().add(subCriteria);
            }
        }
        if (data.getRules() != null && !data.getRules().isEmpty()) {
            return data;
        } else {
            return null;
        }
    }

    public void appendExpression(String phrase, RuleBuilderFieldService fieldService, DataDTO parentDTO,
                                 List<ExpressionDTO> myCriteriaList) throws MVELTranslationException {
        Expression expression = phraseTranslator.createExpression(phrase);
        FieldDTO field = fieldService.getField(expression.getField());
        if (field == null) {
            throw new MVELTranslationException(MVELTranslationException.SPECIFIED_FIELD_NOT_FOUND, "MVEL phrase is not " +
                    "compatible with the RuleBuilderFieldService associated with the current rules builder. " +
                    "Unable to find the field specified: ("+expression.getField()+")");
        }
        SupportedFieldType type = fieldService.getSupportedFieldType(expression.getField());
        ExpressionDTO expressionDTO = createExpressionDTO(expression);

        postProcessCriteria(parentDTO, myCriteriaList, expressionDTO, type);
    }

    public ExpressionDTO createExpressionDTO(Expression expression) {
        ExpressionDTO expressionDTO = new ExpressionDTO();
        expressionDTO.setId(expression.getField());
        expressionDTO.setOperator(expression.getOperator().name());
        expressionDTO.setValue(expression.getValue());
        return expressionDTO;
    }

    public boolean isProjection(Object value) {
        String stringValue = value.toString().trim();
        return stringValue.startsWith("[") && stringValue.endsWith("]") && stringValue.indexOf(",") > 0;
    }

    protected void postProcessCriteria(DataDTO parentDTO, List<ExpressionDTO> myCriteriaList,
                                       ExpressionDTO temp, SupportedFieldType type) {
        int lstIdx = myCriteriaList.size() - 1;
        ExpressionDTO prevExpression = lstIdx != -1 ? myCriteriaList.get(lstIdx) : null;
        boolean sameExpressionId = prevExpression != null && temp.getId().equals(prevExpression.getId());
        
        if (sameExpressionId && isBetweenOperator(prevExpression, temp)) {
            prevExpression.setOperator(BLCOperator.BETWEEN.name());
            boolean hasTempSmallerVal = Long.parseLong(temp.getValue()) < Long.parseLong(prevExpression.getValue());
            String start = hasTempSmallerVal ? temp.getValue() : prevExpression.getValue();
            String end = hasTempSmallerVal ? prevExpression.getValue() : temp.getValue();
            if (type.toString().equals(SupportedFieldType.DATE.toString())) {
                start = "\"" + start + "\"" ;
                end = "\"" + end + "\"";
            }
            prevExpression.setValue("[" + start + "," + end + "]");
            if (parentDTO != null) {
                parentDTO.getRules().add(myCriteriaList.remove(lstIdx));
            }
        } else if (sameExpressionId && isBetweenInclusiveOperator(prevExpression, temp)) {
            prevExpression.setOperator(BLCOperator.BETWEEN_INCLUSIVE.name());
            boolean hasTempSmallerVal = Long.parseLong(temp.getValue()) < Long.parseLong(prevExpression.getValue());
            String start = hasTempSmallerVal ? temp.getValue() : prevExpression.getValue();
            String end = hasTempSmallerVal ? prevExpression.getValue() : temp.getValue();
            if (type.toString().equals(SupportedFieldType.DATE.toString())) {
                start = "\"" + start + "\"" ;
                end = "\"" + end + "\"";
            }
            prevExpression.setValue("[" + start + "," + end + "]");
            if (parentDTO != null) {
                parentDTO.getRules().add(myCriteriaList.remove(lstIdx));
            }
        } else if (isProjection(temp.getValue())) {
            if (parentDTO != null) {
                parentDTO.getRules().add(temp);
            } else {
                myCriteriaList.add(temp);
            }
        } else {
            myCriteriaList.add(temp);
        }
    }

    protected boolean isBetweenOperator(final ExpressionDTO prev, final ExpressionDTO temp) {
        boolean isBetweenOperator;
        final String prevOperator = prev.getOperator();
        final String tempOperator = temp.getOperator();
        
        try {
            final long prevVal = Long.parseLong(prev.getValue());
            final long tempVal = Long.parseLong(temp.getValue());

            isBetweenOperator = (tempVal > prevVal && prevOperator.equals(BLCOperator.GREATER_THAN.name()) 
                                 && tempOperator.equals(BLCOperator.LESS_THAN.name())) 
                                || (prevVal > tempVal && prevOperator.equals(BLCOperator.LESS_THAN.name()) 
                                    && tempOperator.equals(BLCOperator.GREATER_THAN.name()) && tempVal < prevVal);
        } catch (final NumberFormatException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Trying to parse a non-long value to a long: %s, %s", 
                                        StringUtil.sanitize(prev.getValue()), StringUtil.sanitize(temp.getValue())));
            }
            
            isBetweenOperator = false;
        }
        
        return isBetweenOperator;
    }

    protected boolean isBetweenInclusiveOperator(final ExpressionDTO prev, final ExpressionDTO temp) {
        boolean isBetweenOperator;
        final String prevOperator = prev.getOperator();
        final String tempOperator = temp.getOperator();
        
        try {
            final long prevVal = Long.parseLong(prev.getValue());
            final long tempVal = Long.parseLong(temp.getValue());
            isBetweenOperator = (tempVal >= prevVal && prevOperator.equals(BLCOperator.GREATER_OR_EQUAL.name()) 
                                 && tempOperator.equals(BLCOperator.LESS_OR_EQUAL.name())) 
                                || (prevVal >= tempVal && prevOperator.equals(BLCOperator.LESS_OR_EQUAL.name()) 
                                    && tempOperator.equals(BLCOperator.GREATER_OR_EQUAL.name()) && tempVal <= prevVal);
        } catch (final NumberFormatException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Trying to parse a non-long value to a long: %s, %s",
                                        StringUtil.sanitize(prev.getValue()), StringUtil.sanitize(temp.getValue())));
            }

            isBetweenOperator = false;
        }
        
        return isBetweenOperator;
    }

}
