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

import org.broadleafcommerce.core.catalog.service.type.ProductOptionType;

import java.io.Serializable;

/**
 * A product option represents a value that is entered to specify more information about
 * a product prior to entering into the cart.
 *
 * For example, a product of type shirt might have product options of "size" and "color".
 *
 * There is an inherent relationship between product options and product SKUs.  A sku is
 * meant to provide a way to override the pricing of a product for a specific set of options.
 * Inventory can also be tracked at the SKU level.
 *
 * For example, consider a shirt that is sold in 5 colors and 5 sizes.   For this example,
 * there would be 1 product.   It would have 10 options (5 colors + 5 sizes).   The product would
 * have as few as 1 SKu and a many as 26 SKUs.
 *
 * 1 SKU would indicate that the system is not tracking inventory for the items and that all of the
 * variations of shirt are priced the same way.
 *
 * 26 would indicate that there are 25 SKUs that are used to track inventory and potentially
 * override pricing.    The extra "1" sku is used to hold the default pricing.
 *
 *
 */
public interface ProductOption extends Serializable {
    /**
     * Returns unique identifier of the product option.
     * @return
     */
    public Long getId();

    /**
     * Sets the unique identifier of the product option.
     * @param id
     */
    public void setId(Long id);

    /**
     * Returns the option type.   For example, "color", "size", etc.
     * @return
     */
    public ProductOptionType getType();

    /**
     * Sets the option type.
     * @param type
     */
    public void setType(ProductOptionType type);

    /**
     * Gets the option value.  (e.g. "red")
     * @param
     */
    public String getValue();

    /**
     * Sets the option value.  (e.g. "red")
     * @param value
     */
    public void setValue(String value);

    /**
     * Gets the option value.  (e.g. "Red")
     * @param
     */
    public String getOptionLabel();

    /**
     * Sets the option value.  (e.g. "Red")
     * @param label
     */
    public void setOptionLabel(String optionLabel);
    
    public Boolean getRequired();
    
    public void setRequired(Boolean required);
    
    public Product getProduct();

    public void setProduct(Product product);

}
