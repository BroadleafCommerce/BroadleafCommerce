/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.cms.structure.service;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.structure.dto.StructuredContentDTO;

import java.util.List;

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

    
    /**
     * Allows an extension handler to modify the list of structured content items.   For example to alter the order of the
     * passed in list.   
     * 
     * The {@link ExtensionResultHolder} if non null should contain a replacement list to use. 
     * 
     * @param structuredContentList
     * @param resultHolder
     * @return
     */
    public ExtensionResultStatusType modifyStructuredContentDtoList(List<StructuredContentDTO> structuredContentList,
            ExtensionResultHolder resultHolder);
}
