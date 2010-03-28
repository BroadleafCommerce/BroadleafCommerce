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
package org.broadleafcommerce.admin.catalog.commands.sku
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.sku.EditSkuEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.ProductModel;
	import org.broadleafcommerce.admin.catalog.model.SkuModel;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;

	public class EditSkuCommand implements Command
	{
		public function EditSkuCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var ese:EditSkuEvent = EditSkuEvent(event);
			var skuModel:SkuModel = CatalogModelLocator.getInstance().skuModel;
			skuModel.currentSku = ese.sku;
			
			if(ese.showSkusView){
				var productModel:ProductModel = CatalogModelLocator.getInstance().productModel;
				productModel.viewState = ProductModel.STATE_VIEW_SKUS;
			}
			skuModel.viewState = SkuModel.STATE_NONE;
			skuModel.viewState = SkuModel.STATE_EDIT;
		}
		
	}
}