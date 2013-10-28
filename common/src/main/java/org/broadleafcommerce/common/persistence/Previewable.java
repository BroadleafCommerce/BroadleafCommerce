/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
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
