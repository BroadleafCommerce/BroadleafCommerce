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

package org.broadleafcommerce.core.catalog.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.common.vendor.service.type.ContainerShapeType;
import org.broadleafcommerce.common.vendor.service.type.ContainerSizeType;
import org.broadleafcommerce.core.media.domain.Media;

/**
 * Implementations of this interface are used to hold data for a Product.  A product is a general description
 * of an item that can be sold (for example: a hat).  Products are not sold or added to a cart.  {@link Sku}s
 * which are specific items (for example: a XL Blue Hat) are sold or added to a cart.
 * <br>
 * <br>
 * You should implement this class if you want to make significant changes to how the
 * Product is persisted.  If you just want to add additional fields then you should extend {@link ProductImpl}.
 *
 * @author btaylor
 * @see {@link ProductImpl},{@link Sku}, {@link Category}
 */
public interface Product extends Serializable {

    /**
     * The id of the Product.
     *
     * @return the id of the Product
     */
    public Long getId();

    /**
     * Sets the id of the Product.
     *
     * @param id - the id of the product
     */
    public void setId(Long id);

    /**
     * Returns the name of the product that is used for display purposes.
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @return the name of the product
     */
    public String getName();

    /**
     * Sets the name of the product that is used for display purposes.
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @param name - the name of the Product
     */
    public void setName(String name);

    /**
     * Returns a brief description of the product that is used for display.
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @return a brief description of the product
     */
    public String getDescription();

    /**
     * Sets a brief description of the product that is used for display.
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @param description - a brief description of the product
     */
    public void setDescription(String description);

    /**
     * Returns a long description of the product that is used for display.
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @return a long description of the product
     */
    public String getLongDescription();

    /**
     * Sets a long description of the product that is used for display.
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @param longDescription the long description
     */
    public void setLongDescription(String longDescription);

    /**
     * Returns the first date a product will be available that is used to determine whether
     * to display the product.
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @return the first date the product will be available
     */
    public Date getActiveStartDate();

    /**
     * Sets the first date a product will be available that is used to determine whether
     * to display the product.
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @param activeStartDate - the first day the product is available
     */
    public void setActiveStartDate(Date activeStartDate);

    /**
     * Returns the last date a product will be available that is used to determine whether
     * to display the product.
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @return the last day the product is available
     */
    public Date getActiveEndDate();

    /**
     * Sets the last date a product will be available that is used to determine whether
     * to display the product.
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @param activeEndDate - the last day the product is available
     */
    public void setActiveEndDate(Date activeEndDate);

    /**
     * Returns a boolean that indicates if the product is currently active.
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @return a boolean indicates if the product is active.
     */
    public boolean isActive();

    /**
     * Returns a list of {@link Sku}s that are part of this product.
     *
     * @return a list of {@link Sku}s associated with this product
     */
    public List<Sku> getSkus();

    /**
     * Sets the {@link Sku}s that are to be associated with this product.
     *
     * @param skus - a List of {@link Sku}s to associate with this product.
     */
    public void setAllSkus(List<Sku> skus);

    public List<Sku> getAllSkus();
    
    /**
     * Returns a map of key/value pairs that associate the image name (key) with the URL to the image (value)
     * for display purposes. This method is deprecated. Use getProductMedia instead.
     *
     * @return a map of product images
     */
    @Deprecated
    Map<String, String> getProductImages();

    /**
     * Returns a string URL to an image given the string key passed in for this product.
     *
     * @param imageKey - a string key to lookup the image for the product
     *
     * @return a URL to the image associated witht he key passed in.
     */
    @Deprecated
    String getProductImage(String imageKey);

    /**
     * Sets the product images map. This method is deprecated. Use setProductMedia instead.
     *
     * @param productImages - a map of product images
     */
    @Deprecated
    void setProductImages(Map<String, String> productImages);

    /**
     * Returns a map of key/value pairs that associate the media name (key) with the Media object(value)
     *
     * @return a map of product media
     */
    @Deprecated
    public Map<String, Media> getProductMedia();

    /**
     * Sets the product media map.
     *
     * @param productMedia - a map of product images
     */
    @Deprecated
    public void setProductMedia(Map<String, Media> productMedia);

    /**
     * Gets the media for this product. This serves as a pass-through to
     * the {@link getDefaultSku()} media
     * 
     * @return the Media for the default Sku associated with this Product
     * @see Sku
     */
    public Map<String, Media> getMedia();

    /**
     * Gets the media for this product. This serves as a pass-through to
     * the {@link getDefaultSku()} media
     * 
     * @param media Media map to set on the default Sku associated with this Product
     * @see Sku
     */
    public void setMedia(Map<String, Media> media);

    /**
     * Returns all parent {@link Category}(s) this product is associated with.
     *
     * @return the all parent categories for this product
     */
    public List<Category> getAllParentCategories();

    /**
     * Sets all parent {@link Category}s this product is associated with.
     *
     * @param allParentCategories - a List of all parent {@link Category}(s) to associate this product with
     */
    public void setAllParentCategories(List<Category> allParentCategories);
     
    /**
     * Returns the default {@link Category} this product is associated with.
     *
     */
    public Category getDefaultCategory();

    /**
     * Sets the default {@link Category} to associate this product with.
     *
     * @param defaultCategory - the default {@link Category} to associate this product with
     */
    public void setDefaultCategory(Category defaultCategory);

    /**
     * Returns the model number of the product
     * @return the model number
     */
    public String getModel();

    /**
     * Sets the model number of the product
     * @param model
     */
    public void setModel(String model);

    /**
     * Returns the manufacture name for this product
     * @return the manufacture name
     */
    public String getManufacturer();

    /**
     * Sets the manufacture for this product
     * @param manufacturer
     */
    public void setManufacturer(String manufacturer);
    
    /**
     * Returns the {@link Dimension} for this product
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @return a ProductDimensions object
     * 
     */
    public Dimension getDimension();

    /**
     * Sets the {@link Dimension} for this product
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @param dimension
     * 
     */
    public void setDimension(Dimension dimension);

    /**
     * Returns the dimension width
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @return width dimension of the product
     * 
     */
    public BigDecimal getWidth();

    /**
     * Sets the dimension width
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @param width
     * 
     */
    public void setWidth(BigDecimal width);

    /**
     * Returns the dimension height
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @return height dimension of the product
     * 
     */
    public BigDecimal getHeight();

    /**
     * Sets the dimension height
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @param height
     * 
     */
    public void setHeight(BigDecimal height);

    /**
     * Returns the dimension depth
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @return width depth of the product
     * 
     */
    public BigDecimal getDepth();

    /**
     * Sets the dimension depth
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @param depth
     */
    public void setDepth(BigDecimal depth);
    
    /**
     * Gets the dimension girth
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @return the dimension girth
     */
    public BigDecimal getGirth();
    
    /**
     * Sets the dimension girth
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @param girth
     */
    public void setGirth(BigDecimal girth);

    /**
     * Returns the dimension container size
     * 
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @return dimension container size
     */
    public ContainerSizeType getSize();

    /**
     * Sets the dimension container size
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @param size
     */
    public void setSize(ContainerSizeType size);

    /**
     * Gets the dimension container shape
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @return dimension container shape
     */
    public ContainerShapeType getContainer();

    /**
     * Sets the dimension container shape
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @param container
     */
    public void setContainer(ContainerShapeType container);

    /**
     * Returns a String representation of the dimension
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @return a dimension String
     */
    public String getDimensionString();

    /**
     * Returns the weight of the product
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @return weight of product
     */
    public Weight getWeight();

    /**
     * Sets the product weight
     * <br />
     * <br />
     * <b>Note:</b> this is a convenience method that merely serves as
     * a pass-through to the same method via {@link getDefaultSku()}
     * 
     * @param weight
     */
    public void setWeight(Weight weight);

    /**
     * Returns a List of this product's related Cross Sales
     * @return
     */
    public List<RelatedProduct> getCrossSaleProducts();

    /**
     * Sets the related Cross Sales
     * @param crossSaleProducts
     */
    public void setCrossSaleProducts(List<RelatedProduct> crossSaleProducts);

    /**
     * Returns a List of this product's related Up Sales
     * @return
     */
    public List<RelatedProduct> getUpSaleProducts();

    /**
     * Sets the related Up Sales
     * @param upSaleProducts
     */
    public void setUpSaleProducts(List<RelatedProduct> upSaleProducts);

    /**
     * Returns whether or not the product is featured
     * @return isFeaturedProduct as Boolean
     */
    public boolean isFeaturedProduct();

    /**
     * Sets whether or not the product is featured
     * @param isFeaturedProduct
     */
    public void setFeaturedProduct(boolean isFeaturedProduct);
    
    public Sku getDefaultSku();

	public void setDefaultSku(Sku defaultSku);	
    
	public List<ProductAttribute> getProductAttributes();

	public void setProductAttributes(List<ProductAttribute> productAttributes);
	
	public String getPromoMessage();

	public void setPromoMessage(String promoMessage);
	
    public List<ProductOption> getProductOptions();

    public void setProductOptions(List<ProductOption> productOptions);

    /**
     * A product can have a designated URL.   When set, the ProductHandlerMapping will check for this
     * URL and forward this user to the {@link #getDisplayTemplate()}. 
     * 
     * Alternatively, most sites will rely on the {@link Product#getGeneratedUrl()} to define the
     * url for a product page. 
     * 
     * @see org.broadleafcommerce.core.web.catalog.ProductHandlerMapping
     * @return
     */
    public String getUrl();

	/**
	 * Sets the URL that a customer could type in to reach this product.
	 * 
	 * @param url
	 */
	public void setUrl(String url);
	
	/**
	 * Sets a url-fragment.  By default, the system will attempt to create a unique url-fragment for 
	 * this product by taking the {@link Product.getName()} and removing special characters and replacing
	 * dashes with spaces.
	 */	
	public String getUrlKey();

	/**
	 * Sets a url-fragment to be used with this product.  By default, the system will attempt to create a 
	 * unique url-fragment for this product by taking the {@link Product.getName()} and removing special characters and replacing
	 * dashes with spaces.
	 */
	public void setUrlKey(String url);

	/**
	 * Returns the name of a display template that is used to render this product.   Most implementations have a default
	 * template for all products.    This allows for the user to define a specific template to be used by this product.
	 * 
	 * @return
	 */
	public String getDisplayTemplate();

	/**
	 * Sets the name of a display template that is used to render this product.   Most implementations have a default
	 * template for all products.    This allows for the user to define a specific template to be used by this product.
	 * @param displayTemplate
	 */
	public void setDisplayTemplate(String displayTemplate);
	
	/**
	 * Generates a URL that can be used to access the product.  
	 * Builds the url by combining the url of the default category with the getUrlKey() of this product.
	 */
	public String getGeneratedUrl();

}
