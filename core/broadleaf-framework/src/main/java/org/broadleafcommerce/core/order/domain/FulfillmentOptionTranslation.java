/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.locale.domain.Locale;

/**
 * Author: jerryocanas
 * Date: 9/25/12
 */
public interface FulfillmentOptionTranslation {

    /**
     * Returns the id
     *
     * @return
     */
    public Long getId();

    /**
     * Sets the id
     *
     * @param id
     */
    public void setId(Long id);

    /**
     * Returns the long description
     *
     * @return
     */
    public String getLongDescription();

    /**
     * Return the name
     *
     * @return
     */
    public String getName();

    /**
     * Sets the name
     *
     * @param name
     */
    public void setName(String name);

    /**
     * Set the long description
     *
     * @param longDescription
     */
    public void setLongDescription(String longDescription);

}
