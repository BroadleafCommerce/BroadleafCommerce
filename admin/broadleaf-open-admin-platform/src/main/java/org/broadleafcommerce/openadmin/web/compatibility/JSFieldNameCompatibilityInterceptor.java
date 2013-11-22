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
package org.broadleafcommerce.openadmin.web.compatibility;

import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.JSCompatibilityHelper;
import org.broadleafcommerce.openadmin.web.form.component.RuleBuilderField;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

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
                for (Property property : entity.getProperties()) {
                    if (property.getName().contains(".")) {
                        property.setName(JSCompatibilityHelper.encode(property.getName()));
                    }
                }
            }
    
            if (entityForm != null) {
                entityForm.clearFieldsMap();
                for (Map.Entry<String, Field> field : entityForm.getFields().entrySet()) {
                    if (field.getKey().contains(".")) {
                        field.getValue().setName(JSCompatibilityHelper.encode(field.getValue().getName()));
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
