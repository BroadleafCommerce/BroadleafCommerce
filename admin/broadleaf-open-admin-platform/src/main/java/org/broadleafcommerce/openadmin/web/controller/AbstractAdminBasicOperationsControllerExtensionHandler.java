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
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;

import java.util.List;


/**
 * Abstract implementation of {@link AdminBasicOperationsControllerExtensionHandler}.
 * 
 * Individual implementations of this extension handler should subclass this class as it will allow them to 
 * only override the methods that they need for their particular scenarios.
 * 
 * @author ckittrell
 */
public class AbstractAdminBasicOperationsControllerExtensionHandler extends AbstractExtensionHandler implements AdminBasicOperationsControllerExtensionHandler {

    @Override
    public ExtensionResultStatusType buildLookupListGrid(PersistencePackageRequest ppr, ClassMetadata cmd, String owningClass,
            List<SectionCrumb> sectionCrumbs, Model model, MultiValueMap<String, String> requestParams) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

}
