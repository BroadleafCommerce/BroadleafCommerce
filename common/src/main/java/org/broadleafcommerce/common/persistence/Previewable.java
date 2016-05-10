/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.persistence;

/**
 * Interface that exposes properties useful for determining if an entity is intended for preview only,
 * as opposed to standard production entities
 *
 * @author Jeff Fischer
 */
public interface Previewable {

    /**
     * Whether or not this entity is considered a preview entity for testing. You can utilize this field
     * to drive unique behavior for preview entities in your own implementation code. Additionally, this
     * field is utilized by the Enterprise version.
     *
     * @return whether or not this is a test entity
     */
    Boolean getPreview();

    /**
     * Whether or not this entity is considered a preview entity for testing. You can utilize this field
     * to drive unique behavior for preview entities in your own implementation code. Additionally, this
     * field is utilized by the Enterprise version.
     *
     * @param preview whether or not this is a test entity
     */
    void setPreview(Boolean preview);

}
