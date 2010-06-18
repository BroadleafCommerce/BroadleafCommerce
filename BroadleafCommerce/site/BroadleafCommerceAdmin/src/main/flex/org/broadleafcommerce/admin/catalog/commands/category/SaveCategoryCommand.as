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
package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	
	import org.broadleafcommerce.admin.catalog.business.CatalogServiceDelegate;
	import org.broadleafcommerce.admin.catalog.control.events.category.FindAllCategoriesEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.SaveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	
	public class SaveCategoryCommand implements Command, IResponder{
		
		private var nextEvent:CairngormEvent;
		
		public function execute(event:CairngormEvent):void{
			trace("DEBUG: SaveCategoryCommand.execute()");
			var scce:SaveCategoryEvent = SaveCategoryEvent(event);
			var category:Category = scce.category;
			nextEvent = scce.nextEvent;
			var delegate:CatalogServiceDelegate = new CatalogServiceDelegate(this);
			delegate.saveCategory(category);
		}
		
		public function result(data:Object):void{
			if(nextEvent){
				nextEvent.dispatch();			
			}else{
				var facce:FindAllCategoriesEvent = new FindAllCategoriesEvent();
				facce.dispatch();
				
			}
		}		
		
		public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);			
		}
	}
}