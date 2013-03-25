package org.broadleafcommerce.openadmin.web.controller.entity;

import org.broadleafcommerce.openadmin.web.form.component.CriteriaForm;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jeff Fischer
 */
public class BroadleafAdminDeclaredSectionAbstractEntityController extends BroadleafAdminAbstractEntityController {

    protected String sectionKey;

    public BroadleafAdminDeclaredSectionAbstractEntityController(String sectionKey) {
        this.sectionKey = sectionKey;
    }

    public String viewEntityList(HttpServletRequest request, HttpServletResponse response, Model model,
            CriteriaForm criteriaForm) throws Exception {
        return super.viewEntityList(request, response, model, sectionKey, criteriaForm);
    }

    public String viewAddEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            String entityType) throws Exception {
        return super.viewAddEntityForm(request, response, model, sectionKey, entityType);
    }

    public String addEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            EntityForm entityForm, BindingResult result) throws Exception {
        return super.addEntity(request, response, model, sectionKey, entityForm, result);
    }

    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            String id) throws Exception {
        return super.viewEntityForm(request, response, model, sectionKey, id);
    }

    public String saveEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            String id, EntityForm entityForm, BindingResult result,
            RedirectAttributes ra) throws Exception {
        return super.saveEntity(request, response, model, sectionKey, id, entityForm, result, ra);
    }

    public String removeEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            String id,
            EntityForm entityForm, BindingResult result) throws Exception {
        return super.removeEntity(request, response, model, sectionKey, id, entityForm, result);
    }

    public String showSelectCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            String collectionField, CriteriaForm criteriaForm) throws Exception {
        return super.showSelectCollectionItem(request, response, model, sectionKey, collectionField, criteriaForm);
    }

    public String showAddCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            String id,
            String collectionField) throws Exception {
        return super.showAddCollectionItem(request, response, model, sectionKey, id, collectionField);
    }

    public String showUpdateCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            String id,
            String collectionField,
            String collectionItemId) throws Exception {
        return super.showUpdateCollectionItem(request, response, model, sectionKey, id, collectionField, collectionItemId);
    }

    public String addCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            String id,
            String collectionField,
            EntityForm entityForm) throws Exception {
        return super.addCollectionItem(request, response, model, sectionKey, id, collectionField, entityForm);
    }

    public String updateCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            String id,
            String collectionField,
            String collectionItemId,
            EntityForm entityForm) throws Exception {
        return super.updateCollectionItem(request, response, model, sectionKey, id, collectionField, collectionItemId,
                entityForm);
    }

    public String removeCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            String id,
            String collectionField,
            String collectionItemId) throws Exception {
        return super.removeCollectionItem(request, response, model, sectionKey, id, collectionField, collectionItemId);
    }
}
