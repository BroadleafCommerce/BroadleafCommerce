/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.ListGridRecord;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;

/**
 * Abstract class to provide convenience for determining how to handle form 
 * extensions in the admin
 * 
 * @author Kelly Tisdell
 * @author Phillip Verheyden (phillipuniverse)
 */
public abstract class AbstractFormBuilderExtensionHandler extends AbstractExtensionHandler implements FormBuilderExtensionHandler {

    @Override
    public ExtensionResultStatusType modifyUnpopulatedEntityForm(EntityForm ef) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType modifyPopulatedEntityForm(EntityForm ef, Entity entity) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType modifyDetailEntityForm(EntityForm ef) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
    
    @Override
    public ExtensionResultStatusType modifyListGridRecord(String className, ListGridRecord record, Entity entity) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
    
    @Override
    public ExtensionResultStatusType addAdditionalFormActions(EntityForm entityForm) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType modifyListGrid(String className, ListGrid listGrid) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType addAdditionalAdornedFormActions(EntityForm entityForm) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

}
