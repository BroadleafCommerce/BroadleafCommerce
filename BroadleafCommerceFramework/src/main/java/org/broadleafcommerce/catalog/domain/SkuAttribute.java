/*
 * Copyright 2008-2009 the original author or authors.
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
package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;


/**
 * Implementations of this interface are used to hold data about a SKU's Attributes.
 * A SKU Attribute is a designator on a SKU that differentiates it from other similar SKUs
 * (for example: Blue attribute for hat).
 * <br>
 * <br>
 * You should implement this class if you want to make significant changes to how the
 * class is persisted.  If you just want to add additional fields then you should
 * extend {@link SkuAttributeImpl}.
 *
 * @see {@link SkuAttributeImpl}, {@link Sku}
 * @author btaylor
 *
 */
public interface SkuAttribute extends Serializable {

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId();

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(Long id);

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue();

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(String value);

    /**
     * Gets the searchable.
     *
     * @return the searchable
     */
    public Boolean getSearchable();

    /**
     * Sets the searchable.
     *
     * @param searchable the new searchable
     */
    public void setSearchable(Boolean searchable);

    /**
     * Gets the sku.
     *
     * @return the sku
     */
    public Sku getSku();

    /**
     * Sets the sku.
     *
     * @param sku the new sku
     */
    public void setSku(Sku sku);

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName();

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name);

}
