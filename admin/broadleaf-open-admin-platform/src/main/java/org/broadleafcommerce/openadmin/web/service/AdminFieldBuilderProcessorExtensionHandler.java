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
package org.broadleafcommerce.openadmin.web.service;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldWrapper;


/**
 * @author Jon Fleschler (jfleschler)
 */
public interface AdminFieldBuilderProcessorExtensionHandler extends ExtensionHandler {

    /**
     * Takes the current field builder type, the ceiling entity and the field wrapper to perform any special logic.
     * Such actions include adding or removing fields from the fieldWrapper.
     *
     * @param fieldBuilder
     * @param ceilingEntity
     * @param fieldWrapper
     * @return
     */
    public ExtensionResultStatusType modifyRuleBuilderFields(String fieldBuilder, String ceilingEntity, FieldWrapper fieldWrapper);
}
