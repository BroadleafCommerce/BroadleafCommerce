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
package org.broadleafcommerce.admin.catalog.control
{
	import com.adobe.cairngorm.control.FrontController;
	
	import org.broadleafcommerce.admin.catalog.commands.BuildCatalogChainCommand;
	import org.broadleafcommerce.admin.catalog.commands.RetrieveCatalogCommand;
	import org.broadleafcommerce.admin.catalog.commands.StandardizeCatalogObjectsCommand;
	import org.broadleafcommerce.admin.catalog.commands.category.AddCategoriesToCatalogTreeCommand;
	import org.broadleafcommerce.admin.catalog.commands.category.AddFeaturedProductToCategoryCommand;
	import org.broadleafcommerce.admin.catalog.commands.category.CopyCategoryCommand;
	import org.broadleafcommerce.admin.catalog.commands.category.EditCategoryCommand;
	import org.broadleafcommerce.admin.catalog.commands.category.FindAllCatalogCategoriesCommand;
	import org.broadleafcommerce.admin.catalog.commands.category.MoveCategoryCommand;
	import org.broadleafcommerce.admin.catalog.commands.category.NewCategoryCommand;
	import org.broadleafcommerce.admin.catalog.commands.category.RemoveCategoryCommand;
	import org.broadleafcommerce.admin.catalog.commands.category.RemoveFeaturedProductFromCategoryCommand;
	import org.broadleafcommerce.admin.catalog.commands.category.SaveCategoryCommand;
	import org.broadleafcommerce.admin.catalog.commands.category.ViewCategoriesCommand;
	import org.broadleafcommerce.admin.catalog.commands.media.AddMediaCommand;
	import org.broadleafcommerce.admin.catalog.commands.media.EditMediaCommand;
	import org.broadleafcommerce.admin.catalog.commands.media.SaveMediaCommand;
	import org.broadleafcommerce.admin.catalog.commands.media.ShowFileUploadCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.AddProductsToCategoriesCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.AddRelatedSaleProductCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.EditProductCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.FilterProductsCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.FindAllProductsCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.FindProductsByCategoryCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.NewProductCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.RemoveProductCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.RemoveRelatedSaleProductCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.SaveProductCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.UpdateParentsOfProductCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.ViewCurrentProductCommand;
	import org.broadleafcommerce.admin.catalog.commands.sku.AddSkuToProductCommand;
	import org.broadleafcommerce.admin.catalog.commands.sku.AddSkusToProductsCommand;
	import org.broadleafcommerce.admin.catalog.commands.sku.EditSkuCommand;
	import org.broadleafcommerce.admin.catalog.commands.sku.FindAllCatalogSkusCommand;
	import org.broadleafcommerce.admin.catalog.commands.sku.NewSkuCommand;
	import org.broadleafcommerce.admin.catalog.commands.sku.RemoveSkuCommand;
	import org.broadleafcommerce.admin.catalog.commands.sku.SaveSkuCommand;
	import org.broadleafcommerce.admin.catalog.control.events.BuildCatalogChainEvent;
	import org.broadleafcommerce.admin.catalog.control.events.RetrieveCatalogEvent;
	import org.broadleafcommerce.admin.catalog.control.events.StandardizeCatalogObjectsEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.AddCategoriesToCatalogTreeEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.AddFeaturedProductToCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.CopyCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.EditCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.FindAllCategoriesEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.MoveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.NewCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.RemoveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.RemoveFeaturedProductFromCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.SaveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.ViewCategoriesEvent;
	import org.broadleafcommerce.admin.catalog.control.events.media.AddMediaEvent;
	import org.broadleafcommerce.admin.catalog.control.events.media.EditMediaEvent;
	import org.broadleafcommerce.admin.catalog.control.events.media.SaveMediaEvent;
	import org.broadleafcommerce.admin.catalog.control.events.media.ShowFileUploadEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.AddProductsToCategoriesEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.AddRelatedSaleProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.EditProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.FilterProductsEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.FindAllProductsEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.FindProductsByCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.NewProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.RemoveProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.RemoveRelatedSaleProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.SaveProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.UpdateParentsOfProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.ViewCurrentProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.sku.AddSkuToProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.sku.AddSkusToProductsEvent;
	import org.broadleafcommerce.admin.catalog.control.events.sku.EditSkuEvent;
	import org.broadleafcommerce.admin.catalog.control.events.sku.FindAllSkusEvent;
	import org.broadleafcommerce.admin.catalog.control.events.sku.NewSkuEvent;
	import org.broadleafcommerce.admin.catalog.control.events.sku.RemoveSkuEvent;
	import org.broadleafcommerce.admin.catalog.control.events.sku.SaveSkuEvent;
	
	public class CatalogController extends FrontController
	{
		public function CatalogController()
		{
			super();
			addCommand(ShowFileUploadEvent.EVENT_SHOW_FILE_UPLOAD, ShowFileUploadCommand);
			
			addCommand(RetrieveCatalogEvent.EVENT_RETRIEVE_CATALOG, RetrieveCatalogCommand);
			addCommand(StandardizeCatalogObjectsEvent.EVENT_STANDARDIZE_CATALOG_OBJECTS, StandardizeCatalogObjectsCommand);
			addCommand(BuildCatalogChainEvent.EVENT_BUILD_CATALOG_TREE, BuildCatalogChainCommand);
			addCommand(AddCategoriesToCatalogTreeEvent.EVENT_ADD_CATEGORIES_TO_CATALOG_TREE, AddCategoriesToCatalogTreeCommand);
			addCommand(AddProductsToCategoriesEvent.EVENT_ADD_PRODUCTS_TO_CATALOG_TREE, AddProductsToCategoriesCommand);
			addCommand(AddSkusToProductsEvent.EVENT_ADD_SKUS_TO_CATALOG_TREE, AddSkusToProductsCommand);


			addCommand(FindAllCategoriesEvent.EVENT_FIND_ALL_CATALOG_CATEGORIES,FindAllCatalogCategoriesCommand);
			addCommand(ViewCategoriesEvent.EVENT_VIEW_CATEGORIES, ViewCategoriesCommand);
			addCommand(NewCategoryEvent.EVENT_NEW_CATALOG_CATEGORY, NewCategoryCommand);
			addCommand(EditCategoryEvent.EVENT_EDIT_CATALOG_CATEGORY, EditCategoryCommand);
			addCommand(SaveCategoryEvent.EVENT_SAVE_CATALOG_CATEGORY, SaveCategoryCommand);
			addCommand(MoveCategoryEvent.EVENT_MOVE_CATEGORY, MoveCategoryCommand);
			addCommand(CopyCategoryEvent.EVENT_COPY_CATEGORY, CopyCategoryCommand);
			addCommand(RemoveCategoryEvent.EVENT_REMOVE_CATEGORY, RemoveCategoryCommand);
			addCommand(AddFeaturedProductToCategoryEvent.EVENT_ADD_FEATURED_PRODUCT_TO_CATEGORY, AddFeaturedProductToCategoryCommand);
			addCommand(RemoveFeaturedProductFromCategoryEvent.EVENT_REMOVE_FEATURED_PRODUCT_FROM_CATEGORY, RemoveFeaturedProductFromCategoryCommand);

			addCommand(FindAllProductsEvent.EVENT_FIND_ALL_PRODUCTS, FindAllProductsCommand);
			addCommand(FindProductsByCategoryEvent.EVENT_FIND_PRODUCTS_BY_CATEGORY, FindProductsByCategoryCommand);
			addCommand(ViewCurrentProductEvent.EVENT_VIEW_CURRENT_PRODUCT, ViewCurrentProductCommand);
			addCommand(NewProductEvent.EVENT_NEW_CATALOG_PRODUCT, NewProductCommand);
			addCommand(EditProductEvent.EVENT_EDIT_CATALOG_PRODUCT, EditProductCommand);
			addCommand(UpdateParentsOfProductEvent.EVENT_ADD_PARENT_TO_PRODUCT, UpdateParentsOfProductCommand);
			addCommand(SaveProductEvent.EVENT_SAVE_CATALOG_PRODUCT, SaveProductCommand);
			addCommand(FilterProductsEvent.EVENT_FILTER_PRODUCTS, FilterProductsCommand);
			addCommand(RemoveProductEvent.EVENT_REMOVE_PRODUCT, RemoveProductCommand);
			addCommand(AddRelatedSaleProductEvent.EVENT_ADD_RELATED_PRODUCT, AddRelatedSaleProductCommand);
			addCommand(RemoveRelatedSaleProductEvent.EVENT_REMOVE_RELATED_PRODUCT, RemoveRelatedSaleProductCommand);

			addCommand(FindAllSkusEvent.EVENT_FIND_ALL_CATALOG_SKUS, FindAllCatalogSkusCommand);
			addCommand(EditSkuEvent.EVENT_EDIT_SKU, EditSkuCommand);
			addCommand(SaveSkuEvent.EVENT_SAVE_CATALOG_SKU, SaveSkuCommand);
			addCommand(NewSkuEvent.EVENT_NEW_SKU, NewSkuCommand);

			addCommand(AddMediaEvent.EVENT_ADD_MEDIA, AddMediaCommand);
			addCommand(SaveMediaEvent.EVENT_SAVE_MEDIA, SaveMediaCommand);
			addCommand(EditMediaEvent.EVENT_EDIT_MEDIA_EVENT, EditMediaCommand);
			
			addCommand(AddSkuToProductEvent.EVENT_ADD_SKU_TO_PRODUCT, AddSkuToProductCommand);
			addCommand(RemoveSkuEvent.EVENT_REMOVE_SKU, RemoveSkuCommand);
		}
		
	}
}