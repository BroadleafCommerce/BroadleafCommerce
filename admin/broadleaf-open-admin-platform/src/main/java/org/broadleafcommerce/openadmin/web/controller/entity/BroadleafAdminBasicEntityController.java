/*
 * Copyright 2008-2012 the original author or authors.
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

package org.broadleafcommerce.openadmin.web.controller.entity;

import org.broadleafcommerce.openadmin.web.form.component.CriteriaForm;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The default implementation of the {@link #BroadleafAdminAbstractEntityController}. This delegates every call to 
 * super and does not provide any custom-tailored functionality. It is responsible for rendering the admin for every
 * entity that is not explicitly customized by its own controller.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Controller("blAdminBasicEntityController")
@RequestMapping("/{sectionKey}")
public class BroadleafAdminBasicEntityController extends BroadleafAdminAbstractEntityController {

    @Override
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewEntityList(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String sectionKey,
            @ModelAttribute CriteriaForm criteriaForm) throws Exception {
        return super.viewEntityList(request, response, model, sectionKey, criteriaForm);
    }

    @Override
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String viewAddEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String sectionKey,
            @RequestParam(defaultValue = "") String entityType) throws Exception {
        return super.viewAddEntityForm(request, response, model, sectionKey, entityType);
    }

    @Override
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String sectionKey,
            @ModelAttribute EntityForm entityForm, BindingResult result) throws Exception {
        return super.addEntity(request, response, model, sectionKey, entityForm, result);
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String sectionKey,
            @PathVariable String id) throws Exception {
        return super.viewEntityForm(request, response, model, sectionKey, id);
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String saveEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String sectionKey,
            @PathVariable String id,
            @ModelAttribute EntityForm entityForm, BindingResult result,
            RedirectAttributes ra) throws Exception {
        return super.saveEntity(request, response, model, sectionKey, id, entityForm, result, ra);
    }

    @Override
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    public String removeEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String sectionKey,
            @PathVariable String id,
            @ModelAttribute EntityForm entityForm, BindingResult result) throws Exception {
        return super.removeEntity(request, response, model, sectionKey, id, entityForm, result);
    }

    @Override
    @RequestMapping(value = "/{collectionField}/select", method = RequestMethod.GET)
    public String showSelectCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String sectionKey,
            @PathVariable String collectionField,
            @ModelAttribute CriteriaForm criteriaForm) throws Exception {
        return super.showSelectCollectionItem(request, response, model, sectionKey, collectionField, criteriaForm);
    }
    
    @Override
    @RequestMapping(value = "/{collectionField}/{id}/view", method = RequestMethod.GET)
    public String viewCollectionItemDetails(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String sectionKey,
            @PathVariable String collectionField,
            @PathVariable String id) throws Exception {
        return super.viewCollectionItemDetails(request, response, model, sectionKey, collectionField, id);
    }

    @Override
    @RequestMapping(value = "/{id}/{collectionField}", method = RequestMethod.GET)
    public String getCollectionFieldRecords(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String sectionKey,
            @PathVariable String id,
            @PathVariable String collectionField, @ModelAttribute CriteriaForm criteriaForm) throws Exception {
        return super.getCollectionFieldRecords(request, response, model, sectionKey, id, collectionField, criteriaForm);
    }

    @Override
    @RequestMapping(value = "/{id}/{collectionField}/add", method = RequestMethod.GET)
    public String showAddCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String sectionKey,
            @PathVariable String id,
            @PathVariable String collectionField) throws Exception {
        return super.showAddCollectionItem(request, response, model, sectionKey, id, collectionField);
    }

    @Override
    @RequestMapping(value = "/{id}/{collectionField}/{collectionItemId}", method = RequestMethod.GET)
    public String showUpdateCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String sectionKey,
            @PathVariable String id,
            @PathVariable String collectionField,
            @PathVariable String collectionItemId) throws Exception {
        return super.showUpdateCollectionItem(request, response, model, sectionKey, id, collectionField, collectionItemId);
    }

    @Override
    @RequestMapping(value = "/{id}/{collectionField}/add", method = RequestMethod.POST)
    public String addCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String sectionKey,
            @PathVariable String id,
            @PathVariable String collectionField,
            @ModelAttribute EntityForm entityForm) throws Exception {
        return super.addCollectionItem(request, response, model, sectionKey, id, collectionField, entityForm);
    }

    @Override
    @RequestMapping(value = "/{id}/{collectionField}/{collectionItemId}", method = RequestMethod.POST)
    public String updateCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String sectionKey,
            @PathVariable String id,
            @PathVariable String collectionField,
            @PathVariable String collectionItemId,
            @ModelAttribute EntityForm entityForm) throws Exception {
        return super.updateCollectionItem(request, response, model, sectionKey, id, collectionField, collectionItemId, 
                entityForm);
    }

    @Override
    @RequestMapping(value = "/{id}/{collectionField}/{collectionItemId}/delete", method = RequestMethod.POST)
    public String removeCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String sectionKey,
            @PathVariable String id,
            @PathVariable String collectionField,
            @PathVariable String collectionItemId) throws Exception {
        return super.removeCollectionItem(request, response, model, sectionKey, id, collectionField, collectionItemId);
    }
    
    @Override
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        super.initBinder(binder);
    }
    
}
