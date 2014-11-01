/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.common.file.service.StaticAssetPathService;
import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.util.xml.ISO8601DateAdapter;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductAttribute;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.RelatedProduct;
import org.broadleafcommerce.core.catalog.domain.SkuBundleItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * This is a JAXB wrapper around Product.
 *
 * User: Kelly Tisdell
 * Date: 4/10/12
 */
@XmlRootElement(name = "product")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ProductWrapper extends BaseWrapper implements APIWrapper<Product> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected String name;

    @XmlElement
    protected String description;

    @XmlElement
    protected String longDescription;

    @XmlElement
    protected Money retailPrice;

    @XmlElement
    protected Money salePrice;

    @XmlElement
    protected MediaWrapper primaryMedia;

    @XmlElement
    protected Boolean active;

    @XmlElement(name = "productOption")
    @XmlElementWrapper(name = "productOptions")
    protected List<ProductOptionWrapper> productOptions;

    // For bundles
    @XmlElement
    protected Integer priority;

    @XmlElement
    protected Money bundleItemsRetailPrice;

    @XmlElement
    protected Money bundleItemsSalePrice;

    //End for bundles

    @XmlElement
    @XmlJavaTypeAdapter(ISO8601DateAdapter.class)
    protected Date activeStartDate;

    @XmlElement
    @XmlJavaTypeAdapter(ISO8601DateAdapter.class)
    protected Date activeEndDate;

    @XmlElement
    protected String manufacturer;

    @XmlElement
    protected String model;

    @XmlElement
    protected String promoMessage;
    
    @XmlElement
    protected Long defaultCategoryId;

    @XmlElement(name = "upsaleProduct")
    @XmlElementWrapper(name = "upsaleProducts")
    protected List<RelatedProductWrapper> upsaleProducts;

    @XmlElement(name = "crossSaleProduct")
    @XmlElementWrapper(name = "crossSaleProducts")
    protected List<RelatedProductWrapper> crossSaleProducts;

    @XmlElement(name = "productAttribute")
    @XmlElementWrapper(name = "productAttributes")
    protected List<ProductAttributeWrapper> productAttributes;

    @XmlElement(name = "media")
    @XmlElementWrapper(name = "mediaItems")
    protected List<MediaWrapper> media;

    @XmlElement(name = "skuBundleItem")
    @XmlElementWrapper(name = "skuBundleItems")
    protected List<SkuBundleItemWrapper> skuBundleItems;

    @Override
    public void wrapDetails(Product model, HttpServletRequest request) {

        this.id = model.getId();
        this.name = model.getName();
        this.description = model.getDescription();
        this.longDescription = model.getLongDescription();
        this.activeStartDate = model.getActiveStartDate();
        this.activeEndDate = model.getActiveEndDate();
        this.manufacturer = model.getManufacturer();
        this.model = model.getModel();
        this.promoMessage = model.getPromoMessage();
        this.active = model.isActive();

        if (model instanceof ProductBundle) {

            ProductBundle bundle = (ProductBundle) model;
            this.priority = bundle.getPriority();
            this.bundleItemsRetailPrice = bundle.getBundleItemsRetailPrice();
            this.bundleItemsSalePrice = bundle.getBundleItemsSalePrice();

            if (bundle.getSkuBundleItems() != null) {
                this.skuBundleItems = new ArrayList<SkuBundleItemWrapper>();
                List<SkuBundleItem> bundleItems = bundle.getSkuBundleItems();
                for (SkuBundleItem item : bundleItems) {
                    SkuBundleItemWrapper skuBundleItemsWrapper = (SkuBundleItemWrapper) context.getBean(SkuBundleItemWrapper.class.getName());
                    skuBundleItemsWrapper.wrapSummary(item, request);
                    this.skuBundleItems.add(skuBundleItemsWrapper);
                }
            }
        } else {
            this.retailPrice = model.getDefaultSku().getRetailPrice();
            this.salePrice = model.getDefaultSku().getSalePrice();
        }

        if (model.getProductOptions() != null && !model.getProductOptions().isEmpty()) {
            this.productOptions = new ArrayList<ProductOptionWrapper>();
            List<ProductOption> options = model.getProductOptions();
            for (ProductOption option : options) {
                ProductOptionWrapper optionWrapper = (ProductOptionWrapper) context.getBean(ProductOptionWrapper.class.getName());
                optionWrapper.wrapSummary(option, request);
                this.productOptions.add(optionWrapper);
            }
        }

        if (model.getMedia() != null && !model.getMedia().isEmpty()) {
            Media media = model.getMedia().get("primary");
            if (media != null) {
                StaticAssetPathService staticAssetPathService = (StaticAssetPathService) this.context.getBean("blStaticAssetPathService");
                primaryMedia = (MediaWrapper) context.getBean(MediaWrapper.class.getName());
                primaryMedia.wrapDetails(media, request);
                if (primaryMedia.isAllowOverrideUrl()) {
                    primaryMedia.setUrl(staticAssetPathService.convertAssetPath(media.getUrl(), request.getContextPath(), request.isSecure()));
                }
            }
        }
        
        if (model.getDefaultCategory() != null) {
            this.defaultCategoryId = model.getDefaultCategory().getId();
        }

        if (model.getUpSaleProducts() != null && !model.getUpSaleProducts().isEmpty()) {
            upsaleProducts = new ArrayList<RelatedProductWrapper>();
            for (RelatedProduct p : model.getUpSaleProducts()) {
                RelatedProductWrapper upsaleProductWrapper =
                        (RelatedProductWrapper) context.getBean(RelatedProductWrapper.class.getName());
                upsaleProductWrapper.wrapSummary(p, request);
                upsaleProducts.add(upsaleProductWrapper);
            }
        }

        if (model.getCrossSaleProducts() != null && !model.getCrossSaleProducts().isEmpty()) {
            crossSaleProducts = new ArrayList<RelatedProductWrapper>();
            for (RelatedProduct p : model.getCrossSaleProducts()) {
                RelatedProductWrapper crossSaleProductWrapper =
                        (RelatedProductWrapper) context.getBean(RelatedProductWrapper.class.getName());
                crossSaleProductWrapper.wrapSummary(p, request);
                crossSaleProducts.add(crossSaleProductWrapper);
            }
        }

        if (model.getProductAttributes() != null && !model.getProductAttributes().isEmpty()) {
            productAttributes = new ArrayList<ProductAttributeWrapper>();
            if (model.getProductAttributes() != null) {
                for (Map.Entry<String, ProductAttribute> entry : model.getProductAttributes().entrySet()) {
                    ProductAttributeWrapper wrapper = (ProductAttributeWrapper) context.getBean(ProductAttributeWrapper.class.getName());
                    wrapper.wrapSummary(entry.getValue(), request);
                    productAttributes.add(wrapper);
                }
            }
        }

        if (model.getMedia() != null && !model.getMedia().isEmpty()) {
            Map<String, Media> mediaMap = model.getMedia();
            media = new ArrayList<MediaWrapper>();
            StaticAssetPathService staticAssetPathService = (StaticAssetPathService) this.context.getBean("blStaticAssetPathService");
            for (Media med : mediaMap.values()) {
                MediaWrapper wrapper = (MediaWrapper) context.getBean(MediaWrapper.class.getName());
                wrapper.wrapSummary(med, request);
                if (wrapper.isAllowOverrideUrl()) {
                    wrapper.setUrl(staticAssetPathService.convertAssetPath(med.getUrl(), request.getContextPath(), request.isSecure()));
                }
                media.add(wrapper);
            }
        }
    }

    @Override
    public void wrapSummary(Product model, HttpServletRequest request) {
        this.id = model.getId();
        this.name = model.getName();
        this.description = model.getDescription();
        this.longDescription = model.getLongDescription();
        this.active = model.isActive();

        if (model instanceof ProductBundle) {

            ProductBundle bundle = (ProductBundle) model;
            this.priority = bundle.getPriority();
            this.bundleItemsRetailPrice = bundle.getBundleItemsRetailPrice();
            this.bundleItemsSalePrice = bundle.getBundleItemsSalePrice();
        } else {
            this.retailPrice = model.getDefaultSku().getRetailPrice();
            this.salePrice = model.getDefaultSku().getSalePrice();
        }

        if (model.getProductOptions() != null && !model.getProductOptions().isEmpty()) {
            this.productOptions = new ArrayList<ProductOptionWrapper>();
            List<ProductOption> options = model.getProductOptions();
            for (ProductOption option : options) {
                ProductOptionWrapper optionWrapper = (ProductOptionWrapper) context.getBean(ProductOptionWrapper.class.getName());
                optionWrapper.wrapSummary(option, request);
                this.productOptions.add(optionWrapper);
            }
        }

        if (model.getMedia() != null && !model.getMedia().isEmpty()) {
            Media media = model.getMedia().get("primary");
            if (media != null) {
                StaticAssetPathService staticAssetPathService = (StaticAssetPathService) this.context.getBean("blStaticAssetPathService");
                primaryMedia = (MediaWrapper) context.getBean(MediaWrapper.class.getName());
                primaryMedia.wrapDetails(media, request);
                if (primaryMedia.isAllowOverrideUrl()) {
                    primaryMedia.setUrl(staticAssetPathService.convertAssetPath(media.getUrl(), request.getContextPath(), request.isSecure()));
                }
            }
        }
    }

}
