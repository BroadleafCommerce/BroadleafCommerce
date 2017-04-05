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

package org.broadleafcommerce.openadmin.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.web.form.entity.DynamicEntityFormInfo;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.broadleafcommerce.openadmin.web.form.entity.Tab;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafVariableModifierAttrProcessor;
import org.broadleafcommerce.presentation.dialect.BroadleafDialectPrefix;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.support.BindStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processor that returns all the errors within an {@link EntityForm} organized by tab, according to the expression passed
 * in as an argument.
 * 
 * For instance, if you would like to get all of the errors for the {@link EntityForm}, invoke this processor with an
 * attribute that looks like:
 * 
 *      blc_admin:errors="*{*}"
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blErrorsProcessor")
@ConditionalOnTemplating
public class ErrorsProcessor extends AbstractBroadleafVariableModifierAttrProcessor {

    protected static final Log LOG = LogFactory.getLog(ErrorsProcessor.class);

    public static final String GENERAL_ERRORS_TAB_KEY = "generalErrors";
    public static final String GENERAL_ERROR_FIELD_KEY = "generalError";

    @Value("${admin.form.validation.errors.hideTopLevelFieldErrors:false}")
    protected boolean hideTopLevelFieldErrors;

    @Override
    public String getName() {
        return "errors";
    }
    
    @Override
    public String getPrefix() {
        return BroadleafDialectPrefix.BLC_ADMIN;
    }
    
    @Override
    public int getPrecedence() {
        return 10000;
    }
    
    @Override
    public Map<String, Object> populateModelVariables(String tagName, Map<String, String> tagAttributes, String attributeName, String attributeValue, BroadleafTemplateContext context) {

        BindStatus bindStatus = context.getBindStatus(attributeValue);

        Map<String, Object> newLocalVars = new HashMap<>();
        if (bindStatus.isError()) {
            EntityForm form = (EntityForm) ((BindingResult) bindStatus.getErrors()).getTarget();

            // Map of tab name -> (Map field Name -> list of error messages)
            Map<String, Map<String, List<String>>> result = new HashMap<>();
            if (!hideTopLevelFieldErrors) {
                for (FieldError err : bindStatus.getErrors().getFieldErrors()) {
                    //attempt to look up which tab the field error is on. If it can't be found, just use
                    //the default tab for the group
                    String tabName = EntityForm.DEFAULT_TAB_NAME;
                    Tab tab = form.findTabForField(err.getField());
                    if (tab != null) {
                        tabName = tab.getTitle();
                    }

                    Map<String, List<String>> tabErrors = result.get(tabName);
                    if (tabErrors == null) {
                        tabErrors = new HashMap<>();
                        result.put(tabName, tabErrors);
                    }
                    if (err.getField().contains(DynamicEntityFormInfo.FIELD_SEPARATOR)) {
                        //at this point the field name actually occurs within some array syntax
                        String fieldName = extractFieldName(err);
                        String[] fieldInfo = fieldName.split("\\" + DynamicEntityFormInfo.FIELD_SEPARATOR);
                        Field formField = form.getDynamicForm(fieldInfo[0]).findField(fieldName);

                        if (formField != null) {
                            addFieldError(formField.getFriendlyName(), err.getCode(), tabErrors);
                        } else {
                            LOG.warn("Could not find field " + fieldName + " within the dynamic form " + fieldInfo[0]);
                            addFieldError(fieldName, err.getCode(), tabErrors);
                        }
                    } else {
                        if (form.getTabs().size() > 0) {
                            Field formField = form.findField(err.getField());
                            if (formField != null) {
                                addFieldError(formField.getFriendlyName(), err.getCode(), tabErrors);
                            } else {
                                LOG.warn("Could not find field " + err.getField() + " within the main form");
                                addFieldError(err.getField(), err.getCode(), tabErrors);
                            }
                        } else {
                            //this is the code that is executed when a Translations add action contains errors
                            //this branch of the code just puts a placeholder "tabErrors", to avoid errprProcessor parsing errors, and
                            //avoids checking on tabs, fieldGroups or fields (which for translations are empty), thus skipping any warning
                            newLocalVars.put("tabErrors", tabErrors);
                            return newLocalVars;
                        }
                    }
                }
            }

            String translatedGeneralTab = GENERAL_ERRORS_TAB_KEY;
            BroadleafRequestContext blcContext = BroadleafRequestContext.getBroadleafRequestContext();
            if (blcContext != null && blcContext.getMessageSource() != null) {
                translatedGeneralTab = blcContext.getMessageSource().getMessage(translatedGeneralTab, null, translatedGeneralTab, blcContext.getJavaLocale());
            }

            for (ObjectError err : bindStatus.getErrors().getGlobalErrors()) {
                Map<String, List<String>> tabErrors = result.get(GENERAL_ERRORS_TAB_KEY);
                if (tabErrors == null) {
                    tabErrors = new HashMap<>();
                    result.put(translatedGeneralTab, tabErrors);
                }
                addFieldError(GENERAL_ERROR_FIELD_KEY, err.getCode(), tabErrors);
            }

            newLocalVars.put("tabErrors", result);
        }
        return newLocalVars;
    }

    private String extractFieldName(FieldError err) {
        String fieldExpression = err.getField();
        String fieldName = StringUtil.extractFieldNameFromExpression(fieldExpression);
        return fieldName;
    }

    protected void addFieldError(String fieldName, String message, Map<String, List<String>> tabErrors) {
        List<String> messages = tabErrors.get(fieldName);
        if (messages == null) {
            messages = new ArrayList<>();
            tabErrors.put(fieldName, messages);
        }
        messages.add(message);
    }

}
