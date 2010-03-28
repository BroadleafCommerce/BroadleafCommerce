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
package org.broadleafcommerce.admin.catalog.business
{
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.remoting.RemoteObject;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;
	import org.broadleafcommerce.admin.catalog.vo.sku.Sku;
	
	
	public class CatalogServiceDelegate
	{

        private var responder : IResponder;
        private var catalogService : RemoteObject;

		public function CatalogServiceDelegate(responder:IResponder)
		{
			trace("DEBUG: new CatalogServiceDelegate()");
			this.catalogService = CatalogServiceLocator.getInstance().getService();
            this.responder = responder;	
		}
		
		public function findAllCategories():void{
			trace("DEBUG: CatalogServiceDelegate.findAllCategories()");			
			var call:AsyncToken = catalogService.findAllCategories();
			call.addResponder(responder);	
		}
		
		public function saveCategory(category:Category):void{
			trace("DEBUG: CatalogServiceDelegate.saveCategory()");			
			var call:AsyncToken = catalogService.saveCategory(category);
			call.addResponder(responder);
		}
		
		public function updateCategoryParents(category:Category, oldParent:Category, newParent:Category):void{
			trace("DEBUG: CatalogServiceDelegate.updatecategoryParents()");			
			var call:AsyncToken = catalogService.updateCategoryParents(category, oldParent, newParent);
			call.addResponder(responder);			
		}
		
		public function removeCategory(category:Category, parentCategory:Category):void{
			trace("DEBUG: CatalogServiceDelegate.removeCategory()");			
			var call:AsyncToken = catalogService.deleteCategory(category, parentCategory);
			call.addResponder(responder);
		}
		
		public function saveProduct(product:Product):void{
			trace("DEBUG: CatalogServiceDelegate.saveProduct()");			
			var call:AsyncToken = catalogService.saveProduct(product);
			call.addResponder(responder);
		}
		
		public function removeProduct(product:Product):void{
			trace("DEBUG: CatalogServiceDelegate.removeProduct()");			
			var call:AsyncToken = catalogService.deleteProduct(product);
			call.addResponder(responder);
		}
		
		public function findAllProducts():void{
			trace("DEBUG: CatalogServiceDelegate.findAllProducts()");			
			var call:AsyncToken = catalogService.findAllProducts();
			call.addResponder(responder);
		}
		
		public function findProductsByCategory(category:Category):void{
			trace("DEBUG: CatalogServiceDelegate.findProductsByCategory()");			
			var call:AsyncToken = catalogService.findProductsByCategory(category);
			call.addResponder(responder);
		}
		
		public function findAllSkus():void{
			trace("DEBUG: CatalogServiceDelegate.findAllSkus()");			
			var call:AsyncToken = catalogService.findAllSkus();
			call.addResponder(responder);
		}
		
		public function saveSku(sku:Sku):void{
			trace("DEBUG: CatalogServiceDelegate.saveSku()");			
			var call:AsyncToken = catalogService.saveSku(sku);
			call.addResponder(responder);
		}
		
	}
}