package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author by reginaldccole
 */
@Controller("blAdminUserManagementController")
@RequestMapping("/" + AdminUserManagementController.SECTION_KEY)
public class AdminUserManagementController extends AdminBasicEntityController {

    public static final String SECTION_KEY = "user-management";

    @Override
    protected String getSectionKey(Map<String, String> pathVars) {
        //allow external links to work for ToOne items
        if (super.getSectionKey(pathVars) != null) {
            return super.getSectionKey(pathVars);
        }
        return SECTION_KEY;
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
                                 @PathVariable Map<String, String> pathVars,
                                 @PathVariable(value="id") String id) throws Exception {
        // Get the normal entity form for this item
        String returnPath = super.viewEntityForm(request, response, model, pathVars, id);
        EntityForm ef = (EntityForm) model.asMap().get("entityForm");
        // Remove List Grid for Additional Fields
        ef.removeListGrid("additionalFields");

        return returnPath;
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String saveEntity(HttpServletRequest request, HttpServletResponse response, Model model,
                             @PathVariable  Map<String, String> pathVars,
                             @PathVariable(value="id") String id,
                             @ModelAttribute(value="entityForm") EntityForm entityForm, BindingResult result,
                             RedirectAttributes ra) throws Exception {

        // Get the normal entity form for this item
        String returnPath = super.saveEntity(request, response, model, pathVars, id, entityForm, result, ra);
        // Remove List Grid for Additional Fields
        entityForm.removeListGrid("additionalFields");

        return returnPath;
    }

}
