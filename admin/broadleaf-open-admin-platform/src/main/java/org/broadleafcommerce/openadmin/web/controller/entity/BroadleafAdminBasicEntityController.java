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
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.broadleafcommerce.openadmin.web.controller.BroadleafAdminAbstractController;
import org.broadleafcommerce.openadmin.web.editor.NonNullBooleanEditor;
import org.broadleafcommerce.openadmin.web.form.EntityForm;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.handler.AdminNavigationHandlerMapping;
import org.broadleafcommerce.openadmin.web.service.FormBuilderService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;

import java.util.Map;
import java.util.Map.Entry;

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
        Class<?> sectionClass = getClassForSection(sectionKey);
        ClassMetadata cmd = service.getClassMetadata(sectionClass);
        Entity[] rows = service.getRecords(sectionClass, null);

        ListGrid listGrid = formService.getListGrid(cmd, rows);

        model.addAttribute("listGrid", listGrid);
        model.addAttribute("viewType", "listGridForm");

        setModelAttributes(model, sectionKey);
        return "modules/dynamicModule";
    }

    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            String sectionKey,
            String id) throws Exception {
        ClassMetadata cmd = service.getClassMetadata(getClassForSection(sectionKey));

        Entity e = service.getRecord(getClassForSection(sectionKey), id);

        EntityForm ef = formService.getEntityForm(cmd, e);

        Map<String, Entity[]> subRecordsMap = service.getSubRecords(getClassForSection(sectionKey), id, cmd);
        
        for (Entry<String, Entity[]> entry : subRecordsMap.entrySet()) {
            Class<?> clazz = Class.forName(entry.getValue()[0].getType()[0]);
            ClassMetadata subCmd = service.getClassMetadata(clazz);
            ListGrid listGrid = formService.getListGrid(subCmd, entry.getValue());

            ef.getSubordinateListGrids().put(entry.getKey(), listGrid);
        }

        model.addAttribute("ef", ef);

        setModelAttributes(model, sectionKey);
        return "views/entityForm";
    }

    public String saveEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            String sectionKey,
            String id,
            EntityForm entityForm) throws Exception {

        service.updateEntity(entityForm.getEntity(), getClassForSection(sectionKey));

        return "redirect:/" + sectionKey;
    }

    public String viewEntityCollection(HttpServletRequest request, HttpServletResponse response, Model model,
            String sectionKey,
            String id,
            String collectionField) throws Exception {
        Class<?> mainClass = getClassForSection(sectionKey);
        ClassMetadata mainMetadata = service.getClassMetadata(mainClass);

        for (Property p : mainMetadata.getProperties()) {
            if (p.getName().equals(collectionField)) {
                Class<?> collectionClass = Class.forName(((BasicFieldMetadata) p.getMetadata()).getForeignKeyClass());

                ClassMetadata collectionMetadata = service.getClassMetadata(collectionClass);
                Entity[] rows = service.getRecords(collectionClass);

                ListGrid listGrid = formService.getListGrid(collectionMetadata, rows);
                model.addAttribute("listGrid", listGrid);
                model.addAttribute("viewType", "modalListGrid");

                break;
            }
        }

        setModelAttributes(model, sectionKey);
        return "modules/modalContainer";
    }

    protected Class<?> getClassForSection(String sectionKey) {
        sectionKey = "/" + sectionKey;
        AdminSection section = adminNavigationService.findAdminSectionByURI(sectionKey);
        String className = section.getCeilingEntity();
        return entityConfiguration.lookupEntityClass(className);
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
