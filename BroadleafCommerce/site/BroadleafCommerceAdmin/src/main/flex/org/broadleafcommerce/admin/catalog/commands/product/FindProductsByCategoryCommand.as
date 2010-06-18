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
package org.broadleafcommerce.admin.catalog.commands.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.view.ViewLocator;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.catalog.business.CatalogServiceDelegate;
	import org.broadleafcommerce.admin.catalog.control.events.product.FindProductsByCategoryEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.ProductModel;
	import org.broadleafcommerce.admin.catalog.view.product.ProductCanvasViewHelper;

	public class FindProductsByCategoryCommand implements Command, IResponder
	{
		public function FindProductsByCategoryCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: FindProductsByCategoryCommand.execute()");
			var productModel:ProductModel = CatalogModelLocator.getInstance().productModel;
			var fpfce:FindProductsByCategoryEvent = FindProductsByCategoryEvent(event);
			var delegate:CatalogServiceDelegate = new CatalogServiceDelegate(this);
			delegate.findProductsByCategory(fpfce.category);
		}

		public function result(data:Object):void
		{
			trace("DEBUG: FindProductsByCategoryCommand.result()");
			var event:ResultEvent = ResultEvent(data);
			var productModel:ProductModel = CatalogModelLocator.getInstance().productModel;
			productModel.currentProduct.allSkus.removeAll();
			productModel.catalogProducts = ArrayCollection(event.result);
			productModel.filteredCatalogProducts = ArrayCollection(event.result);
			ProductCanvasViewHelper(ViewLocator.getInstance().getViewHelper("productCanvasViewHelper")).selectCurrentProduct();			
		}
		
		public function fault(info:Object):void
		{
			trace("DEBUG: FindProductsByCategoryCommand.fault()");
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}

		
	}
}