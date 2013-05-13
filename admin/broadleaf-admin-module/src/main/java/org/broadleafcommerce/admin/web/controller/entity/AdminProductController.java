/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.admin.web.controller.entity;

import org.broadleafcommerce.admin.server.service.handler.ProductCustomPersistenceHandler;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundleImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.ListGridAction;
import org.broadleafcommerce.openadmin.web.form.entity.DefaultEntityFormActions;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles admin operations for the {@link Product} entity. Editing a product requires custom criteria in order to properly
 * invoke the {@link ProductCustomPersistenceHandler}
 * 
 * @author Andre Azzolini (apazzolini)
 * @see {@link ProductCustomPersistenceHandler}
 */
@Controller("blAdminProductController")
@RequestMapping("/" + AdminProductController.SECTION_KEY)
public class AdminProductController extends AdminBasicEntityController {
    
    protected static final String SECTION_KEY = "product";
    
    @Override
    protected String getSectionKey(Map<String, String> pathVars) {
        //allow external links to work for ToOne items
        if (super.getSectionKey(pathVars) != null) {
            return super.getSectionKey(pathVars);
        }
        return SECTION_KEY;
    }
    
    @Override
    public String[] getSectionCustomCriteria() {
        return new String[]{"productDirectEdit"};
    }
    
    protected String showAddAdditionalSku(HttpServletRequest request, HttpServletResponse response, Model model,
            String id) throws Exception {
        String collectionField = "additionalSkus";
        String mainClassName = getClassNameForSection(SECTION_KEY);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName));
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        FieldMetadata md = collectionProperty.getMetadata();
        
        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(md)
                .withCustomCriteria(new String[] { id });

        ClassMetadata collectionMetadata = service.getClassMetadata(ppr);
        if (collectionMetadata.getCeilingType().equals(SkuImpl.class.getName())) {
            collectionMetadata.setCeilingType(Sku.class.getName());
        }
        
        EntityForm entityForm = formService.createEntityForm(collectionMetadata);
        
        entityForm.getActions().remove(DefaultEntityFormActions.DELETE);

        model.addAttribute("entityForm", entityForm);
        model.addAttribute("viewType", "modal/simpleAddEntity");
                
        model.addAttribute("currentUrl", request.getRequestURL().toString());
        model.addAttribute("modalHeaderType", "addCollectionItem");
        model.addAttribute("collectionProperty", collectionProperty);
        setModelAttributes(model, SECTION_KEY);
        return "modules/modalContainer";
    }
    
    protected String showUpdateAdditionalSku(HttpServletRequest request, HttpServletResponse response, Model model,
            String id,
            String collectionItemId) throws Exception {
        String collectionField = "additionalSkus";
        
        // Find out metadata for the additionalSkus property
        String mainClassName = getClassNameForSection(SECTION_KEY);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName));
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        FieldMetadata md = collectionProperty.getMetadata();
        
        // Find the metadata and the entity for the selected sku
        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(md);
        ClassMetadata collectionMetadata = service.getClassMetadata(ppr);
        if (collectionMetadata.getCeilingType().equals(SkuImpl.class.getName())) {
            collectionMetadata.setCeilingType(Sku.class.getName());
        }
        
        Entity entity = service.getRecord(ppr, collectionItemId, collectionMetadata);
        
        // Find the records for all subcollections of Sku
        Map<String, DynamicResultSet> subRecordsMap = service.getRecordsForAllSubCollections(ppr, entity);
        
        // Build the entity form for the modal that includes the subcollections
        EntityForm entityForm = formService.createEntityForm(collectionMetadata, entity, subRecordsMap);
        
        entityForm.getActions().remove(DefaultEntityFormActions.DELETE);
        
        // Ensure that operations on the Sku subcollections go to the proper URL
        for (ListGrid lg : entityForm.getAllListGrids()) {
            lg.setSectionKey("org.broadleafcommerce.core.catalog.domain.Sku");
        }

        model.addAttribute("entityForm", entityForm);
        model.addAttribute("viewType", "modal/simpleEditEntity");

        model.addAttribute("currentUrl", request.getRequestURL().toString());
        model.addAttribute("modalHeaderType", "updateCollectionItem");
        model.addAttribute("collectionProperty", collectionProperty);
        setModelAttributes(model, SECTION_KEY);
        return "modules/modalContainer";
    }

    @Override
    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}", method = RequestMethod.GET)
    public String showUpdateCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable Map<String, String> pathVars,
            @PathVariable String id,
            @PathVariable String collectionField,
            @PathVariable String collectionItemId) throws Exception {
        if ("additionalSkus".equals(collectionField)) {
            return showUpdateAdditionalSku(request, response, model, id, collectionItemId);
        }
        return super.showUpdateCollectionItem(request, response, model, pathVars, id, collectionField, collectionItemId);
    }
    
    @Override
    @RequestMapping(value = "/{id}/{collectionField}/add", method = RequestMethod.GET)
    public String showAddCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable Map<String, String> pathVars,
            @PathVariable String id,
            @PathVariable String collectionField,
            @RequestParam MultiValueMap<String, String> requestParams) throws Exception {
        if ("additionalSkus".equals(collectionField)) {
            return showAddAdditionalSku(request, response, model, id);
        } 
        return super.showAddCollectionItem(request, response, model, pathVars, id, collectionField, requestParams);
    }
    
    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable Map<String, String> pathVars,
            @PathVariable String id) throws Exception {
        String view = super.viewEntityForm(request, response, model, pathVars, id);
        
        //Skus have a specific toolbar action to generate Skus based on permutations
        EntityForm form = (EntityForm) model.asMap().get("entityForm");
        ListGridAction generateSkusAction = new ListGridAction(ListGridAction.GEN_SKUS).withDisplayText("Generate_Skus")
                                                                .withIconClass("icon-fighter-jet")
                                                                .withButtonClass("generate-skus")
                                                                .withUrlPostfix("/generate-skus");
        
        ListGrid skusGrid = form.findListGrid("additionalSkus");
        if (skusGrid != null) {
            skusGrid.addToolbarAction(generateSkusAction);
            skusGrid.setCanFilterAndSort(false);
        }
        
        // When we're dealing with product bundles, we don't want to render the product options and additional skus
        // list grids. Remove them from the form.
        if (form.getEntityType().equals(ProductBundleImpl.class.getName())) {
            form.removeListGrid("additionalSkus");
            form.removeListGrid("productOptions");
        }
        
        return view;
    }
    
}
