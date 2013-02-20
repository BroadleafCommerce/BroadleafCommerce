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

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.broadleafcommerce.openadmin.web.controller.BroadleafAdminAbstractController;
import org.broadleafcommerce.openadmin.web.editor.NonNullBooleanEditor;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.EntityFormValidator;
import org.broadleafcommerce.openadmin.web.handler.AdminNavigationHandlerMapping;
import org.broadleafcommerce.openadmin.web.service.FormBuilderService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.gwtincubator.security.exception.ApplicationSecurityException;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Andre Azzolini (apazzolini)
 */
public class BroadleafAdminBasicEntityController extends BroadleafAdminAbstractController {

    @Resource(name = "blAdminEntityService")
    protected AdminEntityService service;

    @Resource(name = "blFormBuilderService")
    protected FormBuilderService formService;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name = "blEntityFormValidator")
    protected EntityFormValidator entityValidator;

    public String viewEntityList(HttpServletRequest request, HttpServletResponse response, Model model,
            String sectionKey) throws Exception {
        String sectionClassName = getClassNameForSection(sectionKey);

        PersistencePackageRequest ppr = PersistencePackageRequest.standard().withClassName(sectionClassName);
        ClassMetadata cmd = service.getClassMetadata(ppr);
        Entity[] rows = service.getRecords(ppr);

        ListGrid listGrid = formService.buildMainListGrid(rows, cmd);

        model.addAttribute("listGrid", listGrid);
        model.addAttribute("viewType", "listGrid");

        setModelAttributes(model, sectionKey);
        return "modules/dynamicModule";
    }

    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            String sectionKey,
            String id) throws Exception {
        String sectionClassName = getClassNameForSection(sectionKey);

        ClassMetadata cmd = service.getClassMetadata(sectionClassName);
        Entity entity = service.getRecord(sectionClassName, id);
        Map<String, Entity[]> subRecordsMap = service.getRecordsForAllSubCollections(sectionClassName, id);

        EntityForm entityForm = formService.buildEntityForm(cmd, entity, subRecordsMap);
        
        model.addAttribute("entityForm", entityForm);
        model.addAttribute("viewType", "entityForm");

        setModelAttributes(model, sectionKey);
        return "modules/dynamicModule";
    }

    public String saveEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            String sectionKey,
            String id,
            EntityForm entityForm, BindingResult result) throws Exception {
        String sectionClassName = getClassNameForSection(sectionKey);

        Entity entity = service.updateEntity(entityForm, sectionClassName);
        entityValidator.validate(entityForm, entity, result);

        if (result.hasErrors()) {
            ClassMetadata cmd = service.getClassMetadata(sectionClassName);
            Map<String, Entity[]> subRecordsMap = service.getRecordsForAllSubCollections(sectionClassName, id);

            //re-initialize the field groups as well as sub collections
            EntityForm newForm = formService.buildEntityForm(cmd, entity, subRecordsMap);
            formService.setEntityFormValues(newForm, entityForm);

            model.addAttribute("entityForm", newForm);
            model.addAttribute("viewType", "entityForm");
            setModelAttributes(model, sectionKey);
            return "modules/dynamicModule";
        }

        return "redirect:/" + sectionKey + "/" + id;
    }

    public String showAddCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            String sectionKey,
            String id,
            String collectionField) throws Exception {
        String mainClassName = getClassNameForSection(sectionKey);
        ClassMetadata mainMetadata = service.getClassMetadata(mainClassName);
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        FieldMetadata md = collectionProperty.getMetadata();

        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(md);

        if (md instanceof BasicFieldMetadata) {
            Entity[] rows = service.getRecords(ppr);

            ListGrid listGrid = formService.buildCollectionListGrid(id, rows, collectionProperty);

            model.addAttribute("listGrid", listGrid);
            model.addAttribute("viewType", "modalListGrid");
        } else if (md instanceof BasicCollectionMetadata) {
            BasicCollectionMetadata fmd = (BasicCollectionMetadata) md;

            // When adding items to basic collections, we will sometimes show a form to persist a new record
            // and sometimes show a list grid to allow the user to associate an existing record.
            if (fmd.getAddMethodType().equals(AddMethodType.PERSIST)) {
                ClassMetadata collectionMetadata = service.getClassMetadata(ppr);
                EntityForm entityForm = formService.buildEntityForm(collectionMetadata);

                model.addAttribute("entityForm", entityForm);
                model.addAttribute("viewType", "modalEntityForm");
            } else {
                Entity[] rows = service.getRecords(ppr);
                ListGrid listGrid = formService.buildCollectionListGrid(id, rows, collectionProperty);

                model.addAttribute("listGrid", listGrid);
                model.addAttribute("viewType", "modalListGrid");
            }
        } else if (md instanceof AdornedTargetCollectionMetadata) {
            AdornedTargetCollectionMetadata fmd = (AdornedTargetCollectionMetadata) md;

            // Even though this field represents an adorned target collection, the list we want to show in the modal
            // is the standard list grid for the target entity of this field
            ppr.setOperationTypesOverride(null);
            ppr.setType(PersistencePackageRequest.Type.STANDARD);

            ClassMetadata collectionMetadata = service.getClassMetadata(ppr);

            Entity[] rows = service.getRecords(ppr);
            ListGrid listGrid = formService.buildMainListGrid(rows, collectionMetadata);
            EntityForm entityForm = formService.buildAdornedListForm(fmd, ppr.getAdornedList(), id);

            model.addAttribute("listGrid", listGrid);
            model.addAttribute("entityForm", entityForm);

            if (fmd.getMaintainedAdornedTargetFields().length > 0) {
                listGrid.setListGridType(ListGrid.Type.ADORNED_WITH_FORM);
                model.addAttribute("viewType", "modalAdornedListGridForm");
            } else {
                listGrid.setListGridType(ListGrid.Type.ADORNED);
                model.addAttribute("viewType", "modalAdornedListGrid");
            }
        } else if (md instanceof MapMetadata) {
            MapMetadata fmd = (MapMetadata) md;
            ClassMetadata collectionMetadata = service.getClassMetadata(ppr);

            EntityForm entityForm = formService.buildMapForm(fmd, ppr.getMapStructure(), collectionMetadata, id);
            model.addAttribute("entityForm", entityForm);
            model.addAttribute("viewType", "modalMapEntityForm");
        }

        model.addAttribute("currentUrl", request.getRequestURL().toString());
        setModelAttributes(model, sectionKey);
        return "modules/modalContainer";
    }

    public String addCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            String sectionKey,
            String id,
            String collectionField,
            EntityForm entityForm) throws Exception {
        String mainClassName = getClassNameForSection(sectionKey);
        ClassMetadata mainMetadata = service.getClassMetadata(mainClassName);
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);

        // First, we must save the collection entity
        service.addSubCollectionEntity(entityForm, mainMetadata, collectionProperty, id);

        // Next, we must get the new list grid that represents this collection
        ListGrid listGrid = getCollectionListGrid(mainMetadata, id, collectionProperty);
        model.addAttribute("listGrid", listGrid);

        // We return the new list grid so that it can replace the currently visible one
        setModelAttributes(model, sectionKey);
        return "views/modalListGrid";
    }

    public String removeCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            String sectionKey,
            String id,
            String collectionField,
            String collectionItemId) throws Exception {
        String mainClassName = getClassNameForSection(sectionKey);
        ClassMetadata mainMetadata = service.getClassMetadata(mainClassName);
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);

        String priorKey = request.getParameter("key");

        // First, we must remove the collection entity
        service.removeSubCollectionEntity(mainMetadata, collectionProperty, id, collectionItemId, priorKey);

        // Next, we must get the new list grid that represents this collection
        ListGrid listGrid = getCollectionListGrid(mainMetadata, id, collectionProperty);
        model.addAttribute("listGrid", listGrid);

        // We return the new list grid so that it can replace the currently visible one
        setModelAttributes(model, sectionKey);
        return "views/modalListGrid";
    }

    protected ListGrid getCollectionListGrid(ClassMetadata mainMetadata, String id, Property collectionProperty)
            throws ServiceException, ApplicationSecurityException {
        Entity[] rows = service.getRecordsForCollection(mainMetadata, id, collectionProperty);

        ListGrid listGrid = formService.buildCollectionListGrid(id, rows, collectionProperty);
        listGrid.setListGridType(ListGrid.Type.INLINE);

        return listGrid;
    }

    protected String getClassNameForSection(String sectionKey) {
        sectionKey = "/" + sectionKey;
        AdminSection section = adminNavigationService.findAdminSectionByURI(sectionKey);
        String className = section.getCeilingEntity();
        return className;
    }

    protected void setModelAttributes(Model model, String sectionKey) {
        AdminSection section = adminNavigationService.findAdminSectionByURI("/" + sectionKey);

        model.addAttribute("sectionKey", sectionKey);
        model.addAttribute(AdminNavigationHandlerMapping.CURRENT_ADMIN_MODULE_ATTRIBUTE_NAME, section.getModule());
        model.addAttribute(AdminNavigationHandlerMapping.CURRENT_ADMIN_SECTION_ATTRIBUTE_NAME, section);
    }

    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(Boolean.class, new NonNullBooleanEditor());
    }

}
