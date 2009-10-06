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
	import mx.controls.Alert;
	import mx.events.CloseEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.product.SaveProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.sku.RemoveSkuEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.vo.sku.Sku;

	public class RemoveSkuCommand implements Command
	{
		public function RemoveSkuCommand()
		{
		}

		private var removeSku:Sku;
		
		public function execute(event:CairngormEvent):void
		{
			var a:Alert = Alert.show("Remove Sku?", "", Alert.OK | Alert.CANCEL, null, alertListener, null, Alert.OK);
			a.width = 400;
			removeSku = RemoveSkuEvent(event).sku;
		}
		
		public function alertListener(event:CloseEvent):void{
			if(event.detail == Alert.OK){
				var sku:Sku = removeSku;
				var skuList:ArrayCollection = CatalogModelLocator.getInstance().productModel.currentProduct.allSkus;
				for each(var s:Sku in skuList){
					if(s.id == sku.id){
						var i:int = skuList.getItemIndex(s);
						skuList.removeItemAt(i);
					}
				}
				new SaveProductEvent(CatalogModelLocator.getInstance().productModel.currentProduct).dispatch();
			}
		}
		
	}
}