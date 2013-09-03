/*
 * Copyright 2008-2013 the original author or authors.
 *
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
 */

package org.broadleafcommerce.cms.structure.service;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.dto.StructuredContentDTO;
import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;


/**
 * Extension handler for the {@link StructuredContentService}
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface StructuredContentServiceExtensionHandler extends ExtensionHandler {

    /**
     * Further modifies the fields when parsing a {@link StructuredContent} into a {@link StructuredContentDTO}. This method
     * will be invoked at the end of {@link StructuredContentServiceImpl#buildFieldValues(StructuredContent, StructuredContentDTO, boolean)}.
     * 
     * Note that even though this method should return an {@link ExtensionResultStatusType}, modifications should be made to
     * the {@link StructuredContentDTO} by using information from the {@link StructuredContent}.
     * 
     * @param sc the {@link StructuredContent} that should further be wrapped into the <b>dto</b>
     * @param dto the DTO that has already been mostly populated by Broadleaf. At this stage, this parameter will have all of
     * the properties from the default {@link StructuredContentDTO} already parsed
     * @param secure whether or not the request is secure
     * @return the result of executing this extension handler
     * 
     * @see {@link StructuredContentServiceImpl#buildFieldValues(StructuredContent, StructuredContentDTO, boolean)}
     * @see {@link StructuredContentServiceImpl#buildStructuredContentDTO(StructuredContent, boolean)}
     */
    public ExtensionResultStatusType populateAdditionalStructuredContentFields(StructuredContent sc, StructuredContentDTO dto, boolean secure);
    
}
