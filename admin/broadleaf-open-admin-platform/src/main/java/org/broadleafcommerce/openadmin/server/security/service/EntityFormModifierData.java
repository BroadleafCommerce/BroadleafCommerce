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
import java.util.Collection;

/**
 * Hold generic key/value data for configuring a {@link EntityFormModifier}, along with a modifierType value
 * that should be used to qualify EntityFormModifier instances capable of consuming this configuration.
 *
 * @author Jeff Fischer
 */
public class EntityFormModifierData<T extends EntityFormModifierDataPoint> extends ArrayList<T> {

    public EntityFormModifierData(int initialCapacity) {
        super(initialCapacity);
    }

    public EntityFormModifierData() {
    }

    public EntityFormModifierData(Collection<? extends T> c) {
        super(c);
    }

    protected String modifierType;

    public String getModifierType() {
        return modifierType;
    }

    public void setModifierType(String modifierType) {
        this.modifierType = modifierType;
    }
}
