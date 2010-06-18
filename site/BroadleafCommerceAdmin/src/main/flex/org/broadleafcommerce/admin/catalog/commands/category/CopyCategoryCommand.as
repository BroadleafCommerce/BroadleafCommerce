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
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.catalog.business.CatalogServiceDelegate;
	import org.broadleafcommerce.admin.catalog.control.events.category.CopyCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.FindAllCategoriesEvent;

	public class CopyCategoryCommand implements Command, IResponder
	{
		public function CopyCategoryCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: CopyCategoryCommand.execute()");			
			var cce:CopyCategoryEvent = CopyCategoryEvent(event);
			cce.movedCategory.allParentCategories.addItem(cce.newParent);
			if(cce.droppedIndex > -1){
				cce.newParent.allChildCategories.addItemAt(cce.movedCategory,cce.droppedIndex);
			}else{
				cce.newParent.allChildCategories.addItem(cce.movedCategory);								
			}
			var delegate:CatalogServiceDelegate = new CatalogServiceDelegate(this);
			delegate.updateCategoryParents(cce.movedCategory, null, cce.newParent);
//			var saveNewParentEvent:SaveCategoryEvent = new SaveCategoryEvent(cce.newParent);
//			var sce:SaveCategoryEvent = new SaveCategoryEvent(cce.movedCategory, saveNewParentEvent);
//			sce.dispatch();
			
		}

		public function result(data:Object):void
		{
			trace("DEBUG: MoveCategoryCommand.result()");
			var event:ResultEvent = ResultEvent(data);
			var fce:FindAllCategoriesEvent = new FindAllCategoriesEvent();
			fce.dispatch();
		}
		
		public function fault(info:Object):void
		{
			trace("DEBUG: MoveCategoryCommand.fault()");			
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
			
		}
		
	}
}