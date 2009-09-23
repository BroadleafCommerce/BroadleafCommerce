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
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.catalog.business.CatalogServiceDelegate;
	import org.broadleafcommerce.admin.catalog.control.events.BuildCatalogChainEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	
	public class FindAllCatalogSkusCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void{
			var delegate:CatalogServiceDelegate = new CatalogServiceDelegate(this);
			delegate.findAllSkus();
		}
		
		public function result(data:Object):void{
			var event:ResultEvent = ResultEvent(data);
			CatalogModelLocator.getInstance().skuModel.catalogSkus = ArrayCollection(event.result);
			var bcte:BuildCatalogChainEvent = new BuildCatalogChainEvent();
			bcte.dispatch()
		}
		
		public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);
		}
	}
}