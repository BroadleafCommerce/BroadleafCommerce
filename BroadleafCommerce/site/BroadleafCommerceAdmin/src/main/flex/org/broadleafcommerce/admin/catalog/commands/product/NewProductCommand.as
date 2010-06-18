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
	
	import org.broadleafcommerce.admin.catalog.control.events.product.EditProductEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;
	import org.broadleafcommerce.admin.catalog.vo.sku.Sku;

	public class NewProductCommand implements Command
	{
		
		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: execute : ");
			var product:Product = new Product();
			var defaultCategory:Category = CatalogModelLocator.getInstance().categoryModel.currentCategory
			var sku:Sku = new Sku();
			sku.retailPrice.amount = 0;
			sku.salePrice.amount = 0;
			product.allParentCategories.addItem(defaultCategory);
			product.defaultCategory = defaultCategory;
			product.allSkus.addItem(sku);
			var epe:EditProductEvent = new EditProductEvent(product,true);
			epe.dispatch();
//			CatalogModelLocator.getInstance().productModel.currentProduct = product;			
//			CatalogModelLocator.getInstance().skuModel.currentSku = Sku(product.allSkus.getItemAt(0)); 
//			CatalogModelLocator.getInstance().productModel.viewState = ProductModel.STATE_VIEW_EDIT;			
		}
		
	}
}