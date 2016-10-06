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
package org.broadleafcommerce.openadmin.server.security.service;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapping class meant to house a list of {@link EntityFormModifier} instances and any relevant configuration data
 * for those modifiers, in the form of {@link EntityFormModifierData}, organized by modifierType.
 *
 * @author Jeff Fischer
 */
public class EntityFormModifierConfiguration {

    protected List<EntityFormModifier> modifier = new ArrayList<EntityFormModifier>();
    protected List<EntityFormModifierData<EntityFormModifierDataPoint>> data = new ArrayList<EntityFormModifierData<EntityFormModifierDataPoint>>();

    public EntityFormModifierConfiguration() {
    }

    public EntityFormModifierConfiguration(List<EntityFormModifier> modifier, List<EntityFormModifierData<EntityFormModifierDataPoint>> data) {
        this.modifier = modifier;
        this.data = data;
    }

    public List<EntityFormModifier> getModifier() {
        return modifier;
    }

    public void setModifier(List<EntityFormModifier> modifier) {
        this.modifier = modifier;
    }

    public List<EntityFormModifierData<EntityFormModifierDataPoint>> getData() {
        return data;
    }

    public void setData(List<EntityFormModifierData<EntityFormModifierDataPoint>> data) {
        this.data = data;
    }
}
