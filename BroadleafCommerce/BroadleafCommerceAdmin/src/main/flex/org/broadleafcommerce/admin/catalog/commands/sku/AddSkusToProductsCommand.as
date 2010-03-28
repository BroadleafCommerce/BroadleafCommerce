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
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.sku.AddSkusToProductsEvent;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;
	import org.broadleafcommerce.admin.catalog.vo.sku.Sku;

	public class AddSkusToProductsCommand implements Command
	{
		public function AddSkusToProductsCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var astct:AddSkusToProductsEvent = AddSkusToProductsEvent(event);
			var productsArray:ArrayCollection = astct.productArray;
			var skusArray:ArrayCollection = astct.skusArray;
			for(var i:String in skusArray){
				var sku:Sku = skusArray[i];
				for each(var product:Product in productsArray){
					for each(var skuParent:Product in sku.allParentProducts){
						if(product.id == skuParent.id){
							if(product.allSkus == null){
								product.allSkus = new ArrayCollection();
							}
							product.allSkus.addItem(sku);
						}
					}
				}
			}
		}
		
	}
}