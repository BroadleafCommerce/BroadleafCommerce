/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.catalog.domain;

import java.io.Serializable;

/**
 * The Interface ProductAttribute.
 */
public interface ProductAttribute extends Serializable {

    /**
     * Gets the id.
     * 
     * @return the id
     */
    Long getId();

    /**
     * Sets the id.
     * 
     * @param id the new id
     */
    void setId(Long id);

    /**
     * Gets the value.
     * 
     * @return the value
     */
    String getValue();

    /**
     * Sets the value.
     * 
     * @param value the new value
     */
    void setValue(String value);

    /**
     * Gets the searchable.
     * 
     * @deprecated in favor of Field configuration since 2.0
     * @return the searchable
     */
    Boolean getSearchable();

    /**
     * Sets the searchable.
     * 
     * @deprecated in favor of Field configuration since 2.0
     * @param searchable the new searchable
     */
    void setSearchable(Boolean searchable);

    /**
     * Gets the product.
     * 
     * @return the product
     */
    Product getProduct();

    /**
     * Sets the product.
     * 
     * @param product the new product
     */
    void setProduct(Product product);

    /**
     * Gets the name.
     * 
     * @return the name
     */
    String getName();

    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    void setName(String name);
}
