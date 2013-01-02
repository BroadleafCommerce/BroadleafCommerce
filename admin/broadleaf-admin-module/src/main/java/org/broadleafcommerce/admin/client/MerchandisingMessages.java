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

package org.broadleafcommerce.admin.client;

import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.LocalizableResource.Generate;

/**
 * 
 * @author jfischer
 *
 */
@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
@DefaultLocale("en_US")
public interface MerchandisingMessages extends ConstantsWithLookup {

	public String blcProjectPage();
	public String defaultCategoryName();
	public String newCategoryTitle();
	public String confirmDelete();
	public String defaultProductName();
	public String newProductTitle();
	public String categorySearchTitle();
	public String productSearchTitle();
	public String setPromotionMessageTitle();
	public String productSearchPrompt();
	public String mediaNameDefault();
	public String mediaLabelDefault();
	public String newMediaTitle();
	public String mediaSizeSmall();
	public String mediaSizeMedium();
	public String mediaSizeLarge();
	public String mediaPrimary();
	public String mediaAlternate1();
	public String mediaAlternate2();
	public String mediaAlternate3();
	public String mediaAlternate4();
	public String mediaAlternate5();
	public String mediaAlternate6();
	public String newAttributeTitle();
	public String categorySearchPrompt();
	public String categoryListTitle();
	public String orphanCategoryListTitle();
	public String categoryDetailsTitle();
	public String allChildCategoriesListTitle();
	public String productsTabTitle();
	public String featuredProductsListTitle();
	public String allProductsListTitle();
	public String mediaTabTitle();
	public String mediaListTitle();
	public String productsListTitle();
	public String productDetailsTitle();
	public String productAttributesTitle();
	public String crossSaleProductsTitle();
	public String upsaleProductsTitle();
	public String allParentCategoriesListTitle();
	public String saveButtonTitle();
	public String cancelButtonTitle();
	public String categoryMainTitle();
	public String categoryAttributesTitle();
	public String productMainTitle();
    public String merchandisingModuleTitle();
    public String baseProduct();
    public String skuProduct();
    public String otherProduct();
    public String baseCategory();
    public String baseProductAttribute();
    public String detailsTabTitle();
    public String featuredTabTitle();
    public String categoriesTabTitle();
    public String productOptionsListTitle();
    public String productOptionsTabTitle();
    public String productOptionSearchPrompt();
    public String generateSkusButtonTitle();
    public String generateSkusConfirm();
    public String noSkusGenerated();
    public String skuGenerationFail();
    public String skuGenerationSuccess();
    public String productOptionMainTitle();
    public String productOptionListTitle();
    public String productOptionDetailsTitle();
    public String productOptionValuesTitle();
    public String newProductOptionValue();
    public String skusTabTitle();
    public String skusListTitle();
    public String productBundleItemsTitle();
    public String skuSelect();
    public String editBundleItem();
    public String skuStartDateTooltip();
    public String skuEndDateTooltip();
    public String productBundlePricingModelHelp();
    public String productBundleAutoBundleTooltip();
    public String productBundleGroup();
    public String productBundlePricingModel();
    public String productBundleAutoBundle();
    public String productBundlePromotableItems();
    public String productBundlePromotable();
    public String productBundlePriority();
    public String bundleItemQuantity();
    public String bundleItemSalePrice();
    public String bundleItemSalePriceTooltip();
    public String cloneButtonTitle();
    public String noProductExporters();
    public String selectExporterTitle();
    public String exportProductsButtonTitle();
    public String uniqueSkuError();
    
    public String SkuImpl_Sku_Inventory();
    public String ProductWeight_Shipping();
    public String ProductImpl_Product_Url();
    public String ProductImpl_Product_UrlKey();
    public String ProductImpl_Product_Display_Template();
    public String ProductImpl_SEO();
    public String BundleOrderItemImpl_bundleOrderItem();
    public String ProductImpl_bundleProduct();
    public String OrderItemImpl_baseOrderItem();
    public String SearchFacetImpl_ID();
    public String SearchFacetImpl_description();
    public String SearchFacetImpl_field();
    public String SearchFacetImpl_label();
    public String SearchFacetImpl_showOnSearch();
    public String SearchFacetImpl_searchPriority();
    public String SearchFacetImpl_multiselect();
    public String FieldImpl_EntityType();
    public String FieldImpl_descrpition();
    public String FieldImpl_ID();
    public String FieldImpl_propertyName();
    public String FieldImpl_abbreviation();
    public String FieldImpl_searchable();
    public String FieldImpl_facetFieldType();
    public String SearchFacetRangeImpl_MIN_VALUE();
    public String SearchFacetRangeImpl_MAX_VALUE();
    public String CategorySearchFacetImpl_sequence();

    public String SkuImpl_Sku_Media();
    public String SkuImpl_Sku_Media_Key();
    public String skuGenerationInvalid();

    public String cloneErrorMessage();
    public String cloneSuccessMessage();
}
