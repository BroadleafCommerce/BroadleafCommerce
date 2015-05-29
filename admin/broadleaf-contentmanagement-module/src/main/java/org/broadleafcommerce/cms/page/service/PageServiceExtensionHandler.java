/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.cms.page.service;

import org.broadleafcommerce.cms.field.domain.FieldDefinition;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.page.dto.PageDTO;


/**
 * Extension handler for {@link PageService}
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface PageServiceExtensionHandler extends ExtensionHandler {
    
    public static final String IS_KEY = "IS_KEY";
    
    /**
     * If this method returns something other than {@link ExtensionResultStatusType#NOT_HANDLED}, the result variable
     * in the {@link ExtensionResultHolder} will hold the associated {@link FieldDefinition} for the given {@link Page}
     * and field key.
     * 
     * @param erh
     * @param page
     * @param fieldKey
     * @return
     */
    ExtensionResultStatusType getFieldDefinition(ExtensionResultHolder<FieldDefinition> erh, Page page,
            String fieldKey);

    /**
     * This method provides the opportunity to modify the page fields associated with the pageDto 
     * {@link ExtensionResultHolder}.    Modifying classes should clone the DTO and adjust fields.
     * 
     * @param erh
     * @param pageDto
     * @param page
     * @return
     */
    ExtensionResultStatusType overridePageDto(ExtensionResultHolder<PageDTO> erh, PageDTO pageDto, Page page);

}
