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
	import com.adobe.cairngorm.view.ViewLocator;
	
	import org.broadleafcommerce.admin.catalog.control.events.media.ShowFileUploadEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.view.media.MediaNewWindowViewHelper;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import org.broadleafcommerce.admin.catalog.vo.media.Media;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;
	import org.broadleafcommerce.admin.core.model.ConfigModel;

	public class ShowFileUploadCommand implements Command
	{
		public function ShowFileUploadCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: execute : ");
			var catalogModelLocator:CatalogModelLocator = CatalogModelLocator.getInstance();
//			var currentViewState:String = CatalogCanvasViewHelper(ViewLocator.getInstance().getViewHelper("catalogCanvas")).getViewIndex();
			var currentViewState:String = catalogModelLocator.catalogModel.viewState;
			var categoryViewState:String = CatalogModel.STATE_VIEW_CATEGORY;
			var productViewState:String = CatalogModel.STATE_VIEW_PRODUCT;
			var sfue:ShowFileUploadEvent = ShowFileUploadEvent(event);
			if(currentViewState ==  categoryViewState){
				var category:Category = catalogModelLocator.categoryModel.currentCategory;
				MediaNewWindowViewHelper(ViewLocator.getInstance().getViewHelper("mediaNewWindowViewHelper")).uploadImage(ConfigModel.SERVER_IMAGES+"/category/"+category.id+"/",Media(sfue.viewData));
			}
			if(currentViewState == productViewState){
				var product:Product = catalogModelLocator.productModel.currentProduct;
				MediaNewWindowViewHelper(ViewLocator.getInstance().getViewHelper("mediaNewWindowViewHelper")).uploadImage(ConfigModel.SERVER_IMAGES+"/product/"+product.id+"/",Media(sfue.viewData));			}
		}
		
	}
}