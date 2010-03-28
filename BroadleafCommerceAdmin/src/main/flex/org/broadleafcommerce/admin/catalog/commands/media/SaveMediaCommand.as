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
package org.broadleafcommerce.admin.catalog.commands.media
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.SaveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.media.SaveMediaEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.SaveProductEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryModel;
	import org.broadleafcommerce.admin.catalog.model.ProductModel;
	import org.broadleafcommerce.admin.catalog.vo.media.Media;

	public class SaveMediaCommand implements Command
	{
		public function SaveMediaCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{	
			trace("DEBUG: SaveMediaCommand.execute()");
			var sme:SaveMediaEvent = SaveMediaEvent(event);
			var media:Media = sme.media;
			var catalogModelLocator:CatalogModelLocator = CatalogModelLocator.getInstance();
//			var currentViewState:String = CatalogCanvasViewHelper(ViewLocator.getInstance().getViewHelper("catalogCanvas")).getViewIndex();
			var currentViewState:String = catalogModelLocator.catalogModel.viewState;
			var categoryViewState:String = CatalogModel.STATE_VIEW_CATEGORY;
			var productViewState:String = CatalogModel.STATE_VIEW_PRODUCT;
			
			   	
			
			if(currentViewState ==  categoryViewState){
				var categoryModel:CategoryModel = catalogModelLocator.categoryModel;
				categoryModel.currentCategory.categoryMedia[media.key] = media;
				var isNewMedia:Boolean = true;
				for each(var catMedia:Media in categoryModel.categoryMedia){
					if(catMedia.id == media.id){
						isNewMedia = false;
					}
				}
				if(isNewMedia){
					categoryModel.categoryMedia.addItem(media);					
				}
				var sce:SaveCategoryEvent = new SaveCategoryEvent(catalogModelLocator.categoryModel.currentCategory);
				sce.dispatch();
					
			}
			if(currentViewState == productViewState){
				var productModel:ProductModel = catalogModelLocator.productModel;
				productModel.currentProduct.productMedia[media.key] = media;
				var isNewProductMedia:Boolean = true;
				for each(var prodMedia:Media in productModel.productMedia){
					if(prodMedia.id == media.id){
						isNewProductMedia = false;
					}
				}
				if(isNewProductMedia){
					productModel.productMedia.addItem(media);					
				}
				var spe:SaveProductEvent = new SaveProductEvent(catalogModelLocator.productModel.currentProduct);
				spe.dispatch();
			}			
		}
	}
		
}
