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

package org.broadleafcommerce.core.catalog.domain;

import java.io.Serializable;

/**
 * Stores the values for a given product option.
 *
 * For example, a ProductOption of type "color" might have values of ("red","blue")
 *
 * Created by bpolster.
 */
public interface ProductOptionValue extends Serializable {
    /**
     * Returns unique identifier of the product option value.
     * @return
     */
    public Long getId();

    /**
     * Sets the unique identifier of the product option value.
     * @param id
     */
    public void setId(Long id);

    /**
     * Returns the associated ProductOption
     *
     * @return
     */
    public ProductOption getProductOption();

    /**
     * Sets the associated product option.
     * @param productOption
     */
    public void setProductOption(ProductOption productOption);

    /**
     * Gets the option value.  (e.g. "red")
     * @param
     */
    public String getValue();

    /**
     * Sets the option value.  (e.g. "red")
     * @param attributeValue
     */
    public void setValue(String value);

    /**
     * Returns the order that the option value should be displayed in.
     * @return
     */
    public Long getDisplayOrder();

    /**
     * Sets the display order.
     * @param order
     */
    public void setDisplayOrder(Long order);
}
