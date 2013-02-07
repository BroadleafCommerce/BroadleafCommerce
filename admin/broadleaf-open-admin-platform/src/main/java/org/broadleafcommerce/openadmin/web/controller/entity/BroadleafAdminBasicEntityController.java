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

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.client.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitor;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.broadleafcommerce.openadmin.web.controller.BroadleafAdminAbstractController;
import org.broadleafcommerce.openadmin.web.editor.NonNullBooleanEditor;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.handler.AdminNavigationHandlerMapping;
import org.broadleafcommerce.openadmin.web.service.FormBuilderService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;

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

    public String viewEntityList(HttpServletRequest request, HttpServletResponse response, Model model,
            String sectionKey) throws Exception {
        String sectionClassName = getClassNameForSection(sectionKey);

        ClassMetadata cmd = service.getClassMetadata(sectionClassName);
        Entity[] rows = service.getRecords(sectionClassName, null);

        ListGrid listGrid = formService.buildListGrid(cmd, rows);

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
            EntityForm entityForm) throws Exception {
        String sectionClassName = getClassNameForSection(sectionKey);

        service.updateEntity(entityForm, sectionClassName);

        return "redirect:/" + sectionKey + "/" + id;
    }

    public String viewEntityCollection(HttpServletRequest request, HttpServletResponse response, Model model,
            String sectionKey,
            String id,
            String collectionField) throws Exception {
        String mainClassName = getClassNameForSection(sectionKey);
        ClassMetadata mainMetadata = service.getClassMetadata(mainClassName);

        for (Property p : mainMetadata.getProperties()) {
            if (p.getName().equals(collectionField)) {
                String collectionClassName = ((BasicFieldMetadata) p.getMetadata()).getForeignKeyClass();

                ClassMetadata collectionMetadata = service.getClassMetadata(collectionClassName);
                Entity[] rows = service.getRecords(collectionClassName, null);

                ListGrid listGrid = formService.buildListGrid(collectionMetadata, rows);
                model.addAttribute("listGrid", listGrid);
                model.addAttribute("listGridType", "toOne");
                model.addAttribute("viewType", "modalListGrid");

                break;
            }
        }

        setModelAttributes(model, sectionKey);
        return "modules/modalContainer";
    }

    public String showAddCollectionItem(final HttpServletRequest request, HttpServletResponse response, final Model model,
            String sectionKey,
            String id,
            String collectionField) throws Exception {
        String mainClassName = getClassNameForSection(sectionKey);
        ClassMetadata mainMetadata = service.getClassMetadata(mainClassName);

        for (final Property p : mainMetadata.getProperties()) {
            if (p.getName().equals(collectionField)) {
                p.getMetadata().accept(new MetadataVisitor() {

                    @Override
                    public void visit(BasicFieldMetadata fmd) {
                        // Unnecessary visitor
                    }

                    @Override
                    public void visit(BasicCollectionMetadata fmd) {
                        try {
                            ForeignKey[] foreignKeys = new ForeignKey[] { (ForeignKey) fmd.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY) };
                            ClassMetadata collectionMetadata = service.getClassMetadata(fmd.getCollectionCeilingEntity(), foreignKeys, null);

                            if (fmd.getAddMethodType().equals(AddMethodType.PERSIST)) {
                                EntityForm entityForm = formService.buildEntityForm(collectionMetadata);
                                model.addAttribute("entityForm", entityForm);
                                model.addAttribute("viewType", "modalEntityForm");
                            } else {
                                Entity[] rows = service.getRecords(fmd.getCollectionCeilingEntity(), foreignKeys);
                                ListGrid listGrid = formService.buildListGrid(collectionMetadata, rows);
                                model.addAttribute("listGrid", listGrid);
                                model.addAttribute("listGridType", "basicCollection");
                                model.addAttribute("viewType", "modalListGrid");
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    }

                    @Override
                    public void visit(AdornedTargetCollectionMetadata fmd) {
                        try {
                            AdornedTargetList adornedList = (AdornedTargetList) fmd.getPersistencePerspective()
                                    .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);

                            ClassMetadata collectionMetadata = service.getClassMetadata(fmd.getCollectionCeilingEntity(), adornedList);

                            Entity[] rows = service.getRecords(fmd.getCollectionCeilingEntity(), null);
                            ListGrid listGrid = formService.buildListGrid(collectionMetadata, rows);
                            model.addAttribute("listGrid", listGrid);
                            model.addAttribute("listGridType", "adornedTarget");
                            model.addAttribute("viewType", "modalListGrid");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void visit(MapMetadata fmd) {
                        // TODO Auto-generated method stub

                    }
                });
            }
        }

        model.addAttribute("currentUrl", request.getRequestURL().toString());
        setModelAttributes(model, sectionKey);
        return "modules/modalContainer";
    }

    public String saveNewCollectionItem(HttpServletRequest request, HttpServletResponse response, final Model model,
            String sectionKey,
            final String id,
            String collectionField,
            final EntityForm entityForm) throws Exception {
        String mainClassName = getClassNameForSection(sectionKey);
        ClassMetadata mainMetadata = service.getClassMetadata(mainClassName);

        service.addSubCollectionEntity(entityForm, mainClassName, collectionField, id);


        for (Property p : mainMetadata.getProperties()) {
            if (p.getName().equals(collectionField)) {
                Entity[] rows = service.getRecordsForCollection(mainMetadata, id, p);
                ClassMetadata subCollectionMd = service.getClassMetadata(((BasicCollectionMetadata) p.getMetadata()).getCollectionCeilingEntity());

                ListGrid listGrid = formService.buildListGrid(subCollectionMd, rows);
                listGrid.setSubCollectionFieldName(collectionField);
                model.addAttribute("listGrid", listGrid);
                model.addAttribute("listGridType", "inline");

                break;
            }
        }

        setModelAttributes(model, sectionKey);
        return "views/modalListGrid";
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
