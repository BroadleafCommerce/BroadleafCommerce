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

package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.SkuBundleItem;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is a JAXB wrapper around Product.
 *
 * User: Kelly Tisdell
 * Date: 4/10/12
 */
@XmlRootElement(name = "product")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ProductWrapper extends BaseWrapper implements APIWrapper<Product>{

    @XmlElement
    protected Long id;
    
    @XmlElement
    protected String name;

    @XmlElement
    protected String description;
    
    @XmlElement
    protected Boolean bundle = Boolean.FALSE;

    @XmlElement
    protected Date activeStartDate;

    @XmlElement
    protected Date activeEndDate;

    @XmlElement
    protected String manufacturer;

    @XmlElement
    protected String model;

    @XmlElement
    protected String promoMessage;
    
    @XmlElement
    protected SkuWrapper defaultSku;
    
    @XmlElement(name = "productOption")
    @XmlElementWrapper(name = "productOptions")
    protected List<ProductOptionWrapper> productOptions;
    
    //The following are for Product Bundles
    @XmlElement
	protected Integer priority;
	
	@XmlElement
	protected BigDecimal potentialSavings;
	
	@XmlElement
	protected Money bundleItemsRetailPrice;
	
	@XmlElement
	protected Money bundleItemsSalePrice;
	
	@XmlElement(name="skuBundleItem")
	@XmlElementWrapper(name="skuBundleItems")
	protected List<SkuBundleItemWrapper> skuBundleItems;

    @Override
    public void wrap(Product model, HttpServletRequest request) {
        this.id = model.getId();
        this.name = model.getName();
        this.description = model.getDescription();
        this.activeStartDate = model.getActiveStartDate();
        this.activeEndDate = model.getActiveEndDate();
        this.manufacturer = model.getManufacturer();
        this.model = model.getModel();
        this.promoMessage = model.getPromoMessage();
        
        if (model.getDefaultSku() != null) {
        	this.defaultSku = (SkuWrapper)context.getBean(SkuWrapper.class.getName());
        	this.defaultSku.wrap(model.getDefaultSku(), request);
        }
        
        if (model.getProductOptions() != null) {
        	this.productOptions = new ArrayList<ProductOptionWrapper>();
        	List<ProductOption> options = model.getProductOptions();
        	for (ProductOption option : options) {
        		ProductOptionWrapper optionWrapper = (ProductOptionWrapper)context.getBean(ProductOptionWrapper.class.getName());
        		optionWrapper.wrap(option, request);
        		this.productOptions.add(optionWrapper);
        	}
        }
        
        if (model instanceof ProductBundle) {
        	this.bundle = Boolean.TRUE;
        	ProductBundle bundle = (ProductBundle)model;
        	this.priority = bundle.getPriority();
    		this.potentialSavings = bundle.getPotentialSavings();
    		this.bundleItemsRetailPrice = bundle.getBundleItemsRetailPrice();
    		this.bundleItemsSalePrice = bundle.getBundleItemsSalePrice();
    		
    		if (bundle.getSkuBundleItems() != null) {
    			this.skuBundleItems = new ArrayList<SkuBundleItemWrapper>();
    			List<SkuBundleItem> bundleItems = bundle.getSkuBundleItems();
    			for (SkuBundleItem item : bundleItems) {
    				SkuBundleItemWrapper skuBundleItemsWrapper = (SkuBundleItemWrapper)context.getBean(SkuBundleItemWrapper.class.getName());
    				skuBundleItemsWrapper.wrap(item, request);
    				this.skuBundleItems.add(skuBundleItemsWrapper);
    			}
    		}
        }
    }
}
