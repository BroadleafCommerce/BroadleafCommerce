package org.broadleafcommerce.openadmin.web.interceptor;

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
                for (Property property : entity.getProperties()) {
                    if (property.getName().contains(".")) {
                        property.setName(JSCompatibilityHelper.encodeFieldName(property.getName()));
                    }
                }
            }
    
            if (entityForm != null) {
                entityForm.clearFieldsMap();
                for (Map.Entry<String, Field> field : entityForm.getFields().entrySet()) {
                    if (field.getKey().contains(".")) {
                        field.getValue().setName(JSCompatibilityHelper.encodeFieldName(field.getValue().getName()));
                        if (field.getValue() instanceof RuleBuilderField) {
                            ((RuleBuilderField) field.getValue()).setJsonFieldName(JSCompatibilityHelper.encodeFieldName(((RuleBuilderField) field.getValue()).getJsonFieldName()));
                        }
                    }
                }
                entityForm.clearFieldsMap();
            }
        }
    }
}
