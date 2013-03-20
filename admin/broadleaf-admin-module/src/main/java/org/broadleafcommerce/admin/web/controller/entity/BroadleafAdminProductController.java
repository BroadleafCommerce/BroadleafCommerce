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

package org.broadleafcommerce.admin.web.controller.entity;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.openadmin.web.controller.entity.BroadleafAdminAbstractEntityController;
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
 * Handles admin operations for the {@link StructuredContent} entity. This entity has fields that are 
 * dependent on the value of the {@link StructuredContent#getStructuredContentType()} field, and as such,
 * it deviates from the typical {@link BroadleafAdminAbstractEntityController}.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Controller("blAdminProductController")
@RequestMapping("/product")
public class BroadleafAdminProductController extends BroadleafAdminAbstractEntityController {
    
    /* ****************** */
    /* INTERESTING THINGS */
    /* ****************** */
    
    protected static final String SECTION_KEY = "product";
    
    @Override
    public String[] getSectionCustomCriteria() {
        return new String[]{"productDirectEdit"};
    }
    
    /* ***************** */
    /* BOILERPLATE CALLS */
    /* ***************** */

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id) throws Exception {
        return super.viewEntityForm(request, response, model, SECTION_KEY, id);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String saveEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @ModelAttribute EntityForm entityForm, BindingResult result,
            RedirectAttributes ra) throws Exception {
        return super.saveEntity(request, response, model, SECTION_KEY, id, entityForm, result, ra);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewEntityList(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        return super.viewEntityList(request, response, model, SECTION_KEY);
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String viewAddEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @RequestParam(defaultValue = "") String entityType) throws Exception {
        return super.viewAddEntityForm(request, response, model, SECTION_KEY, entityType);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute EntityForm entityForm, BindingResult result) throws Exception {
        return super.addEntity(request, response, model, SECTION_KEY, entityForm, result);
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    public String removeEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @ModelAttribute EntityForm entityForm, BindingResult result) throws Exception {
        return super.removeEntity(request, response, model, SECTION_KEY, id, entityForm, result);
    }
    
    @RequestMapping(value = "/{collectionField}/select", method = RequestMethod.GET)
    public String showSelectCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String collectionField) throws Exception {
        return super.showSelectCollectionItem(request, response, model, SECTION_KEY, collectionField);
    }

    @RequestMapping(value = "/{id}/{collectionField}/add", method = RequestMethod.GET)
    public String showAddCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @PathVariable String collectionField) throws Exception {
        return super.showAddCollectionItem(request, response, model, SECTION_KEY, id, collectionField);
    }

    @RequestMapping(value = "/{id}/{collectionField}/{collectionItemId}", method = RequestMethod.GET)
    public String showUpdateCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @PathVariable String collectionField,
            @PathVariable String collectionItemId) throws Exception {
        return super.showUpdateCollectionItem(request, response, model, SECTION_KEY, id, collectionField, collectionItemId);
    }

    @RequestMapping(value = "/{id}/{collectionField}/add", method = RequestMethod.POST)
    public String addCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @PathVariable String collectionField,
            @ModelAttribute EntityForm entityForm) throws Exception {
        return super.addCollectionItem(request, response, model, SECTION_KEY, id, collectionField, entityForm);
    }

    @RequestMapping(value = "/{id}/{collectionField}/{collectionItemId}", method = RequestMethod.POST)
    public String updateCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @PathVariable String collectionField,
            @PathVariable String collectionItemId,
            @ModelAttribute EntityForm entityForm) throws Exception {
        return super.updateCollectionItem(request, response, model, SECTION_KEY, id, collectionField, collectionItemId, 
                entityForm);
    }

    @RequestMapping(value = "/{id}/{collectionField}/{collectionItemId}/delete", method = RequestMethod.POST)
    public String removeCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @PathVariable String collectionField,
            @PathVariable String collectionItemId) throws Exception {
        return super.removeCollectionItem(request, response, model, SECTION_KEY, id, collectionField, collectionItemId);
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        super.initBinder(binder);
    }
    
}
