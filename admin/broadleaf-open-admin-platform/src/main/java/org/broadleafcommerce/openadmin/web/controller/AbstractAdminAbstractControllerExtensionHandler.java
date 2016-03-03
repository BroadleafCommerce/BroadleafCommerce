/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.MapMetadata;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.EntityFormAction;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Abstract implementatino of {@link AdminAbstractControllerExtensionHandler}.
 * 
 * Individual implementations of this extension handler should subclass this class as it will allow them to 
 * only override the methods that they need for their particular scenarios.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class AbstractAdminAbstractControllerExtensionHandler extends AbstractExtensionHandler implements AdminAbstractControllerExtensionHandler {

    @Override
    public ExtensionResultStatusType addAdditionalMainActions(String sectionClassName, List<EntityFormAction> actions) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType modifyMainActions(ClassMetadata cmd, List<EntityFormAction> mainActions) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType setAdditionalModelAttributes(Model model, String sectionKey) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
    
    @Override
    public ExtensionResultStatusType overrideClassNameForSection(ExtensionResultHolder erh, String sectionKey, 
            AdminSection section) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType modifyDynamicForm(EntityForm form, String parentEntityId) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType modifyModelForAddCollectionType(HttpServletRequest request,
                                                                     HttpServletResponse response,
                                                                     Model model,
                                                                     String sectionKey,
                                                                     String id,
                                                                     MultiValueMap<String, String> requestParams, MapMetadata md) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType isAddRequest(Entity entity, ExtensionResultHolder<Boolean> resultHolder) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType overrideSaveEntityJsonResponse(HttpServletResponse response, boolean hasValidationErrors, String sectionKey, String id, ExtensionResultHolder<String> resultHolder) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
