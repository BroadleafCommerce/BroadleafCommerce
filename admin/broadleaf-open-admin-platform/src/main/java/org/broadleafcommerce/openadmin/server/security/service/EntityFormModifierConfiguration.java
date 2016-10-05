/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
