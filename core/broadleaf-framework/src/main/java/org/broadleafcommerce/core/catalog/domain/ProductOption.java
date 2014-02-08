/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.core.catalog.service.type.ProductOptionType;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationStrategyType;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationType;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;

import java.io.Serializable;
import java.util.List;

/**
 * <p>A product option represents a value that is entered to specify more information about
 * a product prior to entering into the cart.</p>
 *
 * <p>For example, a product of type shirt might have product options of "size" and "color".</p>
 *
 * <p>There is an inherent relationship between product options and product SKUs.  A sku is
 * meant to provide a way to override the pricing of a product for a specific set of options.
 * Inventory can also be tracked at the SKU level.</p>
 *
 * <p>For example, consider a shirt that is sold in 5 colors and 5 sizes.   For this example,
 * there would be 1 product.   It would have 10 options (5 colors + 5 sizes).   The product would
 * have as few as 1 SKu and a many as 26 SKUs.</p>
 *
 * <p>1 SKU would indicate that the system is not tracking inventory for the items and that all of the
 * variations of shirt are priced the same way.</p>
 *
 * <p>26 would indicate that there are 25 SKUs that are used to track inventory and potentially
 * override pricing.    The extra "1" sku is used to hold the default pricing.</p>
 *
 *  @author bpolster
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
     * These are used primarily to determine how the UI should prompt for and
     * validate the product option.
     *
     * @return
     */
    public ProductOptionType getType();

    /**
     * Sets the option type.   This is primarily used for
     * display to render the option selection.
     *
     * @param type
     */
    public void setType(ProductOptionType type);

    /**
     * Gets the attribute name for where the ProductOptionValue selected for
     * this ProductOption is stored in the OrderItemAttributes for the 
     * OrderItem
     * 
     * @return the name of the OrderItemAttribute to store the selected
     * ProductOptionValue in the Order domain
     * @see {@link OrderItemAttribute}, {@link OrderItem}
     */
    public String getAttributeName();

    /**
     * Sets the attribute name that will be used in storing the selected
     * ProductOptionValue for this ProductOption
     * 
     * @param name - the name of the OrderItemAttribute to store the selected
     * ProductOptionValue in the Order domain
     */
    public void setAttributeName(String name);

    /**
     * The label to show to the user when selecting from the available
     * {@link ProductOptionValue}s. This might be "Color" or "Size"
     * 
     * @return
     */
    public String getLabel();

    /**
     * Sets the label to show the user when selecting from the available
     * {@link ProductOptionValue}s
     * 
     * @param label
     */
    public void setLabel(String label);

    /**
     * 
     * @return whether or not this ProductOption is required
     */
    public Boolean getRequired();

    /**
     * Sets whether or not 
     * @param required
     */
    public void setRequired(Boolean required);
    
    /**
     * Gets the display order of this option in relation to the other {@link ProductOption}s
     * 
     * @return
     */
    public Integer getDisplayOrder();

    /**
     * Gets the display order of this option in relation to the other {@link ProductOption}s
     * 
     * @param displayOrder
     */
    public void setDisplayOrder(Integer displayOrder);


    /**
     * Gets all the Products associated with this ProductOption
     *
     * @deprecated use getProductXrefs instead
     * @return the Products associated with this ProductOption
     */
    @Deprecated
    public List<Product> getProducts();

    /**
     * Set the Products to associate with this ProductOption
     *
     * @deprecated use setProductXrefs instead
     * @param products
     */
    @Deprecated
    public void setProducts(List<Product> products);

    public List<ProductOptionXref> getProductXrefs();

    public void setProductXrefs(List<ProductOptionXref> xrefs);

    /**
     * Gets the available values that a user can select for this ProductOption.
     * This value will be stored in OrderItemAttributes at the OrderItem level. The
     * OrderItemAttribute name will be whatever was returned from {@link #getAttributeName()}
     * 
     * @return the allowed values for this ProductOption
     */
    public List<ProductOptionValue> getAllowedValues();

    /**
     * Set the allowed values for this ProductOption
     * 
     * @param allowedValues
     */
    public void setAllowedValues(List<ProductOptionValue> allowedValues);

    public Boolean getUseInSkuGeneration();

    public ProductOptionValidationType getProductOptionValidationType();

    public void setProductOptionValidationType(ProductOptionValidationType productOptionValidationType);

    public void setUseInSkuGeneration(Boolean useInSkuGeneration);

    void setErrorMessage(String errorMessage);

    void setErrorCode(String errorCode);

    String getErrorMessage();

    String getValidationString();

    void setValidationString(String validationString);

    String getErrorCode();

    void setProductOptionValidationStrategyType(ProductOptionValidationStrategyType productOptionValidationType);

    ProductOptionValidationStrategyType getProductOptionValidationStrategyType();

}
