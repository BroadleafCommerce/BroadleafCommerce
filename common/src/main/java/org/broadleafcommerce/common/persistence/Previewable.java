/*
 * #%L
 * BroadleafCommerce Common Libraries
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
