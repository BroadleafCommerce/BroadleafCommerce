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
package org.broadleafcommerce.openadmin.web.compatibility;

import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.JSCompatibilityHelper;
import org.broadleafcommerce.openadmin.web.form.component.RuleBuilderField;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jeff Fischer
 */
public class JSFieldNameCompatibilityInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView
            modelAndView) throws Exception {
        if (modelAndView != null) {
            Entity entity = (Entity) modelAndView.getModelMap().get("entity");
            EntityForm entityForm = (EntityForm) modelAndView.getModelMap().get("entityForm");
    
            if (entity != null) {
                if (entity.getProperties()!=null){
                for (Property property : entity.getProperties()) {
                    if (property.getName().contains(".")) {
                        property.setName(JSCompatibilityHelper.encode(property.getName()));
                    }
                }
                }
            }
    
            if (entityForm != null) {
                entityForm.clearFieldsMap();
                for (Map.Entry<String, Field> field : entityForm.getFields().entrySet()) {
                    if (field.getKey().contains(".")) {
                        field.getValue().setName(JSCompatibilityHelper.encode(field.getValue().getName()));
                        field.getValue().setAssociatedFieldName(JSCompatibilityHelper.encode(field.getValue().getAssociatedFieldName()));
                        if (field.getValue() instanceof RuleBuilderField) {
                            ((RuleBuilderField) field.getValue()).setJsonFieldName(JSCompatibilityHelper.encode((
                                    (RuleBuilderField) field.getValue()).getJsonFieldName()));
                        }
                    }
                }
                entityForm.clearFieldsMap();
            }
        }
    }
}
