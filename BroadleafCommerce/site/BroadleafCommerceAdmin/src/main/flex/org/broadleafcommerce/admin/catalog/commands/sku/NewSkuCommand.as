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
	
	import org.broadleafcommerce.admin.catalog.control.events.sku.NewSkuEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.SkuModel;
	import org.broadleafcommerce.admin.catalog.vo.sku.Sku;

	public class NewSkuCommand implements Command
	{
		public function NewSkuCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: NewSkuCommand.execute() ");
			var nse:NewSkuEvent = NewSkuEvent(event);
			var skuModel:SkuModel = CatalogModelLocator.getInstance().skuModel;
			skuModel.currentSku = new Sku();
			skuModel.currentSku.allParentProducts.addItem(nse.product);
			skuModel.viewState = SkuModel.STATE_NONE;
			skuModel.viewState = SkuModel.STATE_NEW;
		}
		
	}
}