/*
 * #%L
 * BroadleafCommerce Framework
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
