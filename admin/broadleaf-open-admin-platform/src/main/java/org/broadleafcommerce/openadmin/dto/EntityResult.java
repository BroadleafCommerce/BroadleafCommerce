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
