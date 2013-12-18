/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.dto;

import java.io.Serializable;

/**
 * The DynamicEntityDao infrastructure provides a generic representation of an entity in 
 * the system.   Some utilities and services want both the generic representation and the
 * entity as it was persisted (e.g. the result of the <code>merge</code> call.
 * 
 * This object returns both properties.
 * 
 * @author bpolster
 * 
 * @see {@link Entity}
 * @see {@link Property}
 *
 */
public class EntityResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Entity entity;
    private Object entityBackingObject;
    
    public Entity getEntity() {
        return entity;
    }
    
    public void setEntity(Entity entity) {
        this.entity = entity;
    }
    
    public Object getEntityBackingObject() {
        return entityBackingObject;
    }
    
    public void setEntityBackingObject(Object entityBackingObject) {
        this.entityBackingObject = entityBackingObject;
    }
}
