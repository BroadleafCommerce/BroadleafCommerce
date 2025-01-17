/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafAttributeModifierProcessor;
import org.broadleafcommerce.presentation.model.BroadleafAttributeModifier;
import org.broadleafcommerce.presentation.model.BroadleafBindStatus;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("blErrorsProcessor")
@ConditionalOnTemplating
public class ErrorsProcessor extends AbstractBroadleafAttributeModifierProcessor {
    protected static final Log LOG = LogFactory.getLog(ErrorsProcessor.class);

    public static final String GENERAL_ERRORS_TAB_KEY = "generalErrors";
    public static final String GENERAL_ERROR_FIELD_KEY = "generalError";

    @Value("${admin.form.validation.errors.hideTopLevelFieldErrors:false}")
    protected boolean hideTopLevelFieldErrors;

    public String getName() {
        return "errors";
    }

    public String getPrefix() {
        return "blc_admin";
    }

    public int getPrecedence() {
        return 10000;
    }

    @Override
    public BroadleafAttributeModifier getModifiedAttributes(
            String tagName,
            Map<String, String> tagAttributes,
            String attributeName,
            String attributeValue,
            BroadleafTemplateContext context
    ) {
        BroadleafBindStatus bindStatus = context.getBindStatus(attributeValue);
        
        if (bindStatus.isError()) {
            // Map of tab name -> (Map field Name -> list of error messages)
            Map<String, Map<String, List<String>>> result = new HashMap<>();
            handleFieldErrors(bindStatus, result);
            handleGlobalErrors(bindStatus, result);
            context.setNodeLocalVariable(null, "tabErrors", result);
        }
        return new BroadleafAttributeModifier();
    }

    protected void handleFieldErrors(BroadleafBindStatus bindStatus, Map<String, Map<String, List<String>>> result) {
        if (!hideTopLevelFieldErrors) {
            EntityForm form = (EntityForm) ((BindingResult) bindStatus.getErrors()).getTarget();
            for (FieldError err : bindStatus.getErrors().getFieldErrors()) {
                String errField = err.getField();
                String tabName = getTabName(form, errField);

                Map<String, List<String>> tabErrors = result.computeIfAbsent(tabName, k -> new HashMap<>());
                if (errField.contains(DynamicEntityFormInfo.FIELD_SEPARATOR)) {
                    //at this point the field name actually occurs within some array syntax
                    addFieldErrorForDynamicForm(form, err, tabErrors);
                } else {
                    addFieldErrorForMainForm(form, err, errField, tabErrors);
                }
            }
        }
    }

    protected void addFieldErrorForMainForm(EntityForm form, FieldError err, String errField, Map<String, List<String>> tabErrors) {
        if (form.getTabs().size() > 0) {
            Field formField = form.findField(errField);
            if (formField != null) {
                addFieldError(formField.getFriendlyName(), err.getCode(), tabErrors);
            } else {
                LOG.warn(MessageFormat.format("Could not find field {0} within the main form", errField));
                addFieldError(errField, err.getCode(), tabErrors);
            }
        }
    }

    protected void addFieldErrorForDynamicForm(EntityForm form, FieldError err, Map<String, List<String>> tabErrors) {
        String fieldName = extractFieldName(err);
        String[] fieldInfo = fieldName.split("\\" + DynamicEntityFormInfo.FIELD_SEPARATOR);
        EntityForm dynamicForm = form.getDynamicForm(fieldInfo[0]);
        Field formField = dynamicForm.findField(fieldName);

        if (formField != null) {
            addFieldError(formField.getFriendlyName(), err.getCode(), tabErrors);
        } else {
            LOG.warn(MessageFormat.format("Could not find field {0} within the dynamic form {1}", fieldName, fieldInfo[0]));
            addFieldError(fieldName, err.getCode(), tabErrors);
        }
    }

    protected String getTabName(EntityForm form, String errField) {
        String tabName = EntityForm.DEFAULT_TAB_NAME;
        Tab tab = form.findTabForField(errField);
        if (tab != null) {
            tabName = tab.getTitle();
        }
        return tabName;
    }

    protected void handleGlobalErrors(BroadleafBindStatus bindStatus, Map<String, Map<String, List<String>>> result) {
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
    }

    protected String extractFieldName(FieldError err) {
        String fieldExpression = err.getField();
        return StringUtil.extractFieldNameFromExpression(fieldExpression);
    }

    protected void addFieldError(String fieldName, String message, Map<String, List<String>> tabErrors) {
        List<String> messages = tabErrors.computeIfAbsent(fieldName, k -> new ArrayList<>());
        messages.add(message);
    }

}

